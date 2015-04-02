(function() {
  'use strict';

  $(document).ready(function() { 
    $('.timezone').html(jstz.determine().name());
    $('.event-start').dateTimeField();
    $('.event-end').dateTimeField();
    

    $('.sign-up-start-date,.sign-up-end-date').each(function(index, element) {
      $(element).attr('type', 'text').datepicker();
      var date = $($(element).data('alt-field')).val();
      if (date) {
        $(element).datepicker('setDate', new Date(Date.parse(date)));
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
    
    $('.illusion-create-event-genres input[type="checkbox"]').change(function () {
      var input = $(this).closest('.illusion-create-event-genres').find('input[type="hidden"]');
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
  });

}).call(this);