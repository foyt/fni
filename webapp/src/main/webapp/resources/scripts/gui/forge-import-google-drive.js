(function() {
  'use strict';
  
  $(document).on('change', '.forge-import-google-drive-check-container input', function () {
    if ($('.forge-import-google-drive-check-container input:checked').length > 0) {
      $('.forge-import-google-drive-button').attr('disabled', null);
    } else {
      $('.forge-import-google-drive-button').attr('disabled', 'disabled');
    }
  });
  
  $(document).ready(function () {
    $('.forge-import-google-drive-button').click(function () {
      var selected = new Array();
      
      $('.forge-import-google-drive-check-container input:checked').each(function (index, input) {
        selected.push($(input).val());
      });
      
      var form = $(this).closest('form');
      var prefix = form.attr('name');

      $('input[name="' + prefix + ':import-entry-ids' + '"]').val(selected.join('&'));
      $('input[name="' + prefix + ':import-button' + '"]').click();
    });
  });

}).call(this);