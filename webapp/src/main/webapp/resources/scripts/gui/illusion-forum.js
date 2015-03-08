(function() {
  'use strict';
  
  var illusionClient = new $.RestClient(CONTEXTPATH + '/rest/illusion/', {stringifyData: true});

  illusionClient.add('events');
  illusionClient.events.add('participants');
  illusionClient.events.add('forumPosts');
  
  var _PARTICIPANT_CACHE = {};
  
  $.widget("custom.forumPostEditor", {
    _create : function() {
      this._editor = CKEDITOR.replace(this.element[0], {
        toolbar: [
           { name: 'clipboard',   items : [ 'Cut','Copy','Paste','-','Undo','Redo' ] },
           { name: 'editing',     items : [ 'Find','Replace' ] },
           { name: 'basicstyles', items : [ 'Bold','Italic','Underline','Strike','Subscript','Superscript','-','RemoveFormat' ] },
           { name: 'paragraph',   items : [ 'NumberedList','BulletedList','-','Outdent','Indent','-','Blockquote' ] },
           { name: 'links',       items : [ 'Link','Unlink' ] },
           { name: 'insert',      items : [ 'Image','SpecialChar' ] },
           { name: 'tools',       items : [ 'Maximize' ] }
         ],
         contentsCss: ['//cdnjs.cloudflare.com/ajax/libs/ckeditor/4.2/contents.css', CONTEXTPATH + '/theme/css/illusion-event-forum-editor.css' ]
      });
    },
    
    data: function (data) {
      if (data !== undefined) {
        this._editor.setData(data);
      } else {
        return this._editor.getData();
      }
    },
    
    readOnly: function (readOnly) {
      if (readOnly !== undefined) {
        this._editor.setReadOnly(readOnly);
      } else {
        return this._editor.readOnly;
      }
    },
    
    _destroy : function() {
      this._editor.destroy(false);
    }
  });
  
  $.widget("custom.forumPost", {
    options: {
      eventId: null
    },
    
    _create : function() {
      this.element.on('click', '.illusion-forum-post-content-save', $.proxy(this._onSaveClick, this));
      this.element.on('click', '.illusion-forum-post-edit', $.proxy(this._onEditClick, this));
    },
    
    _destroy: function() {
      this.element.off('click', '.illusion-forum-post-content-save');
      this.element.off('click', '.illusion-forum-post-edit');
    },
    
    id: function () {
      return this.element.attr('data-post-id');  
    },
    
    _getParticipant: function (userId, callback) {
      var cached = _PARTICIPANT_CACHE[userId];
      if (cached) {
        return callback(cached);
      }
      
      illusionClient.events.participants.read(this.options.eventId, {userId: userId}).done(function (data, textStatus, xhrObject){
        switch (xhrObject.status) {
          case 200:
            if ($.isArray(data) && data.length == 1) {
              _PARTICIPANT_CACHE[userId] = data[0];
              callback(data[0]);
            } else {
              $('.notifications').notifications('notification', 'error', 'Several participants found for userId');
            }
          break;
          case 204:
            callback(null);
          break;
          default:
            $('.notifications').notifications('notification', 'error', textStatus);
          break;
        }
      });
    },
    
    load: function (callback) {
      this.element.removeClass('illusion-forum-post-pending').addClass('illusion-forum-post-loading');

      $.ajax('event-forum/' + this.id(), {
        success : $.proxy(function(data) {
          $(this.element).html(data).removeClass('illusion-forum-post-loading');
        }, this)
      });
    },
    
    reload: function (callback) {
      this.load(callback);
    },
    
    save: function (callback) {
      $.ajax(CONTEXTPATH + '/rest/illusion/events/' + this.options.eventId + '/forumPosts/' + this.id(), {
        type: 'PUT',
        contentType: "application/json",
        dataType : "json",
        data: JSON.stringify({
          'content': this.element.find('.illusion-forum-post-content-editor').forumPostEditor('data')
        }),
        accepts: {
          'json' : 'application/json'
        },
        success : function(data) {
          if ($.isFunction(callback)) {
            callback();
          }
        }
      });
    },
    
    edit: function () {
      this.element.find('.illusion-forum-post-content, .illusion-forum-post-footer').hide();
      this.element.find('.illusion-forum-post-content-save').show();
      this.element.find('.illusion-forum-post-content-editor')
        .html(this.element.find('.illusion-forum-post-content').html())
        .show()
        .forumPostEditor();
    },
    
    closeEditor: function () {
      this.element.find('.illusion-forum-post-content-save').hide();
      this.element.find('.illusion-forum-post-content-editor').forumPostEditor('destroy').hide();
      this.element.find('.illusion-forum-post-content, .illusion-forum-post-footer').show();
    },
    
    _onEditClick: function () {
      this.edit();
    },
    
    _onSaveClick: function (event) {
      this.save($.proxy(function () {
        this.closeEditor();
        this.reload();
      }, this));
    }
  });
  
  $.widget("custom.forum", {
    _create : function() {
      $('.illusion-forum-post-editor').forumPostEditor();
      $('.illusion-forum-post-reply').click($.proxy(function (event) {
        event.preventDefault();
        
        var content = $('.illusion-forum-post-editor')
          .forumPostEditor('readOnly', true)
          .forumPostEditor('data');
        
        illusionClient.events.forumPosts.create(this.options.eventId, {'content': content}).done(function (data, textStatus, xhrObject){
          if (xhrObject.status !== 200) {
            $('.notifications').notifications('notification', 'error', textStatus);
          }
          $('.illusion-forum-post-editor')
            .forumPostEditor('data', '')
            .forumPostEditor('readOnly', false);
        });
      }, this));
      
      $('.illusion-forum-post').forumPost({
        eventId: this.options.eventId,
        eventUrlName: this.options.eventUrlName
      });
      
      $('.illusion-forum-post-pending').forumPost('load');
      var socketUrl = (window.location.protocol == 'https:' ? 'wss:' : 'ws:') + '//' + window.location.host + CONTEXTPATH + '/ws/forum/' + this.options.topicId;
      this._webSocket = this._openWebSocket(socketUrl);
      this._webSocket.onmessage = $.proxy(this._onWebSocketMessage, this);
      $(window).on('beforeunload', $.proxy(this._onWindowBeforeUnload, this));
    },
    
    _onWebSocketMessage: function (event) {
      var data = $.parseJSON(event.data);
      
      $('<div>')
        .attr('data-post-id', data.postId)
        .addClass("illusion-forum-post")
        .appendTo($('.illusion-forum-posts'))
        .forumPost({
          eventId: this.options.eventId,
          eventUrlName: this.options.eventUrlName
        })
        .forumPost('load');
    },
    
    _onWindowBeforeUnload: function (event) {
      this._webSocket.onclose = function () {};
      this._webSocket.close();
    },
    
    _openWebSocket: function (url) {
      if ((typeof window.WebSocket) !== 'undefined') {
        return new WebSocket(url);
      } else if ((typeof window.MozWebSocket) !== 'undefined') {
        return new MozWebSocket(url);
      }
      
      return null;
    },
    
    _destroy : function() {
    }
  });
  
  $(document).ready(function() {
    $(document.body).forum({
      eventId: $('#event-id').val(),
      eventUrlName: $('#event-url-name').val(),
      topicId: $('#topicId').val()
    });
  });

}).call(this);