EditProfileViewController = Class.create({
  initialize : function() {
    this._changeProfileImageListener = this._onChangeProfileImage.bindAsEventListener(this);
    this._saveBasicButtonClickListener = this._onSaveBasicButtonClick.bindAsEventListener(this);
    this._createNewInternalAuthButtonClickListener = this._onCreateNewInternalAuthButtonClick.bindAsEventListener(this);
    this._addInternalAuthSourceLinkClickListener = this._onAddInternalAuthSourceLinkClick.bindAsEventListener(this);
    this._authSourceChangePasswordLinkClickListener = this._onAuthSourceChangePasswordLinkClick.bindAsEventListener(this);
    this._authSourceChangePasswordButtonClickListener = this._onAuthSourceChangePasswordButtonClick.bindAsEventListener(this);
    this._saveNotificationsButtonClickListener = this._onSaveNotificationsButtonClick.bindAsEventListener(this);
    this._newProfileImageId = null;
  },
  destroy: function () {
    Event.stopObserving($('editProfileImageChangeLink'), "click", this._changeProfileImageListener);
	Event.stopObserving($('basic-edit-profile-save'), "click", this._saveBasicButtonClickListener);
	Event.stopObserving($('notification-settings-save'), "click", this._saveNotificationsButtonClickListener);

    $$('a.addAuthSourceLink_INTERNAL').invoke("stopObserving", "click", this._addInternalAuthSourceLinkClickListener);
  	$$('#auth-sources-new-internal-auth-save').invoke("stopObserving", "click", this._createNewInternalAuthButtonClickListener);
  	$$('a.enabledAuthSourceChangePasswordLink').invoke("stopObserving", "click", this._authSourceChangePasswordLinkClickListener);
  	$$('input[name="auth-sources-change-password-save"]').invoke("stopObserving", "click", this._authSourceChangePasswordButtonClickListener);
  },
  setup: function () {
    Event.observe($('editProfileImageChangeLink'), "click", this._changeProfileImageListener);
    Event.observe($('basic-edit-profile-save'), "click", this._saveBasicButtonClickListener);
  	Event.observe($('notification-settings-save'), "click", this._saveNotificationsButtonClickListener);

    $$('a.addAuthSourceLink_INTERNAL').invoke("observe", "click", this._addInternalAuthSourceLinkClickListener);
  	$$('#auth-sources-new-internal-auth-save').invoke("observe", "click", this._createNewInternalAuthButtonClickListener);
    $$('a.enabledAuthSourceChangePasswordLink').invoke("observe", "click", this._authSourceChangePasswordLinkClickListener);
    $$('input[name="auth-sources-change-password-save"]').invoke("observe", "click", this._authSourceChangePasswordButtonClickListener);
    
  	this._tabControl = new S2.UI.Tabs($('editProfileTabsContainer'));
  },
  _getNotificationSetting: function (id) {
    return $(id).checked ? '1' : '0';
  },
  _onSaveBasicButtonClick: function (event) {
    Event.stop(event);
    var button = Event.element(event);
    var form = button.form;
    var _this = this;
    var notifySuccess = function () {
      getNotificationQueue().addNotification(new NotificationMessage({
        text: getLocale().getText('editProfile.basicInfoSavedMessage'),
        className: 'infoMessage',
        duration: 3000
      }));
    };
    
    API.post(CONTEXTPATH + '/v1/users/SELF/updateBasicInfo', {
      parameters: {
        firstName: form.firstName.value,
        lastName: form.lastName.value,
        nickname: form.nickname.value
      },
      onSuccess: function (jsonResponse) {
        if (_this._newProfileImageId) {
          API.post(CONTEXTPATH + '/v1/users/SELF/updateProfileImage', {
            parameters: {
              imageId: _this._newProfileImageId
            },      
            onSuccess: function (jsonResponse) {
              _this._newProfileImageId = null;
              $('editProfileBasic').down('.editProfileImage').src = CONTEXTPATH + '/v1/users/SELF/profileImage/128x128?z=' + new Date().getTime();
              notifySuccess();
            }
          });
        } else {
          notifySuccess();
        }
      }
    });
  },
  _onChangeProfileImage: function (event) {
    var dialog = new ImageSelectDialogController();
    dialog.open();
    dialog.addListener("select", this, this._onImageSelectDialogSelect);
  },
  _onImageSelectDialogSelect: function (event) {
    this._newProfileImageId = event.imageId; 
    $('editProfileBasic').down('.editProfileImage').src = CONTEXTPATH + '/v1/materials/images/' +this._newProfileImageId + '/128x128';
  },
  _onAddInternalAuthSourceLinkClick: function (event) {
    var newAuthSourceInternal = $(document.body).down('.newAuthSourceInternal');
    if (newAuthSourceInternal) {
      newAuthSourceInternal.show();
      var form = newAuthSourceInternal.down('form');
      form.password1.validate(true);
      form.password2.validate(true);
      
      newAuthSourceInternal.scrollTo();
      var password1 = newAuthSourceInternal.down('input[name="password1"]');
      
      if ((typeof password1.focus) == 'function')
        password1.focus();
    }
  },
  _onCreateNewInternalAuthButtonClick: function (event) {
    Event.stop(event);
    
    var overlay = new S2.UI.Overlay();
    overlay.element.addClassName('loadingPane');
    $(document.body).insert(overlay);
    
    var button = Event.element(event);
    var form = button.form;
    
    var password = hex_md5(form.password1.value);
    var baseUrl = location.protocol + '//' + location.hostname + ((location.port && location.port != 80) ? ':' + location.port : '') + CONTEXTPATH;
    var redirectUrl = baseUrl + '/editprofile.jsf';
    
    API.put(CONTEXTPATH + '/v1/users/SELF/createInternalAuth', {
      parameters: {
        password: password,
        redirectUrl: redirectUrl 
      },
      onComplete: function (transport) {
        overlay.destroy();
      },
      onSuccess: function (jsonResponse) {
        $$('.newAuthSourceInternal').invoke('hide');
        $$('.addAuthSourceLink_INTERNAL').invoke('hide');
        
        var message = getLocale().getText('editProfile.authSourceAdded', jsonResponse.response.confirmEmail);

        getNotificationQueue().addNotification(new NotificationMessage({
          text: message,
          className: 'infoMessage' 
        }));
      }
    });
  },
  _onAuthSourceChangePasswordLinkClick: function (event) {
    Event.stop(event);
    var link = Event.element(event);
    var authSource = link.up('.enabledAuthSource');
    var form = authSource.down('form');

    authSource.down('.enabledAuthSourceChangePasswordFormContainer').show();

    form.oldPassword.validate(true);
    form.password1.validate(true);
    form.password2.validate(true);
  },
  _onAuthSourceChangePasswordButtonClick: function (event) {
    Event.stop(event);
    var button = Event.element(event);
    var form = button.form;
    
    API.put(CONTEXTPATH + '/v1/users/SELF/updateInternalAuth', {
      parameters: {
        oldPassword: hex_md5(form.oldPassword.value),
        newPassword: hex_md5(form.password1.value)
      },
      onSuccess: function (jsonResponse) {
        getNotificationQueue().addNotification(new NotificationMessage({
          text: getLocale().getText('editProfile.authSourcePasswordChanged'),
          className: 'infoMessage',
          duration: 3000
        }));

        form.oldPassword.value = '';
        form.password1.value = '';
        form.password2.value = '';

        button.up('.enabledAuthSource').down('.enabledAuthSourceChangePasswordFormContainer').hide();
      },
      onFailure: function(message, code, httpError, defaultHandler) {
        if (code == 403) {
          getNotificationQueue().addNotification(new NotificationMessage({
            text: getLocale().getText('editProfile.authSourceIncorrectOldPassword'),
            className: 'warningMessage',
            duration: 3000
          }));
        } else {
          defaultHandler(message, code, httpError);
        }
      }
    });
  },
  _onSaveNotificationsButtonClick: function (event) {
    Event.stop(event);
    
    var keys = new Array();
    keys.push('notifications.friendrequest.mail','notifications.friendrequestaccepted.mail','notifications.removedfromfriends.mail','notifications.materialshared.mail','notifications.privatemessage.mail');
    
    var values = new Array();
    values.push(this._getNotificationSetting('notificationSettingFriendRequest'));
    values.push(this._getNotificationSetting('notificationSettingFriendRequestAccepted'));
    values.push(this._getNotificationSetting('notificationSettingRemovedFromFriends'));
    values.push(this._getNotificationSetting('notificationSettingMaterialShared'));
    values.push(this._getNotificationSetting('notificationSettingPrivateMessageReceived'));

    API.post(CONTEXTPATH + '/v1/users/SELF/updateUserSettings', {
      parameters: {
        keys: keys.toString(),
        values: values.toString()
      },
      onSuccess: function (jsonResponse) {
        getNotificationQueue().addNotification(new NotificationMessage({
          text: getLocale().getText('editProfile.notificationsSavedMessage'),
          className: 'infoMessage',
          duration: 3000
        }));
      }
    });
  }
});
