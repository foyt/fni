(function() {
  'use strict';
  
  $(document).on('click', '.jsf-messages-container ul li a', function (e) {
    $(this).closest('li').slideUp();
  });
  
}).call(this);