MapTools = Class.create({
  initialize : function() {
    this.domNode = new Element("div", {
      className: "mapTools"
    });
    
    this._toolsContainer = new Element("div", {
      className: "mapLayerToolsContainer"
    }); 
    
    this.domNode.appendChild(this._toolsContainer);

    this._toolButtonClickListener = this._onToolButtonClick.bindAsEventListener(this);
    this._colorButtonClickListener = this._onColorButtonClick.bindAsEventListener(this);
    
    this._activeToolName = null;
  },
  setup: function () {
    document.body.appendChild(this.domNode);

    this._basicTools = this.createToolGroup();
    this._paintToolsToolGroup = this.createToolGroup();
    this._moveButton = this.createToolButton(this._basicTools, "move");
    this._colorsToolGroup = this.createToolGroup();
    this._fillColorButton = this.createColorButton(this._colorsToolGroup, "fill");

    // TODO: Where should this group be made?
    this._brushToolButton = this.getMapController().createToolButton(this.getMapController()._paintToolsToolGroup, "brush"); 

    // TODO: Where should this group be made?
    this._freeHandToolButton = this.getMapController().createToolButton(this.getMapController()._paintToolsToolGroup, "freeHand"); 
  },
  getActiveToolName: function () {
    return this._activeToolName;
  },
  getToolsContainer: function () {
    return this._toolsContainer;
  },
  createToolGroup: function () {
    toolButton = new Element("div", {
      className: "mapLayerToolGroup"
    });
    
    this.getToolsContainer().appendChild(toolButton);
    
    return toolButton;
  },
  createToolButton: function (toolGroup, name) {
    var buttonClass = "mapLayerToolButton" + name.camelize().charAt(0).toUpperCase() + name.camelize().substring(1);
    var toolButton = new Element("div", {
      className: "mapLayerToolButton " + buttonClass
    });
    
    toolButton.store("name", name);
    toolButton.store("group", toolGroup);
    
    // TODO: release listener
    Event.observe(toolButton, "click", this._toolButtonClickListener);
    
    toolGroup.appendChild(toolButton);
    
    return toolButton;
  },
  createColorButton: function (toolGroup, name) {
    var buttonClass = "mapLayerColorButton" + name.camelize().charAt(0).toUpperCase() + name.camelize().substring(1);
    var colorButton = new Element("div", {
      className: "mapLayerColorButton " + buttonClass
    });
    
    colorButton.store("name", name);
    colorButton.store("group", toolGroup);
    
    // TODO: release listener
    Event.observe(colorButton, "click", this._colorButtonClickListener);
    
    toolGroup.appendChild(colorButton);
    
    return colorButton;
  },
  setActiveButton: function (group, name) {
    var buttonClass = "mapLayerToolButton" + name.camelize().charAt(0).toUpperCase() + name.camelize().substring(1);
    this.getToolsContainer().select('.mapLayerToolButtonDown').each(function (button) {
      button.removeClassName('mapLayerToolButtonDown');
    });
    
    this.getToolsContainer().down('.' + buttonClass).addClassName("mapLayerToolButtonDown");
    
    this._activeToolName = name;
    
    switch (name) {
      case 'move':
        this.setCursor("move");
      break;
      case 'brush':
        this.setCursor("draw");
      break;
      case 'freeHand':
        this.setCursor("freehand");
      break;
    }
  },
  isButtonActive: function (name) {
    return this._activeToolName == name;
  },
  _onToolButtonClick: function (event) {
    var buttonElement = Event.element(event);
    var groupElement = buttonElement.retrieve("group");
    var buttonName = buttonElement.retrieve("name");
      
    this.setActiveButton(groupElement, buttonName);
  },
  _onColorButtonClick: function (event) {
    var buttonElement = Event.element(event);
    var groupElement = buttonElement.retrieve("group");
    var buttonName = buttonElement.retrieve("name");

    var dialogController = new MapPaintPickerDialogController();
    dialogController.addListener("beforeClose", this, this._beforePaintPickerDialogCloseListener);
    dialogController.open();
  }
});

Object.extend(MapController.prototype, fni.events.FNIEventSupport);

MapLayerController = Class.create({
  initialize: function (mapController) {
    this._mapController = mapController;
    this._layerElement = new Element("div", {
      className: "mapLayer"
    });
  },
  setup: function (container) {
    this._container = container;
    container.appendChild(this._layerElement);
  },
  render: function () {
    
  },
  moveBy: function (x, y) {
    
  },
  getLayerElement: function () {
    return this._layerElement;
  },
  getMapController: function () {
    return this._mapController;
  },
  isButtonActive: function (name) {
    return this.getMapController().isButtonActive(name);
  }
});

