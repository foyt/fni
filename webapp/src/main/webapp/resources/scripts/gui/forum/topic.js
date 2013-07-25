(function() {
  $(document).ready(function() {
    CKEDITOR.replaceClass = 'forum-post-editor';
    CKEDITOR.replaceAll( function( textarea, config ) {
      config.toolbar = 'Forum';
    });
  });
}).call(this);