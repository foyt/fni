(function() {
  'use strict';
  
  $(document).on('click', '.tag a', function (event) {
    var prefix = $(this).closest('form').attr('name');

    $('input[name="' + prefix + ':remove-tag-text' + '"]').val($(this).data('tag'));
    $('input[name="' + prefix + ':remove-tag-command-button' + '"]').click();
  });
  
}).call(this);