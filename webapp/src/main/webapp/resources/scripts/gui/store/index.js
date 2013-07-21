(function() {
  'use strict';
  
  function showError(jqXHR, textStatus, errorThrown) {
    // TODO: Better error handling
    alert(errorThrown);
  }
  
  function openImageUploadDialog(productId) {
    var url = CONTEXTPATH + '/store/productImages/';
    var maxFileSize = 1000000;
    var previewWidth = 64;
    var previewHeight = 64;
    
    $.ajax(CONTEXTPATH + '/store/dialogs/imageupload.jsf?productId=' + productId, {
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

  /* Image popups */
  
  function initializeImagePopups() {
    $('.store-product').each(function (productIndex, product) {
      var galleryItems = new Array();
      
      $(product).find('.store-product-thumbnails-container img').each(function (thumbnailIndex, thumbnail) {
        galleryItems.push({
          src: $(thumbnail).data('url')
        });
      });
      
      $('.store-product .store-product-images-container a').magnificPopup({ 
        type: 'image',
        gallery: {
          enabled: true
        },
        items: galleryItems
      });
    });
  };
  
  /* Jsf events */
  
  window.onJsfCategoryChange = function (event) {
    if (event.status == 'success') {
      initializeImagePopups(); 
    }
  };
  
  window.onJsfProductUpdate = function (event) {
    if (event.status == 'success') {
      var form = $(event.source).closest('form');
      var file = form.find('input[name="file"]');
      if (file.val()) {
        var fileForm = $('#edit-product-file-form form');
        file.appendTo(fileForm);
        fileForm.submit();
      }
      
      // TODO: This should wait for upload response
      
      var dialog = $(document).data('open-dialog');
      $(dialog).dialog('close');
      $(document).data('open-dialog', null);
    }
  };
  
  window.onJsfProductCreate = function (event) {
    if (event.status == 'success') {
      var form = $(event.source).closest('form');
      var file = form.find('input[name="file"]');
      if (file.val()) {
        var fileForm = $('#create-product-file-form form');
        file.appendTo(fileForm);
        fileForm.submit();
      } 
      
      // TODO: This should wait for upload response
      
      var dialog = $(document).data('open-dialog');
      $(dialog).dialog('close');
      $(document).data('open-dialog', null);
    }
  };
  
  /* Jsf Actions */
  
  /**
   * Execute product admin action
   */
  
  function executeProductAdminAction(productId, command) {
    var operatorForm = $('#product-admin-operator-container form');
    var prefix = operatorForm.attr('name');
    operatorForm.find('input[name="' + prefix + ':product-id"]').val(productId);
    operatorForm.find('input[name="' + prefix + ':' + command + '"]').click();
  }

  /* Live Listeners */
  
  /**
   * Product Admin Actions / Publish Product
   */
 
  $(document).on('click', '.product-publish', function (event) {
    var productId = $(this).data('product-id');
    var productName = $(this).data('product-name');
    
    $.ajax(CONTEXTPATH + '/store/dialogs/productpublish.jsf?productId=' + productId + '&productName=' + productName, {
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
              executeProductAdminAction(productId, 'publish');
              $(this).dialog("close");
            }
          }]
        });
      }
    });
  });
  
  /**
   * Product Admin Actions / Publish Product
   */
 
  $(document).on('click', '.product-unpublish', function (event) {
    var productId = $(this).data('product-id');
    var productName = $(this).data('product-name');
    
    $.ajax(CONTEXTPATH + '/store/dialogs/productunpublish.jsf?productId=' + productId + '&productName=' + productName, {
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
            'class': 'unpublish-button',
            'text': 'Unpublish',
            'click': function(event) { 
              executeProductAdminAction(productId, 'unpublish');
              $(this).dialog("close");
            }
          }]
        });
      }
    });
  });
  
  /**
   * Product Admin Actions / Edit Product
   */
 
  $(document).on('click', '.product-edit', function (event) {
    var productId = $(this).data('product-id');
    
    $.ajax(CONTEXTPATH + '/store/product/' + productId + '/dialog/edit', {
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
              var form = $(this).find('.store-edit-product-form');
              var prefix = form.attr('name');
              var submitButton = form.find('input[name="' + prefix + ':submit-button"]');
              submitButton.click();
              $(document).data('open-dialog', this);
            }
          }]
        });
        
        var form = dialog.find('.store-edit-product-form');
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
      }
    });
  });
  
  /**
   * Product Admin Actions / Add Images
   */
  
  $(document).on('click', '.product-add-images-action', function (e) {
    openImageUploadDialog($(this).data('product-id'));
  });
  
  /**
   * Product Admin Actions / Delete Product
   */
 
  $(document).on('click', '.product-delete', function (event) {
    var productId = $(this).data('product-id');
    var productName = $(this).data('product-name');
    
    $.ajax(CONTEXTPATH + '/store/dialogs/productdelete.jsf?productId=' + productId + '&productName=' + productName, {
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
              executeProductAdminAction(productId, 'delete');
              $(this).dialog("close");
            }
          }]
        });
      }
    });
  });
  
  /**
   * Store Admin Panel / Create Product
   */
 
  $(document).on('click', '#store-admin-panel .store-admin-create-product', function (event) {
    $.ajax(CONTEXTPATH + '/store/product/dialog/create', {
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
            'class': 'create-button',
            'text': 'Create',
            'click': function(event) {
              var form = $(this).find('.store-create-product-form');
              var prefix = form.attr('name');
              var submitButton = form.find('input[name="' + prefix + ':submit-button"]');
              submitButton.click();
              $(document).data('open-dialog', this);
            }
          }]
        });
        
        var form = dialog.find('.store-create-product-form');
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
      }
    });
  });
  
  /**
   * Product thumbnails / Mouse Enter
   */
  
  $(document).on('mouseenter', '.store-product-thumbnails-container img', function (e) {
    var product = $(this).closest('.store-product');
    var productId = product.data('product-id');
    var imageUrl = $(this).data('url');
    var thumbnailUrl = imageUrl + '?width=128&height=128';
   
    $('.store-product[data-product-id="' + productId + '"] .store-product-image-container a').attr("href", imageUrl);
    $('.store-product[data-product-id="' + productId + '"] .store-product-image-container img').attr("src", thumbnailUrl);
  });
  
}).call(this);