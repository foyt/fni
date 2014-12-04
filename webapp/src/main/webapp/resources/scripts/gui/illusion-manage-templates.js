(function() {
  'use strict';

  $(document).on('click', '.illusion-remove-template', function (event) {
    var eventId =  $(this).data('event-id');
    var name = $(this).data('template-name');
    
    dust.render("illusion-template-remove", {
      name: name
    }, function(err, html) {
      if (!err) {
        var dialog = $(html);
        dialog.dialog({
          modal: true,
          width: 400,
          buttons: [{
            'text': dialog.data('remove-button'),
            'click': function(event) { 
              $('.jsfActionTemplateName').val(name);
              $('.jsfActionDeleteTemplate')[0].click();
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