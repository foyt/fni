(function() {
  'use strict';
  
  function showSlash(message) {
    $('.illusion-group-chat-splash-container').append(
      $('<div>')
        .hide()
        .addClass('illusion-group-chat-splash-text')
        .text(message)
        .show("fade")
        .delay(10000)
        .hide("puff")
    );  
  };
  
  $(document).ready(function() {
    var boshService = $('#xmpp-bosh-service').val();
    var userJid = $('#xmpp-user-jid').val();
    var password = $('#xmpp-password').val();
    $('.illusion-group-chat-input').attr('disabled', 'disabled');
    
    var stropheConnection = new Strophe.Connection(boshService);
    stropheConnection.connect(userJid, password, function (status) {
      if (status == Strophe.Status.CONNFAIL) {
        // TODO: Proper error handling...
        alert('Xmpp Connection Failed');
      } else if (status == Strophe.Status.CONNECTED) {
        $(document).trigger('strophe.connect', {
          stropheConnection: stropheConnection
        });
      };
    }); 
  });
  
  $(document).on('strophe.connect', function (event, data) {
    var stropheConnection = data.stropheConnection;
    var roomName = $('#xmpp-room').val();
    var nickname = $('#user-nickname').val();

    stropheConnection.muc.join(roomName, nickname, function msg_handler_cb(messageXml, room) {
      var from = messageXml.getAttribute('from');
      var to = messageXml.getAttribute('to');
      var bodyElement = messageXml.getElementsByTagName('body')[0];
      var delayElement = messageXml.getElementsByTagName('delay')[0];
      var xElement = messageXml.getElementsByTagName('x')[0];
        
      $(document).trigger('strophe.muc.message', {
        message: messageXml,
        room: room,
        from: from,
        to: to,
        body: bodyElement ? Strophe.getText(bodyElement) : null,
        delay: delayElement ? delayElement.getAttribute('stamp') : null,
        x: xElement ? xElement.getAttribute('stamp') : null
      });
      
      return true;
    }, function pres_handler_cb(presenceXml, room) {
      $(document).trigger('strophe.muc.join', {
        stropheConnection: stropheConnection,
        room: room
      });

      return true;
    });
  });
  
  $(document).on('strophe.muc.join', function (event, data) {
    var room = data.room;
    
    $('.illusion-group-chat-input').removeAttr('disabled');
    $('.illusion-group-chat-input').keydown(function (event){
      if (event.keyCode == 13) {
        room.groupchat($(this).val());
        $(this).val('');
      }
    });
  });
  
  $(document).on('strophe.muc.message', function (event, data) {
    showSlash(data.body);
  });
  
}).call(this);