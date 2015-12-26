(function() {
  'use strict';
    
  $(document).ready(function () {
    $('.store-product-image-link').magnificPopup({ 
      type: 'image'
    });
    
    var baseUrl = window.location.protocol + '//' + window.location.hostname;
    if (window.location.port !== 443) {
      baseUrl += ':' + window.location.port;
    }
    baseUrl += CONTEXTPATH + '/store/'; 
    
    $(".store-product-share-button").each(function (index, element) {
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
          },
          pinterest: {
            enabled: false
          },
          reddit: {
            enabled: false
          },
          linkedin: {
            enabled: false
          },
          whatsapp: {
            enabled: false
          },
          email: {
            enabled: false
          },
        }
      });
      
      $(window).click($.proxy(function (event) {
        if ($(event.target).closest('.store-product-share-button').length == 0) {
          this.close();
        }
      }, share));
    });
    
  });

}).call(this);