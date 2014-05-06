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
            source: 'GAMELIBRARY'
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
    
    var baseUrl = window.location.protocol + '//' + window.location.hostname;
    if (window.location.port !== 443) {
      baseUrl += ':' + window.location.port;
    }
    baseUrl += CONTEXTPATH + '/gamelibrary/'; 
    
    $(".gamelibrary-publication-share-button").each(function (index, element) {
      var share = new Share('#' + $(element).attr('id'), {
        url: baseUrl + $(element).data('url-name'),
        ui: {
          button_font: false,
          button_text: $(element).data('button-text')
        },
        networks: {
          google_plus: {
            enabled: true
          },
          twitter: {
            enabled: true
          },
          facebook: {
            enabled: true,
            load_sdk: false
          }
        }
      });
      
      $(window).click($.proxy(function (event) {
        if ($(event.target).closest('.gamelibrary-publication-share-button').length == 0) {
          this.close();
        }
      }, share));
    });
    
  });

}).call(this);