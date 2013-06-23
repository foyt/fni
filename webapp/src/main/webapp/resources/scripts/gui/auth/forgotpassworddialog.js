ForgotPasswordDialogController = Class.create(ModalDialogController, {
  initialize: function ($super, options) {
    $super({
      title: getLocale().getText('auth.forgotPasswordDialog.dialogTitle'),
      contentUrl: CONTEXTPATH + '/auth/forgotpassworddialog.jsf',
      width: 600,
      height: 194,
      position: 'fixed'
    });
    
    this._sendButtonClickListener = this._onSendButtonClick.bindAsEventListener(this);
  },
  destroy: function ($super) {
    Event.stopObserving(this._sendButton, "click", this._sendButtonClickListener);
    $super();
  },
  
  setup: function ($super) {
    $super(function () {
      var dialog = this.getDialog();

      this._emailInput = dialog.content.down('input[name="email"]');
      this._sendButton = dialog.content.down('input[name="sendButton"]');
      
      Event.observe(this._sendButton, "click", this._sendButtonClickListener);
    }); 
  },
  _onSendButtonClick: function (event) {
    var redirectUrl = location.protocol + '//' + location.hostname + ((location.port && location.port != 80) ? ':' + location.port : '') + CONTEXTPATH + '/login';
	  
    this.close();
    var overlay = new S2.UI.Overlay();
    overlay.element.addClassName('loadingPane');
    $(document.body).insert(overlay);
    
    var email = this._emailInput.value;
    
    API.post(CONTEXTPATH + '/v1/users/resetPassword', {
      parameters: {
        email: email,
        redirectUrl: redirectUrl
      },
      onComplete: function (transport) {
        overlay.destroy();
      },
      onSuccess: function (jsonResponse) {
  	    getNotificationQueue().addNotification(new NotificationMessage({
  	      text: getLocale().getText('auth.forgotPasswordDialog.passwordResetMailSent', email),
  	      className: 'infoMessage'
  	    }));  
      }
    });
  }
});