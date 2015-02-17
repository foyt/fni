(function() {
  'use strict';
  
  var illusionClient = new $.RestClient(CONTEXTPATH + '/rest/illusion/');

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
    
    data: function () {
      return this._editor.getData();
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
      this.element.find('.illusion-forum-post-content-save').click($.proxy(this._onSaveClick, this));
      this.element.find('.illusion-forum-post-edit').click($.proxy(this._onEditClick, this));
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
      this.element.addClass('illusion-forum-post-loading');
      
      illusionClient.events.forumPosts.read(this.options.eventId, this.id()).done($.proxy(function (post, textStatus, xhrObject){
        switch (xhrObject.status) {
          case 200:
            this._getParticipant(post.authorId, $.proxy(function (participant) {
              this.element.find('.illusion-forum-post-author-name')
                .html(participant.displayName);
              
              this.element.find('.illusion-forum-post-author-image-container img').remove();
              this.element.find('.illusion-forum-post-author-image-container').append(
                $('<img>').attr('src', CONTEXTPATH + "/illusion/eventAvatar/" + this.options.eventUrlName + "/" + participant.id + "?size=80")
              );
              
              this.element.find('.illusion-forum-post-sent')
                .html(formatJavaLocale($('.unformatted-locales').attr('data-post-sent'), 
                    new Date(Date.parse(post.created))));
              
              this.element.find('.illusion-forum-post-modified')
                .html(formatJavaLocale($('.unformatted-locales').attr('data-post-modified'), 
                  new Date(Date.parse(post.modified))));
      
              this.element.find('.illusion-forum-post-content').html(post.content);
              this.element.removeClass('illusion-forum-post-loading');
              
              if ($.isFunction(callback)) {
                callback();
              }
            }, this));
          break;
          default:
            $('.notifications').notifications('notification', 'error', textStatus);
          break;
        }
      }, this));
      
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
    },
    
    _destroy : function() {
      
    }
  });
  
  $(document).ready(function() {
    var eventId = $('#event-id').val();
    var eventUrlName = $('#event-url-name').val();
    
    $('.illusion-forum-post-editor').forumPostEditor();
    $('.illusion-forum-post-reply').click(function (event) {
      event.preventDefault();
      
      $.ajax(CONTEXTPATH + '/rest/illusion/events/' + eventId + '/forumPosts/', {
        type: 'POST',
        contentType: "application/json",
        dataType : "json",
        data: JSON.stringify({
          'content': $('.illusion-forum-post-editor').forumPostEditor('data')
        }),
        accepts: {
          'json' : 'application/json'
        },
        success : function(data) {
          window.location.reload(true);
        }
      });
    });
    
    $('.illusion-forum-post').forumPost({
      eventId: eventId,
      eventUrlName: eventUrlName
    });

    $('.illusion-forum-post-pending').waypoint(function(direction) {
      $(this).waypoint('destroy');
      $(this).removeClass('illusion-forum-post-pending')
      $(this).forumPost('load');
    }, {
      offset: '110%'
    });
  });

}).call(this);