(function() {
  'use strict';
  
  $(document).ready(function() { 
    $('.timezone').html(jstz.determine().name());
    $('.event-start').dateTimeField();
    $('.event-end').dateTimeField();
    
    $('.sign-up-start-date,.sign-up-end-date').each(function (index, element) {
      $(element)
        .attr('type', 'text')
        .datepicker();
      
      var date = $($(element).data('alt-field')).val();
      if (date) {
        var date = new Date(Date.parse(date));
        date.setMinutes(date.getMinutes() + date.getTimezoneOffset());
        $(element).datepicker('setDate', date);
      }
      
      $(element).change(function() {
        if (!$(this).val()) {
          $($(this).data('alt-field')).val('');
        } else {
          var date = $(this).datepicker('getDate');
          date.setMinutes(date.getMinutes() - date.getTimezoneOffset());
          $($(this).data('alt-field')).val(date.toISOString().split('T')[0]);
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
    
    $(".illusion-event-settings-location")
      .geocomplete({
        blur: true
      })
      .bind("geocode:result", function(event, result){
        $('.location-lat').val(result.geometry.location.lat());
        $('.location-lon').val(result.geometry.location.lng());
      });
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