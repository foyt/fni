(function() {
  'use strict';
  
  $(document).ready(function () {
    var form = $('.gamelibrary-edit-publication-form');
    var prefix = form.attr('name');
  
    var createTagElement = function (inputText) {
      var text = $.trim(inputText.toLowerCase());
      if (text) {
        var tagsInputElement = $('input[name="' + prefix + ':tags' + '"]');
        var tagsElement = $('.tags');
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
    
    $('a.add-new-tag').click(function (event) {
      var section = $(this).closest('.section');
      var tagInput = section.find('input[name="new-tag"]');
      var text = tagInput.val();
      if (text) {
        createTagElement(text);
        tagInput.val('');
      }
    });
    
    $('select[name="' + prefix + ':tag-select"]').change(function (event) {
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
    
    if ($('select[name="' + prefix + ':license-select"]').val() == 'CC') {
      $('.license-cc-container').show();
      $('.license-other').hide();
    } else {
      $('.license-cc-container').hide();
      $('.license-other').show();
    }
    
    $('select[name="' + prefix + ':license-select"]').change(function (event) {
      var value = $(this).val();
      var section = $(this).closest('.section');
  
      if (value == 'CC') {
        $(section).find('.license-cc-container').show();
        $(section).find('.license-other').hide();
      } else {
        $(section).find('.license-cc-container').hide();
        $(section).find('.license-other').show();
      }
    });
    
    $(document).on('click', '.remove-tag', function () {
      var tagContainer = $(this).parent();
      var text = tagContainer.data('tag');
      var tagsInputElement = $('input[name="' + prefix + ':tags' + '"]');
      var existingTagsStr = tagsInputElement.val();
      var existingTags = existingTagsStr ? existingTagsStr.split(';') : new Array(); 
      var removeIndex = existingTags.indexOf(text);
      if (removeIndex != -1) {
        existingTags.splice(removeIndex, 1);
        tagsInputElement.val(existingTags.join(';'));
      }
      tagContainer.remove();
    });
    
    $('select[name="' + prefix + ':author-select"]').change(function (event) {
      var authorId = $(this).val();
      if (authorId) {
        var authorName = $(this).find("option:selected").text();
        var authorsInputElement = $('input[name="' + prefix + ':author-ids' + '"]');
        var authorsContainer = $('.authors-container');
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
    
    $(document).on('click', '.remove-author', function () {
      var author = $(this).closest('.author');
      var authorId = $(author).attr('data-author-id');
      if (authorId) {
        var authorsInputElement = $('input[name="' + prefix + ':author-ids' + '"]');
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
  });

  window.onJsfPublicationUpdate = function (event) {
    if (event.status == 'success') {
      var form = $(event.source).closest('form');
      var file = form.find('input[name="file"]');
      if (file.val()) {
        var fileForm = $('#edit-publication-file-form form');
        file.appendTo(fileForm);
        fileForm.submit();
      } else {
        window.location = CONTEXTPATH + '/gamelibrary/unpublished/';
      }
    }
  };
  
}).call(this);