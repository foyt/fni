(function() {
  'use strict';
  
  $(document).ready(function() { 
    $('.timezone').html(jstz.determine().name());
    $('.event-start').dateTimeField();
    $('.event-end').dateTimeField();
    
    $('.sign-up-start-date,.sign-up-end-date').each(function (index, element) {
      $(element)
        .attr('type', 'text')
        .datepicker({
          altField: $(element).data('alt-field'),
          altFormat: $.datepicker.ISO_8601
        });
      
      var date = $($(element).data('alt-field')).val();
      if (date) {
        $(element).datepicker('setDate', new Date(Date.parse(date)));
      }
      
      $(element).change(function(){
        if (!$(element).val()) {
          $($(element).data('alt-field')).val('');
        }
      });
    });

    $('.illusion-event-settings-genres input[type="checkbox"]').change(function () {
      var input = $(this).closest('.illusion-event-settings-genres').find('input[type="hidden"]');
      var inputVal = input.val();
      var genres = inputVal ? inputVal.split('&') : [];
      var id = $(this).val();
      
      if ($(this).prop('checked')) {
        genres.push($(this).val());
      } else {
        genres.splice($.inArray(id, genres), 1);
      }
      
      input.val(genres.join('&'));
    });
    
    var genres = $('.illusion-event-settings-genres input[type="hidden"]').val();
    if (genres) {
      var genreIds = genres.split('&');
      for (var i = 0, l = genreIds.length; i < l; i++) {
        $('.illusion-event-settings-genres input[value="' + genreIds[i] + '"]').attr('checked', 'checked');
      }
    }
  });
  
  $(document).on('click', '.illusion-remove-event', function (event) {
    var eventId = $('#event-id').val();
    var eventName = $('#event-name').val();
    
    dust.render("illusion-remove-event", {
      eventName: eventName
    }, function(err, html) {
      if (!err) {
        var dialog = $(html);
        dialog.dialog({
          modal: true,
          width: 400,
          buttons: [{
            'text': dialog.data('remove-button'),
            'class': 'remove-button',
            'click': function(event) { 
              $.ajax(CONTEXTPATH + '/rest/illusion/events/' + eventId, {
                type: 'DELETE',
                success: function (jqXHR, textStatus) {
                  window.location.href = CONTEXTPATH + "/";
                },
                error: function (jqXHR, textStatus, errorThrown) {
                  $('.notifications').notifications('notification', 'error', textStatus);
                }
              });
            }
          }, {
            'text': dialog.data('cancel-button'),
            'class': 'cancel-button',
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