MapCanvasLayerController = Class.create(MapLayerController, {
  initialize: function ($super, mapController) {
    $super(mapController);
    
    
  },
  setup: function ($super, container) {
    $super(container);
      
//    this.getLayerElement().appendChild(this._offscreenCanvas);
    
    // TODO: Release listeners
    
    
  },
  render: function ($super) {
    
  },
  moveBy: function ($super, x, y) {
    
  }
});

MapDrawingLayerController = Class.create(MapCanvasLayerController, {
  initialize: function ($super, mapController) {
    $super(mapController);
    
    this._shape = 'rect';
    this._brushSize = 15;
    this._color = "rgba(200,0,0,0.6)";
    
  },
  setup: function ($super, container) {
    $super(container);

    this.getMapController().addListener("layerMouseDragStart", this, this._onLayerMouseDragStart);
    this.getMapController().addListener("layerMouseDrag", this, this._onLayerMouseDrag);
    this.getMapController().addListener("layerMouseDown", this, this._onLayerMouseDown);
    this.getMapController().addListener("layerMouseUp", this, this._onLayerMouseUp);
  },
  _onLayerMouseDragStart: function (event) {
    if (this.isButtonActive("brush")) {
      this._linePoints = new Array();
    }
  },
  _onLayerMouseDrag: function (event) {
    if (this.isButtonActive("brush")) {
      this._linePoints.push({
        x: event.canvasX,
        y: event.canvasY
      });
//      this._clearCanvas(this.getOffscreenCanvas());
      
      this._copyToScreen();
      this._drawLinePointsToCanvas(this.getScreenCanvas(), false);
    }
  },
  _onLayerMouseDown: function (event) {
    if (this.isButtonActive("brush")) {
      this._linePoints.push({
        x: event.canvasX,
        y: event.canvasY
      });

      this._copyToScreen();
      this._drawLinePointsToCanvas(this.getScreenCanvas(), false);
    }
  },
  _onLayerMouseUp: function (event) {
    if (this.isButtonActive("brush")) {
//      this._clearCanvas(this.getOffscreenCanvas());
      this._drawLinePointsToCanvas(this.getOffscreenCanvas(), true);
      this._copyToScreen();
      this._linePoints = undefined;
    }
  },
  _drawLinePointsToCanvas: function (canvas, translate) {
    if (this._linePoints) {
      var ctx = canvas.getContext("2d");
      
      ctx.strokeStyle = this._color;
      ctx.lineWidth = this._brushSize;
      ctx.lineCap = 'round';
      ctx.lineJoin = 'round';
      
      var halfBrush = this._brushSize / 2;
      
      ctx.beginPath();
      
      if (this._linePoints.length == 1) {
        var point = {
          x: this._linePoints[0].x,
          y: this._linePoints[0].y
        };

        if (translate)
          point = this._transformScreenToOffscreen(point.x, point.y);
        
        ctx.moveTo(point.x - halfBrush, point.y - halfBrush);
        ctx.lineTo(point.x - halfBrush, point.y - halfBrush);
      } else {
        for (var i = 0, l = this._linePoints.length - 1; i < l; i++) {
          var point1 = {
            x: this._linePoints[i].x,
            y: this._linePoints[i].y
          };
          var point2 = {
            x: this._linePoints[i + 1].x,
            y: this._linePoints[i + 1].y
          };

           if (translate) {
            point1 = this._transformScreenToOffscreen(point1.x, point1.y);
            point2 = this._transformScreenToOffscreen(point2.x, point2.y);
          }
          
          ctx.moveTo(point1.x - halfBrush, point1.y - halfBrush);
          ctx.lineTo(point2.x - halfBrush, point2.y - halfBrush);
        }
      }
      
      ctx.closePath();
      ctx.stroke();
      ctx.fill();
    }
  }
});

MapImageLayerController = Class.create(MapLayerController, {
  initialize: function ($super, mapController) {
    $super(mapController);
  },
  setup: function ($super, container) {
    $super(container);
  },
  render: function ($super) {
    
  }
});

