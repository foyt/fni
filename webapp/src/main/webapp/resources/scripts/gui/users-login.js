(function() {
  'use strict';
  $(document).ready(function() {
    
    $('.users-login-forgot-password-link').click(function () {
      dust.render("users-forgot-password", {
      }, function(err, html) {
        if (!err) {
          var dialog = $(html);
          dialog.dialog({
            modal: true,
            width: 400,
            buttons: [{
              'text': dialog.data('send-button'),
              'click': function(event) {
                var email = dialog.find('input[name="email"]').val();
                var form = $('.users-login-forgot form');
                var prefix = form.attr('name');
                $('input[name="' + prefix + ':users-login-forgot-password-email' + '"]').val(email);
                $('.users-login-forgot-password-send').click();
                $(this).dialog("close");
              }
            }, {
              'text': dialog.data('cancel-button'),
              'click': function(event) { 
                $(this).dialog("close");
              }
            }]
          });
        } else {
         // TODO: Proper error handling...
          alert(err);
        }
      });
    });
    
    $('.user-login-login-panel form').submit(function (event) {
      var prefix = $(this).attr('name');
      var passwordInput = $(this).find('input[name="' + prefix + ':password"]');
      var passwordEncodedInput = $(this).find('input[name="' + prefix + ':password-encoded"]');
      passwordEncodedInput.val(hex_md5(passwordInput.val())); 
      passwordInput.val('');
    });

    $('.user-login-register-panel form').submit(function (event) {
      var prefix = $(this).attr('name');
      var password1Input = $(this).find('input[name="' + prefix + ':password1"]');
      var password2Input = $(this).find('input[name="' + prefix + ':password2"]');
      $(this).find('input[name="' + prefix + ':password1-encoded"]').val(hex_md5(password1Input.val())); 
      $(this).find('input[name="' + prefix + ':password2-encoded"]').val(hex_md5(password2Input.val())); 
      password1Input.val('');
      password2Input.val('');
    });

  });

}).call(this);