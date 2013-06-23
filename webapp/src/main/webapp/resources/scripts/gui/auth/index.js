IndexViewController = Class.create({
  initialize : function() {
    this._registerButtonClickListener = this._onRegisterButtonClick.bindAsEventListener(this);
    this._forgotPasswordLinkClickListener = this._onForgotPasswordLinkCLick.bindAsEventListener(this);
    this._documentActionListener = this._onDocumentAction.bindAsEventListener(this);
    
    document.observe("fni:action", this._documentActionListener);
  },
  destroy : function() {
    Event.stopObserving(this._registerButton, "click", this._registerButtonClickListener);
    Event.stopObserving($('forgotPasswordLink'), "click", this._forgotPasswordLinkClickListener);
  },
  setup : function() {
    this._registerButton = $('register-registerButton');
    Event.observe(this._registerButton, "click", this._registerButtonClickListener);
    this._resetRegisterForm();
    
    this._forgotPasswordLink = $('forgotPasswordLink');
    
    Event.observe(this._forgotPasswordLink, "click", this._forgotPasswordLinkClickListener);
  },
  _resetRegisterForm: function () {
	$('register').select('input').each(function (e) {
	  if (e.type.toLowerCase() != 'submit') {
	    e.value = '';
	  }
	});  
  },
  
  _onDocumentAction: function(event) {
	switch (event.memo.action) {
	  case 'RESET_PASSWORD':
		var dialog = new ResetPasswordDialogController({
		  key: event.memo.parameters.get('key')
		});
		dialog.open();
	  break;
	};  
  },

  _onRegisterButtonClick: function (event) {
    Event.stop(event);
  
    var firstName = $('register-firstName').value;
    var lastName = $('register-lastName').value;
    var email = $('register-email').value;
    var password = hex_md5($('register-password1').value);
    
    var overlay = new S2.UI.Overlay();
    overlay.element.addClassName('loadingPane');
    $(document.body).insert(overlay);

    var _this = this;
    API.post(CONTEXTPATH + '/v1/users/createUser', {
      parameters: {
        firstName: firstName,
        lastName: lastName,
        email: email,
        password: password
      },
      onComplete: function (transport) {
        overlay.destroy();
      },
      onSuccess: function (jsonResponse) {
        _this._resetRegisterForm();
        
        getNotificationQueue().addNotification(new NotificationMessage({
          text: getLocale().getText('auth.login.registerVerificationMailSent', email),
          className: 'infoMessage'
        }));
      }
    });
  },
  _onForgotPasswordLinkCLick: function (event) {
    var dialog = new ForgotPasswordDialogController();
    dialog.open();
  }
});