MapTileLayerController = Class.create(MapCanvasLayerController, {
  initialize: function ($super, mapController) {
    $super(mapController);
    
    this._brushSize = 5;
    this._color = "rgba(200,0,200,0.6)";
  },
  setup: function ($super, container) {
    $super(container);
    
    this.getMapController().addListener("layerMouseDragStart", this, this._onLayerMouseDragStart);
    this.getMapController().addListener("layerMouseDrag", this, this._onLayerMouseDrag);
    this.getMapController().addListener("layerMouseDown", this, this._onLayerMouseDown);
    this.getMapController().addListener("layerMouseUp", this, this._onLayerMouseUp);
    
    
    /**
    this._tilesToolGroup = this.getMapController().createToolGroup();
    this._freeHandToolButton = this.getMapController().createToolButton(this._paintToolsToolGroup, "freeHand"); 
    this.getMapController().setActiveButton(this._paintToolsToolGroup, "freeHand");
    
    **/
    /**
    var _this = this;
    API.get('/v1/map/listMapTiles', {
      onSuccess: function (jsonResponse) {
        var mapTiles = jsonResponse.mapTiles;
        for (var i = 0, l = mapTiles.length; i < l; i++) {
          var id = mapTiles[i].id;
          var title = mapTiles[i].title;
          
          var tileButton = _this.getMapController().createToolButton(_this._tilesToolGroup, "MapTile-" + id); 
          tileButton.setAttribute("tileImageId", id);
          tileButton.setStyle({
            backgroundImage: 'url(' + CONTEXTPATH + "/v1/map/tile/22x22/" + id + ')'
          });
          
          Event.observe(tileButton, "click", _this._mapTileButtonClickListener);
        }
      }
    });
    **/
  },
  /**
  _getClosestTileCorner: function (x, y) {
    return {
      x: Math.floor(x / this._tileWidth) * this._tileWidth,
      y: Math.floor(y / this._tileHeight) * this._tileHeight
    };
  },
  **/
  _onLayerMouseDragStart: function (event) {
    if (this.isButtonActive("freeHand")) {
      this._linePoints = new Array();
    }
  },
  _onLayerMouseDrag: function (event) {
    if (this.isButtonActive("freeHand")) {
      this._linePoints.push({
        x: event.canvasX,
        y: event.canvasY
      });
  //    this._clearCanvas(this.getOffscreenCanvas());
      this._copyToScreen();
      this._drawLinePointsToCanvas(this.getScreenCanvas());
      
//      var data = this._tileImage.data;
    }
  },
  _onLayerMouseDown: function (event) {
    if (this.isButtonActive("freeHand")) {
      this._linePoints.push({
        x: event.canvasX,
        y: event.canvasY
      });
      this._copyToScreen();
      this._drawLinePointsToCanvas(this.getScreenCanvas());
    }
  },
  _onLayerMouseUp: function (event) {
    if (this.isButtonActive("freeHand")) {
  //    this._clearCanvas(this.getOffscreenCanvas());
      this._plotLinePoints();
      this._linePoints = undefined;
      this._copyToScreen();
    }
  },
  _drawLinePointsToCanvas: function (canvas) {
    var ctx = canvas.getContext("2d");
    
    ctx.strokeStyle = this._color;
    ctx.lineWidth = this._brushSize;
    ctx.lineCap = 'round';
    ctx.lineJoin = 'round';
    
    var halfBrush = this._brushSize / 2;
    
    ctx.beginPath();
    
    if (this._linePoints.length > 0) {
      if (this._linePoints.length == 1) {
        var point = this._linePoints[0];
        ctx.moveTo(point.x - halfBrush, point.y - halfBrush);
        ctx.lineTo(point.x - halfBrush, point.y - halfBrush);
      } else {
        for (var i = 0, l = this._linePoints.length - 1; i < l; i++) {
          var point1 = this._linePoints[i];
          var point2 = this._linePoints[i + 1];
          ctx.moveTo(point1.x - halfBrush, point1.y - halfBrush);
          ctx.lineTo(point2.x - halfBrush, point2.y - halfBrush);
        }
  
        ctx.moveTo(this._linePoints[this._linePoints.length - 1].x - halfBrush, this._linePoints[this._linePoints.length - 1].y - halfBrush);
        ctx.lineTo(this._linePoints[0].x - halfBrush, this._linePoints[0].y - halfBrush);
      }
    }
    
    ctx.closePath();
    ctx.stroke();
    ctx.fill();
  },
  _getLinePointsBoundingBox: function () {
    var x1 = Infinity;
    var y1 = Infinity;
    var x2 = 0;
    var y2 = 0;
    
    for (var i = 0, l = this._linePoints.length; i < l; i++) {
      x1 = Math.min(x1, this._linePoints[i].x);
      x2 = Math.max(x2, this._linePoints[i].x);
      y1 = Math.min(y1, this._linePoints[i].y);
      y2 = Math.max(y2, this._linePoints[i].y);
    }
    
    return {
      x: x1,
      y: y1,
      width: x2 - x1,
      height: y2 - y1
    };
  },
  _plotLinePoints: function () {
    var outline = new Array();
    
    for (var i = 0, l = this._linePoints.length - 1; i < l; i++) {
      var point1 = this._linePoints[i];
      var point2 = this._linePoints[i + 1];
      this._plotLine(outline, point1.x, point1.y, point2.x, point2.y);
    }
    
    this._plotLine(outline, this._linePoints[this._linePoints.length - 1].x, this._linePoints[this._linePoints.length - 1].y, this._linePoints[0].x, this._linePoints[0].y);
    
    var yValues = Object.keys(outline).sort(function (a,b) {
      return a - b;
    });
    
    var ctx = this.getOffscreenCanvas().getContext("2d");
    ctx.beginPath();
    
    for (var yIndex = 0, yCount = yValues.length; yIndex < yCount; yIndex++) {
      var y = parseInt(yValues[yIndex]);
      var xValues = outline[y].uniq().sort(function (a,b) {
        return a - b;
      });
      
      var xCount = xValues.length;
      if (xCount > 1) {
        var x1 = xValues[0];
        var last = x1;
        var x = 0;
        var i = 1;
        while (i < xCount) {
          x = xValues[i];

          if (x1 == null) {
            x1 = x;
          } else {
            if (last != (x - 1)) {
              for (var copyX = x1; copyX < x; copyX++) {
                var point = this._transformScreenToOffscreen(copyX, y);
                ctx.fillStyle = this.getMapController().getFillColor(point.x, point.y);
                ctx.fillRect(point.x, point.y, 1, 1); 
              }
              
              x1 = null;
            }
          }

          last = x;
          i++;
        }
      }
    };
    
    ctx.closePath();
    ctx.stroke();
    ctx.fill();
  },
  _plotLine: function (outline, x1, y1, x2, y2) {
    var dx = Math.abs(x2 - x1);
    var dy = Math.abs(y2 - y1);
    var sx = (x1 < x2) ? 1 : -1;
    var sy = (y1 < y2) ? 1 : -1;
    var err = dx - dy;
    this._plotLineDot(outline, x1, y1);
    while (!((x1 == x2) && (y1 == y2))) {
      var e2 = err << 1;
      if (e2 > -dy) {
        err -= dy;
        x1 += sx;
      }
      if (e2 < dx) {
        err += dx;
        y1 += sy;
      }

      this._plotLineDot(outline, x1, y1);
    }
  },
  _plotLineDot: function (outline, x, y) {
    if (!outline[y]) {
      outline[y] = new Array();
    }
    
    outline[y].push(x);
  }

});

