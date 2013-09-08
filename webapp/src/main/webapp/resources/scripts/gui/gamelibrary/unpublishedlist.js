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

  /* Live Listeners */
  
  /**
   * Publication Admin Actions / Publish Publication
   */
 
  $(document).on('click', '.publication-publish', function (event) {
    var publicationId = $(this).data('publication-id');
    var publicationName = $(this).data('publication-name');
    var publishButton = $(this).parent().find('input[type="submit"]');
    
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
              publishButton.click();
            }
          }]
        });
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
    var deleteButton = $(this).parent().find('input[type="submit"]');
    
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
              deleteButton.click();
            }
          }]
        });
      }
    });
  });
  
}).call(this);