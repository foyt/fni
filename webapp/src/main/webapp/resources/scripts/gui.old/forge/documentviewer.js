DocumentViewer = Class.create(ForgeWorkspaceWindow, {
  initialize: function ($super, options) {
    $super(Object.extend({
      title: options.materialTitle,
      className: 'forgeViewDocumentDialog',
      contentUrl: CONTEXTPATH + '/forge/viewdocument.page?materialId=' + options.materialId
    }, options));
  }
});