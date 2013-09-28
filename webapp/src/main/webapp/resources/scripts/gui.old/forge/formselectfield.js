FormFolderSelectField = Class.create({
  initialize: function () {
    this._folderId = null;
	  
	this.domNode = new Element("div", {
	  className: "formSelectField"
	});

	this._selectedLabel = new Element("span", {
	  className: "formSelectFolderSelected"
	}).update(getLocale().getText("forge.formFolderSelectField.homeFolder"));
	 
	this._selectButton = new Element("a", {
	  href: "javascript:void(null)",
	  className: "formSelectFolderSelectButton"
	}).update(getLocale().getText("forge.formFolderSelectField.selectButton"));
	
	this.domNode.appendChild(this._selectedLabel);
	this.domNode.appendChild(this._selectButton);
	 
    this._selectButtonClickListener = this._onSelectButtonClick.bindAsEventListener(this);
    
    this._folderSelectDialogFolderSelectListener = this._onFolderSelectDialogFolderSelect.bindAsEventListener(this);
    this._folderSelectDialogAfterCloseListener = this._onFolderSelectDialogAfterClose.bindAsEventListener(this);
    
    Event.observe(this._selectButton, "click", this._selectButtonClickListener);
  },
  deinitialize: function () {
    Event.stopObserving(this._selectButton, "click", this._selectButtonClickListener);
  },
  getFolderId: function () {
	return this._folderId;  
  },
  _onSelectButtonClick: function (event) {
	this.domNode.fire("fni:dialogOpen", {
	  	  
	});
	  
    var dialog = new SelectFolderDialogController({
	});
	  	
	dialog.open();

	dialog.getDialog().element.observe("fni:folderSelect", this._folderSelectDialogFolderSelectListener);
	dialog.getDialog().element.observe("ui:dialog:after:close", this._folderSelectDialogAfterCloseListener);
  },
  _onFolderSelectDialogFolderSelect: function (event) {
    this._selectedLabel.update(event.memo.folderName);
	this._folderId = event.memo.folderId;
	this.domNode.fire("fni:change", {
	  folderId: this._folderId,
	  folderName: event.memo.folderName
	});
  },
  _onFolderSelectDialogAfterClose: function (event) {
	this.domNode.fire("fni:dialogClose", {
    });
  }
});