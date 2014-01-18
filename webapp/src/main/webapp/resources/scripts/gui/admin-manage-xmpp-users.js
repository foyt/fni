(function() {
  'use strict';
  
  function changeStatus(userRow, status) {
    userRow.find('.manage-xmpp-user-actions a').remove();
    userRow.find('.manage-xmpp-user-status')
      .removeClass('manage-xmpp-user-status-error')
      .removeClass('manage-xmpp-user-status-warning')
      .removeClass('manage-xmpp-user-status-ok');
    
    userRow.data('status', status);
    userRow.find('.manage-xmpp-user-status').text(status);
    
    switch (status) {
      case 'OK':
        userRow.find('.manage-xmpp-user-status')
          .addClass('manage-xmpp-user-status-ok');
      break;
      case 'MISSING_JID':
        userRow.find('.manage-xmpp-user-status')
          .addClass('manage-xmpp-user-status-error');
          
        userRow.find('.manage-xmpp-user-actions')
          .append(
            $('<a>')
              .attr('href', 'javascript:void(null)')
              .addClass('manage-xmpp-user-create')
              .text($('#js-locales').data('create-link'))
          );
      break;
      case 'ERROR':
        userRow.find('.manage-xmpp-user-status')
          .addClass('manage-xmpp-user-status-error');
      break;
      case 'UNCHECKED':
        userRow.find('.manage-xmpp-user-status')
          .addClass('manage-xmpp-user-status-warning');
          
        userRow.find('.manage-xmpp-user-actions')
          .append(
            $('<a>')
              .attr('href', 'javascript:void(null)')
              .addClass('manage-xmpp-user-check')
              .text($('#js-locales').data('check-link'))
          );
      break;
      default:
        throw new Error('Unknown status: ' + status);
      break;
    }
  };

  $(document).ready(function() {
    var boshService = $('#xmpp-bosh-service').val();
    var xmppDomain = $('#xmpp-domain').val();
    
    $('.manage-xmpp-user').each(function (i, userRow) {
      changeStatus($(userRow), $(userRow).data('status'));
    });
    
    $('.manage-xmpp-mass-create').click(function (event) {
      $('.manage-xmpp-user-create').click();
    });

    $('.manage-xmpp-mass-check').click(function (event) {
      $('.manage-xmpp-user-check').click();
    });

    $('.manage-xmpp-user .manage-xmpp-user-check').click(function (event) {
      var stropheConnection = new Strophe.Connection(boshService);
      var userRow = $(this).closest('.manage-xmpp-user');
      var userJid = userRow.data('user-jid');
      var userPassword = userRow.data('user-password');
      
      stropheConnection.connect(userJid, userPassword, function (status) {
        if (status == Strophe.Status.CONNFAIL) {
          changeStatus(userRow, 'ERROR');
          stropheConnection.disconnect();
        } else if (status == Strophe.Status.CONNECTED) {
          changeStatus(userRow, 'OK');
          stropheConnection.disconnect();
        }
      });
    });

    $('.manage-xmpp-user .manage-xmpp-user-create').click(function (event) {
      var stropheConnection = new Strophe.Connection(boshService);
      
      var userRow = $(this).closest('.manage-xmpp-user');
      var userId = userRow.data('user-id');
      var userEmail = userRow.data('user-email');
      var userJid = userRow.data('user-jid');
      var userPassword = userRow.data('user-password');

      stropheConnection.register.connect(xmppDomain, function (status) {
        if (status === Strophe.Status.REGISTER) {
          // fill out the fields
          stropheConnection.register.fields.username = userJid;
          stropheConnection.register.fields.password = userPassword;
          stropheConnection.register.fields.email = userEmail;
          stropheConnection.register.submit();
        } else if (status === Strophe.Status.REGISTERED) {
          $('input[name="register-form:user-id"]').val(userId);
          $('input[name="register-form:user-jid"]').val(userJid);
          $('input[name="register-form:user-password"]').val(userPassword);
          $('input[name="register-form:save"]').click();
          stropheConnection.disconnect();
          changeStatus(userRow, 'OK');
        } else if (status === Strophe.Status.CONFLICT) {
          changeStatus(userRow, 'ERROR');
          stropheConnection.disconnect();
        } else if (status === Strophe.Status.NOTACCEPTABLE) {
          changeStatus(userRow, 'ERROR');
          stropheConnection.disconnect();
        } else if (status === Strophe.Status.REGIFAIL) {
          changeStatus(userRow, 'ERROR');
          stropheConnection.disconnect();
        }
      });

    });

  });
  
}).call(this);