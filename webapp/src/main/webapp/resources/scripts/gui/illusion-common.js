(function() {
  'use strict';

  $(document).on('click', '.illusion-event-navigation-menu', function () {
    $(this).closest('.illusion-event-navigation-menu')
      .addClass('illusion-event-navigation-menu-open');
  });

  $(document).on('click',  function (event) {
    if ($(event.target).closest('.illusion-event-navigation-menu-open').length == 0) {
      $('.illusion-event-navigation-menu-open').removeClass('illusion-event-navigation-menu-open');
    }
  });
  
  
}).call(this);