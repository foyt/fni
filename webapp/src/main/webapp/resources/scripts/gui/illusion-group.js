(function() {
  'use strict';
  
  $.widget("custom.illusionChat", {
    options : {
      userJid: null,
      userRole: null,
      password: null,
      boshService: null,
      roomJid: null,
      nickname: null,
      chatBotJid: null,
      messageAvatarSize: 32
    },
    
    _create : function() {
      this._chatBotNick = null;
      this._chatBotPresent = false;
      this._groupUrlName = Strophe.getNodeFromJid(this.options.roomJid);
      $(this.element).find('.illusion-group-chat-input')
        .keydown($.proxy(this._onChatInputKeydown, this))
        .attr('disabled', 'disabled');
      
      $(this.element).chatClient({
        userJid: this.options.userJid,
        password: this.options.password,
        boshService: this.options.boshService,
        rooms: [{
          jid: this.options.roomJid,
          nickname: this.options.nickname
        }] 
      });
      
      $(this.element).on('chat.error', $.proxy(this._onChatError, this));
      $(this.element).on('chat.connectionfailure', $.proxy(this._onChatConnectionFailure, this));
      $(this.element).on('chat.authfail', $.proxy(this._onChatAuthFail, this));
      $(this.element).on('chat.disconnect', $.proxy(this._onChatDisconnect, this));
      $(this.element).on('chat.muc.error', $.proxy(this._onChatMucError, this));
      $(this.element).on('chat.muc.delayed-chat', $.proxy(this._onChatMucDelayedChat, this));
      $(this.element).on('chat.muc.delayed-groupchat', $.proxy(this._onChatMucDelayedGroupChat, this));
      $(this.element).on('chat.muc.chat', $.proxy(this._onChatMucChat, this));
      $(this.element).on('chat.muc.groupchat', $.proxy(this._onChatMucGroupChat, this));
      $(this.element).on('chat.muc.participant-join', $.proxy(this._onChatMucParticipantJoin, this));
      $(this.element).on('chat.muc.participant-exit', $.proxy(this._onChatMucParticipantExit, this));
      $(this.element).on('chat.muc.join', $.proxy(this._onChatMucJoin, this));
      $(this.element).on('chat.muc.exit', $.proxy(this._onChatMucExit, this));
      $(this.element).on('chat.command.roll', $.proxy(this._onChatCommandRoll, this));
      $(this.element).on('chat.command.changeBotNick', $.proxy(this._onChatChangeBotNick, this));
    },
    
    changeNick: function (nickname) {
      $('.illusion-group-chat-input').attr('disabled', 'disabled');
      $('input[name="command-form:userNickname"]').val(nickname);
      $('input[name="command-form:updateUserNickname"]').click();
    },
    
    inviteParticipants: function (userJids) {
      for (var i = 0, l = userJids.length; i < l; i++) {
        var userJid = userJids[i];
        $(this.element).chatClient('inviteUser', this.options.roomJid, userJid);
      }
    },
    
    leaveRoom: function () {
      $(this.element).chatClient('leaveRoom', this.options.roomJid, this.options.nickname);
    },
    
    sendGroupchat: function (message) {
      $(this.element).chatClient('sendGroupchat', this.options.roomJid, message);
    },
    
    sendPrivateMessage: function (nick, message) {
      $(this.element).chatClient('sendPrivateMessage', this.options.roomJid, nick, message);
    },
    
    changeChatBotNick: function (nick) {
      // TODO: Better error handling
      if (this.options.userRole != 'GAMEMASTER') {
        alert('Only Gamemaster can do this');
      } else {    
        if (nick) {
          if (this._chatBotPresent) {
            this.sendPrivateMessage(this._chatBotNick, '/roomSetting nick ' + nick);
          } else {
            alert('Chatbot is not present'); 
          }
        } else {
          alert('Cannot change chatbot nick to empty');
        }
      }
    },
    
    disconnect: function (reason) {
      this.leaveRoom();
      $(this.element).chatClient('disconnect', reason);
    },
    
    _onChatMucJoin: function (event, data) {
      $(this.element).find('.illusion-group-chat-input').removeAttr('disabled');
    },
    
    _onChatMucExit: function (event, data) {
      $(this.element).find('.illusion-group-chat-input').attr('disabled', 'disabled');
    },
    
    _onChatMucParticipantJoin: function (event, data) {
      if (Strophe.getBareJidFromJid(data.jid) == this.options.chatBotJid) {
        this._chatBotNick = data.nick;
        this._chatBotPresent = true;
      } else {
        this._addParticipant(data.jid);
      }
    },

    _onChatMucParticipantExit: function (event, data) {
      if (Strophe.getBareJidFromJid(data.jid) == this.options.chatBotJid) {
        this._chatBotNick = null;
        this._chatBotPresent = false;
      } else {
        this._removeParticipant(data.jid);
      }
    },
    
    _onChatMucChat: function (event, data) {
      // TODO: Received private chat message
    },
    
    _onChatMucGroupChat: function (event, data) {
      var nick = Strophe.getResourceFromJid(data.from);
      this._addGroupChatMessage(new Date(), data.fromUser, nick, data.body);
    },
    
    _onChatMucDelayedGroupChat: function (event, data) {
      var time = new Date(Date.parse(data.delayStamp));
      var nick = Strophe.getResourceFromJid(data.from);
      this._addGroupChatMessage(time, data.fromUser, nick, data.body);
    },
    
    _onChatMucDelayedChat: function (event, data) {
      // TODO: Received private chat message
    },
    
    _onChatError: function (event, data) {
      // TODO: Proper error handling
      alert('Unknown chat error');
    },
    
    _onChatConnectionFailure: function (event, data) {
      // TODO: Proper error handling
      alert('Chat connection failed');
    },
    
    _onChatAuthFail: function (event, data) {
      // TODO: Proper error handling
      alert('Chat authentication failed');
    },
    
    _onChatDisconnect: function (event, data) {
      // TODO: More elegant way to reconnect...
      window.location.reload(true);
    },

    _onChatMucError: function (event, data) {
      // TODO: Proper error handling
      
      switch (data.error) {
        case 'not-authorized':
          alert('Could not join password protected room');
        break;
        case 'forbidden':
          alert('Could not join because you are banned from the room');
        break;
        case 'item-not-found':
          alert('Could not join because room does not exist');
        break;
        case 'not-allowed':
          alert('Could not create new room');
        break;
        case 'not-acceptable':
          alert('Could not join becase nickname is reserved');
        break;
        case 'registration-required':
          alert('Could not join because you are not invited to the room');
        break;
        case 'conflict':
          alert('Could noy join because someone with same nickname is already in the room');
        break;
        case 'service-unavailable':
          alert('Could not join because room is full');
        break;
        default:
          alert('Could not join the room');
        break;
      }
    }, 
    
    _onChatInputKeydown: function (event){
      if (event.keyCode == 13) {
        event.preventDefault();
        
        var message = $(event.target).val();
        if (message) {
          if (message.indexOf('/') == 0) {
            var commandText = message.substring(1);
            var argIndex = commandText.indexOf(' ');
            var command = null;
            var commandArgs = null;

            if (argIndex > -1) {
              command = commandText.substring(0, argIndex);
              commandArgs = commandText.substring(argIndex + 1);
              
              $(this.element).trigger('chat.command.' + command, {
                args: commandArgs
              });
            } else {
              command = commandText;
            }
          } else {
            this.sendGroupchat($(event.target).val());
          }
        }

        $(event.target).val(null);
      }
    },
      
    _onChatCommandRoll: function (event, data) {
      this.sendGroupchat('/roll ' + data.args);
    },
    
    _onChatChangeBotNick: function (event, data) {
      this.changeChatBotNick(data.args);
    },
    
    _addGroupChatMessage: function (time, fromJid, fromNick, body) {
      var message = $('<div>');
      
      if (fromJid) {
        message.append($('<img>')
          .addClass('illusion-group-chat-message-avatar')
          .attr("src", this._getAvatarUrl(fromJid, this.options.messageAvatarSize))
        );
      }; 
      
      // TODO: Localize time
      
      message
        .addClass('illusion-group-chat-message')
        .append($('<div>')
          .addClass('illusion-group-chat-message-sent')
          .text(time.toUTCString())
        )
        .append($('<div>')
          .addClass('illusion-group-chat-message-from')
          .text(Strophe.unescapeNode(fromNick))
        )
        .append($('<div>')
          .addClass('illusion-group-chat-message-body')
          .html((body||'')
              .replace(/\n/g, '<br/>')
              .replace(/\[\+\]/g, '<span class="illusion-group-chat-fudge-plus"/>')
              .replace(/\[\ \]/g, '<span class="illusion-group-chat-fudge-empty"/>')
              .replace(/\[\-\]/g, '<span class="illusion-group-chat-fudge-minus"/>'))
        );
      
      $('.illusion-group-chat-messages')
        .append(message)
        .scrollTo(message);
    },
    
    _addParticipant: function (userJid) {
      var jid = Strophe.getBareJidFromJid(userJid);
      
      dust.render("illusion-group-participant", {
        groupUrlName: this._groupUrlName,
        jid: jid
      }, function(err, html) {
        $(html).appendTo('.illusion-group-participants');
      });
    },
    
    _removeParticipant: function (userJid) {
      var jid = Strophe.getBareJidFromJid(userJid);
      $('.illusion-group-participants .illusion-group-participant').each(function (index, participant) {
        if ($(participant).data('jid') == jid) {
          $(participant).remove();
        }
      });
    },
    
    _getAvatarUrl: function (userJid, size) {
      return CONTEXTPATH + '/illusion/groupAvatar/' + this._groupUrlName + '/' + Strophe.getBareJidFromJid(userJid) + '?size=' + size;
    },
    
    _destroy : function() {
    }
  });
  
  $(document).ready(function() {
    $('.illusion-group-chat-container').illusionChat({
      userJid: $('#xmpp-user-jid').val(),
      userRole: $('#user-role').val(),
      password: $('#xmpp-password').val(),
      boshService: $('#xmpp-bosh-service').val(),
      roomJid: $('#xmpp-room').val(),
      nickname: $('#user-nickname').val(),
      chatBotJid: $('#chat-bot-jid').val()
    });
    
    $('.illusion-group-user-menu')
      .hide()
      .menu({
        select: function( event, ui ) {
          event.preventDefault();
          $(this).hide();
          var action = ui.item.find('a').data('action');
          $(document).trigger('illusion.user.' + action, { });
        }
      });
    
    $('.illusion-group-admin-menu')
      .hide()
      .menu({
        select: function( event, ui ) {
          event.preventDefault();
          $(this).hide();
          var action = ui.item.find('a').data('action');
          $(document).trigger('illusion.admin.' + action, { });
        }
      });
    
    $(document).on("click", '.illusion-group-user .illusion-group-user-image', function (event) {
      $(this).closest('.illusion-group-user').find('.illusion-group-user-menu').show();
    });
    
    $(document).on("click", '.illusion-group-participant .illusion-group-participant-image', function (event) {
      $(this).closest('.illusion-group-participant').find('.illusion-group-participant-menu').show();
    });

    $(document).on("click", '.illusion-group-admin-image', function (event) {
      $(this).closest('.illusion-group-admin').find('.illusion-group-admin-menu').show();
    });
  });
  
  $(window).unload(function () {
    $('.illusion-group-chat-container').illusionChat('disconnect');
  });

  $(window).click(function (event) {
    if ($(event.target).closest('.illusion-group-participant').length == 0) {
      $('.illusion-group-participant-menu').hide();
    }

    if ($(event.target).closest('.illusion-group-user').length == 0) {
      $('.illusion-group-user-menu').hide();
    }

    if ($(event.target).closest('.illusion-group-admin').length == 0) {
      $('.illusion-group-admin-menu').hide();
    }
  });
  
  /* User commands */
  
  $(document).on("illusion.user.changeNickname", function (event, data) {
    dust.render("illusion-group-change-nick", {
      name: $('#user-nickname').val()
    }, function(err, html) {
      if (!err) {
        var dialog = $(html);
        dialog.dialog({
          modal: true,
          width: 400,
          buttons: [{
            'text': dialog.data('rename-button'),
            'click': function(event) { 
              var name = $(this).find('input[name="name"]').val();
              if (name) {
                $('.illusion-group-chat-container').illusionChat('changeNick', name);
              }
              
              $(this).dialog("close");
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
  
  $(document).on("illusion.user.changeAvatar", function (event, data) {
    dust.render("illusion-group-change-avatar", {
      name: $('#user-nickname').val()
    }, function(err, html) {
      if (!err) {
        var dialog = $(html);
        
        dialog.imageDialog({
          okButtonText: dialog.data('change-button'),
          cancelButtonText: dialog.data('cancel-button'),
          uploadHintText: dialog.data('upload-hint')
        });
        
        dialog.on('imageDialog.okClick', function (event, data) {
          var groupJid = $('#xmpp-room').val();
          var groupUrlName = Strophe.getNodeFromJid(groupJid);
          var userJid = $('#xmpp-user-jid').val();
          
          $.ajax(CONTEXTPATH + '/illusion/groupAvatar/' + groupUrlName + '/' + userJid, {
            type: 'POST',
            data: {
              'data': data.imageData
            },
            success: function (event) {
              window.location.reload(true);
            }
          });
        });
        
      } else {
        // TODO: Proper error handling...
        alert(err);
      }
    }); 
  });
  
  /* Admin commands */
  
  $(document).on("illusion.admin.invitePlayers", function (event, data) {
    dust.render("illusion-group-invite-players", { }, function(err, html) {
      if (!err) {
        var dialog = $(html);
        dialog.dialog({
          modal: true,
          width: 600,
          buttons: [{
            'text': dialog.data('invite-button'),
            'click': function(event) { 
              var userIds = $.map($(this).find('input[name="invite"]').tokenInput('get'), function(o) { return o["id"]; });
              var message = $(this).find('textarea[name="message"]');
              var groupJid = $('#xmpp-room').val();
              var groupUrlName = Strophe.getNodeFromJid(groupJid);

              $.ajax(CONTEXTPATH + '/illusion/groupInvite/' + groupUrlName, {
                type: 'POST',
                traditional: true,
                data: {
                  'userId': userIds,
                  'message': message
                },
                success: function (data) {
                  $('.illusion-group-chat-container').illusionChat('inviteParticipants', data.inviteJids);
                }
              });
              
              $(this).dialog("close");
            }
          }, {
            'text': dialog.data('cancel-button'),
            'click': function(event) { 
              $(this).dialog("close");
            }
          }]
        });
        
        var inviteInput = dialog.find('input[name="invite"]');
        inviteInput.tokenInput(CONTEXTPATH + "/search/?source=USERS", {
          hintText: $(inviteInput).data('hint'),
          noResultsText: $(inviteInput).data('no-results'),
          searchingText: $(inviteInput).data('searching'),
          preventDuplicates: true,
          onResult: function (results) {
            return results['USERS'];
          }
        });
      } else {
        // TODO: Proper error handling...
        alert(err);
      }
    });
  });
  
  $(document).on("illusion.admin.changeBotNickname", function (event, data) {
    dust.render("illusion-group-rename-chatbot", { }, function(err, html) {
      if (!err) {
        var dialog = $(html);
        dialog.dialog({
          modal: true,
          width: 400,
          buttons: [{
            'text': dialog.data('rename-button'),
            'click': function(event) { 
              var nickname = $(this).find('input[name="name"]').val();
              if (nickname) {
                $('.illusion-group-chat-container').illusionChat('changeChatBotNick', nickname);
              }
              
              $(this).dialog("close");
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
  
  $(document).on("illusion.admin.changeBotAvatar", function (event, data) {
    dust.render("illusion-group-change-avatar", { }, function(err, html) {
      if (!err) {
        var dialog = $(html);
        
        dialog.imageDialog({
          okButtonText: dialog.data('change-button'),
          cancelButtonText: dialog.data('cancel-button'),
          uploadHintText: dialog.data('upload-hint')
        });
        
        dialog.on('imageDialog.okClick', function (event, data) {
          var groupJid = $('#xmpp-room').val();
          var groupUrlName = Strophe.getNodeFromJid(groupJid);
          var chatBotJid = $('#chat-bot-jid').val();
          
          $.ajax(CONTEXTPATH + '/illusion/groupAvatar/' + groupUrlName + '/' + chatBotJid, {
            type: 'POST',
            data: {
              'data': data.imageData
            },
            success: function (event) {
              window.location.reload(true);
            }
          });
        });
        
      } else {
        // TODO: Proper error handling...
        alert(err);
      }
    }); 
  });
  
}).call(this);