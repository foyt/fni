(function() {
  'use strict';
  
  var stropheConnection;
  var boshService;
  var userJid;
  var password;
  var nickname;
  var chatBotJid = null;

  $(document).ready(function() {
    boshService = $('#xmpp-bosh-service').val();
    userJid = $('#xmpp-user-jid').val();
    password = $('#xmpp-password').val();
    nickname = $('#user-nickname').val();
    chatBotJid = $('#chat-bot-jid').val();
    
    $('.illusion-create-group-save').attr('disabled', 'disabled');
    
    stropheConnection = new Strophe.Connection(boshService);
    stropheConnection.connect(userJid, password, function (status) {
      if (status == Strophe.Status.CONNFAIL) {
        // TODO: Proper error handling...
        alert('Xmpp Connection Failed');
      } else if (status == Strophe.Status.CONNECTED) {
        stropheConnected = true;
        $('.illusion-create-group-save').removeAttr('disabled');
      }
    });

    $(document).on('click', '.illusion-create-group-save', function (event) {
      event.preventDefault();
      
      var prefix = $(this).closest('form').attr('name');
      var roomName = $('input[name="' + prefix + ':xmpp-room' + '"]').val();
      
      stropheConnection.muc.join(roomName, nickname, null, function pres_handler_cb(presenceXml, room) {
        var xElement = presenceXml.getElementsByTagName('x')[0];
        if (xElement) {
          var statusElement = xElement.getElementsByTagName('status')[0];
          if (statusElement && ("110" == statusElement.getAttribute("code"))) {
            $(document).trigger('strophe.muc.join', {
              stropheConnection: stropheConnection,
              room: room
            });
            
            return true;
          }
          
          var item = xElement.getElementsByTagName('item')[0];
          if (item) {
            $(document).trigger('strophe.muc.presense', {
              stropheConnection: stropheConnection,
              room: room,
              affiliation: item.getAttribute('affiliation'),
              jid: item.getAttribute('jid'),
              nick: item.getAttribute('nick'),
              role: item.getAttribute('role')
            });
            
            return true;
          }
        }

        return true;
      });
      
      return false;
    });
  });
  
  $(document).on('strophe.muc.join', function (event, data) {
    var room = data.room;
    var stropheConnection = data.stropheConnection;
    var prefix = $('.illusion-create-group-panel form').closest('form').attr('name');
    var description = $('textarea[name="' + prefix + ':description' + '"]').val();
    
    room.configure(function () {
      stropheConnection.muc.saveConfiguration(room.name, new Strophe.x.Form({
        type: "submit",
        fields: [new Strophe.x.Field({
          "type": "boolean",
          "var": "muc#roomconfig_persistentroom",
          "required": true,
          "value": "1"
        }), new Strophe.x.Field({
          "type": "boolean",
          "var": "muc#roomconfig_publicroom",
          "required": true,
          "value": "0"
        }), new Strophe.x.Field({
          "type": "list-single",
          "var": "muc#roomconfig_anonymity",
          "required": true,
          "value": "nonanonymous"
        }), new Strophe.x.Field({
          "type": "boolean",
          "var": "muc#roomconfig_changesubject",
          "required": true,
          "value": "0"
        }), new Strophe.x.Field({
          "type": "boolean",
          "var": "muc#roomconfig_membersonly",
          "required": true,
          "value": "1"
        }), new Strophe.x.Field({
          "type": "text-single",
          "var": "muc#roomconfig_roomdesc",
          "required": true,
          "value": description
        })]
      }), function () {
        room.invite(chatBotJid, "Please join us");
      }, function () {
        // TODO: Proper error handling
        alert('Xmpp room creation failed');
      });                
    });
  });
  
  $(document).on('strophe.muc.presense', function (event, data) {
    if (Strophe.getBareJidFromJid(data.jid) == chatBotJid) {
      var room = data.room;
      var prefix = $('.illusion-create-group-panel form').attr('name');
      room.message(data.nick, '/roomSetting locale ' + LOCALE, null, 'chat');
      $('input[name="' + prefix + ':save' + '"]').click();
    }
  });
  
}).call(this);