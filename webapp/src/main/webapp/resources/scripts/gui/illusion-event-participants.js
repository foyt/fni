(function() {
  'use strict';
  
  $(document).ready(function() { 
    
    $('.illusion-invite-participants').click(function (event) {
      dust.render("illusion-event-invite-participants", { 
        'NAME': $('input[name="eventName"]').val(),
        'JOIN': $('input[name="joinUrl"]').val(),
        'DETAILS': $('input[name="introUrl"]').val(),
      }, function(err, html) {
        if (!err) {
          var dialog = $(html);
          dialog.dialog({
            modal: true,
            width: 600,
            buttons: [{
              'text': dialog.data('invite-button'),
              'click': function(event) {
                var emails = $(this).find('input[name="invite"]').val().split(',');
                var mailSubject = $(this).find('input[name="mail-subject"]').val();
                var mailContent = $(this).find('textarea[name="mail-content"]').val();
                var eventUrlName = $('input[name="eventUrlName"]').val();

                $.ajax(CONTEXTPATH + '/illusion/eventInvite/' + eventUrlName, {
                  type: 'POST',
                  traditional: true,
                  data: {
                    'email': emails,
                    'mailSubject': mailSubject,
                    'mailContent': mailContent
                  },
                  complete: function (jqXHR, textStatus) {
                    window.location.reload(true);
                  }
                });
                
              }
            }, {
              'text': dialog.data('cancel-button'),
              'click': function(event) {
                $(this).dialog("close");
              }
            }]
          });
          
          var inviteInput = dialog.find('input[name="invite"]');
          
          inviteInput.tagsInput({
            'defaultText': $(inviteInput).data('hint'),
            'removeWithBackspace' : false,
            'minChars' : 5, 
            'delimiter': [',',' ',';'],
            'width': '100%',
            'minInputWidth': 160,
            'pattern':/^([a-zA-Z0-9_.+-])+\@(([a-zA-Z0-9-])+\.)+([a-zA-Z0-9]{2,4})+$/
          });

        } else {
          // TODO: Proper error handling...
          alert(err);
        }
      });
    });
    
  });

}).call(this);