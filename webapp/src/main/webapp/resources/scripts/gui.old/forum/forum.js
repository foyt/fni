(function() {
  'use strict';
  $(document).ready(function() {
    $('.forum-new-topic-link').click(function (e) {
      $(this).hide();
      
      CKEDITOR.replaceClass = 'forum-topic-contents-editor';
      CKEDITOR.replaceAll( function( textarea, config ) {
        config.toolbar = 'Forum';
      });
      
      $('.forum-new-topic-editor-container').show();
    });
  });
}).call(this);