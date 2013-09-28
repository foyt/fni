DocumentEditor = Class.create(ForgeWorkspaceWindow, {
  initialize: function ($super, options) {
    $super(Object.extend({
      title: options.documentTitle,
      contentUrl: CONTEXTPATH + '/forge/editdocument.page?documentId=' + options.documentId,
      className: 'forgeEditDocumentDialog'
    }, options));

    this.addListener("contentLoad", this, this._onContentLoad);
    this.addListener("editorInitialized", this, this._onEditorInitialized);
    this.addListener("resize", this, this._onResize);
    this.addListener("minimize", this, this._onMinimize);
    this.addListener("undock", this, this._onUndock);
    this.addListener("maximize", this, this._onMaximize, 1);
    this.addListener("afterMaximize", this, this._onAfterMaximize); 
    this.addListener("restoreMaximized", this, this._onRestoryMaximized, 1);
    this.addListener("afterRestoreMaximized", this, this._onAfterRestoryMaximized);
  },
  destroy: function ($super) {
    this._destroyEditor();
    $super();
  },
  _getDocumentId: function () {
    return this.getNamedElement("documentId").value;
  },
  _setDocumentId: function (documentId) {
    this.getNamedElement("documentId").value = documentId;
  }, 
  _getRevision: function () {
    return this.getNamedElement("revision").value;
  }, 
  _getDocumentTitle: function () {
    return this.getNamedElement("documentTitle").value;
  },
  _setDocumentTitle: function (title) {
    if (this._getDocumentTitle() != title) {
      this.getNamedElement("documentTitle").value = title;
      this._setUnsaved(true);
    }
  },
  _getParentFolderId: function () {
    return this.getNamedElement("parentFolderId").value;
  },
  _resizeEditor: function () {
    if (this._ckInstance) {
      var height = this.getContentElement().getHeight();
      this._ckInstance.resize('', height - 2, false, false);
    }
  },
  _setUnsaved: function (unsaved) {
    this._unsaved = unsaved;
    this.setTitle(this._getDocumentTitle() + (unsaved ? ' *' : ''));
  },
  _createEditor: function () {
    // 110 is a estimate of toolbar height. Real height is set after the editor is fully loaded
    var height = this.getContentElement().getHeight();
    var contentElement = this.getNamedElement("content");
    contentElement.id = this.getComponentId();
    
    var _this = this;
    this._ckInstance = CKEDITOR.replace(contentElement, {
      fullPage : true,
      toolbar: 'ForgeDocument',
      height: height - 410,
      autoUpdateElement: false,
      language: getLocale().getLanguage(),
      extraPlugins: 'docprops,fnimods,fnigenericbrowser,ckc',
      fniGenericBrowser:{
        enabledInDialogs: ['image', 'link'],
        connectorUrl: CONTEXTPATH + '/forge/ckeditorconnector.json'
      },
      fniMods:  {
        dialogs: {
          'docProps': {
            'tabs': {
              'general': {
                'elements': {
                  'index:2': {
                    'style': 'display: none'
                  },
                  'index:3': {
                    'style': 'display: none'
                  },
                  'index:4': {
                    'style': 'display: none'
                  }
                }
              },
              'meta': {
                'elements': {
                  'metaAuthor': {
                    'style': 'display: none'
                  },
                  'metaCopyright': {
                    'style': 'display: none'
                  }
                }
              }
            }
          }
        }
      },
      ckc: {
        documentId: this._getDocumentId(),
        originalRevision: this._getRevision(),
        updateInterval: 500,
        unsavedWarningInterval: 30000,
        connectorUrl: CONTEXTPATH + '/ckc/',
        messageHandler: function (severity, message) {
          switch (severity) {
            case 'INFO':
              getNotificationQueue().addNotification(new NotificationMessage({
                text: message,
                className: "infoMessage",
                duration: 3000
              }));
            break;
            case 'WARNING':
              getNotificationQueue().addNotification(new NotificationMessage({
                text: message,
                className: "warningMessage",
                duration: 3000
              }));
            break;
            default:
              getNotificationQueue().addNotification(new NotificationMessage({
                text: message,
                className: "errorMessage"
              }));
            break;
          }
        }
      }
    });
    
    this._ckInstance.on("ckcPropertiesChanged", function (event) {
      for (var i = 0, l = event.data.changedProperties.length; i < l; i++) {
        if (event.data.changedProperties[i].name == "title") {
          _this._setDocumentTitle(event.data.changedProperties[i].value);
          _this._setUnsaved(false);
        }
      }
    });
    
    this._ckInstance.on("ckcDocumentCreate", function (event) {
      _this.startLoading();
      
      var properties = event.data.properties;
      var title = properties['title'];

      if (!title) {
        _this.stopLoading();

        getNotificationQueue().addNotification(new NotificationMessage({
          text: getLocale().getText('forge.editDocument.documentRequiresTitle'),
          className: "warningMessage",
          duration: 3000
        }));

        var editor = event.editor;
        editor.openDialog("docProps");

        event.cancel();
      }
    });
    
    this._ckInstance.on("ckcDocumentCreated", function (event) {
      _this.stopLoading();
      
      var properties = event.data.properties;
      var title = properties['title'];
      
      _this._setDocumentTitle(title);
      _this._setUnsaved(false);
      _this.changeComponentId('material-editor-window-' + event.data.documentId);
    });
    
    this._ckInstance.on("ckcPropertiesChange", function (event) {
      var properties = event.data.properties;
      var title = properties['title'];

      if (!title) {
        getNotificationQueue().addNotification(new NotificationMessage({
          text: getLocale().getText('forge.editDocument.documentRequiresTitle'),
          className: "warningMessage",
          duration: 3000
        }));
        
        var editor = event.editor;
        editor.openDialog("docProps");
        
        event.cancel();
      }
    });
  },
  _destroyEditor: function () {
    this._ckInstance.destroy();
    this._ckInstance = null;
  },
  _onContentLoad: function (event) {
    this._createEditor();
    this._setUnsaved(!(this._getDocumentId() > 0));
  },
  _onEditorInitialized: function (event) {
    this._resizeEditor();
  },
  _onResize: function (event) {
    this._resizeEditor();
  },
  _onMinimize: function (event) {
    this._destroyEditor();
  },
  _onUndock: function (event) {
    this._createEditor();
  }, 
  _onMaximize: function (event) {
    this._destroyEditor();
  },
  _onAfterMaximize: function (event) {
    this._createEditor();
  }, 
  _onRestoryMaximized: function (event) {
    this._destroyEditor();
  },
  _onAfterRestoryMaximized: function (event) {
    this._createEditor();
  }
});

CKEDITOR.on('instanceReady', function(evt) {
  var editor = evt.editor;
  var component = getComponentById(editor.name);
  if (component) {
    component.fire("editorInitialized", { });
  }
}, null, null, 1);