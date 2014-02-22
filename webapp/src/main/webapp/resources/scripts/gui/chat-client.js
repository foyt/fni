(function() {
  'use strict';

  $.widget("custom.chatClient", {
    options : {
      userJid: null,
      password: null,
      boshService: null,
      autoConnect: true,
      rooms: [] 
    },
    _create : function() {
      $.each(['userJid', 'password', 'boshService'], $.proxy(function (index, optionName) {
        if (!this.options[optionName]) {
          throw new Error(optionName + " not defined");
        }
      }, this));

      this._connection = new Strophe.Connection(this.options.boshService);
      
      if (this.options.rooms) {
        if (!this._connection.muc) {
          throw new Error("muc plugin not present");
        }
      }
      
      $(this.element).on('chat.connect', $.proxy(this._onChatConnect, this));
      
      if (this.options.autoConnect) {
        this.connect();
      }
    },
    
    connect: function () {
      this._connection.connect(this.options.userJid, this.options.password, $.proxy(function (status) {
        switch (status) {
          case Strophe.Status.ERROR:
            $(this.element).trigger('chat.error', { });
          break;
          case Strophe.Status.CONNECTING:
            $(this.element).trigger('chat.connecting', { });
          break;
          case Strophe.Status.CONNFAIL:
            $(this.element).trigger('chat.connectionfailure', { });
          break;
          case Strophe.Status.AUTHENTICATING:
            $(this.element).trigger('chat.authenticating', { });
          break;
          case Strophe.Status.AUTHFAIL:
            $(this.element).trigger('chat.authfail', { });
          break;
          case Strophe.Status.CONNECTED:
            $(this.element).trigger('chat.connect', { });
          break;
          case Strophe.Status.DISCONNECTED:
            $(this.element).trigger('chat.disconnect', { });
          break;
          case Strophe.Status.DISCONNECTING:
            $(this.element).trigger('chat.disconnecting', { });
          break;
          case Strophe.Status.ATTACHED:
            $(this.element).trigger('chat.attached', { });
          break;
        }
      }, this));   
    },
    
    joinRoom: function (roomJid, nickname, password, history) {
      this._connection.muc.join(roomJid, nickname, $.proxy(this._chatRoomMessageHandler, this), $.proxy(this._chatRoomPresenceHandler, this), $.proxy(this._chatRoomRosterHandler, this), password, history); 
    },
    
    leaveRoom: function (roomJid, nickname, exit_msg) {
      this._connection.muc.join(roomJid, nickname, null, history); 
    },

    inviteUser: function (roomJid, userJid, reason) {
      this._connection.muc.invite(roomJid, userJid, reason);
    },
    
    sendGroupchat: function (roomJid, message, html_message) {
      this._connection.muc.groupchat(roomJid, message, html_message);
    },
    
    sendPrivateMessage: function (roomJid, nick, message, html_message) {
      this._connection.muc.message(roomJid, nick, message, html_message, 'chat');
    },
    
    disconnect: function (reason) {
      this._connection.disconnect(reason);
    },
    
    _onChatConnect: function (event) {
      $.each(this.options.rooms, $.proxy(function (index, room) {
        this.joinRoom(room.jid, Strophe.escapeNode(room.nickname), room.password, room.history);
      }, this));
    },
    
    _getChild: function (parent, name) {
      for (var i = 0, l = parent.childNodes.length; i < l; i++) {
        if (name == parent.childNodes[i].tagName.toLowerCase()) {
          return parent.childNodes[i];
        }
      }
      
      return null;
    },
    
    _chatRoomMessageHandler: function (messageXml, room) {
      var xElement = this._getChild(messageXml, 'x');
      var delayElement = this._getChild(messageXml, 'delay');
      var inviteElement = xElement ? this._getChild(xElement, 'invite') : null;
      
      if (inviteElement) {
        var reasonElement = this._getChild(inviteElement, 'reason');
        var reason = reasonElement ? reasonElement.textContent : null;
        var inviteFrom = inviteElement.getAttribute('from');
        
        $(this.element).trigger("chat.muc.invitation", {
          room: room,
          reason: reason,
          inviteFrom: inviteFrom,
          stanza: messageXml
        });
      } else {
        var bodyElement = this._getChild(messageXml, 'body');
        var body = Strophe.getText(bodyElement);
        var event = (delayElement ? 'chat.muc.delayed-' : 'chat.muc.') + messageXml.getAttribute('type');
        var from = messageXml.getAttribute('from');
        var fromUser = xElement ? xElement.getAttribute('from') : null;
        if (!fromUser) {  
          var nick = Strophe.getResourceFromJid(from);
          fromUser = room.roster[nick] ? room.roster[nick].jid : null;
        }
        
        var eventData = {
          room: room,
          body: body,
          from: from,
          fromUser: fromUser,
          stanza: messageXml
        };
        
        if (delayElement) {
          eventData.delayStamp = delayElement.getAttribute('stamp');
        }
        
        $(this.element).trigger(event, eventData);
      }

      return true;
    },
    
    _chatRoomPresenceHandler: function (presenceXml, room) {
      var from = presenceXml.getAttribute('from');
      var to = presenceXml.getAttribute('to');
      var type = presenceXml.getAttribute('type');
      
      if (type == 'error') {
        var error = 'unknown';
        var errorCode = null;
        var errorType = null;
        
        var errorElement = this._getChild(presenceXml, 'error');
        if (errorElement) {
          var errorChild = errorElement.firstChild;
          if (errorChild) {
            error = errorChild.tagName;
          }

          errorType = errorElement.getAttribute('type');
          errorCode = errorElement.getAttribute('code');
        }

        $(this.element).trigger('chat.muc.error', {
          errorType: errorType,
          errorCode: errorCode,
          error: error
        });
      } else {
        var xElement = this._getChild(presenceXml, 'x');
        if (xElement) {
          var nick = null;
          var affiliation = null;
          var role = null;
          var jid = null;
          var statusCode = null;
          
          var itemElement = this._getChild(xElement, 'item');
          var statusElement = this._getChild(xElement, 'status');
          
          if (itemElement) {
            nick = itemElement.getAttribute('nick');
            affiliation = itemElement.getAttribute('affiliation');
            role = itemElement.getAttribute('role');
            jid = itemElement.getAttribute('jid');
          }
          
          if (statusElement) {
            statusCode = statusElement.getAttribute("code");
          }
          
          var event = 'chat.muc.' + (statusCode == "110" ? '' : 'participant-') + (type == 'unavailable' ? 'exit' : 'join');
          $(this.element).trigger(event, {
            from: from,
            to: to,
            nick: nick,
            affiliation: affiliation,
            role: role,
            jid: jid
          });
        }
      }
      
      return true;
    },
    
    _chatRoomRosterHandler: function (roster, room) {
      return true;
    },
    
    _destroy : function() {
    }
  });
  

}).call(this);