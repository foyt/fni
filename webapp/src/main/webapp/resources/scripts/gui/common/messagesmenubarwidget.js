MessagesMenuBarWidget = Class.create(SiteMenuBarWidget, {
  initialize: function ($super) {
    $super();
    
    this._folderId = 'INBOX';

    this._composeMessageButtonClickListener = this._onComposeMessageButtonClick.bindAsEventListener(this);
    this._folderClickListener = this._onFolderClick.bindAsEventListener(this);
    this._messageClickListener = this._onMessageClick.bindAsEventListener(this);
    this._messageStarClickListener = this._onMessageStarClick.bindAsEventListener(this);
    this._messageListTrashClickListener = this._onMessageListTrash.bindAsEventListener(this);
    this._messageListReplyClickListener = this._onMessageListReply.bindAsEventListener(this);
    this._messageListReplyAllClickListener = this._onMessageListReplyAll.bindAsEventListener(this);
    this._messageListForwardClickListener = this._onMessageListForward.bindAsEventListener(this);
    
    this.addListener("show", this, this._onShow);
    this.addListener("hide", this, this._onHide);
  },
  setup: function ($super, menuElement, contentElement) {
    $super(menuElement, contentElement);
    
    if (!isLoggedIn()) {
      this.disableMenu();
    } 

    if (this.isEnabled()) {
      this.getContentElement().select('.siteMenuBarWidgetMessagesFolder').invoke("observe", "click", this._folderClickListener);
      Event.observe($('siteMenuBarWidgetMessagesComposeButton'), "click", this._composeMessageButtonClickListener);
 
      var _this = this;
      API.get(CONTEXTPATH + '/v1/messages/listFolders', {
        onSuccess: function (jsonResponse) {
          var folders = jsonResponse.response.folders;
          for (var i = 0, l = folders.length; i < l; i++) {
            _this._addFolder(folders[i].id, folders[i].name);
          }
        }
      });
    }
  },
  destroy: function ($super) {
    this._clearWorkspace();
    
    Event.stopObserving($('siteMenuBarWidgetMessagesInboxFolder'), "click", this._inboxFolderClickListener);
    Event.stopObserving($('siteMenuBarWidgetMessagesStarredFolder'), "click", this._starredFolderClickListener);
    Event.stopObserving($('siteMenuBarWidgetMessagesOutboxFolder'), "click", this._outboxFolderClickListener);
    Event.stopObserving($('siteMenuBarWidgetMessagesTrashFolder'), "click", this._trashFolderClickListener);
    $('siteMenuBarWidgetMessagesCustomFoldersContainer').select('.siteMenuBarWidgetMessagesFolder').invoke("stopObserving", "click", this._customFolderClickListener);
    Event.stopObserving($('siteMenuBarWidgetMessagesComposeButton'), "click", this._composeMessageButtonClickListener);
    
    $super();
  },
  getName: function ($super) {
    return 'messages';
  },
  viewMessage: function (messageId) {
    this._viewMessage(messageId, true);
  },
  _clearWorkspace: function () {
    $('siteMenuBarWidgetMessagesWorkareaContainer').select('.siteMenuBarWidgetMessagesListMessageActions a').invoke('purge');
    $('siteMenuBarWidgetMessagesWorkareaContainer').select('.siteMenuBarWidgetMessagesListStar').invoke('purge');
    var messages = $('siteMenuBarWidgetMessagesWorkareaContainer').select('.siteMenuBarWidgetMessagesListMessage');
    messages.invoke('purge');
    messages.invoke("remove");
    $('siteMenuBarWidgetMessagesWorkareaContainer').update('');
    
    var composeMessage = $('siteMenuBarWidgetMessagesComposeMessage');
    if (composeMessage) {
      composeMessage.down('input[type="submit"]').purge();
    }
  },
  _startWorkspaceLoading: function () {
    $('siteMenuBarWidgetMessagesWorkareaContainer').addClassName("siteMenuBarWidgetMessagesWorkareaLoading");
  },
  _stopWorkspaceLoading: function () {
    $('siteMenuBarWidgetMessagesWorkareaContainer').removeClassName("siteMenuBarWidgetMessagesWorkareaLoading");
  },
  _addMessage: function (container, id, read, time, sender, subject, starred, supportTrash, supportStar) {
    var messageElement = new Element("div", {
      className: "siteMenuBarWidgetMessagesListMessage"
    });
    
    if (!read) {
      messageElement.addClassName("siteMenuBarWidgetMessagesListMessageUnread");
    }
    
    var messageActions = new Element("div", {
      className: "siteMenuBarWidgetMessagesListMessageActions",
      href: 'javascript:void(null)'
    });
    
    if (supportTrash) {
      var trashButton = new Element("a", {
        className: "siteMenuBarWidgetMessagesListMessageTrash",
        href: 'javascript:void(null)',
        title: getLocale().getText('common.siteMenuBar.messagesWidget.messageListTrashButtonLabel')
      });
      messageActions.appendChild(trashButton);
      Event.observe(trashButton, "click", this._messageListTrashClickListener);
    }
    
    var replyButton = new Element("a", {
      className: "siteMenuBarWidgetMessagesListMessageReply",
      href: 'javascript:void(null)',
      title: getLocale().getText('common.siteMenuBar.messagesWidget.messageListReplyButtonLabel')
    });
    messageActions.appendChild(replyButton);
    Event.observe(replyButton, "click", this._messageListReplyClickListener);
    
    var replyAllButton = new Element("a", {
      className: "siteMenuBarWidgetMessagesListMessageReplyAll",
      href: 'javascript:void(null)',
      title: getLocale().getText('common.siteMenuBar.messagesWidget.messageListReplyAllButtonLabel')
    });
    messageActions.appendChild(replyAllButton);
    Event.observe(replyAllButton, "click", this._messageListReplyAllClickListener);
    
    var forwardButton = new Element("a", {
      className: "siteMenuBarWidgetMessagesListMessageForward",
      href: 'javascript:void(null)',
      title: getLocale().getText('common.siteMenuBar.messagesWidget.messageListForwardButtonLabel')
    });
    messageActions.appendChild(forwardButton);
    Event.observe(forwardButton, "click", this._messageListForwardClickListener);
    
    messageElement.appendChild(messageActions);
    
    messageElement.appendChild(new Element("input", {
      type: "hidden",
      name: "messageId",
      value: id
    }));
    
    if (supportStar) {
      var starElement = new Element("div", {
        className: "siteMenuBarWidgetMessagesListStar"
      });
      if (starred) {
        starElement.addClassName("siteMenuBarWidgetMessagesListStarred");
      }
      messageElement.appendChild(starElement);
      Event.observe(starElement, "click", this._messageStarClickListener);
    }

    var senderElement = new Element("div", {
      className: "siteMenuBarWidgetMessagesListSender"
    }).update(sender.truncate(15));
    
    var subjectElement = new Element("div", {
      className: "siteMenuBarWidgetMessagesListSubject"
    }).update(subject.truncate(30));
    
    var timeStr = '';
    var dateTime = new Date(time);
    var now = new Date();
    
    if ((dateTime.getUTCFullYear() == now.getUTCFullYear()) && (dateTime.getUTCMonth() == now.getUTCMonth()) && (dateTime.getUTCDate() == now.getUTCDate())) {
      timeStr = getLocale().getTime(time);
    } else {
      timeStr = getLocale().getDate(time);
    }
    
    var timeElement = new Element("div", {
      className: "siteMenuBarWidgetMessagesListTime"
    }).update(timeStr);
    
    messageElement.appendChild(senderElement);
    messageElement.appendChild(subjectElement);
    messageElement.appendChild(timeElement);
    
    container.appendChild(messageElement);
    
    Event.observe(messageElement, "click", this._messageClickListener);
  },
  _addFolder: function (id, name) {
    var folderElement = new Element("div", {
      className: "siteMenuBarWidgetMessagesFolder"
    });
    folderElement.appendChild(new Element("input", {
      name: "folderId",
      value: id,
      type: "hidden"
    }));
    folderElement.appendChild(new Element("div", {
      className: "siteMenuBarWidgetMessagesFolderIcon"
    }));
    folderElement.appendChild(new Element("div", {
      className: "siteMenuBarWidgetMessagesFolderName"
    }).update(name));
    
    Event.observe(folderElement, "click", this._folderClickListener);
    
    $('siteMenuBarWidgetMessagesCustomFoldersContainer').appendChild(folderElement);
  },
  _viewMessage: function (messageId, markAsRead) {
    this._startWorkspaceLoading();
    this._clearWorkspace();
    
    var _this = this;
    API.get(CONTEXTPATH + '/v1/messages/' + messageId, {
      onSuccess: function (jsonResponse) {
        if (markAsRead) {
          API.put(CONTEXTPATH + '/v1/messages/' + messageId + '/markRead', {
            onSuccess: function (response) {
              _this._setUnreadMessageCount(response.response.unreadMessages);
            }
          });
        }
        
        var message = jsonResponse.response;
        var recipients = new Array();
        for (var i = 0, l = message.recipients.length; i < l; i++) {
          recipients.push(message.recipients[i].fullName);
        }
        
        var fromElement = new Element("div", {
          className: "siteMenuBarWidgetMessagesViewMessageHeader"
        });
        fromElement.appendChild(new Element("label", {
        }).update(getLocale().getText('common.siteMenuBar.messagesWidget.viewMessageFrom')));
        fromElement.appendChild(new Element("span", {
        }).update(message.sender.fullName));
        
        var subjectElement = new Element("div", {
          className: "siteMenuBarWidgetMessagesViewMessageHeader"
        });
        subjectElement.appendChild(new Element("label", {
        }).update(getLocale().getText('common.siteMenuBar.messagesWidget.viewMessageSubject')));
        subjectElement.appendChild(new Element("span", {
        }).update(message.subject));
        
        var dateElement = new Element("div", {
          className: "siteMenuBarWidgetMessagesViewMessageHeader"
        });
        dateElement.appendChild(new Element("label", {
        }).update(getLocale().getText('common.siteMenuBar.messagesWidget.viewMessageDate')));
        dateElement.appendChild(new Element("span", {
        }).update(getLocale().getDateTime(message.sent)));
        
        var toElement = new Element("div", {
          className: "siteMenuBarWidgetMessagesViewMessageHeader"
        });
        toElement.appendChild(new Element("label", {
        }).update(getLocale().getText('common.siteMenuBar.messagesWidget.viewMessageTo')));
        toElement.appendChild(new Element("span", {
        }).update(recipients.join(',')));
        
        var headers = new Element("div", {
          className: "siteMenuBarWidgetMessagesViewMessageHeaders"
        });
        
        headers.appendChild(fromElement);
        headers.appendChild(subjectElement);
        headers.appendChild(dateElement);
        headers.appendChild(toElement);
        
        $('siteMenuBarWidgetMessagesWorkareaContainer').appendChild(headers);
        $('siteMenuBarWidgetMessagesWorkareaContainer').appendChild(new Element("div", {
          className: "siteMenuBarWidgetMessagesViewMessageContent"
        }).update(message.content));
        
        _this._stopWorkspaceLoading();
      }
    });
  },
  _composeMessage: function (type, refId, refThread, refContent, refDate, refFrom, refRecipients) {
    var _this = this;
    this._startWorkspaceLoading();
    this._clearWorkspace();
    this._loadFriends(function (friends) {
      var friendChoices = new Array();
      
      for (var i = 0, l = friends.length; i < l; i++) {
        friendChoices.push(new SelectAutocompleterChoice(friends[i].id, friends[i].fullName));
      }

      var container = new Element("div", {
        id: "siteMenuBarWidgetMessagesComposeMessage"
      });

      var recipients = new Element("div", {
        className: "siteMenuBarWidgetMessagesComposeMessageRecipients"
      });
      recipients.appendChild(new Element("label").update(getLocale().getText('common.siteMenuBar.messagesWidget.sendMessageTo')));
      var recipientsSelectAutocompleter = new SelectAutocompleter({
        choices: friendChoices,
        required: true
      });
      
      for (var i = 0, l = refRecipients.length; i < l; i++) {
        recipientsSelectAutocompleter.addSelectedById(refRecipients[i]);
      }
      
      recipients.appendChild(recipientsSelectAutocompleter.domNode);
      
      var form = new Element("form", {
        name: "composeMessage"
      });
      
      form.appendChild(recipients);
      container.appendChild(form);

      var subject = new Element("div", {
        className: "siteMenuBarWidgetMessagesComposeMessageSubject"
      });
      var subjectInput = new Element("input", {
        type: "text",
        value: '',
        className: "required",
        name: 'subject'
      });
      subject.appendChild(new Element("label").update(getLocale().getText('common.siteMenuBar.messagesWidget.sendMessageSubject')));
      subject.appendChild(subjectInput);
      form.appendChild(subject);
      
      var content = new Element("div", {
        className: "siteMenuBarWidgetMessagesComposeMessageContent"
      });
      
      var contentValue = '';
      switch (type) {
        case 'NEW':
        break;
        case 'REPLY':
          contentValue = '\n\n' + getLocale().getDate(refDate) + ' ' + refFrom + ':\n\n';
        break;
        case 'REPLYALL':
          contentValue = '\n\n' + getLocale().getDate(refDate) + ' ' + refFrom + ':\n\n';
        break;
        case 'FORWARD':
          contentValue = '\n\nForwarded message:\n\n';
        break;
      }
      
      if (refContent)
        contentValue += refContent.replace(/\>\s+/g, '>').replace(/\s+\</g, '<').replace(/\<\/p\>/g, '\n').replace(/\<br\/\>/g, '\n').stripTags().strip();
      
      var contentInput = new Element("textarea", {
        className: "required",
        name: "content"
      }).update(contentValue);
      
      subject.appendChild(new Element("label").update(getLocale().getText('common.siteMenuBar.messagesWidget.sendMessageContent')));
      content.appendChild(contentInput);

      form.appendChild(content);
      
      var sendButton = new Element("input", {
        type: "submit",
        className: "formvalid",
        value: getLocale().getText('common.siteMenuBar.messagesWidget.sendMessageSendButton')
      });

      form.appendChild(sendButton);
      
      Event.observe(sendButton, "click", function (event) {
        Event.stop(event);
        
        var subject = subjectInput.value;
        var content = contentInput.value;
        var recipientIds = recipientsSelectAutocompleter.getSelectedIds();
        
        API.put(CONTEXTPATH + '/v1/messages/sendMessage', {
          parameters: {
            threadId: refThread,
            subject: subject,
            content: content,
            recipients: recipientIds
          },
          onSuccess: function (jsonResponse) {
            _this._loadFolderMessages();
          }
        });
        
        _this._loadFolderMessages();
      });
      
      initializeValidation(form);
      
      $('siteMenuBarWidgetMessagesWorkareaContainer').appendChild(container);
      
      _this._stopWorkspaceLoading();
    });
  },
  _loadFolderMessages: function () {
    if (this._messageRequest) {
      this._messageRequest.transport.abort();
    }
    
    this._startWorkspaceLoading();
    this._clearWorkspace();
    var _this = this;
    this._messageRequest = API.get(CONTEXTPATH + '/v1/messages/listMessages/' + this._folderId, {
      onSuccess: function (jsonResponse) {
        _this._messageRequest = undefined;
        
        var container = new Element("div", {
          id: "siteMenuBarWidgetMessagesList"
        });
        
        var unreadCount = 0;
        var messages = jsonResponse.response.messages;
        
        if (messages.length > 0) {
          for (var i = 0, l = messages.length; i < l; i++) {
            var message = messages[i];
            _this._addMessage(container, message.id, message.read, message.sent, message.sender.fullName, message.subject, message.starred, _this._folderId != 'OUTBOX' && _this._folderId != 'TRASH',  _this._folderId != 'OUTBOX');
            if (!message.read) {
              unreadCount++;
            }
          }
        } else {
          var emptyMessage = '';
          switch (_this._folderId) {
            case 'INBOX':
              emptyMessage = getLocale().getText('common.siteMenuBar.messagesWidget.emptyInboxFolderMessage');
            break;
            case 'STARRED':
              emptyMessage = getLocale().getText('common.siteMenuBar.messagesWidget.emptyStarredFolderMessage');
            break;
            case 'OUTBOX':
              emptyMessage = getLocale().getText('common.siteMenuBar.messagesWidget.emptyOutboxFolderMessage');
            break;
            case 'TRASH':
              emptyMessage = getLocale().getText('common.siteMenuBar.messagesWidget.emptyTrashFolderMessage');
            break;
            default:
              emptyMessage = getLocale().getText('common.siteMenuBar.messagesWidget.emptyFolderMessage');
            break;
          }
          
          container.appendChild(new Element("div", {
            className: "siteMenuBarWidgetMessagesListEmpty"
          }).update(emptyMessage));
        }
        
        $('siteMenuBarWidgetMessagesWorkareaContainer').appendChild(container);
        
        if (_this._folderId == 'INBOX') {
          _this._setUnreadMessageCount(unreadCount);
        }
        
        _this._stopWorkspaceLoading();
        
        _this.fire("folderLoad");
      }
    });
  },
  _setUnreadMessageCount: function (unreadCount) {
    var unreadElement = $('siteMenuBarWidgetMessagesInboxFolder').down('.siteMenuBarWidgetMessagesFolderUnread');
    if (unreadCount > 0) {
      unreadElement.update(unreadCount).show();
      this.showNotification(unreadCount);
    } else {
      unreadElement.hide();
      this.hideNotification();
    }
  },
  _loadFriends: function (callback) {
    API.get(CONTEXTPATH + '/v1/friends/SELF/listFriends', {
      onSuccess: function (jsonResponse) {
        callback(jsonResponse.response.friends);
      }
    });
  },
  _loadMessage: function (messageId, callback) {
    API.get(CONTEXTPATH + '/v1/messages/' + messageId, {
      onSuccess: function (jsonResponse) {
        callback(jsonResponse.response);
      }
    });
  },
  _onShow: function (event) {
    this._loadFolderMessages();
  },
  _onHide: function (event) {
    this._clearWorkspace();
  },
  _onComposeMessageButtonClick: function (event) {
    this._composeMessage('NEW', null, null, null, null, null, []);
  },
  _onFolderClick: function (event) {
    var folderElement = Event.element(event);
    if (!folderElement.hasClassName('siteMenuBarWidgetMessagesFolder'))
      folderElement = folderElement.up('.siteMenuBarWidgetMessagesFolder');
    
    this.getContentElement().select('.siteMenuBarWidgetMessagesSelectedFolder').invoke('removeClassName', 'siteMenuBarWidgetMessagesSelectedFolder');
    folderElement.addClassName('siteMenuBarWidgetMessagesSelectedFolder');
    
    this._folderId = folderElement.down('input[name="folderId"]').value;
    this._loadFolderMessages();
  },
  _onMessageClick: function (event) {
    var messageElement = Event.element(event);
    if (!messageElement.hasClassName('siteMenuBarWidgetMessagesListMessage'))
      messageElement = messageElement.up('.siteMenuBarWidgetMessagesListMessage');
    var messageId = messageElement.down('input[name="messageId"]').value;
    
    var markAsRead = this._folderId != 'OUTBOX' && messageElement.hasClassName('siteMenuBarWidgetMessagesListMessageUnread');
    this._viewMessage(messageId, markAsRead);
  },
  _onMessageStarClick: function (event) {
    Event.stop(event);
    
    var message = Event.element(event).up('.siteMenuBarWidgetMessagesListMessage');
    var messageId = message.down('input[name="messageId"]').value;
    var messageStar = message.down('.siteMenuBarWidgetMessagesListStar');
    var starred = messageStar.hasClassName('siteMenuBarWidgetMessagesListStarred');
    
    var _this = this;
    if (starred) {
      API.put(CONTEXTPATH + '/v1/messages/' + messageId + '/unstar', {
        onSuccess: function (jsonResponse) {
          messageStar.removeClassName('siteMenuBarWidgetMessagesListStarred');
          if (_this._folderId == 'STARRED') {
            _this._loadFolderMessages();
          }
        }
      });
    } else {
      API.put(CONTEXTPATH + '/v1/messages/' + messageId + '/star', {
        onSuccess: function (jsonResponse) {
          messageStar.addClassName('siteMenuBarWidgetMessagesListStarred');
          if (_this._folderId == 'STARRED') {
            _this._loadFolderMessages();
          }
        }
      });
    }
  },
  _onMessageListTrash: function (event) {
    Event.stop(event);
    var message = Event.element(event).up('.siteMenuBarWidgetMessagesListMessage');
    var messageId = message.down('input[name="messageId"]').value;

    var _this = this;
    API.post(CONTEXTPATH + '/v1/messages/' + messageId + '/moveMessage', {
      parameters: {
        folderId: 'TRASH'
      },
      onSuccess: function (jsonResponse) {
        _this._loadFolderMessages();
      }
    });
  },
  _onMessageListReply: function (event) {
    Event.stop(event);
    var message = Event.element(event).up('.siteMenuBarWidgetMessagesListMessage');
    var messageId = message.down('input[name="messageId"]').value;
    var _this = this;
    this._loadMessage(messageId, function (message) {
      _this._composeMessage('REPLY', message.id, message.threadId, message.content, message.sent, message.sender.fullName, [
        message.sender.id
      ]);
    });
  },
  _onMessageListReplyAll: function (event) {
    Event.stop(event);
    var message = Event.element(event).up('.siteMenuBarWidgetMessagesListMessage');
    var messageId = message.down('input[name="messageId"]').value;
    var _this = this;
    this._loadMessage(messageId, function (message) {
      var recipients = new Array();
      
      for (var i = 0, l = message.recipients.length; i < l; i++) {
        recipients.push(message.recipients[i].id);
      }
      
      recipients.push(message.sender.id);
      
      _this._composeMessage('REPLYALL', message.id, message.threadId, message.content, message.sent, message.sender.fullName, recipients);
    });
  },
  _onMessageListForward: function (event) {
    Event.stop(event);
    var message = Event.element(event).up('.siteMenuBarWidgetMessagesListMessage');
    var messageId = message.down('input[name="messageId"]').value;
    var _this = this;
    this._loadMessage(messageId, function (message) {
      _this._composeMessage('FORWARD', message.id, message.threadId, message.content, message.sent, message.sender.fullName, []);
    });
  }
});