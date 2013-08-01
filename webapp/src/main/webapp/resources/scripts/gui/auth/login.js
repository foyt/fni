(function() {
  'use strict';
  $(document).ready(function() {
    var internalLoginForm = $('.internal-login-container form');
    internalLoginForm.submit(function (event) {
      var prefix = $(this).attr('name');
      var passwordInput = $(this).find('input[name="' + prefix + ':password"]');
      var passwordEncodedInput = $(this).find('input[name="' + prefix + ':password-encoded"]');
      passwordEncodedInput.val(hex_md5(passwordInput.val())); 
      passwordInput.val('');
    });
    
    $('#forgot-password-link').click(function (event) {
      $.ajax(CONTEXTPATH + '/auth/dialogs/forgotpassword.jsf', {
        async: false,
        success : function(data, textStatus, jqXHR) {
          var dialog = $(data).dialog({
            modal: true,
            width: 600,
            buttons: [{
              'class': 'cancel-button',
              'text': 'Cancel',
              'click': function(event) { 
                $(this).dialog("close");
              }
            }, {
              'class': 'publish-button',
              'text': 'Reset Password',
              'click': function(event) { 
                var form = $('#forgot-password-form-container form');
                var prefix = form.attr('name');
                form.find('input[name="' + prefix + ':email"]').val($(this).find('input[name="email"]').val());
                form.find('input[name="' + prefix + ':send-button"]').click();
              }
            }]
          });
        }
      });
    });
  });
}).call(this);