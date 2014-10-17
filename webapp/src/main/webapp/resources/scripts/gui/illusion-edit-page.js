(function() {
  'use strict';
  
  $(document).ready(function () {
    var PLUGIN_DIR = CONTEXTPATH + '/uresources/ckplugins';
    
    var editor = $('.illusion-page-editor').coOpsCK({
      externalPlugins: {
        'ajax': PLUGIN_DIR + '/ajax/',
        'xml': PLUGIN_DIR + '/xml/',
        'autogrow': PLUGIN_DIR + '/autogrow/',
        'change': PLUGIN_DIR + '/change/',
        'coops': PLUGIN_DIR + '/coops/',
        'coops-connector': PLUGIN_DIR + '/coops-connector/',
        'coops-dmp': PLUGIN_DIR + '/coops-dmp/',
        'coops-cursors': PLUGIN_DIR + '/coops-cursors/',
        'coops-sessionevents': PLUGIN_DIR + '/coops-sessionevents/',
        'fnidynlist': PLUGIN_DIR + '/fnidynlist/',
        'fnigenericbrowser': PLUGIN_DIR + '/fnigenericbrowser/'
      },
      extraPlugins: 'coops,coops-connector,coops-dmp,coops-sessionevents,autogrow,fnigenericbrowser',
      serverUrl: COOPS_SERVER_URL,
      editorOptions: {
        autoGrowOnStartup: true,
        skin: 'moono',
        height: 500,
        language: LOCALE,
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
      }
    });
    
    editor.on('statusChange', function (event, data) {
      $('.illusion-edit-page-editor-status').html($('.illusion-edit-page-editor-status').data(data.status));
    });
    
    $('.illusion-edit-page-editor-status').html($('.illusion-edit-page-editor-status').data('loading'));
    
    editor.on("error", function (event, data) {
      $('.notifications').find('.connection-lost-notification').notification("hide");
      
      switch (data.severity) {
        case 'CRITICAL':
        case 'SEVERE':
          $('.notifications').notifications('notification', 'error', data.message);
        break;
        case 'WARNING':
          $('.notifications').notifications('notification', 'warning', data.message);
        break;
        default:
          $('.notifications').notifications('notification', 'info', data.message);
        break;
      }
    });

    editor.on("collaboratorJoined", function (event, data) {
      $('.collaborators').collaborators("addCollaborator", data.sessionId, data.displayName, data.email);
    });

    editor.on("collaboratorLeft", function (event, data) {
      $('.collaborators').collaborators("removeCollaborator", data.sessionId);
    });
    
    editor.on("patchReceived", function (event, data) {
      $.each(data.properties, function (key, value) {
        if (key === 'title') {
          $('.illusion-edit-page-title').val(value);
        }
      });
    });
    
    $('.illusion-edit-page-title').change(function (event) {
      var oldValue = $(this).parent().data('old-value');
      var value = $(this).val();
      $(this).parent().data('old-value', value);
      $('.illusion-page-editor').coOpsCK("changeProperty", 'title', oldValue, value);
    });

    $('.collaborators').collaborators();
  });
  
}).call(this);