ChatMenuBarWidget = Class.create(SiteMenuBarWidget, {
  initialize: function ($super) {
    $super();
    
    this._pingInterval = 3000;
    this._checkInternal = 1000;
    this._loginInternal = 60000;
    this._loginTries = 5;
    this._loggedIn = false;
    this._activeUserJid = null;
    this._checkLoopRunning = false;
    this._chatsWithNewMessages = new Array();
    
    this._friendContainerClickListener = this._onFriendContainerClick.bindAsEventListener(this);
    this._sendButtonClickListener = this._onSendButtonClick.bindAsEventListener(this);
    this._tabControlTabsChange = this._onTabControlTabsChange.bindAsEventListener(this);
    this._tabStatusClickListener = this._onTabStatusClick.bindAsEventListener(this);
    this._editorKeyDownListener = this._onEditorKeyDown.bindAsEventListener(this);

    // TODO: character per name = 30 / tab count
  },
  setup: function ($super, menuElement, contentElement) {
    $super(menuElement, contentElement);

//    if (isLoggedIn()) {
//      this._login();
//    } else {
//      this.disableMenu();
//    }
    this.disableMenu();
  },
  destroy: function ($super) {
    // TODO: Release: this._friendContainerClickListener
    // TODO: Release: this._tabStatusClickListener
    
    
    $super();
  },
  show: function ($super) {
    $super();
    
    this.hideNotification();
    
    while (this._chatsWithNewMessages.length > 0) {
      var user = this._chatsWithNewMessages.pop();
      this._setNewMessageCount(user.userJid, user.userName, user.presenceMode, user.count);
    }
  },
  getName: function ($super) {
    return 'chat';
  },
  _getActiveUserJid: function () {
    return this._activeUserJid;
  },
  _setActiveUserJid: function (userJid) {
    this._activeUserJid = userJid;

    if (this._checkLoopRunning == false) {
      this._checkLoopRunning = true;
      this._checkMessages(userJid);
    }
  },
  _setNewMessageCount: function (userJid, userName, userPresenceMode, count) {
    this._openChat(userJid, userName, userPresenceMode, false);
    
    if (this._tabControl.list.select('li').length == 1)
      this._setActiveUserJid(userJid);
    
    var tabAnchor = this._tabControl.list.down('a[href="#chat-tab-' + userJid + '"]');
    var tab = tabAnchor.parentNode;
    var panel = tab.retrieve('ui.panel');
    var statusElement = tab.down('.siteMenuBarWidgetChatTabStatus');
    var countElement = statusElement.down('.siteMenuBarWidgetChatTabStatusNewCount');
    if (count > 0) {
      if (!countElement) { 
        countElement = new Element("span", {
          className: "siteMenuBarWidgetChatTabStatusNewCount"
        });
        statusElement.appendChild(countElement);
      }
      
      countElement.update(count);
      statusElement.addClassName("siteMenuBarWidgetChatTabStatusUnreadMessages");
    } else {
      var presenceMode = panel.down('input[name="presenceMode"]').value;
      
      if (countElement)
        countElement.remove();
      statusElement.removeClassName("siteMenuBarWidgetChatTabStatusUnreadMessages");
      
      this._setTabPresenceMode(userJid, presenceMode);
    }
  },
  _setUserPresence: function (userJid, presenceType, presenceMode) {
    this._setListPresence(userJid, presenceType, presenceMode);
  },
  _setListPresence: function (userJid, presenceType, presenceMode) {
    var friendsContainer = $('siteMenuBarWidgetChatOnlineFriendsContainer');
    var userJidInputs = friendsContainer.select('input[name="userJid"]');
    var userJidInput = null;
    for (var i = 0, l = userJidInputs.length; i < l; i++) {
      if (userJidInputs[i].value == userJid) {
        userJidInput = userJidInputs[i];
        break;
      }
    }
    
    if (userJidInput) {
      var friendContainer = userJidInput.parentNode;
      var statusElement = friendContainer.down('.siteMenuBarWidgetChatOnlineFriendStatus');
      
      if (presenceType == 'unavailable') {
        friendContainer.remove();
      } else {
        statusElement.removeClassName("siteMenuBarWidgetChatOnlineFriendStatusOnline");
        statusElement.removeClassName("siteMenuBarWidgetChatOnlineFriendStatusAway");
        statusElement.removeClassName("siteMenuBarWidgetChatOnlineFriendStatusBusy");
        
        switch (presenceMode) {
          case 'chat':
          case 'available':
            statusElement.addClassName("siteMenuBarWidgetChatOnlineFriendStatusOnline");
          break;
          case 'away':
          case 'xa':
            statusElement.addClassName("siteMenuBarWidgetChatOnlineFriendStatusAway");
          break;
          case 'dnd':
            statusElement.addClassName("siteMenuBarWidgetChatOnlineFriendStatusBusy");
          break;
        }
      }
    } else {
      if (presenceType == 'available') {
        // TODO: Name...
        this._addBuddy(userJid, userJid, presenceMode);
      }
    }
    
    
    if (friendsContainer.select('.siteMenuBarWidgetChatOnlineFriendContainer').length > 0) {
      friendsContainer.down('.siteMenuBarWidgetChatNoFriendsOnline').hide();
    } else {
      friendsContainer.down('.siteMenuBarWidgetChatNoFriendsOnline').show();
    }
  },
  _updateCredentials: function (userJid, password) {
    API.post(CONTEXTPATH + '/v1/chat/updateCredentials', {
      parameters: {
        userJid: userJid,
        password: password
      }
    });
  },
  _pingChanges: function () {
    var _this = this;
    API.get(CONTEXTPATH + '/v1/chat/pingChanges', {
      onSuccess: function (jsonResponse) {
        try {
          if (_this.isVisible()) {
            var changes = jsonResponse.response.changes;
            if (changes) {
              for (var i = 0, l = changes.length; i < l; i++) {
                switch (changes[i].type) {
                  case 'message':
                    _this._setNewMessageCount(changes[i].fromJid, changes[i].fromName, changes[i].fromPresenceMode, changes[i].count);
                  break;
                  case 'presence':
                    _this._setUserPresence(changes[i].fromJid, changes[i].presenceType, changes[i].presenceMode);
                  break;
                }
              }
            }
          } else {
            var messageCount = jsonResponse.response.messageCount;
            if (messageCount > 0) {
              _this.showNotification(messageCount);
              
              var changes = jsonResponse.response.changes;
              if (changes) { 
                for (var i = 0, l = changes.length; i < l; i++) {
                  var change = changes[i];
                  if (change.type == 'message') {
                    for (var j = 0, l2 = _this._chatsWithNewMessages.length; j < l2; j++) {
                      if (_this._chatsWithNewMessages[j].userJid == change.fromJid) {
                        _this._chatsWithNewMessages[j].count = change.count;
                        _this._chatsWithNewMessages[j].presenceMode = change.fromPresenceMode;
                        break;
                      };
                    }
                    
                    _this._chatsWithNewMessages.push({
                      userJid: change.fromJid,
                      userName: change.fromName,
                      presenceMode: change.fromPresenceMode,
                      count: change.count
                    });
                  };
                }
              }
            } else {
              _this.hideNotification();
            }
          }
        } finally {
          setTimeout(function () {
            _this._pingChanges();
          }, _this._pingInterval);
        }
      }
    });
  },
  _checkMessages: function (fromJid) {
    var _this = this;
    if (this.isVisible() && fromJid) {
      API.get(CONTEXTPATH + '/v1/chat/' + fromJid + '/listMessages', {
        onSuccess: function (jsonResponse) {
          var postponedCheck = false;
          try {
            var activeUserJid = _this._getActiveUserJid();
            var notForActiveUser = false;
            var messages = jsonResponse.response.messages;
            
            if (_this.isVisible() && activeUserJid && messages && (messages.length > 0)) {  
              var messageIds = new Array();
              for (var i = 0, l = messages.length; i < l; i++) {
                if (messages[i].fromJid == activeUserJid) {
                  _this._addMessage(messages[i].fromName, messages[i].body, messages[i].sent, false);
                  messageIds.push(messages[i].id);
                } else {
                  notForActiveUser = true;
                }
              }
              
              if (notForActiveUser == false) {
                if (messageIds.length > 0) {
                  postponedCheck = true;
                  _this._markMessagesReceived(messageIds, function () {
                    setTimeout(function () {
                      _this._checkMessages(_this._getActiveUserJid());
                    }, _this._checkInternal);
                  });
                }
  
                _this._setNewMessageCount(activeUserJid, null, null, 0);
              }
            } 
          } finally {
            if (postponedCheck == false) {
              setTimeout(function () {
                _this._checkMessages(_this._getActiveUserJid());
              }, _this._checkInternal);
            }
          }
        }
      });
    } else {
      setTimeout(function () {
        _this._checkMessages();
      }, this._checkInternal);
    }
  },
  _markMessagesReceived: function (messageIds, callback) {
    API.post(CONTEXTPATH + '/v1/chat/markMessagesReceived', {
      parameters: {
        messageIds: messageIds
      },
      onComplete: function () {
        if (Object.isFunction(callback))
          callback();
      }
    });
  },
  _login: function () {
    this._loginTries--;
    this.startLoadingAnimation();
    var _this = this;
    API.post(CONTEXTPATH + '/v1/chat/login', {
      onComplete: function () {
        _this.stopLoadingAnimation();
      },
      onSuccess: function (jsonResponse) {
        _this.enableMenu();
        _this._loggedIn = true;
        _this._listOnlineBuddies();
        _this._pingChanges();
      },
      onFailure: function (responseText, status, isHttpError, defaultHandler) {
        if (_this._loginTries > 0) {
          setTimeout(function () {
            _this.disableMenu();
            _this._login();
          }, _this._loginInternal);
        }
      }
    });
  },
  _sendMessage: function (recipientJid, message) {
    var sent = new Date();
    
    var _this = this;
    API.put(CONTEXTPATH + '/v1/chat/' + recipientJid + '/sendMessage', {
      parameters: {
        message: message
      },
      onSuccess: function (jsonResponse) {
        _this._addMessage(getLocale().getText("common.siteMenuBar.chatWidget.me"), message, sent, true);
      }
    });
  },
  _listOnlineBuddies: function () {
    this.startLoadingAnimation();
    
    var _this = this;
    API.get(CONTEXTPATH + '/v1/chat/listOnlineBuddies', {
      onComplete: function () {
        _this.stopLoadingAnimation();
      },
      onSuccess: function (jsonResponse) {
        var buddies = jsonResponse.response.buddies;
        if (buddies) {
          for (var i = 0, l = buddies.length; i < l; i++) {
             _this._addBuddy(buddies[i].userJid, buddies[i].name, buddies[i].presenceMode);
          }
        }
      }
    });
  },
  _addBuddy: function (userJid, userName, presenceMode) {
    var container = new Element("div", {
      className: "siteMenuBarWidgetChatOnlineFriendContainer"
    });
    
    var statusContainer = new Element("div", {
      className: "siteMenuBarWidgetChatOnlineFriendStatus"
    });
      
    var friendName = new Element("div", {
      className: "siteMenuBarWidgetChatOnlineFriendName"
    }).update(userName||userJid);
    
    container.appendChild(statusContainer);
    container.appendChild(friendName);
    container.appendChild(new Element("input", {type: 'hidden', name: "userJid", value: userJid }));
    container.appendChild(new Element("input", {type: 'hidden', name: "presenceMode", value: presenceMode }));
    
    $('siteMenuBarWidgetChatOnlineFriendsContainer').appendChild(container);
    
    this._setListPresence(userJid, 'available', presenceMode);
    
    Event.observe(container, "click", this._friendContainerClickListener);
  },
  _initializeTabs: function () {
    if (this._tabControl) {
      this._tabControl.destroy();
    }
    
    var tabsContainer = $('siteMenuBarWidgetChatTabsContainer');
    if (tabsContainer.select('ul').length > 0) {
      this._tabControl = new S2.UI.Tabs(tabsContainer);
      Event.observe(this._tabControl.element, "ui:tabs:change", this._tabControlTabsChange);
      tabsContainer.select('.siteMenuBarWidgetChatTabStatus').invoke("observe",  "click", this._tabStatusClickListener);
      this._tabControl.setSelectedPanel(this._tabControl.tabs.first().retrieve('ui.panel'));
    }
  },
  _openChat: function (userJid, name, presenceMode, select) {
    var tabsContainer = $('siteMenuBarWidgetChatTabsContainer');
    
    var tabId = 'chat-tab-' + userJid;
    var panel = $(tabId);
    
    if (!panel) {
      panel = new Element("div", {
        id: tabId
      });
      
      var tab = new Element("li");
      
      var tabLink = new Element("a", {
        href: '#chat-tab-' + userJid
      }).update(name);
      
      var status = new Element("span", {
        className: "siteMenuBarWidgetChatTabStatus"
      });
      
      Event.observe(status, "click", this._tabStatusClickListener);
       
      tab.appendChild(tabLink);
      tab.appendChild(status);
      
      tabsContainer.down('ul').insert({
        bottom:tab
      });
      
      tabsContainer.insert({
        top:panel
      });
      
      var messagesContainer = new Element("div", {
        className: "siteMenuBarWidgetChatMessagesContainer"
      });
      var editorContainer = new Element("div", {
        className: "siteMenuBarWidgetChatEditorContainer"
      }); 
      var editor = new Element("textarea", {
        className: "siteMenuBarWidgetChatEditor"
      }); 
      var sendButton = new Element("button", {
        className: "siteMenuBarWidgetChatSendButton"
      }).update(getLocale().getText("common.siteMenuBar.chatWidget.sendButtonLabel"));
      
      editorContainer.appendChild(editor);
      editorContainer.appendChild(sendButton);
      
      Event.observe(sendButton, "click", this._sendButtonClickListener);
      Event.observe(editor, "keydown", this._editorKeyDownListener);
      
      panel.appendChild(messagesContainer);
      panel.appendChild(editorContainer);
      panel.appendChild(new Element("input", { type: "hidden", value: userJid, name: "userJid" }));
      panel.appendChild(new Element("input", { type: "hidden", value: presenceMode, name: "presenceMode" }));
      
      this.getContentElement().down('.siteMenuBarWidgetChatInstructions').hide();
      
      this._initializeTabs();
    } 

    if (select) {
      this._tabControl.setSelectedPanel(panel);
    }
    
    this._setTabPresenceMode(userJid, presenceMode);
  },
  _setTabPresenceMode: function (userJid, presenceMode) {
    var tabLink = this._tabControl.list.down('a[href="#chat-tab-' + userJid + '"]');
    var statusElement = tabLink.parentNode.down('.siteMenuBarWidgetChatTabStatus');
    
    statusElement.removeClassName("siteMenuBarWidgetChatTabStatusOffline");
    statusElement.removeClassName("siteMenuBarWidgetChatTabStatusOnline");
    statusElement.removeClassName("siteMenuBarWidgetChatTabStatusBusy");
    statusElement.removeClassName("siteMenuBarWidgetChatTabStatusAway");
    
    switch (presenceMode) {
      case 'chat':
      case 'available':
        statusElement.addClassName("siteMenuBarWidgetChatTabStatusOnline");
      break;
      case 'away':
      case 'xa':
        statusElement.addClassName("siteMenuBarWidgetChatTabStatusAway");
      break;
      case 'dnd':
        statusElement.addClassName("siteMenuBarWidgetChatTabStatusBusy");
      break;
      case 'offline':
        statusElement.addClassName("siteMenuBarWidgetChatTabStatusOffline");
      break;
    }
  },
  _addStatusMessage: function (name, status, dateTime) {
    var className = "";
    var text = "";
    
    switch (status) {
      case 'online':
        className = 'siteMenuBarWidgetChatStatusOnline';
        text = getLocale().getText("common.siteMenuBar.chatWidget.statusOnline", name);
      break;
      case 'away':
        className = 'siteMenuBarWidgetChatStatusAway';
        text = getLocale().getText("common.siteMenuBar.chatWidget.statusAway", name);
      break;
      case 'offline':
        className = 'siteMenuBarWidgetChatStatusOffline';
        text = getLocale().getText("common.siteMenuBar.chatWidget.statusOffline", name);
      break;
    }
    
    var container = new Element("div", {
      className: "siteMenuBarWidgetChatStatus " + className
    });
    
    container.appendChild(new Element("div", {
      className: "siteMenuBarWidgetChatStatusDate"
    }).update(getLocale().getDate(dateTime)));

    container.appendChild(new Element("div", {
      className: "siteMenuBarWidgetChatStatusTime"
    }).update(getLocale().getTime(dateTime)));
    
    container.appendChild(new Element("div", {
      className: "siteMenuBarWidgetChatStatusText"
    }).update(text));
    
    var selectedPanel = this._tabControl.list.down('.ui-tabs-selected').retrieve('ui.panel');
    selectedPanel.down('.siteMenuBarWidgetChatMessagesContainer').appendChild(container);
  },
  _addMessage: function (name, message, dateTime, sentMessage) {
    var container = new Element("div", {
      className: "siteMenuBarWidgetChatMessage"
    });
    
    if (sentMessage) {
      container.addClassName("siteMenuBarWidgetChatSentMessage");
    }
    
    var image = new Element("img", {
      src: CONTEXTPATH + "/themes/default_dev/gfx/icons/32x32/actions/profile-anonymous.png"
    });
    
    var imageContainer = new Element("div", {
      className: "siteMenuBarWidgetChatMessageSenderImage"
    });
    imageContainer.appendChild(image);
    
    container.appendChild(imageContainer);
    
    container.appendChild(new Element("div", {
      className: "siteMenuBarWidgetChatMessageDate"
    }).update(getLocale().getDate(dateTime)));

    container.appendChild(new Element("div", {
      className: "siteMenuBarWidgetChatMessageTime"
    }).update(getLocale().getTime(dateTime)));

    container.appendChild(new Element("div", {
      className: "siteMenuBarWidgetChatMessageSenderName"
    }).update(name.escapeHTML()));
    
    container.appendChild(new Element("div", {
      className: "siteMenuBarWidgetChatMessageContent"
    }).update(message ? (message.escapeHTML().replace(/\n/g, '<br/>')) : ''));
    
    var selectedPanel = this._tabControl.list.down('.ui-tabs-selected').retrieve('ui.panel');
    var messagesContainer = selectedPanel.down('.siteMenuBarWidgetChatMessagesContainer');
    messagesContainer.appendChild(container);
    
    new S2.FX.Scroll(messagesContainer, {
      to: container.getLayout().get("top")
    }).play().start();
  },
  _onFriendContainerClick: function (event) {
    var element = Event.element(event);
    if (!element.hasClassName("siteMenuBarWidgetChatOnlineFriendContainer"))
      element = element.up('.siteMenuBarWidgetChatOnlineFriendContainer');
    
    var userJid = element.down('input[name="userJid"]').value;
    var presenceMode = element.down('input[name="presenceMode"]').value;
    var name = element.down('.siteMenuBarWidgetChatOnlineFriendName').innerHTML;
    
    this._openChat(userJid, name, presenceMode, true);
  },
  _onSendButtonClick: function (event) {
    var sendButton = Event.element(event);
    var editorContainer = sendButton.up('.siteMenuBarWidgetChatEditorContainer');
    var editor = editorContainer.down('.siteMenuBarWidgetChatEditor');
    var tabPanel = editorContainer.up('.ui-tabs-panel');
    
    var userJid = tabPanel.down('input[name="userJid"]').value;
    var message = editor.value;
    
    this._sendMessage(userJid, message);
    
    editor.value = '';
  },
  _onEditorKeyDown: function (event) {
    if (event.keyCode == 13) {
      Event.stop(event);

      var editor = Event.element(event);
      if (!event.ctrlKey) {
        var tabPanel = editor.up('.ui-tabs-panel');
        var userJid = tabPanel.down('input[name="userJid"]').value;
        var message = editor.value;
        this._sendMessage(userJid, message);
        editor.value = '';
      } else {
        editor.value = editor.value + '\n';
      }
    }
  },
  _onTabControlTabsChange: function (event) {
    var userJid = event.memo.to.panel.down('input[name="userJid"]').value;
    this._setActiveUserJid(userJid);
  },
  _onTabStatusClick: function (event) {
    this._activeUserJid = null;
    this._checkLoopRunning = false;
    
    var statusElement = Event.element(event);
    var tab = statusElement.up('li');
    var tabLink = tab.down('a');
    
    var panel = $(tabLink.getAttribute("href").substring(1));
    panel.childElements().invoke("purge");
    tab.remove();
    panel.remove();
    
    this._initializeTabs();
  }
});