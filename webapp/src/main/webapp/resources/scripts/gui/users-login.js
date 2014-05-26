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
              'class': 'ok-button',
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
              'class': 'cancel-button',
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

  });

}).call(this);