(function() {
  'use strict';
  
  $(document).on('click', '.menu-tools-locale', function (e) {
    $(this).closest('.menu-tools-locale-container').find('.menu-tools-locale-list').show();
    
    $(document).one('click', function (e) {
      $('.menu-tools-locale-list').hide();
    });
  });

  
}).call(this);