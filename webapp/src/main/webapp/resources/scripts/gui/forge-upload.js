(function() {
  'use strict';
  
  var uploadCount = 0;
  
  function roundTo(value, digits) {
    var mul = Math.pow(10, digits);
    return Math.round(value * mul) / mul;
  };
  
  function getHumanReadableFileSize(bytes) {
    if (bytes > (1024 * 1024))
      return roundTo(bytes / (1024 * 1024), 2) + ' MB';
    else if (bytes > 1024)
      return roundTo(bytes / 1024, 2) + ' kB';
    else
      return bytes + ' B';
  };

  function updateFileCount() {
    if ($('.forge-upload-file-container').length > 0) {
      $('.forge-upload-no-files').hide();
    } else {
      $('.forge-upload-no-files').show();
    }
  };
  
  $(document).ready(function() {
    $('#forge-upload-field-button').fileupload({
      url : CONTEXTPATH + '/tempUpload',
      autoUpload : true,
      dataType : 'json',
      maxFileSize: 5000000, 
      disableImageResize: /Android(?!.*Chrome)|Opera/.test(window.navigator.userAgent),
      previewMaxWidth: 48,
      previewMaxHeight: 48,
      previewCrop: true
    })
    .prop('disabled', !$.support.fileInput).parent().addClass($.support.fileInput ? undefined : 'disabled')
    .on('fileuploadadd', function (e, data) {
      $('.forge-upload-upload-button').attr('disabled', 'disabled');
      
      if (data.files && data.files.length == 1) {
        var file = data.files[0];

        var fileCell = $('<div>')
          .addClass('flex-cell-full forge-upload-file-container')
          .data('data', data);
        var fileRow = $('<div>').addClass('flex-row').append(fileCell);
        
        var infoCell = $('<div>')
          .addClass('flex-cell-12 flex-cell-first forge-upload-file');
        
        var previewCell = $('<div>')
          .addClass('flex-cell-4 flex-cell-last flex-right forge-upload-file-preview');
        
        $('<div class="forge-upload-file-name">').text(file.name).appendTo(infoCell);
        $('<div class="forge-upload-file-type">').text(file.type).appendTo(infoCell);
        $('<div class="forge-upload-file-size">').text(getHumanReadableFileSize(file.size)).appendTo(infoCell);
        $('<div class="forge-upload-file-remove">').appendTo(infoCell);
        $('<div class="forge-upload-file-progress">').appendTo(infoCell).progressbar({
          value: 0
        });
        
        fileCell.append($('<div>') 
            .addClass('flex-row')
            .append(infoCell)
            .append(previewCell));
        
        data.context = fileRow.appendTo('.forge-upload-files');
        
        updateFileCount();
        
        data.process();
        
        uploadCount++;
      }
    })
    .on('fileuploadprocessalways', function (e, data) {
      if (data.files && data.files.length == 1) {
        var file = data.files[0];
        
        if (file.preview) {
          data.context.find('.forge-upload-file-preview').append($('<img>').attr('src', file.preview.toDataURL()));
        } else {
          switch (file.type) {
            case 'application/pdf':
              data.context.find('.forge-upload-file-preview').append('<div class="forge-upload-file-icon-pdf"/>');
            break;
            case 'image/svg+xml':
              data.context.find('.forge-upload-file-preview').append('<div class="forge-upload-file-icon-svg"/>');
            break;
            default:
              data.context.find('.forge-upload-file-preview').append('<div class="forge-upload-file-icon-file"/>');
            break;
          }
        }
      }
    })
    .on('fileuploadprogress', function (e, data) {
      var progress = parseInt(data.loaded / data.total * 100, 10);
      data.context.find('.forge-upload-file-progress').progressbar({
        value: progress
      });
    })
    .on('fileuploaddone', function (e, data) {
      var fileName = data.files[0].name;
      var fileType = data.files[0].type;

      data.context
        .find('.forge-upload-file-container')
        .attr({
          'data-file-id': data.result.fileId,
          'data-file-name': fileName,
          'data-file-type': fileType
        });
      
      uploadCount--;
      
      if (uploadCount == 0) {
        $('.forge-upload-upload-button').removeAttr('disabled', 'disabled');
      }
    });

    $('.forge-upload-upload-button').click(function (e) {
      var parentFolderId = $('input[name="parentFolderId"]').val();
      var convert = $('input[name="convert"]').prop("checked");
      var fileInfos = $.map($('.forge-upload-file-container'), function (fileContainer) {
        return {
          fileId: $(fileContainer).attr('data-file-id'),
          fileName: $(fileContainer).attr('data-file-name'),
          fileType: $(fileContainer).attr('data-file-type')
        };
      });
      
      $('.file-infos').val(JSON.stringify(fileInfos));
      $('.convert').val(convert);
      $('.save')[0].click();
    });
    
    $(document).on('click','.forge-upload-file-remove', function (e) {
      $(this).closest('.forge-upload-file-container').remove();
      updateFileCount();
    });
  });

}).call(this);