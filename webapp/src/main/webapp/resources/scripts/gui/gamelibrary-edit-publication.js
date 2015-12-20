(function() {
  'use strict';
  
  $(document).on('click', '.tag a', function (event) {
    var prefix = $(this).closest('form').attr('name');

    $('input[name="' + prefix + ':remove-tag-text' + '"]').val($(this).data('tag'));
    $('input[name="' + prefix + ':remove-tag-command-button' + '"]').click();
  });
  
  $(document).on('click', '.author a', function (event) {
    var prefix = $(this).closest('form').attr('name');

    $('input[name="' + prefix + ':remove-author-id' + '"]').val($(this).data('author-id'));
    $('input[name="' + prefix + ':remove-author-command-button' + '"]').click();
  });

}).call(this);