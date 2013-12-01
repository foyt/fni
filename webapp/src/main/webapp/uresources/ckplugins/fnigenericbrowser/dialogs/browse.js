(function() {
  
  ServerConnection = {
    doGet : function(url, onSuccess) {
      var response = CKEDITOR.ajax.load(url);
      if (!response) {
        alert('Failed to load files from server');
      }
      
      var responseJson = eval("(" + response + ")");
      onSuccess(responseJson);
    },
    listMaterials: function (editor, dialogName, parent, callback) {
      var connectorUrl = editor.config.fniGenericBrowser.connectorUrl;
      var url = connectorUrl + (connectorUrl.indexOf("?") > 0 ? '&' : '?') + "dialog=" + dialogName +  '&action=LIST_MATERIALS';
      if (parent) {
        url += "&parent=" + parent;
      }
      
      this.doGet(url, function (responseJson) {
        callback(responseJson);
      }, function () {
        alert('Could not execute listMaterials method');
      });
    }  
  };
  
  function loadMaterials(dialog, parent) {
    var rootDialog = dialog._.editor._fniGenericBrowserDialog;
    var dialogName = rootDialog._.name;
    
    var materialsList = dialog.getContentElement("materials", "list");
    materialsList.removeRows();

    materialsList.startLoading();
    ServerConnection.listMaterials(dialog._.editor, dialogName, parent, function (materials){
      var rows = new Array();
      
      for (var i = 0, l = materials.length; i < l; i++) {
        var id = materials[i].id;
        var type = materials[i].type;
        var path = materials[i].path;
        var icon = '<span class="fnigenericbrowser-icon fnigenericbrowser-icon-' + materials[i].icon + '"/>';
        var name = 
          '<span class="fnigenericbrowser-info">' + 
            '<span class="fnigenericbrowser-name">' + materials[i].name + '</span>' +
            '<span class="fnigenericbrowser-modified">' + materials[i].date + '</span>' +
            '<span class="fnigenericbrowser-creator">' + materials[i].creator + '</span>' +
          '</span>';

        rows.push( [ icon, name, type, path, id || '' ]);
      }
      
      materialsList.addRows(rows);
      materialsList.stopLoading();
    });
  };

  CKEDITOR.dialog.add("fnigenericbrowser_browse", function(editor) {
    var lang = editor.lang.fnigenericbrowser.browseDialog;

    return {
      title : lang.title,
      minWidth : 400,
      minHeight : 440,
      className: "fnigenericbrowser_materials",
      contents : [ {
        id : 'materials',
        label : lang.materialsTabLabel,
        expand : false,
        padding : 0,
        elements : [ {
          type : 'dynList',
          id : 'list',
          label : lang.materialsListTitle,
          contentHeight : 400,
          contentWidth: 400,
          onEmptyText : lang.materialsEmptyFolder,
          useHoverEffect : true,
          rowStyle : "cursor: pointer",
          onRowClick : function(event) {
            var dialog = this;
            var list = dialog.getContentElement("materials", "list");
            var resourceType = list.getCellValue(event.rowId, 2);
            var resourcePath = list.getCellValue(event.rowId, 3);
            var resourceId = list.getCellValue(event.rowId, 4);

            switch (resourceType) {
              case 'ParentFolder':
              case 'Folder':
                loadMaterials(dialog, resourceId);
              break;
              default:
                var editor = dialog._.editor;
                CKEDITOR.tools.callFunction(editor._._fniGenericBrowserSetResult, {
                  value : resourcePath
                });
                dialog.hide();
              break;
            }
          },
          columns : [ {
            title : '&nbsp;',
            type : 'span'
          }, {
            title : lang.materialsListNameColumnTitle
          }, {
            type : 'hidden'
          }, {
            type : 'hidden'
          }, {
            type : 'hidden'
          } ]
        } ]
      }],
      buttons : [ CKEDITOR.dialog.cancelButton ],
      onShow : function(event) {
        var dialog = event.sender;
        loadMaterials(dialog, null);
      }
    };
  });
})();