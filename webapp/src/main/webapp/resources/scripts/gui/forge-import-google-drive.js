(function() {
  'use strict';
  
  $(document).on('change', '.forge-import-google-drive-check-container input', function () {
    if ($('.forge-import-google-drive-check-container input:checked').length > 0) {
      $('.forge-import-google-drive-button').attr('disabled', null);
    } else {
      $('.forge-import-google-drive-button').attr('disabled', 'disabled');
    }
    
    var form = $(this).closest('form');
    var prefix = form.attr('name');
    var selected = $.map($('.forge-import-google-drive-check-container input:checked'), function (input) {
      return $(input).val();
    });
    
    $('input[name="' + prefix + ':import-entry-ids' + '"]').val(selected.join('&'));
  });

}).call(this);