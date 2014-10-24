(function() {
  'use strict';
  
  $(document).ready(function() { 
    $('.timezone').html(jstz.determine().name());
    
    $('.datepicker').each(function (index, element) {
      $(element).datepicker({
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
      $(element).timepicker({
        timeFormat: 'G:i'
      }).on("change", function (e, data) {
        $($(this).data('alt-field')).val($(this).timepicker('getTime').toISOString());
      });
      
      var time = $($(element).data('alt-field')).val();
      if (time) {
        $(element).timepicker('setTime', new Date(Date.parse(time)));
      }
    });
    
  });

}).call(this);