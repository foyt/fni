(function() {
  'use strict';
  
  $(document).ready(function () {
    var PLUGIN_DIR = CONTEXTPATH + '/uresources/ckplugins';

    CKEDITOR.plugins.addExternal('ajax', PLUGIN_DIR + '/ajax/');
    CKEDITOR.plugins.addExternal('xml', PLUGIN_DIR + '/xml/');
    CKEDITOR.plugins.addExternal('autogrow', PLUGIN_DIR + '/autogrow/');
    CKEDITOR.plugins.addExternal('change', PLUGIN_DIR + '/change/');
    CKEDITOR.plugins.addExternal('coops', PLUGIN_DIR + '/coops/');
    CKEDITOR.plugins.addExternal('coops-connector', PLUGIN_DIR + '/coops-connector/');
    CKEDITOR.plugins.addExternal('coops-dmp', PLUGIN_DIR + '/coops-dmp/');
    CKEDITOR.plugins.addExternal('coops-cursors', PLUGIN_DIR + '/coops-cursors/');
    CKEDITOR.plugins.addExternal('coops-sessionevents', PLUGIN_DIR + '/coops-sessionevents/');
    CKEDITOR.plugins.addExternal('fnidynlist', PLUGIN_DIR + '/fnidynlist/');
    CKEDITOR.plugins.addExternal('fnigenericbrowser', PLUGIN_DIR + '/fnigenericbrowser/');
    
    var editor = CKEDITOR.replace($('.forge-ckdocument-editor').attr('name'), { 
      skin: 'moono',
      language: LOCALE,
      extraPlugins: 'coops,coops-connector,coops-dmp,coops-sessionevents,autogrow,fnigenericbrowser',
      autoGrow_onStartup: true,
      readOnly: true,
      height: 500,
      contentsCss: ['//cdnjs.cloudflare.com/ajax/libs/ckeditor/4.3.2/contents.css', CONTEXTPATH + '/uresources/forge-ckeditor-embedded.css' ],
      coops: {
        serverUrl: COOPS_SERVER_URL,
        readOnly: COOPS_READONLY
      },
      fniGenericBrowser:{
        enabledInDialogs: ['image', 'link'],
        connectorUrl: CONTEXTPATH + '/forge/ckbrowserconnector/'
      },
      toolbar: [
        { name: 'document', items : [ 'Templates' ] },
        { name: 'clipboard', items : [ 'Cut','Copy','Paste','PasteText','PasteFromWord','-','Undo','Redo' ] },
        { name: 'editing', items : [ 'Find','Replace','-','SelectAll','-', 'Scayt' ] },
        { name: 'paragraph', items : [ 'NumberedList','BulletedList','-','Outdent','Indent','-','Blockquote','-','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock','-','BidiLtr','BidiRtl' ] },
        { name: 'links', items : [ 'Link','Unlink','Anchor' ] },
        { name: 'insert', items : [ 'Image', 'Table','HorizontalRule', 'SpecialChar','PageBreak' ] },
        { name: 'styles', items : [ 'Styles','Format','Font','FontSize' ] },
        { name: 'basicstyles', items : [ 'Bold','Italic','Underline','Strike','Subscript','Superscript','-','RemoveFormat' ] },
        { name: 'colors', items : [ 'TextColor','BGColor' ] },
        { name: 'tools', items : [ 'ShowBlocks', 'Maximize' ] }
      ]
    });
    
    /* CoOps status messages */
    
    editor.on("CoOPS:SessionStart", function (event) {
      $('.forge-ckdocument-editor-status>span').css('display', 'none');
      $('.forge-ckdocument-editor-status-loaded').css('display', 'block');
    });
    
    editor.on("CoOPS:ContentDirty", function (event) {
      $('.forge-ckdocument-editor-status>span').css('display', 'none');
      $('.forge-ckdocument-editor-status-unsaved').css('display', 'block');
    });
    
    editor.on("CoOPS:PatchSent", function (event) {
      $('.forge-ckdocument-editor-status>span').css('display', 'none');
      $('.forge-ckdocument-editor-status-saving').css('display', 'block');
    });
    
    editor.on("CoOPS:PatchAccepted", function (event) {
      $('.forge-ckdocument-editor-status>span').css('display', 'none');
      $('.forge-ckdocument-editor-status-saved').css('display', 'block');
    });

    editor.on("CoOPS:ConnectionLost", function (event) {
      $('.notifications').notifications('notification', 'load', event.data.message).addClass('connection-lost-notification');
    });

    editor.on("CoOPS:Reconnect", function (event) {
      $('.notifications').find('.connection-lost-notification').notification("hide");
    });

    editor.on("CoOPS:CollaboratorJoined", function (event) {
      $('.collaborators').collaborators("addCollaborator", event.data.sessionId, event.data.displayName||'Anonymous', event.data.email||(event.data.sessionId + '@no.invalid'));
    });

    editor.on("CoOPS:CollaboratorLeft", function (event) {
      $('.collaborators').collaborators("removeCollaborator", event.data.sessionId);
    });

    // CoOPS Errors
    
    editor.on("CoOPS:Error", function (event) {
      $('.notifications').find('.connection-lost-notification').notification("hide");
      
      switch (event.data.severity) {
        case 'CRITICAL':
        case 'SEVERE':
          $('.notifications').notifications('notification', 'error', event.data.message);
        break;
        case 'WARNING':
          $('.notifications').notifications('notification', 'warning', event.data.message);
        break;
        default:
          $('.notifications').notifications('notification', 'info', event.data.message);
        break;
      }
    });
    
    /* CoOps title */
    
    $('.forge-ckdocument-title').change(function (event) {
      var oldValue = $(this).parent().data('old-value');
      var value = $(this).val();
      if (value) {
        $(this).parent().data('old-value', value);
        
        editor.fire("propertiesChange", {
          properties : [{
            property: 'title',
            oldValue: oldValue,
            currentValue: value
          }]
        });
      }
    });
    
    editor.on("CoOPS:PatchReceived", function (event) {
      var properties = event.data.properties;
      if (properties) {
        $.each(properties, function (key, value) {
          if (key === 'title') {
            $('.forge-ckdocument-title').val(value);
          }
        });
      }
    });

    $('.collaborators').collaborators();
  });
  
}).call(this);