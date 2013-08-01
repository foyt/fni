(function() {
  'use strict';
  $(document).ready(function() {
    $('#site-menu-bar-widget-locale').click(function (event) {
      $(this).addClass('site-menu-bar-widget-active');
      $('#site-menu-bar-widget-locale-content').show();
    });

    $(document).mousedown(function (event) {
      var element = event.target;
      if ($(element).closest('#site-menu-bar-widget-locale-content').length == 0) {
        $('#site-menu-bar-widget-locale').removeClass('site-menu-bar-widget-active');
        $('#site-menu-bar-widget-locale-content').hide();
      }
    });
  });
  
}).call(this);