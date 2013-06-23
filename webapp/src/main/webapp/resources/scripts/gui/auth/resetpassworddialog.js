ResetPasswordDialogController = Class.create(ModalDialogController, {
  initialize: function ($super, options) {
    $super(Object.extend({
      title: getLocale().getText('auth.resetPasswordDialog.dialogTitle'),
      contentUrl: CONTEXTPATH + '/auth/resetpassworddialog.jsf',
      width: 600,
      height: 246,
      position: 'fixed'
    }, options||{}));

    this._resetButtonClickListener = this._onResetButtonClick.bindAsEventListener(this);
  },
  destroy: function ($super) {
    Event.stopObserving(this._resetButton, "click", this._resetButtonClickListener);
    $super();
  },
  
  setup: function ($super) {
    $super(function () {
      var dialog = this.getDialog();

      this._passwordInput = dialog.content.down('input[name="password1"]');
      this._resetButton = dialog.content.down('input[name="resetButton"]');

      Event.observe(this._resetButton, "click", this._resetButtonClickListener);
    }); 
  },
  _onResetButtonClick: function (event) {
    var _this = this;
	API.get(CONTEXTPATH + '/v1/users/resetPassword/' + this.getOptions().key, {
      parameters: {
    	password: hex_md5(this._passwordInput.value)
      },
	  onSuccess: function (jsonResponse) {
	    getNotificationQueue().addNotification(new NotificationMessage({
	      text: getLocale().getText('auth.resetPasswordDialog.passwordChangedMessage'),
	      className: 'infoMessage',
          duration: 3000
	    }));  

        _this.close();
	  }
    });
  }
});