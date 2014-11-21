(function() {
  'use strict';
  
  $(document).ready(function() { 
    $('.timezone').html(jstz.determine().name());
    
    $('.datepicker').each(function (index, element) {
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
    
    $('.timepicker').each(function (index, element) {
      $(element)
        .attr('type', 'text')
        .timepicker({
          timeFormat: 'G:i'
        })
        .on("change", function (e, data) {
          var time = $(this).timepicker('getTime');
          $($(this).data('alt-field')).val(time ? time.toISOString() : '');
        });
      
      var time = $($(element).data('alt-field')).val();
      if (time) {
        $(element).timepicker('setTime', new Date(Date.parse(time)));
      }
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