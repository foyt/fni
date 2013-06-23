MoveToFolderDialogController_FolderNode = Class.create(EditableTreeNode, {
  initialize: function ($super, options) {
    $super(options);
    
    this._folderId = this.getComponentOptions().folderId;
    
    this.getIconNode().addClassName("materialFolderNode");
  },
  doLoadChildren: function ($super, callback) {
    if (this.getFolderId() != -1) {
      var _this = this;
      API.get(CONTEXTPATH + '/v1/materials/folders/' + this.getFolderId() + '/listFolders', {
        onSuccess: function (jsonResponse) {
          var folders = jsonResponse.response.folders;
          for (var i = 0, l = folders.length; i < l; i++) {
            var folder = folders[i];
            _this.getTree().addChildNode(_this, new MoveToFolderDialogController_FolderNode({
              nodeText: folder.title,
              folderId: folder.id
            }));
          }
          
          callback();
        }
      });
    } else {
      callback();
    }
  },
  getFolderId: function () {
    return this._folderId;
  },
  setFolderId: function (folderId) {
    this._folderId = folderId;
  }
});

MoveToFolderDialogController = Class.create(ModalDialogController, {
  initialize: function ($super, options) {
    $super(Object.extend({
      title: getLocale().getText('forge.moveToFolder.moveToFolderDialogTitle'),
      content: this._createContent(),
      width: 600,
      height: 313,
      position: 'fixed'
    }, options));
    
    this._createFolderClickListener = this._onCreateFolderClick.bindAsEventListener(this);
  },
  getMaterialId: function () {
    return this._options.materialId;
  },
  destroy: function ($super) {
    Event.stopObserving(this.getDialog().content.down('.moveToFolderDialogCreateFolderButton'), "click", this._createFolderClickListener);
    
    $super();
  },
  setup: function ($super, callback) {
    $super(callback);
    var treeContainerElement = this.getDialog().content.down('.namedTreeContainer');
    var createFolderButton = this.getDialog().content.down('.moveToFolderDialogCreateFolderButton');
    
    this._treeComponent = new TreeComponent({});
    var homeNode = new MoveToFolderDialogController_FolderNode({
      nodeText: getLocale().getText('forge.moveToFolder.homeFolderLabel'),
      folderId: "HOME"
    });
    this._treeComponent.setSelectedNode(homeNode);
    
    this._treeComponent.addChildNode(homeNode);
    treeContainerElement.appendChild(this._treeComponent.domNode);
    
    Event.observe(createFolderButton, "click", this._createFolderClickListener);
  },
  _createContent: function () {
    var contentContainer = new Element("div", {
      className: "dialogContent moveToFolderDialogContent"
    }); 
    
    var form = new Element("form");
    Event.observe(form, "submit", function (event) {
      Event.stop(event);
    });
    
    var fieldsetsContainer = new Element("div", { 
      className: "stackedLayoutContainer"
    });
    
    var fieldsContainer = new Element("fieldset", {
      className: "stackedLayoutFieldsContainer"
    }); 
    
    var buttonsContainer = new Element("fieldset", {
      className: "stackedLayoutButtonsContainer"
    });
    
    fieldsetsContainer.appendChild(fieldsContainer);
    fieldsetsContainer.appendChild(buttonsContainer);
    
    fieldsContainer.appendChild(new Element("div", {
      className: "formHelpField"
    }).update(getLocale().getText('forge.moveToFolder.helpText')));
    
    var createFolderButton = new Element("a", {
      className: "moveToFolderDialogCreateFolderButton"
    });
    createFolderButton.writeAttribute({
      title: getLocale().getText('forge.moveToFolder.createFolderLabel')
    });
    
    fieldsContainer.appendChild(createFolderButton);
    fieldsContainer.appendChild(new Element("div", {
      className: "namedTreeContainer"
    }));

    buttonsContainer.appendChild(this._createButton(getLocale().getText('forge.moveToFolder.cancelButtonLabel'), "cancel", null, false));
    buttonsContainer.appendChild(this._createButton(getLocale().getText('forge.moveToFolder.moveButtonLabel'), "ok", function (event) {
      var dialog = event.dialog;
      
      dialog.startLoading();
      API.put(CONTEXTPATH + '/v1/materials/-/' + dialog.getMaterialId() + '/move', {
        parameters: {
          parentFolder: event.dialog._treeComponent.getSelectedNode().getFolderId()
        },
        onComplete: function () {
          dialog.stopLoading();
        },
        onSuccess: function (jsonResponse) { 
          dialog.close();
          getWorkspaceController().reloadMaterialLists(["starred", "materials", "modified"]);
        }
      });
    }, true));

    form.appendChild(fieldsetsContainer);
    contentContainer.appendChild(form);

    return contentContainer;
  },
  _onCreateFolderClick: function (event) {
    var newFolderNode = new MoveToFolderDialogController_FolderNode({
      nodeText: getLocale().getText('forge.moveToFolder.newFolderName'),
      folderId: -1
    });
    
    newFolderNode.addListener("textEditCommit", this, this._onNewFolderNodeEditTextCommit);
    newFolderNode.addListener("textEditCancel", this, this._onNewFolderNodeEditTextCancel);

    var selectedNode = this._treeComponent.getSelectedNode();
    selectedNode.open(function() {
      selectedNode.getTree().addChildNode(selectedNode, newFolderNode);
      newFolderNode.setEdit(true);
      selectedNode.open();
      selectedNode.getTree().setSelectedNode(newFolderNode);
      // TODO: Scroll to view
    });
  },
  _onNewFolderNodeEditTextCancel: function (event) {
    var node = event.component;
    if (node.getFolderId() === -1) {
      var parentNode = node.getParentNode();
      parentNode.getTree().setSelectedNode(parentNode);
      parentNode.removeChildNode(node);
    }
  },
  _onNewFolderNodeEditTextCommit: function (event) {
    var node = event.component;
    var parentNode = node.getParentNode();
    var folderId = parentNode.getFolderId();
    var text = event.text;
    
    this.startLoading();
    var _this = this;
    API.post(CONTEXTPATH + '/v1/materials/folders/' + folderId + '/createFolder', {
      parameters: {
        title: text
      },
      onComplete: function () {
        _this.stopLoading();
      },
      onSuccess: function (jsonResponse) {
        node.setFolderId(jsonResponse.response.id);
      },
      onFailure: function (message, code, isHttpError, defaultHandler) {
        var parentNode = node.getParentNode();
        parentNode.getTree().setSelectedNode(parentNode);
        parentNode.removeChildNode(node);

        getNotificationQueue().addNotification(new NotificationMessage({
          text: message,
          className: "errorMessage"
        }));
      }
    });
  }
});