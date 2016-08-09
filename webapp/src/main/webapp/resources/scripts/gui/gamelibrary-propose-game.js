(function() {
  /* global LOCALE */
  
  'use strict';
  
  function checkLicense() {
    var creativeCommons = $('.gamelibrary-propose-game-license-select').val() == 'CREATIVE_COMMONS';
    if (creativeCommons) {
      $('.gamelibrary-propose-game-form-section-cc').show();
      $('.gamelibrary-propose-game-form-section-otherlicense').hide();
      $('.gamelibrary-propose-game-form-section-cc input').attr('required', 'required');
      $('.gamelibrary-propose-game-form-section-otherlicense input').removeAttr('required');
    } else {
      $('.gamelibrary-propose-game-form-section-cc').hide();
      $('.gamelibrary-propose-game-form-section-otherlicense').show();
      $('.gamelibrary-propose-game-form-section-cc input').removeAttr('required');
      $('.gamelibrary-propose-game-form-section-otherlicense input').attr('required', 'required');
    }
  }

  $(document).ready(function () {
    checkLicense();
    
    var existingTagsData = $('.gamelibrary-propose-game-form-tags').data('existing-tags');
    var existingTags = existingTagsData ? existingTagsData.split(',') : [];
    
    $('.gamelibrary-propose-game-form-tags').tagsInput({
      'autocomplete_url': 'about:blank',
      'autocomplete': {
        source: existingTags
      },
      'defaultText': $('.gamelibrary-propose-game-form-tags').data('help-text'),
      width: '100%',
      height: '120px'
    });
    
    $('input[type="file"]').each(function (index, fileInput) {
      var formId = $(fileInput.form).attr('name');
      
      $(fileInput).uploadField({
        fileIdInput: $('input[name="' + formId + ':' + $(fileInput).attr('name') + '-file-id"]'),
        fileNameInput: $('input[name="' + formId + ':' + $(fileInput).attr('name') + '-filename"]'),
        contentTypeInput: $('input[name="' + formId + ':' + $(fileInput).attr('name') + '-content-type"]')
      });
    });
    
    $('form').submit(function () {
      $('input[type="file"]').remove();
    });
    
    $('.license')
      .licenseSelector({
        locale: LOCALE == 'fi' ? 'fi' : 'en'
      });

  });
  
  $(document).on('change', '.gamelibrary-propose-game-license-select', function (event) {
    checkLicense();
  });

  $(document).on("uploadStart", '.upload-field', function () {
    $('.gamelibrary-propose-game-send').attr('disabled', 'disabled');
  });
  
  $(document).on("uploadDone", '.upload-field', function () {
    var uploading = false;
    
    $('.upload-field').each(function (index, fileField) {
      if ($(fileField).uploadField('uploading')) {
        uploading = true;
      }
    });
    
    if (!uploading) {
      $('.gamelibrary-propose-game-send').removeAttr('disabled');
    }
  });

}).call(this);