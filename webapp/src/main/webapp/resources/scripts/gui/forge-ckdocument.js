(function() {
  'use strict';
  
  $(document).ready(function () {
    var PLUGIN_DIR = CONTEXTPATH + '/forge/ckplugins';
    
    CKEDITOR.plugins.addExternal('change', PLUGIN_DIR + '/change/');
    CKEDITOR.plugins.addExternal('coops', PLUGIN_DIR + '/coops/');
    CKEDITOR.plugins.addExternal('coops-rest', PLUGIN_DIR + '/coops-rest/');
    CKEDITOR.plugins.addExternal('coops-dmp', PLUGIN_DIR + '/coops-dmp/');
    CKEDITOR.plugins.addExternal('coops-ws', PLUGIN_DIR + '/coops-ws/');
    CKEDITOR.plugins.addExternal('mrmonkey', PLUGIN_DIR + '/mrmonkey/');
  
    var editor = CKEDITOR.replace($('.forge-ckdocument-editor').attr('name'), { 
      skin: 'moono',
      extraPlugins: 'coops,coops-rest,coops-ws,coops-dmp,mrmonkey',
      readOnly: true,
      height: 500,
      coops: {
        serverUrl: COOPS_SERVER_URL,
        websocket: {
          cursorsVisible: true,
          cursorAlpha: 0.9,
          cursorBlinks: true,
          cursorBlinkInterval: 1.2
        }
      }
    }, 'Content loading...');
    
    editor.on("CoOPS:ContentDirty", function (event) {
      $('.forge-ckdocument-editor-status').html('Unsaved');
    });
    
    editor.on("CoOPS:ContentPatch", function (event) {
      $('.forge-ckdocument-editor-status').html('Saving...');
    });
    
    editor.on("CoOPS:PatchAccepted", function (event) {
      $('.forge-ckdocument-editor-status').html('Saved');
    });
  });
  
}).call(this);