MapTokenLayerController = Class.create(MapLayerController, {
  initialize: function ($super, mapController) {
    $super(mapController);
  },
  setup: function ($super, container) {
    $super(container);
  },
  render: function ($super) {
    
  }
});

MapFOWLayerController = Class.create(MapLayerController, {
  initialize: function ($super, mapController) {
    $super(mapController);
  },
  setup: function ($super, container) {
    $super(container);
  },
  render: function ($super) {
    
  }
});

MapPaintPickerDialogController = Class.create(ModalDialogController, {
  initialize: function ($super) {
    $super({
      title: 'Choose Paint', // TODO: Localize
      contentUrl: CONTEXTPATH + '/illusion/mappaintpickerdialog.page'
    });
    
    this._selectedTileId = null;
    this._selectedColor = null;
    
    this._tileClickListener = this._onTileClick.bindAsEventListener(this);
    this._uiDialogBeforeCloseListener = this._onUiDialogBeforeClose.bindAsEventListener(this);
  },
  destroy: function ($super) {
    if (this._tabControl) {
      this._tabControl.destroy();
      this._tabControl.element.purge();
    }
    $super();
  },
  setup: function ($super) {
    $super(function () {
      var dialog = this.getDialog();
      Event.observe(dialog.toElement(), "ui:dialog:before:close", this._uiDialogBeforeCloseListener)
      
      var tabsContainer = dialog.toElement().down(".namedTabsContainer");
      
      this._tabControl = new S2.UI.Tabs(tabsContainer);
      
      dialog._position();
      
      var tiles = tabsContainer.select(".mapPaintPickerTile");
      for (var i = 0, l = tiles.length; i < l; i++) {
        var tile = tiles[i];
        // TODO: Release listener
        Event.observe(tile, "click", this._tileClickListener);
      }
    }); 
  },
  getSelectedTile: function () {
    return this._selectedTileId;
  },
  getSelectedColor: function () {
    return this._selectedColor;
  },
  _setSelectedTile: function (tileId) {
    this._selectedTileId = tileId;
    this._selectedColor = null;
  },
  _onTileClick: function (event) {
    var tileElement = Event.element(event);
    var tileId = tileElement.down('input[name="tileId"]').value;
    this._setSelectedTile(tileId);
    this.getDialog().close();
  },
  _onUiDialogBeforeClose: function (event) {
    this.fire("beforeClose", {
      tileId: this.getSelectedTile(),
      color: this.getSelectedColor()
    });
  }
});