ImportGoogleDocuments = Class.create(ModalDialogController, {
  initialize: function ($super, options) {
    $super(Object.extend({
      title: getLocale().getText('forge.importGoogleDocuments.importDialogTitle'),
      iconURL: THEMEPATH + '/gfx/icons/22x22/mimetypes/google-document.png',
      contentUrl: CONTEXTPATH + '/forge/importgoogledocumentsdialog.page',
      useIframe: true,
      width: 600,
      height: 600
    }, options));
  }
});