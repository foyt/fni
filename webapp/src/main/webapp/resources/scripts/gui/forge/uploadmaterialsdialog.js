UploadMaterialsDialogController = Class.create(ModalDialogController, {
  initialize: function ($super) {
    $super({
      title: getLocale().getText('forge.uploadMaterials.uploadDialogTitle'),
      contentUrl: CONTEXTPATH + '/forge/uploadmaterialsdialog.page',
      width: 600,
      height: 362,
      position: 'fixed'
    });
    
    this._uploadInProgress = false;
    this._uploadButtonClickListener = this._onUploadButtonClick.bindAsEventListener(this);
    this._uploadFrameClickListener = this._onUploadFrameLoad.bindAsEventListener(this);

    this._folderSelectDialogOpenListener = this._onFolderSelectDialogOpen.bindAsEventListener(this);
    this._folderSelectDialogCloseListener = this._onFolderSelectDialogClose.bindAsEventListener(this);
  },
  destroy: function ($super) {
    Event.stopObserving(this._folderSelect.domNode, "fni:dialogOpen", this._folderSelectDialogOpenListener);
    Event.stopObserving(this._folderSelect.domNode, "fni:dialogClose", this._folderSelectDialogCloseListener);
	this._folderSelect.deinitialize();
    $super();
  },
  setup: function ($super) {
    $super(function () {
      var dialog = this.getDialog();

      this._uploadButton = dialog.content.down('.namedUploadButton');
      this._uploadForm = this._uploadButton.form;
      this._uploadFrame = dialog.content.down('.namedUploadFrame');

      Event.observe(this._uploadButton, "click", this._uploadButtonClickListener);
      Event.observe(this._uploadFrame, "load", this._uploadFrameClickListener);
      
      if (Prototype.Browser.WebKit) {
        this._uploadFrame._loaded = true;  
      }
      
      this._folderSelect = new FormFolderSelectField();
      dialog.content.down(".namedSelectFolder").appendChild(this._folderSelect.domNode);
      Event.observe(this._folderSelect.domNode, "fni:dialogOpen", this._folderSelectDialogOpenListener);
      Event.observe(this._folderSelect.domNode, "fni:dialogClose", this._folderSelectDialogCloseListener);

      this._uploadComponent = new UploadComponent({
        name: 'documents',
        queueHeight: 120,
        required: true
      });
      
      dialog.content.down('.namedUploadFiles').appendChild(this._uploadComponent.domNode);
    }); 
  },
  _checkProgress: function () {
    var _this = this;
    API.get(CONTEXTPATH + '/v1/materials/-/uploadStatus', {
      onSuccess: function (jsonResponse) {
        if (jsonResponse.response.length > 0) {
          for (var i = 0, l = jsonResponse.response.length; i < l; i++) {
            var fileInfo = jsonResponse.response[i];
            _this._setFieldStatus(fileInfo.fieldName, fileInfo.status);
            if (fileInfo.status == "UPLOADING")
              _this._setUploadedProgress(fileInfo.fieldName, fileInfo.uploaded);
            else if (fileInfo.status == "PROCESSING")
              _this._setProcessedProgress(fileInfo.fieldName, fileInfo.processed);
          }
        }
        
        if (_this._uploadInProgress) {
          setTimeout(function () {
            _this._checkProgress();
          }, 500);
        }
      }
    });
  },
  _setUploadedProgress: function (fieldName, bytesUploaded) {
    var fileSize = this._uploadComponent.getFileSize(fieldName);
    if (fileSize > 0) {
      var progress = Math.round((bytesUploaded / fileSize) * 100);
      this._uploadComponent.setProgress(fieldName, progress);
      this._uploadComponent.setProgressText(fieldName, getLocale().getText('forge.uploadMaterials.statusUploading', progress));
    } else {
      this._uploadComponent.setProgressText(fieldName, getLocale().getText('forge.uploadMaterials.statusUploadingUnknown'));
    }
  },
  _setProcessedProgress: function (fieldName, processed) {
    this._uploadComponent.setProgress(fieldName, processed);
    this._uploadComponent.setProgressText(fieldName, getLocale().getText('forge.uploadMaterials.statusProcessing', processed));
  },
  _setFieldStatus: function (fieldName, status) {
    switch (status) {
      case 'PENDING':
        this._uploadComponent.setProgress(fieldName, 0);
        this._uploadComponent.setProgressText(fieldName, '');
      break;
      case 'COMPLETE':
        this._uploadComponent.setProgressText(fieldName, getLocale().getText('forge.uploadMaterials.statusComplete'));
        this._uploadComponent.setProgressBackgroundColor(fieldName, "#00ff00");
        this._uploadComponent.setProgress(fieldName, 100);
        this._uploadComponent.showProgressBar(fieldName);
      break;
      case 'PROCESSING':
        this._uploadComponent.setProgressText(fieldName, getLocale().getText('forge.uploadMaterials.statusProcessing', 0));
        this._uploadComponent.setProgressBackgroundColor(fieldName, "#ffc100");
        this._uploadComponent.showProgressBar(fieldName);
      break;
      case 'FAILED':
        this._uploadComponent.setProgressText(fieldName, getLocale().getText('forge.uploadMaterials.statusFailed'));
        this._uploadComponent.setProgressBackgroundColor(fieldName, "#ff0000");
        this._uploadComponent.setProgress(fieldName, 100);
      break;
      case 'UPLOADING':
        this._uploadComponent.setProgressText(fieldName,  getLocale().getText('forge.uploadMaterials.statusUploading', 0));
        this._uploadComponent.setProgressBackgroundColor(fieldName, "#63cfff");        
        this._uploadComponent.showProgressBar(fieldName);
      break;
    }
  },
  _onUploadButtonClick: function (event) {
	var dialog = this.getDialog();
	var content = dialog.content;
	content.down('input[name="parentFolderId"]').value = this._folderSelect.getFolderId()||'HOME';
      
    this._uploadInProgress = true;
    this._uploadComponent.setDisabled(true);
    this._uploadButton.setAttribute("disabled", "disabled");
    this._uploadForm.submit();
    var _this = this;
    setTimeout(function () {
      _this._checkProgress();
    }, 1000);
  },
  _onUploadFrameLoad: function (event) {
    if (this._uploadFrame._loaded != true) {
      this._uploadFrame._loaded = true;
    } else {
      getWorkspaceController().reloadMaterialLists(["materials", "modified"]);
      this._uploadInProgress = false;
      this.close();
    }
  },
  _onFolderSelectDialogOpen: function (event) {
	 this.hide();
  },
  _onFolderSelectDialogClose: function (event) {
	 this.show();
  }
});