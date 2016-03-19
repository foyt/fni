(function() {
  'use strict';

  $(document).on('click', '.material-share', function () {
    $('<div>').materialShareDialog({
      materialId: $('.material-id').val()
    });
  });
  
}).call(this);