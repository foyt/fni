(function() {
  'use strict';
  
  $(document).ready(function() { 
    webshim.polyfill('forms');
    
    $('.proceed-to-payment').click(function (event) {
      event.preventDefault();
      
      var form = $(event.target).closest('form');
      var paymentDetails = {};
      
      $.each(form.serializeArray(), function (index, object) {
        paymentDetails[object.name] = object.value;
      });
      
      $('.payment-details').val(JSON.stringify(paymentDetails));
      $('.form-submit')[0].click();
    });
  });

}).call(this);