SiteMenuBarWidgetFriends = Class.create(SiteMenuBarWidget, {
  initialize: function ($super) {
    $super();
    
    this._suggestFriendsFirstResult = 0;
    
    this._providerInfoClickListener = this._onProviderInfoClick.bindAsEventListener(this);
    this._addAsFriendClickListener = this._onAddAsFriendClick.bindAsEventListener(this);
    this._removeFriendClickListener = this._onRemoveFriendClick.bindAsEventListener(this);
    this._friendsSuggestMoreClickListener = this._onFriendsSuggestMoreClick.bindAsEventListener(this);
    this._addByEmailClickListener = this._onAddByEmailClick.bindAsEventListener(this);
    
    this.addListener("show", this, this._onShow);
  },
  setup: function ($super, menuElement, contentElement) {
    $super(menuElement, contentElement);
    
    if (!isLoggedIn()) {
      this.disableMenu();
    }

    this.getContentElement().select('form').invoke("writeAttribute", 'autocomplete', 'off');

    var tabsContainer = contentElement.down('.siteMenuBarWidgetFriendsTabsContainer');
    
    new S2.UI.Tabs(tabsContainer);
    
    var providerInfos = contentElement.select('.siteMenuBarFriendsWidgetInviteFriendsProviderInfo');
    for (var i = 0, l = providerInfos.length; i < l; i++) {
      Event.observe(providerInfos[i], "click", this._providerInfoClickListener);
    }
    
    Event.observe($('sitemenubarfriendswidget-suggest').down('.sitemenubarfriendswidgetSuggestMore'), "click", this._friendsSuggestMoreClickListener);
    Event.observe($('siteMenuBarFriendsWidgetInviteFriendsProviderEmail').down('[name="addByEmail"]'), "click", this._addByEmailClickListener);
  },
  destroy: function ($super) {
    $super();
    
    var providerInfos = contentElement.select('.siteMenuBarFriendsWidgetInviteFriendsProviderInfo');
    for (var i = 0, l = providerInfos.length; i < l; i++) {
      Event.stopObserving(providerInfos[i], "click", this._providerInfoClickListener);
    }
    
    Event.stopObserving($('sitemenubarfriendswidget-suggest').down('.sitemenubarfriendswidgetSuggestMore'), "click", this._friendsSuggestMoreClickListener);
    Event.stopObserving($('siteMenuBarFriendsWidgetInviteFriendsProviderEmail').down('[name="addByEmail"]'), "click", this._addByEmailClickListener);
  },
  getName: function ($super) {
    return 'friends';
  },
  _doAddFriend: function (container, supportCommonFriendCount, supportAddAsFriend, supportRemove, userId, name, hasProfileImage, commonFriendCount) {
    var friendContainer = new Element("div", {
      className: "siteMenuBarFriendsWidgetFriendContainer"
    });
    
    var friendImageContainer = new Element("div", {
      className: "siteMenuBarFriendsWidgetFriendImageContainer"
    });
    
    var friendImageInnerContainer = new Element("div", {
      className: "siteMenuBarFriendsWidgetFriendImageInnerContainer"
    });
    
    var friendImage = null;
    
    if (hasProfileImage) {
      friendImage = new Element("img", {
        src: CONTEXTPATH + '/v1/users/' + userId + '/profileImage/64x64'
      });
    } else {
      friendImage = new Element("div", {
        className: "siteMenuBarFriendsWidgetFriendImageNone"
      });
    }
    
    friendImageContainer.appendChild(friendImageInnerContainer);
    friendImageInnerContainer.appendChild(friendImage);
    friendContainer.appendChild(friendImageContainer);

    var friendName = new Element("a", {
      className: "siteMenuBarFriendsWidgetFriendName",
      href: CONTEXTPATH + '/users/' + userId
    }).update(name);
    
    friendContainer.appendChild(friendName);

    if (supportCommonFriendCount) {
      var mutualFriends = new Element("div", {
        className: "siteMenuBarFriendsWidgetFriendMutualFriends"
      }).update(commonFriendCount == 1 ? getLocale().getText('common.siteMenuBar.friendsWidget.oneMutualFriend') : getLocale().getText('common.siteMenuBar.friendsWidget.mutualFriends', commonFriendCount));
      friendContainer.appendChild(mutualFriends);
    }
    
    if (supportAddAsFriend) {
      var addFriendLink = new Element("a", {
        className: "siteMenuBarFriendsWidgetFriendAddLink",
        href: "javascript:void(null);"
      }).update(getLocale().getText('common.siteMenuBar.friendsWidget.addAsAFriend'));
      
      addFriendLink.store("friendId", userId);
      
      friendContainer.appendChild(addFriendLink);
      Event.observe(addFriendLink, "click", this._addAsFriendClickListener);
    }
    
    if (supportRemove) {
      var removeFriendLink = new Element("a", {
        className: "siteMenuBarFriendsWidgetRemoveFriendLink",
        href: "javascript:void(null);"
      }).update(getLocale().getText('common.siteMenuBar.friendsWidget.removeFriend'));
      
      removeFriendLink.store("friendId", userId);
      removeFriendLink.store("friendName", name);

      friendContainer.appendChild(removeFriendLink);
      Event.observe(removeFriendLink, "click", this._removeFriendClickListener);
    }
    
    container.appendChild(friendContainer);
  },
  _addFriend: function (userId, name, hasProfileImage) {
    this._doAddFriend($('sitemenubarfriendswidget-list'), false, false, true, userId, name, hasProfileImage, 0);
  },
  _addSuggestedFriend: function (userId, name, hasProfileImage, commonFriendCount) {
    this._doAddFriend($('sitemenubarfriendswidget-suggest').down('.sitemenubarfriendswidgetSuggestContent'), true, true, false, userId, name, hasProfileImage, commonFriendCount);
  },
  _suggestFriends: function () {
    $('sitemenubarfriendswidget-suggest').down('.sitemenubarfriendswidgetSuggestContent').update('');
    var _this = this;
    API.get(CONTEXTPATH + '/v1/friends/SELF/suggestFriends', {
      parameters: {
        firstResult: this._suggestFriendsFirstResult,
        maxResults: 4
      },
      onSuccess: function (jsonResponse) {
        var commonFriends = jsonResponse.response;
        for (var i = 0, l = commonFriends.length; i < l; i++) {
          var commonFriend = commonFriends[i];
          
          _this._addSuggestedFriend(commonFriend.user.id, commonFriend.user.fullName, commonFriend.user.profileImageId != null, commonFriend.commonFriendCount); 
        }
        
        _this._suggestFriendsFirstResult = commonFriends.length == 4 ? _this._suggestFriendsFirstResult + 4 : 0;
      }
    });    
  },
  _onProviderInfoClick: function (event) {
    var providerInfoElement = Event.element(event);
    if (!providerInfoElement.hasClassName('siteMenuBarFriendsWidgetInviteFriendsProviderInfo'))
      providerInfoElement = providerInfoElement.up('.siteMenuBarFriendsWidgetInviteFriendsProviderInfo');
    
    var providerElement = providerInfoElement.up('.siteMenuBarFriendsWidgetInviteFriendsProvider');
    var providerOptions = providerElement.down('.siteMenuBarFriendsWidgetInviteFriendsProviderOptions');
    if (providerOptions.getStyle('display') == 'none') {
      providerOptions.setStyle({
        display: ''
      });
      providerOptions.select('input').invoke("validate");
    } else {
      providerOptions.setStyle({
        display: 'none'
      });
    }
  },
  _onFriendsSuggestMoreClick: function (event) {
    this._suggestFriends();
  },
  _onAddByEmailClick: function (event) {
    var submitElement = Event.element(event);
    var form = submitElement.form;
    
    API.post(CONTEXTPATH + '/v1/friends/SELF/addFriend', {
      parameters: {
        friendEmail: form.emailAddress.value
      },
      onSuccess: function (jsonResponse) {
        getNotificationQueue().addNotification(new NotificationMessage({
          text: getLocale().getText('common.siteMenuBar.friendsWidget.friendRequestSent'),
          className: 'infoMessage',
          duration: 3000
        }));   
      }
    });
  },
  _onAddAsFriendClick: function (event) {
    var addLink = Event.element(event);
    
    var friendId = addLink.retrieve("friendId");
    
    API.post(CONTEXTPATH + '/v1/friends/SELF/addFriend', {
      parameters: {
        friendId: friendId,
      },
      onSuccess: function (jsonResponse) {
        getNotificationQueue().addNotification(new NotificationMessage({
          text: getLocale().getText('common.siteMenuBar.friendsWidget.friendRequestSent'),
          className: 'infoMessage',
          duration: 3000
        }));   
        
        siteMenuBarController.closeMenuWidget();
      }
    });
  },
  _onRemoveFriendClick: function (event) {
    var link = Event.element(event);
    
    var friendId = link.retrieve("friendId");
    var friendName = link.retrieve("friendName");
      
    var dialog = new ConfirmDialogController({
      title: getLocale().getText('common.siteMenuBar.friendsWidget.removeDialogTitle'),
      text: getLocale().getText('common.siteMenuBar.friendsWidget.removeDialogContent', friendName),
      buttons: [{
        label: getLocale().getText('common.siteMenuBar.friendsWidget.removeDialogCancelButton'),
        type: 'cancel'
      }, {
        label: getLocale().getText('common.siteMenuBar.friendsWidget.removeDialogRemoveButton'),
        type: 'delete',
        formValid: true,
        onClick: function (event) {
          event.dialog.close();
          
          API.doDelete(CONTEXTPATH + '/v1/friends/SELF/removeFriend/' + friendId, {
            onSuccess: function (jsonResponse) {
              getNotificationQueue().addNotification(new NotificationMessage({
                text: getLocale().getText('common.siteMenuBar.friendsWidget.friendRemovedSuccessfully'),
                className: 'infoMessage',
                duration: 3000
              }));              
            }
          });
        }
      }]
    });
    
    dialog.open();
  },
  _onShow: function (event) {
    this._suggestFriends();
    
    $('sitemenubarfriendswidget-list').update('');
    var _this = this;
    API.get(CONTEXTPATH + '/v1/friends/SELF/listFriends', {
      parameters: {
        firstResult: 0,
        maxResults: 4
      },
      onSuccess: function (jsonResponse) {
        var friends = jsonResponse.response.friends;
        for (var i = 0, l = friends.length; i < l; i++) {
          var friend = friends[i];
          
          _this._addFriend(friend.id, friend.fullName, friend.profileImageId != null); 
        }
      }
    });
  }
});