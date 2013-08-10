(function() {
  'use strict';
  
  function showError(jqXHR, textStatus, errorThrown) {
    // TODO: Better error handling
    alert(errorThrown);
  }
  
  function openImageUploadDialog(publicationId) {
    var url = CONTEXTPATH + '/gamelibrary/publicationImages/' + publicationId;
    var maxFileSize = 1000000;
    var previewWidth = 64;
    var previewHeight = 64;
    
    $.ajax(CONTEXTPATH + '/gamelibrary/dialogs/imageupload.jsf', {
      async: false,
      success : function(data, textStatus, jqXHR) {
        var dialog = $(data).dialog({
          modal: true,
          width: 800,
          maxHeight: 600,
          buttons: [{
            'class': 'cancel-button',
            'text': 'Cancel',
            'click': function(event) { 
              $(this).dialog("close");
            }
          },{
            'class': 'upload-button',
            'text': 'Upload',
            'disabled': "disabled",
            'click': function(event) { 
              var button = $(this).closest('.ui-dialog').find('.upload-button');
              if (button.data('close') == true) {
                $(this).dialog('close');
              } else {
                button.attr('disabled', 'disabled').addClass('ui-state-disabled');
                button.find('span').text('Processing...');
                
                var data = button.data();
                // TODO: always?
                data.submit().always($.proxy(function () {
                  var button = $(this).closest('.ui-dialog').find('.upload-button');
                  button.removeAttr('disabled').removeClass('ui-state-disabled');
                  button.find('span').text('Close');
                  button.data('close', true);
                }, this));
              }
            }
          }]
        });
        
        $(dialog).data('title', $(dialog).dialog( "option", "title" ));

        $(dialog).find('input[name="files"]').fileupload({
          url: url,
          dataType: 'json',
          autoUpload: false,
          acceptFileTypes: /(\.|\/)(gif|jpe?g|png)$/i,
          maxFileSize: maxFileSize, 
          disableImageResize: /Android(?!.*Chrome)|Opera/.test(window.navigator.userAgent),
          previewMaxWidth: previewWidth,
          previewMaxHeight: previewHeight,
          previewCrop: true
        }).on('fileuploadadd', function (e, data) {
          $(dialog).closest('.ui-dialog').find('.upload-button')
            .removeAttr('disabled')
            .removeClass('ui-state-disabled')
            .data(data);
          
          $.each(data.files, function (index, file) {
            data.context = 
              $('<div class="file-queue-item"/>')
                .append($('<span class="file-queue-item-name"/>').html(file.name))
                .appendTo($(dialog).find('.file-queue'));
           });
          
          
        }).on('fileuploadprocessalways', function (e, data) {
          var index = data.index;
          var file = data.files[index];
          if (file.error) {
            $(data.context).append(file.error);
          } else {
            if (file.preview) {
              $(data.context).prepend(file.preview);
            } else {
              $(data.context).prepend($('<div class="file-queue-preview-failed"/>'));
            }
          }  
        }).on('fileuploadprogressall', function (e, data) {
          var progress = parseInt(data.loaded / data.total * 100, 10);
          $(dialog).dialog( "option", "title", $(dialog).data('title') + ' ' + (progress + ' %'));
        }).on('fileuploadfail', function (e, data) {
          var error = "";
          
          $.each(data.result.files, function (index, file) {
            if (error) {
              error += '\n';
            }
            
            error += file.error;
          });
          
          showError(null, error, null);
        });
      },
      error: function (jqXHR, textStatus, errorThrown) {
        showError(jqXHR, textStatus, errorThrown);
      }
    });
  };
  
  /* Jsf events */
  
  window.onJsfPublicationUpdate = function (event) {
    if (event.status == 'success') {
      var form = $(event.source).closest('form');
      var file = form.find('input[name="file"]');
      if (file.val()) {
        var fileForm = $('#edit-publication-file-form form');
        file.appendTo(fileForm);
        $('#edit-publication-file-form iframe').load(function (event) {
          window.location.href = CONTEXTPATH + '/gamelibrary/unpublished/';
        });
        
        fileForm.submit();
      } else {
        window.location.href = CONTEXTPATH + '/gamelibrary/unpublished/';
      }
      
      // TODO: This should wait for upload response
      
      var dialog = $(document).data('open-dialog');
      $(dialog).dialog('close');
      $(document).data('open-dialog', null);
    }
  };
  
  window.onJsfPublicationCreate = function (event) {
    if (event.status == 'success') {
      var form = $(event.source).closest('form');
      var file = form.find('input[name="file"]');
      if (file.val()) {
        var fileForm = $('#create-publication-file-form form');
        file.appendTo(fileForm);
        fileForm.submit();
      } 
      
      // TODO: This should wait for upload response
      
      var dialog = $(document).data('open-dialog');
      $(dialog).dialog('close');
      $(document).data('open-dialog', null);
    }
  };

  /* Live Listeners */
  
  /**
   * Publication Admin Actions / Publish Publication
   */
 
  $(document).on('click', '.publication-publish', function (event) {
    var publicationId = $(this).data('publication-id');
    var publicationName = $(this).data('publication-name');
    
    $.ajax(CONTEXTPATH + '/gamelibrary/dialogs/publicationpublish.jsf?publicationId=' + publicationId + '&publicationName=' + publicationName, {
      async: false,
      success : function(data, textStatus, jqXHR) {
        var dialog = $(data).dialog({
          modal: true,
          width: 400,
          maxHeight: 600,
          buttons: [{
            'class': 'cancel-button',
            'text': 'Cancel',
            'click': function(event) { 
              $(this).dialog("close");
            }
          }, {
            'class': 'publish-button',
            'text': 'Publish',
            'click': function(event) { 
              executePublicationAdminAction(publicationId, 'publish');
            }
          }]
        });
      }
    });
  });
  
  /**
   * Publication Admin Actions / Edit Publication
   */
 
  $(document).on('click', '.publication-edit', function (event) {
    var publicationId = $(this).data('publication-id');
    
    $.ajax(CONTEXTPATH + '/gamelibrary/publications/' + publicationId + '/dialog/edit', {
      async: false,
      success : function(data, textStatus, jqXHR) {
        var dialog = $(data).dialog({
          modal: true,
          width: 800,
          maxHeight: 600,
          buttons: [{
            'class': 'cancel-button',
            'text': 'Cancel',
            'click': function(event) { 
              $(this).dialog("close");
            }
          }, {
            'class': 'save-button',
            'text': 'Save',
            'click': function(event) {
              var form = $(this).find('.gamelibrary-edit-publication-form');
              var prefix = form.attr('name');
              var submitButton = form.find('input[name="' + prefix + ':submit-button"]');
              submitButton.click();
              $(document).data('open-dialog', this);
            }
          }]
        });
        
        var form = dialog.find('.gamelibrary-edit-publication-form');
        var prefix = form.attr('name');
        
        var createTagElement = function (inputText) {
          var text = $.trim(inputText.toLowerCase());
          if (text) {
            var tagsInputElement = dialog.find('input[name="' + prefix + ':tags' + '"]');
            var tagsElement = dialog.find('.tags');
            var existingTagsStr = tagsInputElement.val();
            var existingTags = existingTagsStr ? existingTagsStr.split(';') : new Array(); 
            if (existingTags.indexOf(text) == -1) {
              $('<span class="tag" data-tag="{0}"><span>{1}</span><a class="remove-tag" href="javascript:void(null);"></a></span>'.replace('{0}', text).replace('{1}', text))
                .appendTo(tagsElement);
              existingTags.push(text);
              tagsInputElement.val(existingTags.join(';'));
            }
          }
        };
        
        var tagsInputElement = dialog.find('input[name="' + prefix + ':tags' + '"]');
        var existingTagsStr = tagsInputElement.val();
        var existingTags = existingTagsStr ? existingTagsStr.split(';') : new Array(); 
        tagsInputElement.val('');
        for (var i = 0, l = existingTags.length; i < l; i++) {
          createTagElement(existingTags[i]);
        }

        dialog.find('a.add-new-tag').click(function (event) {
          var section = $(this).closest('.dialog-section');
          var tagInput = section.find('input[name="new-tag"]');
          var text = tagInput.val();
          if (text) {
            createTagElement(text);
            tagInput.val('');
          }
        });
        
        dialog.find('select[name="' + prefix + ':tag-select"]').change(function (event) {
          var text = $(this).val();
          if (text == 'new') {
            $(this.form).find('.new-tag-container').show();
          } else {
            $(this.form).find('.new-tag-container').hide();
            if (text) {
              createTagElement(text);
            }
          }
        });
        
        dialog.on('click', '.remove-tag', function () {
          var tagContainer = $(this).parent();
          var text = tagContainer.data('tag');
          var tagsInputElement = dialog.find('input[name="' + prefix + ':tags' + '"]');
          var existingTagsStr = tagsInputElement.val();
          var existingTags = existingTagsStr ? existingTagsStr.split(';') : new Array(); 
          var removeIndex = existingTags.indexOf(text);
          if (removeIndex != -1) {
            existingTags.splice(removeIndex, 1);
            tagsInputElement.val(existingTags.join(';'));
          }
          tagContainer.remove();
        });
        
        dialog.find('select[name="' + prefix + ':license-select"]').change(function (event) {
          var value = $(this).val();
          var section = $(this).closest('.dialog-section');

          if (value == 'CC') {
            $(section).find('.license-cc-container').show();
            $(section).find('.license-other').hide();
          } else {
            $(section).find('.license-cc-container').hide();
            $(section).find('.license-other').show();
          }
        });
        
        if (dialog.find('select[name="' + prefix + ':license-select"]').val() == 'CC') {
          dialog.find('.license-cc-container').show();
          dialog.find('.license-other').hide();
        } else {
          dialog.find('.license-cc-container').hide();
          dialog.find('.license-other').show();
        }
      }
    });
  });
  
  /**
   * Publication Admin Actions / Add Images
   */
  
  $(document).on('click', '.publication-add-images-action', function (e) {
    openImageUploadDialog($(this).data('publication-id'));
  });
  
  /**
   * Publication Admin Actions / Delete Publication
   */
 
  $(document).on('click', '.publication-delete', function (event) {
    var publicationId = $(this).data('publication-id');
    var publicationName = $(this).data('publication-name');
    
    $.ajax(CONTEXTPATH + '/gamelibrary/dialogs/publicationdelete.jsf?publicationId=' + publicationId + '&publicationName=' + publicationName, {
      async: false,
      success : function(data, textStatus, jqXHR) {
        var dialog = $(data).dialog({
          modal: true,
          width: 400,
          maxHeight: 600,
          buttons: [{
            'class': 'cancel-button',
            'text': 'Cancel',
            'click': function(event) { 
              $(this).dialog("close");
            }
          }, {
            'class': 'delete-button',
            'text': 'Delete',
            'click': function(event) { 
              executePublicationAdminAction(publicationId, 'delete');
              $(this).dialog("close");
            }
          }]
        });
      }
    });
  });
  
}).call(this);