(function() {
  'use strict';
  
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
      $('.forge-upload-upload-button').removeAttr("disabled");
      $('.forge-upload-no-files').hide();
    } else {
      $('.forge-upload-upload-button').attr("disabled", "disabled");
      $('.forge-upload-no-files').show();
    }
  };
  
  $(document).ready(function() {
    $('#forge-upload-field-button').fileupload({
      url : CONTEXTPATH + '/forge/upload/',
      autoUpload: false,
      dataType : 'json',
      maxFileSize: 5000000, 
      disableImageResize: /Android(?!.*Chrome)|Opera/.test(window.navigator.userAgent),
      previewMaxWidth: 48,
      previewMaxHeight: 48,
      previewCrop: true
    })
    .prop('disabled', !$.support.fileInput).parent().addClass($.support.fileInput ? undefined : 'disabled')
    .on('fileuploadadd', function (e, data) {
      if (data.files && data.files.length == 1) {
        var file = data.files[0];
        data.context = $('<div class="forge-upload-file-container">');
        var fileElement = $('<div class="forge-upload-file">').appendTo(data.context);

        $('<div class="forge-upload-file-name">').text(file.name).appendTo(fileElement);
        $('<div class="forge-upload-file-type">').text(file.type).appendTo(fileElement);
        $('<div class="forge-upload-file-size">').text(getHumanReadableFileSize(file.size)).appendTo(fileElement);
        $('<div class="forge-upload-file-remove">').appendTo(fileElement);
        
        data.context.data(data);
        data.context.appendTo('.forge-upload-files');
        
        $('<div class="forge-upload-file-progress">').appendTo(fileElement).progressbar({
          value: 0
        });
       
        updateFileCount();
      }
    })
    .on('fileuploadprocessalways', function (e, data) {
      var file = data.files[0];
      var node = $(data.context);
      if (file.preview) {
        $(file.preview).addClass("forge-upload-file-preview");
        node.prepend(file.preview);
      } else {
        switch (file.type) {
          case 'application/pdf':
            node.prepend('<div class="forge-upload-file-icon-pdf"/>');
          break;
          case 'image/svg+xml':
            node.prepend('<div class="forge-upload-file-icon-svg"/>');
          break;
          default:
            node.prepend('<div class="forge-upload-file-icon-file"/>');
          break;
        }
      }
    })
    .on('fileuploadprogress', function (e, data) {
      var progress = parseInt(data.loaded / data.total * 100, 10);
      $(data.context[0]).find('.forge-upload-file-progress').progressbar({
        value: progress
      });
    });
    
    $('.forge-upload-upload-button').click(function (e) {
      $('.forge-upload-file-container').data().submit().always(function () {
        $(this).remove();
        updateFileCount();
      });
    });
    
    $(document).on('click','.forge-upload-file-remove', function (e) {
      $(this).closest('.forge-upload-file-container').remove();
      updateFileCount();
    });
  });

}).call(this);