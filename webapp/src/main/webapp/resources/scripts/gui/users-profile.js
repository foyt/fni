(function() {
  'use strict';
  
  function search() {
    var input = $('.users-profile-search');
    var query = $.trim(input.val());
    if (query) {
      if (input.data('searching') != true) {
        input.data('searching', true);
        $('.users-profile-search-loading').show();
        $.ajax({
          url : CONTEXTPATH + "/search/",
          data : {
            q: query,
            source: 'USERS'
          },
          success : function(data) {
            input.data('searching', false);
            $('.users-profile-search-loading').hide();
            
            dust.render("users-search", data, function(err, html) {
              if (!err) {
                $('.users-profile-search-result').show().html(html);
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
      $('.users-profile-search-result').hide();
    }
  }

  $(document).on('keyup', '.users-profile-search', function (event) {
    search();
  });
  
  $(document).on('mouseup', function (event) {
    var target = event.target;
    if ($(target).closest('.users-profile-search-container').length == 0) {
      $('.users-profile-search-result').hide(); 
    }
  });
  
  $(document).ready(function () {
    $('.publication-image').magnificPopup({ 
      type: 'image'
    });
  });
  
}).call(this);