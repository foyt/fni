(function() {
  'use strict';

  $(document).ready(function() { 
    $('.timezone').html(jstz.determine().name());
    $('.event-start').dateTimeField();
    $('.event-end').dateTimeField();
    

    $('.sign-up-start-date,.sign-up-end-date').each(function(index, element) {
      $(element).attr('type', 'text').datepicker({
        altField : $(element).data('alt-field'),
        altFormat : $.datepicker.ISO_8601
      });
      var date = $($(element).data('alt-field')).val();
      if (date) {
        $(element).datepicker('setDate', new Date(Date.parse(date)));
      }
      $(element).change(function() {
        if (!$(element).val()) {
          $($(element).data('alt-field')).val('');
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