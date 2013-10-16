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
      contentsCss: ['//cdnjs.cloudflare.com/ajax/libs/ckeditor/4.2/contents.css', CONTEXTPATH + '/forge/ckcontents.css' ],
      coops: {
        serverUrl: COOPS_SERVER_URL,
        websocket: {
          cursorsVisible: true,
          cursorAlpha: 0.9,
          cursorBlinks: true,
          cursorBlinkInterval: 1.2
        }
      },
      toolbar: [
        { name: 'document', items : [ 'Templates' ] },
        { name: 'clipboard', items : [ 'Cut','Copy','Paste','PasteText','PasteFromWord','-','Undo','Redo' ] },
        { name: 'editing', items : [ 'Find','Replace','-','SelectAll','-', 'Scayt' ] },
        { name: 'paragraph', items : [ 'NumberedList','BulletedList','-','Outdent','Indent','-','Blockquote', ,'-','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock','-','BidiLtr','BidiRtl' ] },
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
    
    editor.on("CoOPS:ContentPatch", function (event) {
      $('.forge-ckdocument-editor-status>span').css('display', 'none');
      $('.forge-ckdocument-editor-status-saving').css('display', 'block');
    });
    
    editor.on("CoOPS:PatchAccepted", function (event) {
      $('.forge-ckdocument-editor-status>span').css('display', 'none');
      $('.forge-ckdocument-editor-status-saved').css('display', 'block');
    });
    
  });
  
}).call(this);