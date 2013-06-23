UploadMapTileDialogController = Class.create(ModalDialogController, {
  initialize: function ($super) {
    $super({
      title: 'Upload Map Tile', // TODO: Locale
      contentUrl: CONTEXTPATH + '/illusion/uploadmaptiledialog.page'
    });
    
    this._uploadButtonClickListener = this._onUploadButtonClick.bindAsEventListener(this);
  },
  destroy: function ($super) {
    
    $super();
  },
  setup: function ($super) {
    $super(function () {
      var dialog = this.getDialog();

      this._uploadButton = dialog.content.down('.namedUploadButton');
      this._uploadForm = this._uploadButton.form;
      
      Event.observe(this._uploadButton, "click", this._uploadButtonClickListener);
      
      this._uploadComponet = new UploadComponent({
        name: 'images',
        queueHeight: 120
      });
      
      dialog.content.down('.namedUploadFiles').appendChild(this._uploadComponet.domNode);
      //this.createButton(this._uploadButton, true);
    }); 
  },
  _onUploadButtonClick: function (event) {
    this._uploadForm.submit();
  }
});