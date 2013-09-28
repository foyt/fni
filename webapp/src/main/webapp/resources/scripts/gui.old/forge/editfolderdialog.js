EditFolderDialogController = Class.create(ModalDialogController, {
  initialize: function ($super, options) {
    $super(Object.extend({
      title: getLocale().getText('forge.editFolder.editFolderDialogTitle'),
      contentUrl: CONTEXTPATH + '/forge/editfolderdialog.page?folderId=' + options.folderId,
      width: 600,
      height: 192,
      position: 'fixed'
    }, options));
    
    this._saveButtonClickListener = this._onSaveButtonClick.bindAsEventListener(this);
    this._cancelButtonClickListener = this._onCancelButtonClick.bindAsEventListener(this);
  },
  destroy: function ($super) {
    Event.stopObserving(this._saveButton, "click", this._saveButtonClickListener);
    Event.stopObserving(this._cancelButton, "click", this._cancelButtonClickListener);
    
    $super();
  },
  setup: function ($super) {
    $super(function () {
      var dialog = this.getDialog();
      
      var dialogContent = dialog.content;
      
      this._titleInput = dialogContent.down('input[name="title"]');
      this._saveButton = dialogContent.down('.namedSaveButton');
      this._cancelButton = dialogContent.down('.namedCancelButton');
      
      Event.observe(this._saveButton, "click", this._saveButtonClickListener);
      Event.observe(this._cancelButton, "click", this._cancelButtonClickListener);
    });
  },
  getFolderId: function () {
    return this._options.folderId;
  },
  _getFolderTitle: function () {
    return this._titleInput.value;
  },
  _onSaveButtonClick: function (event) {
    this.startLoading();
    var _this = this;
    API.put(CONTEXTPATH + '/v1/materials/folders/' + this.getFolderId() + '/updateFolder', {
      parameters: {
        title: this._getFolderTitle()
      },
      onComplete: function () {
        _this.stopLoading();
        callback();
      },
      onSuccess: function (jsonResponse) {
        _this.close();
        getWorkspaceController().reloadMaterialLists();
      }
    });
  },
  _onCancelButtonClick: function (event) {
    this.close();
  }
});