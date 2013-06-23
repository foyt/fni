ImageSelectDialogController = Class.create(ModalDialogController, {
  initialize: function ($super, options) {
    $super(Object.extend({
      title: getLocale().getText('common.imageSelectDialog.dialogTitle'),
      content: this._createContent(),
      width: 600,
      height: 310,
      position: 'fixed'
    }, options));
  },
  getMaterialId: function () {
    return this._options.materialId;
  },
  destroy: function ($super) {
    $super();
  },
  setup: function ($super, callback) {
    $super(callback);
    var treeContainerElement = this.getDialog().content.down('.namedTreeContainer');
    
    this._treeComponent = new TreeComponent({});
    var homeNode = new ImageSelectDialogController_FolderNode({
      nodeText: getLocale().getText('common.imageSelectDialog.homeFolderLabel'),
      folderId: "HOME"
    });
    this._treeComponent.setSelectedNode(homeNode);
    this._treeComponent.addListener("nodeSelected", this, this._onTreeComponentNodeSelected);
    
    this._treeComponent.addChildNode(homeNode);
    treeContainerElement.appendChild(this._treeComponent.domNode);
    treeContainerElement.store("treeComponent", this._treeComponent);
    
    this.getDialog().content.down('.namedTreeContainer').validate();
  },
  _createContent: function () {
    var contentContainer = new Element("div", {
      className: "dialogContent imageSelectDialogContent"
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
    }).update(getLocale().getText('common.imageSelectDialog.helpText')));
    
    fieldsContainer.appendChild(new Element("div", {
      className: "namedTreeContainer imageSelectDialogTreeContainer"
    }));

    buttonsContainer.appendChild(this._createButton(getLocale().getText('common.imageSelectDialog.cancelButtonLabel'), "cancel", null, false));
    buttonsContainer.appendChild(this._createButton(getLocale().getText('common.imageSelectDialog.selectButtonLabel'), "ok", function (event) {
      var dialog = event.dialog;
      dialog.close();
      dialog.fire("select", {
        imageId: dialog._treeComponent.getSelectedNode().getImageId()
      });
    }, true));

    var previewContainer = new Element("div", {
      className: "imageSelectDialogPreviewContainer"
    });
    
    var previewInnerContainer = new Element("div", {
      className: "imageSelectDialogPreviewInnerContainer"
    });

    var previewImage = new Element("img", {
      className: "imageSelectDialogPreviewImage",
      src: 'about:blank'
    });
    
    var previewTitle = new Element("div", {
      className: "imageSelectDialogPreviewTitle"
    });
    
    previewContainer.appendChild(previewInnerContainer);
    previewContainer.appendChild(previewTitle);
    previewInnerContainer.appendChild(previewImage);
    
    form.appendChild(fieldsetsContainer);
    form.appendChild(previewContainer);
    
    contentContainer.appendChild(form);
    
    return contentContainer;
  },
  _onTreeComponentNodeSelected: function (event) {
    var node = event.node;
    var previewImage = this.getDialog().content.down('.imageSelectDialogPreviewImage');
    var previewTitle = this.getDialog().content.down('.imageSelectDialogPreviewTitle');
    
    if (node.getType() == 'IMAGE') {
      var title = node.getText();
      previewImage.src = CONTEXTPATH + '/v1/materials/images/' + node.getImageId() + '/128x128';
      previewTitle.update(title.truncate(20));
      previewImage.setAttribute("title", title);
      previewTitle.setAttribute("title", title);
    } else {
      previewImage.src = 'about:blank';
      previewTitle.update('');
      previewImage.removeAttribute("title");
      previewTitle.removeAttribute("title");
    }
    
    this.getDialog().content.down('.namedTreeContainer').validate();
  }
});

ImageSelectDialogController_FolderNode = Class.create(EditableTreeNode, {
  initialize: function ($super, options) {
    $super(options);
    
    this._folderId = this.getComponentOptions().folderId;
    
    this.getIconNode().addClassName("materialFolderNode");
  },
  doLoadChildren: function ($super, callback) {
    if (this.getFolderId() != -1) {
      var _this = this;
      API.get(CONTEXTPATH + '/v1/materials/-/' + this.getFolderId() + '/list?types=FOLDER&types=IMAGE&sort=FOLDERS_FIRST', {
        onSuccess: function (jsonResponse) {
          var materials = jsonResponse.response;
          for (var i = 0, l = materials.length; i < l; i++) {
            var material = materials[i];
            switch (material.type) {
              case 'FOLDER':
                _this.getTree().addChildNode(_this, new ImageSelectDialogController_FolderNode({
                  nodeText: material.title,
                  folderId: material.id
                }));
              break;
              case 'IMAGE':
                _this.getTree().addChildNode(_this, new ImageSelectDialogController_ImageNode({
                  nodeText: material.title,
                  imageId: material.id
                }));
              break;
            }
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
  },
  getType: function () {
    return "FOLDER";
  }
});

ImageSelectDialogController_ImageNode = Class.create(EditableTreeNode, {
  initialize: function ($super, options) {
    $super(Object.extend(options, {
      childless: true
    }));
    
    this._imageId = this.getComponentOptions().imageId;
    
    this.getIconNode().addClassName("materialImageNode");
  },
  getImageId: function () {
    return this._imageId;
  },
  setImageId: function (imageId) {
    this._imageId = imageId;
  },
  getType: function () {
    return "IMAGE";
  }
});

ImageSelectDialogImageRequiredFieldValidator = Class.create(fi.internetix.validation.FieldValidator, {
  initialize : function($super) {
    $super();
  },
  validate: function ($super, field) {
    var treeComponent = field.retrieve("treeComponent");
    if (treeComponent) {
      if (field.form === undefined) {
        field.form = field.up('form');
      }
      
      if (treeComponent.getSelectedNode().getType() == "IMAGE")
        return fi.internetix.validation.FieldValidator.STATUS_VALID;
    }
    
    return fi.internetix.validation.FieldValidator.STATUS_INVALID;
  },
  getType: function ($super) {
    return fi.internetix.validation.FieldValidator.TYPE_MANDATORY;
  },
  getClassName: function () {
    return 'imageSelectDialogTreeContainer';
  }
});

fi.internetix.validation.FieldValidatorVault.registerValidator(new ImageSelectDialogImageRequiredFieldValidator());