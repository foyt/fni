(function() {
  'use strict';

  $(document).on('click', '.illusion-remove-page', function (event) {
    var eventId =  $(this).data('event-id');
    var pageId = $(this).data('page-id');
    var pageTitle = $(this).data('page-title');
    
    dust.render("illusion-page-remove", {
      pageTitle: pageTitle
    }, function(err, html) {
      if (!err) {
        var dialog = $(html);
        dialog.dialog({
          modal: true,
          width: 400,
          buttons: [{
            'text': dialog.data('remove-button'),
            'click': function(event) { 
              $.ajax(CONTEXTPATH + '/rest/illusion/events/' + eventId + '/pages/' + pageId, {
                type: 'DELETE',
                success: function (jqXHR, textStatus) {
                  window.location.reload(true);
                },
                error: function (jqXHR, textStatus, errorThrown) {
                  $('.notifications').notifications('notification', 'error', textStatus);
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
      } else {
        $('.notifications').notifications('notification', 'error', err);
      }
    });
  });
  
}).call(this);