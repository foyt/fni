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
    
  });

}).call(this);