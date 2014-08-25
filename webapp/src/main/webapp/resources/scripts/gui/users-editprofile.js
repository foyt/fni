(function() {
  'use strict';
  
  $(document).ready(function () {
    CKEDITOR.replace($('.about-editor').attr('name'), { 
      skin: 'moono',
      language: LOCALE,
      toolbar: [
        { name: 'clipboard',   items : [ 'Cut','Copy','Paste','-','Undo','Redo' ] },
        { name: 'editing',     items : [ 'Find','Replace','-','SelectAll','-','Scayt' ] },
        { name: 'basicstyles', items : [ 'Bold','Italic','Underline','Strike','Subscript','Superscript','-','RemoveFormat' ] },
        { name: 'paragraph',   items : [ 'NumberedList','BulletedList','-','Outdent','Indent','-','Blockquote' ] },
        { name: 'links',       items : [ 'Link','Unlink' ] },
        { name: 'insert',      items : [ 'Image','SpecialChar' ] },
        { name: 'tools',       items : [ 'Maximize' ] }
      ]  
    });
    
    $('.users-editprofile-add-authentication-source select').change(function (event) {
      $('.users-editprofile-enable-authsource-container').hide();
      
      switch ($(this).val()) {
        case 'INTERNAL':
          $('.users-editprofile-enable-internal-authsource-container').show();
        break;
        case 'FACEBOOK':
          $('.users-editprofile-enable-facebook-authsource-container').show();
        break;
        case 'GOOGLE':
          $('.users-editprofile-enable-google-authsource-container').show();
        break;
        case 'YAHOO':
          $('.users-editprofile-enable-yahoo-authsource-container').show();
        break;
      }
    });
    
    $('.users-editprofile-new-internal-auth-link').click(function(event) {
      var form = $(this).closest('form');
      var prefix = $(form).attr('name');

      var password1Input = $(this).find('input[name="' + prefix + ':new-internal-auth-password1"]');
      var password2Input = $(this).find('input[name="' + prefix + ':new-internal-auth-password2"]');
      var password1EncodedInput = $(this).find('input[name="' + prefix + ':new-internal-auth-password1-encoded"]');
      var password2EncodedInput = $(this).find('input[name="' + prefix + ':new-internal-auth-password2-encoded"]');
      
      password1EncodedInput.val(hex_md5(password1Input.val())); 
      password2EncodedInput.val(hex_md5(password2Input.val())); 
      password1Input.val('');
      password2Input.val('');
    });
    
    $(document).on('click', '.users-editprofile-image-change', function (event) {
      dust.render("users-profile-image-upload", {
        userId: $(this).data('user-id')
      }, function(err, html) {
        if (!err) {
          var dialog = $(html);
          dialog.dialog({
            modal: true,
            width: 600,
            buttons: [{
              'text': dialog.data('upload-button'),
              'click': function(event) { 
                $(this).find('iframe').load($.proxy(function () {
                  window.location.reload();
                }, this));
                
                $(this).find('form').submit();
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
    
    $('.users-editprofile-authentication-source-change-password').click(function(event) {
      $('.users-editprofile-authentication-source-change-password-container').show();
    });
    
    $('.users-editprofile-authentication-source-change-password-container input[type="submit"]').click(function() {
      var form = $(this).closest('form');
      var prefix = $(form).attr('name');

      var password1Input = $(form).find('.users-editprofile-authentication-source-change-password-password1');
      var password2Input = $(form).find('.users-editprofile-authentication-source-change-password-password2');
      var password1EncodedInput = $(form).find('input[name="' + prefix + ':change-password1-encoded"]');
      var password2EncodedInput = $(form).find('input[name="' + prefix + ':change-password2-encoded"]');
      
      password1EncodedInput.val(hex_md5(password1Input.val())); 
      password2EncodedInput.val(hex_md5(password2Input.val())); 
      password1Input.val('');
      password2Input.val('');
    });
    
    $('input[required="required"]').each(function (index, input) {
      if (!$(input).val()) {
        input.setCustomValidity($(input).data('requiredmessage'));
      }
      
      $(input).on('invalid', function (e) {
        this.setCustomValidity($(this).data('requiredmessage'));
      });
      
      $(input).on('input', function (e) {
        this.setCustomValidity('');
      });
    });
  });
  
}).call(this);