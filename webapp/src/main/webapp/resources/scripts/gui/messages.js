(function() {
  'use strict';
  
  $(document).ready(function() {
    $('input[name="jsf-message"]').each(function (index, input) {
      $('.notifications').notifications('notification', $(input).data('severity'), $(input).val());
    });
    
    $('.faces-messages').remove();
  });
  
}).call(this);