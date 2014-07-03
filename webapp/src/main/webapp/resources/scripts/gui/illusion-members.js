(function() {
  'use strict';
  
  $(document).ready(function() { 
    
    $('.illusion-invite-members').click(function (event) {
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
                var message = $(this).find('textarea[name="message"]').val();
                var groupUrlName = $('input[name="groupUrlName"]').val();
                
                $.ajax(CONTEXTPATH + '/illusion/groupInvite/' + groupUrlName, {
                  type: 'POST',
                  traditional: true,
                  data: {
                    'userId': userIds,
                    'message': message
                  }
                });
                
                window.location.reload(true);
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
    
  });

}).call(this);