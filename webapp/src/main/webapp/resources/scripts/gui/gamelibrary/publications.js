(function() {
  'use strict';
  
  /* Image popups */
  
  function initializeImagePopups() {
    $('.gamelibrary-publication').each(function (publicationIndex, publication) {
      var galleryItems = new Array();
      
      $(publication).find('.gamelibrary-publication-thumbnails-container img').each(function (thumbnailIndex, thumbnail) {
        galleryItems.push({
          src: $(thumbnail).data('url')
        });
      });
      
      if (galleryItems.length == 0) {
        // There is only one image and thus no thumbnails so we need to
        // use the publication image
        
        var url = $(publication).find('.gamelibrary-publication-image-container img').data('url');
        if (url) {
          galleryItems.push({
            src: url
          });
        }
      }
      
      $(publication).find('.gamelibrary-publication-images-container a').magnificPopup({ 
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
  
  /* Jsf Actions */
  
  /**
   * Execute publication admin action
   */
  
  window.executePublicationAdminAction = function (publicationId, command) {
    var operatorForm = $('#publication-admin-operator-container form');
    var prefix = operatorForm.attr('name');
    operatorForm.find('input[name="' + prefix + ':publication-id"]').val(publicationId);
    operatorForm.find('input[name="' + prefix + ':' + command + '"]').click();
  };
  
  /**
   * Store Admin Panel / Create Publication
   */
 
  $(document).on('click', '#gamelibrary-admin-panel .gamelibrary-admin-create-publication', function (event) {
    $.ajax(CONTEXTPATH + '/gamelibrary/publications/dialog/create', {
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
              var form = $(this).find('.gamelibrary-create-publication-form');
              var prefix = form.attr('name');
              var submitButton = form.find('input[name="' + prefix + ':submit-button"]');
              submitButton.click();
              $(document).data('open-dialog', this);
            }
          }]
        });
        
        var form = dialog.find('.gamelibrary-create-publication-form');
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
        
        dialog.find('select[name="' + prefix + ':author-select"]').change(function (event) {
          var authorId = $(this).val();
          if (authorId) {
            var authorName = $(this).find("option:selected").text();
            var authorsInputElement = dialog.find('input[name="' + prefix + ':author-ids' + '"]');
            var authorsContainer = dialog.find('.authors-container');
            var authorsStr = $(authorsInputElement).val();
            var authors = authorsStr ? authorsStr.split(',') : new Array();
            
            if (authors.indexOf(authorId) == -1) {
              $('<span data-author-id="{0}" class="author"><span>{1}</span><a href="javascript:void(null);" class="remove-author"></a></span>'.replace('{0}', authorId).replace('{1}', authorName))
                .appendTo(authorsContainer);
              authors.push(authorId);
              authorsInputElement.val(authors.join(','));
            }
            
            $(this).val('');
          }
        });
        
        dialog.on('click', '.remove-author', function () {
          var author = $(this).closest('.author');
          var authorId = $(author).attr('data-author-id');
          if (authorId) {
            var authorsInputElement = dialog.find('input[name="' + prefix + ':author-ids' + '"]');
            var authorsStr = $(authorsInputElement).val();
            var authors = authorsStr ? authorsStr.split(',') : new Array();
            var authorIndex = authors.indexOf(authorId);
            if (authorIndex > -1) {
              authors.splice(authorIndex, 1);
              authorsInputElement.val(authors.join(','));
              author.remove();
            }
          }
        });
      }
    });
  });
  
  /**
   * Publication thumbnails / Mouse Enter
   */
  
  $(document).on('mouseenter', '.gamelibrary-publication-thumbnails-container img', function (e) {
    var publication = $(this).closest('.gamelibrary-publication');
    var publicationId = publication.data('publication-id');
    var imageUrl = $(this).data('url');
    var thumbnailUrl = imageUrl + '?width=128&height=128';
   
    $('.gamelibrary-publication[data-publication-id="' + publicationId + '"] .gamelibrary-publication-image-container a').attr("href", imageUrl);
    $('.gamelibrary-publication[data-publication-id="' + publicationId + '"] .gamelibrary-publication-image-container img').attr("src", thumbnailUrl);
  });
  
  $(document).ready(function () {
    initializeImagePopups();
  });
  
}).call(this);