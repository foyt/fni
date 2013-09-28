VectorImageEditor = Class.create(ForgeWorkspaceWindow, {
  initialize: function ($super, options) {
    $super(Object.extend({
      title: options.vectorImageTitle,
      contentUrl: CONTEXTPATH + '/forge/editvectorimage.page?vectorImageId=' + options.vectorImageId,
      className: 'forgeEditVectorImageDialog'
    }, options));

    this.addListener("contentLoad", this, this._onContentLoad);
    
    this._editorFrameLoadListener = this._onEditorFrameLoad.bindAsEventListener(this);
  },
  destroy: function ($super) {
    $super();
  },
  _getVectorImageId: function () {
    return this.getNamedElement("vectorImageId").value;
  },
  _getParentFolderId: function () {
    return this.getNamedElement("parentFolderId").value;
  },
  _setVectorImageId: function (vectorImageId) {
    this.getNamedElement("vectorImageId").value = vectorImageId;
  }, 
  _getVectorImageTitle: function () {
    return this.getNamedElement("vectorImageTitle").value;
  },
  _setVectorImageTitle: function (title) {
    if (this._getVectorImageTitle() != title) {
      this.getNamedElement("vectorImageTitle").value = title;
      this._setUnsaved(true);
    }
  },
  _setUnsaved: function (unsaved) {
    this._unsaved = unsaved;
    this.setTitle(this._getVectorImageTitle() + (unsaved ? ' *' : ''));
  },
  _onContentLoad: function (event) {
    this._editorContainer = this.getNamedElement("editorContainer");
    
    var width = this.getContentElement().getWidth();
    var height = this.getContentElement().getHeight();
    
    this._editorFrame = new Element("iframe", {
      width: width + 'px',
      height: height + 'px',
      src: CONTEXTPATH + '/resources/scripts/svg-edit/svg-editor.html'
    });
    
    Event.observe(this._editorFrame, "load", this._editorFrameLoadListener);
    
    this._editorContainer.appendChild(this._editorFrame);
    
    this._setUnsaved(!(this._getVectorImageId() > 0));
  }, 
  _onEditorFrameLoad: function (event) {
    this._svgEmbeddedEdit = new embedded_svg_edit(this._editorFrame);
    var svgDataElement = this.getNamedElement("data");
    var svgData = svgDataElement.value;
    if (svgData)
      this._svgEmbeddedEdit.setSvgString(svgData);
    
    svgDataElement.remove();
    
    var frameDocument;
    frameDocument = this._editorFrame.contentDocument;
    if (!frameDocument) {
      frameDocument = frame.contentWindow.document;
    }
    
    var injectStyle = frameDocument.createElement('link');
    injectStyle.setAttribute("type", "text/css");
    injectStyle.setAttribute("rel", "stylesheet");
    injectStyle.setAttribute("href", THEMEPATH + '/css/svgeditor_embedded.css');
    frameDocument.getElementsByTagName('head')[0].appendChild(injectStyle);
    
    var editorPanel = frameDocument.getElementById('editor_panel');
    var saveButton = frameDocument.createElement('div');
    saveButton.setAttribute('class', 'tool_button');
    saveButton.setAttribute('id', 'save_button');
    saveButton.setAttribute('title', 'TODO: Tool button' );
    editorPanel.insertBefore(saveButton, frameDocument.getElementById('tool_wireframe'));
    
    var _this = this;
    saveButton.onclick = function (event) {
      _this._onSaveButtonClick(event);
    };
  },
  _onSaveButtonClick: function (event) {
    var _this = this;
    this._svgEmbeddedEdit.getSvgString()(function(data, error) {
      if (error) {
        getNotificationQueue().addNotification(new NotificationMessage({
          text: error,
          className: "errorMessage"
        }));
      } else {
        _this._save(data);
      }
    });
  },
  _save: function (data) {
    var _this = this;
    if (this._getVectorImageId() === 'NEW') {
      this.startLoading();
      API.post(CONTEXTPATH + '/v1/materials/vectorImages/createVectorImage', {
        parameters: {
          parentFolderId: this._getParentFolderId(),
          title: this._getVectorImageTitle(),
          data: data
        },
        onComplete: function () {
          _this.stopLoading();
        },
        onSuccess: function (jsonResponse) {
          _this._setVectorImageId(jsonResponse.response.id);
          _this._setUnsaved(false);
          
          getNotificationQueue().addNotification(new NotificationMessage({
            text: getLocale().getText("forge.editVectorImage.savedMessage"),
            className: "infoMessage",
            duration: 3000
          }));
        }
      });
    } else {
      this.startLoading();
      API.put(CONTEXTPATH + '/v1/materials/vectorImages/' + this._getVectorImageId() + '/updateVectorImage', {
        parameters: {
          title: this._getVectorImageTitle(),
          data: data
        },
        onComplete: function () {
          _this.stopLoading();
        },
        onSuccess: function (jsonResponse) {
          _this._setUnsaved(false);
          
          getNotificationQueue().addNotification(new NotificationMessage({
            text: getLocale().getText("forge.editVectorImage.savedMessage"),
            className: "infoMessage",
            duration: 3000
          }));
        }
      });
    }
  }
});