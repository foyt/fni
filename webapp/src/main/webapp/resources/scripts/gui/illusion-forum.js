(function() {
  'use strict';
  
  $(document).ready(function() {
    var editor = CKEDITOR.replace($('.illusion-forum-post-editor')[0], {
      toolbar: [
         { name: 'clipboard',   items : [ 'Cut','Copy','Paste','-','Undo','Redo' ] },
         { name: 'editing',     items : [ 'Find','Replace','-','SelectAll','-','Scayt' ] },
         { name: 'basicstyles', items : [ 'Bold','Italic','Underline','Strike','Subscript','Superscript','-','RemoveFormat' ] },
         { name: 'paragraph',   items : [ 'NumberedList','BulletedList','-','Outdent','Indent','-','Blockquote' ] },
         { name: 'links',       items : [ 'Link','Unlink' ] },
         { name: 'insert',      items : [ 'Image','SpecialChar' ] },
         { name: 'tools',       items : [ 'Maximize' ] }
       ],
       contentsCss: ['//cdnjs.cloudflare.com/ajax/libs/ckeditor/4.2/contents.css', CONTEXTPATH + '/forum/ckcontents.css' ]
    });
    
    $('.illusion-forum-post-reply').click(function (event) {
      event.preventDefault();
      
      var eventId = $('#event-id').val()
      
      $.ajax(CONTEXTPATH + '/rest/illusion/events/' + eventId + '/forumPosts/', {
        type: 'POST',
        contentType: "application/json",
        dataType : "json",
        data: JSON.stringify({
          'content': editor.getData()
        }),
        accepts: {
          'json' : 'application/json'
        },
        success : function(data) {
          window.location.reload(true);
        }
      });
    });
  });
  
}).call(this);