IndexViewController = Class.create({
  initialize : function() {
    /**
    this._sheet = new Sheet({
      columns: 2,
      rows: 10
    });
    **/
  },
  destroy: function () {
  },
  setup: function () {
    /**
    var sheetBuilder = new SheetBuilder(this._sheet);
    sheetBuilder.setup();
    
    var sheetContainer = new Element("div", {
      className: "sheetContainer"    
    });
    
    $('illusionWorkspaceContent').appendChild(sheetContainer);
    
    this._sheet.setup(sheetContainer);
    
    **/
    
    this._loadMaps();
  },
  _loadMaps: function () {
    API.get(CONTEXTPATH + '/v1/map/listSessionMaps?illusionSessionId=' + ILLUSION_SESSION_ID, {
      onSuccess: function (jsonResponse) {
        var response = jsonResponse.response;
        for (var i = 0, l = response.length; i < l; i++) {
          var mapMeta = response[i];
          var map = new Map({
        	  container: $('illusionWorkspaceContent'),
            id: mapMeta.id,
            millimetersPerPoint: mapMeta.millimetersPerPoint
          });
          
          for (var layerIndex = 0, layersLength = mapMeta.layers.length; layerIndex < layersLength; layerIndex++) {
            var layer = mapMeta.layers[layerIndex];
            switch (layer.type) {
              case "VECTOR_IMAGE":
                map.addVectorImageLayer(layer.id, layer.name||'Untitled', layer.vectorImageId);
              break;
              case "TOKEN":
                map.addTokenLayer(layer.id, layer.name||'Untitled');
              break;
            }
          }
        }
      }
    });
  }
});

var viewController = new IndexViewController();

Event.observe(document, "dom:loaded", function (event) {
  viewController.setup();
});

Event.observe(window, "beforeunload", function (event) {
  viewController.destroy();
});
