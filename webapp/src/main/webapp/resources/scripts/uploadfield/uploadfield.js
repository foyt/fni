(function() {
  'use strict';
  
  $.widget("custom.uploadField", {
    options : {

    },
    _create : function() {
      this.element.addClass('upload-field');
      this._uploading = false;
      
      $(this.element[0].form).submit($.proxy(this._onFormSubmit, this));
      
      this._required = this.element.attr('required') == 'required';
      this.element
        .hide()
        .removeAttr('required');
      
      this._fileNameInput = this.options.fileNameInput;
      this._contentTypeInput = this.options.contentTypeInput;
      this._fileIdInput = this.options.fileIdInput;
      
      if (!this.options.fileNameInput) {
        this._fileNameInput = $('<input>').attr({
          type: 'hidden',
          name: this.element.attr('name') + '-filename'
        }).insertAfter(this.element);
      }
      
      if (!this._contentTypeInput) {
        this._contentTypeInput = $('<input>').attr({
          type: 'hidden',
          name: this.element.attr('name') + '-content-type'
        }).insertAfter(this.element);
      }
      
      if (!this._fileIdInput) {
        this._fileIdInput = $('<input>').attr({
          type: 'hidden',
          name: this.element.attr('name') + '-file-id'
        }).insertAfter(this.element);
      }
      
      $('<input>')
        .attr({
          type: 'file',
          name: 'file'
        })
        .insertAfter(this.element)
        .fileupload({
          url : CONTEXTPATH + '/tempUpload',
          autoUpload : true,
          formData: function (form) {
            return {};
          },
          add : $.proxy(this._onFileUploadAdd, this),
          done : $.proxy(this._onFileUploadDone, this),
          fail : $.proxy(this._onFileUploadFail, this),
          always: $.proxy(this._onFileUploadAlways, this),
          progress : $.proxy(this._onFileUploadProgress, this)          
        });
      
      if (this.options.fileNameInput.val() && this._contentTypeInput.val() && this._fileIdInput.val()) {
        this._createFileElement(this.options.fileNameInput.val(), 100)
          .addClass('done')
      }
    },
    
    _destroy: function () {
      this.element.find('input[type="file"]').remove();
    },
    
    uploading: function () {
      return this._uploading;
    },
    
    _createFileElement: function (fileName, progress) {
      return $('<div>')
        .addClass('upload-field-file')
        .append(
          $('<div>').addClass('upload-field-file-progress').progressbar({ value: progress })
        )
        .append(
          $('<div>').addClass('upload-field-file-name').text(fileName)
        )
        .appendTo(this.element.parent());
    },
    
    _onFileUploadAdd : function(e, data) {
      this.element.trigger("uploadStart");
      
      this._uploading = true;
      this.element.parent().find('.upload-field-file').remove();
      
      data.context = this._createFileElement(data.files[0].name, 0)
        .addClass('loading');
      
      data.submit();
    },
    
    _onFileUploadDone : function(e, data) {
      var fileId = data.result.fileId;
      var fileName = data.files[0].name;
      var contentType = data.files[0].type;
      
      this._fileNameInput.val(fileName);
      this._contentTypeInput.val(contentType);
      this._fileIdInput.val(fileId);
      
      this._uploading = false;
      
      $(data.context)
        .removeClass('loading')
        .addClass('done')
        
      this.element.trigger("uploadDone");
    },
    
    _onFileUploadFail: function(e, data) {
      var jqXHR = data.jqXHR;
      var error = jqXHR ? jqXHR.responseText || jqXHR.statusText || 'Error' : 'Error';
      $('.notifications').notifications('notification', 'error', error);
    },
    
    _onFileUploadAlways: function (e, data) {
    },
    
    _onFileUploadProgress : function(e, data) {
      var progress = parseInt(data.loaded / data.total * 100, 10);
      $(data.context).find('.upload-field-file-progress').progressbar("value", progress);
    },

    _onFormSubmit: function () {
      
    }
  });
  
}).call(this);