ImageViewer = Class.create(ForgeWorkspaceWindow, {
  initialize: function ($super, options) {
    $super(Object.extend({
      title: options.materialTitle,
      className: 'forgeViewImageDialog',
      contentUrl: CONTEXTPATH + '/forge/viewimage.page?materialId=' + options.materialId
    }, options));
  }
});