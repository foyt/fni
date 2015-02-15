(function() {
  'use strict';
  
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
      this._editor.destroy();
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
    
    reload: function (callback) {
      $.ajax(CONTEXTPATH + '/rest/illusion/events/' + this.options.eventId + '/forumPosts/' + this.id(), {
        type: 'GET',
        contentType: "application/json",
        dataType : "json",
        accepts: {
          'json' : 'application/json'
        },
        success : $.proxy(function(data) {
          var modified = this.element.find('.illusion-forum-post-modified');
          var pattern = $('.illusion-forum-posts').attr('data-date-format-long');

          modified.html(formatJavaLocale(modified.attr('data-locale'), new Date(Date.parse(data.modified))));
          this.element.find('.illusion-forum-post-content').html(data.content);
          
          if ($.isFunction(callback)) {
            callback();
          }
        }, this)
      });
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
      this.element.find('.illusion-forum-post-content-editor').show().forumPostEditor();
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
    var eventId = $('#event-id').val()
    
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
      eventId: eventId
    });
  });

}).call(this);