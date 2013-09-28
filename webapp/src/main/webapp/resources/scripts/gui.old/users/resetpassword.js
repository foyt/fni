(function() {
  'use strict';
  $(document).ready(function() {
    var internalLoginForm = $('form');
    internalLoginForm.submit(function (event) {
      var prefix = $(this).attr('name');
      
      var password1Input = $(this).find('input[name="' + prefix + ':password1"]');
      var password2Input = $(this).find('input[name="' + prefix + ':password2"]');
      
      $(this).find('input[name="' + prefix + ':password1-encoded"]').val(
         hex_md5(password1Input.val())
      );
      
      $(this).find('input[name="' + prefix + ':password2-encoded"]').val(
         hex_md5(password2Input.val())
      );
      
      password1Input.val('');
      password2Input.val('');
    });
  });
}).call(this);