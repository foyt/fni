(function() {
  'use strict';

  $.widget("custom.imageDialog", {
    options : {
      okButtonText: 'Ok',
      cancelButtonText: 'Cancel',
      uploadHintText: 'Change image by clicking here or by dragging image file into this box',
      imageWidth: 128,
      imageHeight: 128
    },
    _create : function() {
      if (!window.FileReader) {
        alert('Your browser does not support FileReader');
      }
      
      $('<div>')
        .addClass('image-dialog-upload-field-container')
        .append(
          $('<input>')
            .change($.proxy(this._onUploadFieldChange, this))
            .addClass('image-dialog-upload-field')
            .attr({
              'type': 'file',
              'accept': 'image/*'
            })
        )
        .append(
          $('<span>')
            .addClass('image-dialog-upload-field-hint')
            .text(this.options.uploadHintText)
        )
        .appendTo(this.element);
      
      $('<div>')
        .addClass('image-dialog-preview-container')
        .append($('<canvas>').addClass('image-dialog-preview'))
        .appendTo(this.element);
      
      $('<div>')
        .addClass('image-dialog-image-container')
        .append(
          $('<img>')
            .load($.proxy(this._onImageLoad, this))
            .addClass('image-dialog-image')
        )
        .appendTo(this.element);
      
      this._jCropApi = null;
        
      this.element.dialog({
        modal: true,
        width: 660,
        buttons: [{
          'text': this.options.okButtonText,
          'click': function(event) {
            $(this).dialog("close");
          }
        }, {
          'text': this.options.cancelButtonText,
          'click': function(event) { 
            $(this).dialog("close");
          }
        }]
      });
    },
    
    _onImageLoad: function (event) {
      var imageElement = $(event.target);
      var originalWidth = $(imageElement).width();
      var originalHeight = $(imageElement).height();
      var xRatio = originalWidth / this.element.find('.image-dialog-image-container').width();
      var yRatio = originalHeight / this.element.find('.image-dialog-image-container').height();
      var ratio = xRatio > yRatio ? xRatio : yRatio;
      var newWidth = originalWidth / ratio;
      var newHeight = originalHeight / ratio;
      
      if (this._jCropApi) {
        this._jCropApi.destroy();
      }
      
      var _this = this;
      $(imageElement)
        .attr({
          'width': newWidth,
          'height': newHeight,
          'data-ratio': ratio
        })
        .Jcrop({
          aspectRatio: 1,
          setSelect: [0, 0, this.options.imageWidth, this.options.imageHeight],
          minSize: [1, 1],
          onChange: $.proxy(this._onCropChange, this),
          onSelect: $.proxy(this._onCropChange, this),
          allowSelect: true,
          allowMove: true,
          allowResize: true
        }, function () {
          _this._jCropApi = this;
          _this.element.find('.image-dialog-image-container').removeClass('image-dialog-image-loading');
        });
    },
    
    _onUploadFieldChange: function (event) {
      var files = event.target.files;
      if (files.length == 1) {
        var file = files[0];
        if (file.type.match('image.*')) {
          this.element.find('.image-dialog-image-container').addClass('image-dialog-image-loading');    
          setTimeout($.proxy(function () {
            var reader = new FileReader();
            reader.onload = $.proxy(this._onFileReaderLoad, this);
            reader.readAsDataURL(files[0]);
          }, this), 0);
        }
      };
    },
    
    _onFileReaderLoad: function (event) {
      var fileReader = event.target;
      this.element.find('.image-dialog-image')
        .removeAttr('width')
        .removeAttr('height')
        .attr('src', fileReader.result);
    },
    
    _onCropChange: function(coords) {
      if (coords.w > 0 && coords.h > 0) {
        var previewCanvas = this.element.find('.image-dialog-preview');
        var canvasHeight = previewCanvas.height();
        var canvasWidth = previewCanvas.width();
        var ratio = this.element.find('.image-dialog-image').data('ratio');
        
        previewCanvas.attr({
          width: canvasWidth,
          height: canvasHeight,
        });
        
        var previewContext = previewCanvas.get(0).getContext('2d');
        previewContext.drawImage(this.element.find('.image-dialog-image').get(0), coords.x * ratio, coords.y * ratio, coords.w * ratio, coords.h * ratio, 0, 0, canvasHeight, canvasWidth);
      }
    },
    
    _destroy : function() {
    }
  });

}).call(this);
