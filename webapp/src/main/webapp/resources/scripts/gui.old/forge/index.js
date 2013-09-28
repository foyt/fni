IndexViewController = Class.create({
  initialize : function() {
    this._createDocumentMenuItemClickListener = this._onCreateDocumentMenuItemClick.bindAsEventListener(this);
    this._createVectorImageMenuItemClickListener = this._onCreateVectorImageMenuItemClick.bindAsEventListener(this);
    this._uploadMaterialsMenuItemClickListener = this._onUploadMaterialsMenuItemClick.bindAsEventListener(this);
    this._importGoogleDocumentMenuItemClickListener = this._onImportGoogleDocumentMenuItemClick.bindAsEventListener(this);
  },
  destroy : function() {
    Event.stopObserving($('forgeCreateDocumentMenuItem'), "click", this._createDocumentMenuItemClickListener);
    Event.stopObserving($('forgeCreateVectorImageMenuItem'), "click", this._createVectorImageMenuItemClickListener);
    Event.stopObserving($('forgeUploadMaterialsMenuItem'), "click", this._uploadMaterialsMenuItemClickListener);
    Event.stopObserving($('forgeImportGoogleDocumentMenuItem'), "click", this._importGoogleDocumentMenuItemClickListener);
  },
  setup : function() {
    Event.observe($('forgeCreateDocumentMenuItem'), "click", this._createDocumentMenuItemClickListener);
    Event.observe($('forgeCreateVectorImageMenuItem'), "click", this._createVectorImageMenuItemClickListener);
    Event.observe($('forgeUploadMaterialsMenuItem'), "click", this._uploadMaterialsMenuItemClickListener);
    Event.observe($('forgeImportGoogleDocumentMenuItem'), "click", this._importGoogleDocumentMenuItemClickListener);
    
    initializeWorkspaceController({
      dockingBarContainer: $('forgeWorkspaceWindowDockingBar'),
      windowContainer: $('forgeWorkspaceContent')
    });

    var action = getJsVariable("action");
    if (action) {
      var actionParameters = getJsVariable("actionParameters");
      this._handleAction(action, actionParameters);
    }
  },
  _handleAction: function (action, actionParameters) {
    var parameters = new Hash();
    
    if (actionParameters) {
      var parametersArray = actionParameters.split(';');
      for (var i = 0, l = parametersArray.length; i < l; i++) {
        var parameterArray = parametersArray[i].split(':');
        parameters.set(parameterArray[0], parameterArray[1]);
      }
    }
    
    switch (action) {
      case 'editmaterial':
        this._handleEditMaterialAction(parameters);
      break;
      case 'viewmaterial':
        this._handleViewMaterialAction(parameters);
      break;
      case 'importgoogledocuments':
        this._handleImportGoogleDocumentsAction(parameters);
      break;
    }
  },
  _handleEditMaterialAction: function (parameters) {
    getWorkspaceController().editMaterial(parameters.get('materialId'), parameters.get('materialType'), parameters.get('materialTitle'));
  },
  _handleViewMaterialAction: function (parameters) {
    getWorkspaceController().viewMaterial(parameters.get('materialId'), parameters.get('materialType'), parameters.get('materialArchetype'), parameters.get('materialTitle'), parameters.get('materialPath'));
  },
  _handleImportGoogleDocumentsAction: function (parameters) {
    this._openImportGoogleDocumentsDialog();
  },
  _openImportGoogleDocumentsDialog: function () {
    var dialog = new ImportGoogleDocuments();
    dialog.open();
  },
  _openUploadMaterialsDialog: function () {
    var dialog = new UploadMaterialsDialogController();
    dialog.open();
  },
  _onCreateDocumentMenuItemClick: function (event) {
    getWorkspaceController().createMaterial('DOCUMENT');
  },
  _onCreateVectorImageMenuItemClick: function (event) {
    getWorkspaceController().createMaterial('VECTOR_IMAGE');
  },
  _onUploadMaterialsMenuItemClick: function (event) {
    Event.stop(event);
    this._openUploadMaterialsDialog();
  }, 
  _onImportGoogleDocumentMenuItemClick: function (event) {
    Event.stop(event);
    this._openImportGoogleDocumentsDialog();
  }
});