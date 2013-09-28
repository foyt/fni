VectorImageViewer = Class.create(ForgeWorkspaceWindow, {
  initialize: function ($super, options) {
    $super(Object.extend({
      title: options.materialTitle,
      className: 'forgeViewVectorImageDialog',
      contentUrl: CONTEXTPATH + '/forge/viewvectorimage.page?materialId=' + options.materialId
    }, options));
  }
});