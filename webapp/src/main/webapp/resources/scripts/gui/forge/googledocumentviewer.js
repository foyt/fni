GoogleDocumentViewer = Class.create(ForgeWorkspaceWindow, {
  initialize: function ($super, options) {

    var className = '';
    switch (options.materialArchetype) {
      case 'DOCUMENT':
        className = 'googleDocumentDocumentViewer';
      break;
      case 'DRAWING':
        className = 'googleDocumentDrawingViewer';
      break;
      case 'SPREADSHEET':
        className = 'googleDocumentSpreadsheetViewer';
      break;
      case 'PRESENTATION':
        className = 'googleDocumentPresentationViewer';
      break;
    }
    
    $super(Object.extend({
      title: options.materialTitle,
      className: className,
      contentUrl: CONTEXTPATH + '/forge/viewgoogledocument.page?materialId=' + options.materialId
    }, options));
  }
});