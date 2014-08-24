(function() {
  'use strict';
  
  function search() {
    var input = $('.menu-tools-search-text');
    
    var query = $.trim(input.val());
    if (query) {
      if (input.data('searching') != true) {
        input.data('searching', true);
        $('.menu-tools-search-loading').show();
        $.ajax({
          url : CONTEXTPATH + "/search/",
          data : {
            q: query
          },
          success : function(data) {
            input.data('searching', false);
            $('.menu-tools-search-loading').hide();
            
            dust.render("search", data, function(err, html) {
              if (!err) {
                $('.menu-tools-search-result').show().html(html);
              } else {
                // TODO: Proper error handling...
                alert(err);
              }
            }); 
            
            if (query != $.trim(input.val())) {
              search();
            }
          }
        });
      }
    } else {
      $('.menu-tools-search-result').hide();
    }
  }
  
  $(document).on('click', '.menu-about', function (e) {
    var container = $(this).closest('.menu-about-container');
    var list = container.find('.menu-about-list');
    var offset = (container.width() - $(this).width()) / 2;

    list.css('margin-left', (offset - 8) + 'px');
    list.show();
    
    var attachListener = function () {
      $(document).one('click', function (e) {
        if ($(e.target).closest('.menu-about').length == 0) {
          $('.menu-about-list').hide();
        } else {
          attachListener();
        }
      });
    };
    
    attachListener();
  });
  
  $(document).on('click', '.menu-tools-locale', function (e) {
    $(this).closest('.menu-tools-locale-container').find('.menu-tools-locale-list').show();
    
    var attachListener = function () {
      $(document).one('click', function (e) {
        if ($(e.target).closest('.menu-tools-locale').length == 0) {
          $('.menu-tools-locale-list').hide();
        } else {
          attachListener();
        }
      });
    };
    
    attachListener();
  });
  
  $(document).on('click', '.menu-tools-account', function (e) {
    $(this).closest('.menu-tools-account-container').find('.menu-tools-account-list').show();
    
    var attachListener = function () {
      $(document).one('click', function (e) {
        if ($(e.target).closest('.menu-tools-account').length == 0) {
          $('.menu-tools-account-list').hide();
        } else {
          attachListener();
        }
      });
    };
    
    attachListener();
  });
  
  $(document).on('keyup', '.menu-tools-search-text', function (event) {
    search();
  });
  
  $(document).on('mouseup', function (event) {
    var target = event.target;
    if ($(target).closest('.menu-tools-search-container').length == 0) {
      $('.menu-tools-search-result').hide(); 
    }
  });
  
  window.onChangeLocale = function (e) {
    if (e.status === 'success') {
      window.location.reload(true);
    }
  }
  
}).call(this);