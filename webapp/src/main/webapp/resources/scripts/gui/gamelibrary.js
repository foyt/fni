(function() {
  'use strict';
  
  function search() {
    var input = $('.gamelibrary-search');
    
    var query = $.trim(input.val());
    if (query) {
      if (input.data('searching') != true) {
        input.data('searching', true);
        $('.gamelibrary-search-loading').show();
        $.ajax({
          url : CONTEXTPATH + "/search/",
          data : {
            q: query,
            search: 'gamelibrarypublications'
          },
          success : function(data) {
            input.data('searching', false);
            $('.gamelibrary-search-loading').hide();
            
            dust.render("gamelibrary-search", data, function(err, html) {
              if (!err) {
                $('.gamelibrary-search-result').show().html(html);
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
      $('.gamelibrary-search-result').hide();
    }
  }

  $(document).on('keyup', '.gamelibrary-search', function (event) {
    search();
  });
  
  $(document).on('mouseup', function (event) {
    var target = event.target;
    if ($(target).closest('.gamelibrary-search-container').length == 0) {
      $('.gamelibrary-search-result').hide(); 
    }
  });

  $(document).ready(function () {
    $('.gamelibrary-publication-image-link').magnificPopup({ 
      type: 'image'
    });
  });

}).call(this);