(function() {
  'use strict';

  $(document).ready(function () {
    $('textarea.illusion-event-template-editor')
      .codeMirror({
        mode: "text/x-jade"
      });
  });
  
}).call(this);