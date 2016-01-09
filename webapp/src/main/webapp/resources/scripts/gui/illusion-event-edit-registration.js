(function() {
  'use strict';

  $(document).ready(function () {
    $('textarea.editor')
      .codeMirror({
        mode: "application/json"
      });
  });
  
}).call(this);