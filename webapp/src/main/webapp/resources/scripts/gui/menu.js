(function() {
  'use strict';
  $(document).ready(function() {
    $('#site-menu-bar-widget-locale').click(function (event) {
      $(this).addClass('site-menu-bar-widget-active');
      $('#site-menu-bar-widget-locale-content')
        .addClass('site-menu-bar-widget-content-active')
        .show();
    });
    
    $('#site-menu-bar-widget-account').click(function (event) {
      $(this).addClass('site-menu-bar-widget-active');
      $('#site-menu-bar-widget-account-content')
        .addClass('site-menu-bar-widget-content-active')
        .show();
    });

    $(document).mousedown(function (event) {
      var element = event.target;
      if ($(element).closest('.site-menu-bar-widget-content').length == 0) {
        $('.site-menu-bar-widget-content-active')
          .removeClass('site-menu-bar-widget-content-active')
          .hide();
        
        $('.site-menu-bar-widget-active').removeClass('site-menu-bar-widget-active');
      }
    });
  });
  
}).call(this);