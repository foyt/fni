(function() {
  'use strict';
  
  var stropheConnection = null;
  var stropheConnected = false;

  $(document).ready(function() {
    var boshService = $('#xmpp-bosh-service').val();
    var userJid = $('#xmpp-user-jid').val();
    var password = $('#xmpp-password').val();
    var nickname = $('#user-nickname').val();
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
            })]
          }), function () {
            $('input[name="' + prefix + ':save' + '"]').click();
          }, function () {
            // TODO: Proper error handling
            alert('Xmpp room creation failed');
          });                
        });
      });
      
      return false;
    });
  });
  
  $(window).unload(function() {
    if (stropheConnected && stropheConnection) {
      stropheConnection.disconnect();
    }
  });
  
}).call(this);