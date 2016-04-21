(function() {
  'use strict';

  function search() {
    var input = $('.forge-search');
    var query = $.trim(input.val());
    if (query) {
      if (input.data('searching') != true) {
        input.data('searching', true);
        $('.forge-search-loading').show();
        $.ajax({
          url : CONTEXTPATH + "/search/",
          data : {
            q: query,
            source: 'FORGE'
          },
          success : function(data) {
            input.data('searching', false);
            $('.forge-search-loading').hide();
            
            dust.render("forge-public-search", data, function(err, html) {
              if (!err) {
                $('.forge-search-result').show().html(html);
              } else {
                $('.notifications').notifications('notification', 'error', err);
              }
            }); 
            
            if (query != $.trim(input.val())) {
              search();
            }
          }
        });
      }
    } else {
      $('.forge-search-result').hide();
    }
  }

  $(document).on('keyup', '.forge-search', function (event) {
    search();
  });
  
  $(document).on('mouseup', function (event) {
    var target = event.target;
    if ($(target).closest('.forge-search-container').length == 0) {
      $('.forge-search-result').hide(); 
    }
  });
  
  $(document).ready(function () {
    $('p.description').each(function (index, p) {
      $(p).attr('title', $(p).text());
      $clamp(p, {clamp: 8});
    });
  });

}).call(this);