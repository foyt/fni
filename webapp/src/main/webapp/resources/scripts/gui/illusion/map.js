/**
 * Location in Map
 */
MapLocation = Class.create({
  initialize: function (x, y) {
    this._x = x;
    this._y = y;
  },
  getX: function () {
    return this._x;
  },
  getY: function () {
    return this._y;
  },
  set: function (x, y) {
    this._x = x;
    this._y = y;
  }
});

MapScale = Class.create({
  initialize: function (millimetersPerPoint) {
    this._millimetersPerPoint = millimetersPerPoint;
  },
  setMillimetersPerPoint: function (millimetersPerPoint) {
    this._millimetersPerPoint = millimetersPerPoint;
  },
  setCentimetersPerPoint: function (centimetersPerPoint) {
    this.setMillimetersPerPoint(centimetersPerPoint / 10);
  },
  setMetersPerPoint: function (metersPerPoint) {
    this.setCentimetersPerPoint(metersPerPoint / 100);
  },
  setKilometersPerPoint: function (kilometersPerPoint) {
    this.setMetersPerPoint(kilometersPerPoint / 1000);
  },
  setMegametersPerPoint: function (megametersPerPoint) {
    this.setKilometersPerPoint(megametersPerPoint / 1000);
  },
  getMillimetersPerPoint: function () {
    return this._millimetersPerPoint;
  },
  getMillimeters: function (pixels) {
    return pixels / this.getMillimetersPerPoint();
  },
  getCentimeters: function (pixels) {
    return this.getMillimeters(pixels) / 10;
  },
  getMeters: function (pixels) {
    return this.getCentimeters(pixels) / 100;
  },
  getKilometers: function (pixels) {
    return this.getMeters(pixels) / 1000;
  }
})

MapPaint = Class.create({
  
});

MapLayer = Class.create({
  initialize: function (map, options) {
    this._options = options;
    this._map = map;
  },
  render: function (screenContext) {
    
  },
  moveBy: function (x, y) {
    
  },
  getType: function () {
	  return "Unknown";  
  },
  getName: function () {
    return this._options.name;  
  },
  getMap: function () {
    return this._map; 
  },
  
  /* Events */
  
  fire: function (eventName, eventData) {
    var event = document.fire("fnimaplayer:" + eventName, eventData);
    return !event.stopped;
  },
  addListener: function (eventName, listener) {
    document.observe("fnimaplayer:" + eventName, listener);
  },
  removeListener: function (eventName, listener) {
    document.stopObserving("fnimaplayer:" + eventName, listener);
  }
});
/**
MapCanvasLayer = Class.create(MapLayer, {
  initialize: function ($super, map, options) {
    $super(map, options);
    
    this._top = 0;
    this._left = 0;
    this._width = map.getDisplayWidth();
    this._height = map.getDisplayHeight();
    this._offscreenScaleRatio = 1;
    this._zoom = 1.0;
    
    this._offscreenWidth = this._width * this._offscreenScaleRatio;
    this._offscreenHeight = this._height * this._offscreenScaleRatio;
    this._offscreenOffsetTop = this._offscreenHeight / 2;
    this._offscreenOffsetLeft = this._offscreenHeight / 2;
    
    this._screenCanvas = new Element("canvas", {
      className: "mapScreenCanvas",
      width: this._width,
      height: this._height
    });
    
    this._offscreenCanvas = new Element("canvas", {
      className: "mapOffscreenCanvas",
      width: this._offscreenWidth,
      height: this._offscreenHeight
    });
    
    this._offscreenCtx = this._offscreenCanvas.getContext('2d');
//    this._offscreenCtx.scale(this._offscreenScaleRatio, this._offscreenScaleRatio);
    
    this._mouseDown = false;

    
    this.domNode.appendChild(this._screenCanvas);
  },
  render: function ($super) {

  },
  moveBy: function ($super, x, y) {
    
  },
  getScreenCanvas: function () {
    return this._screenCanvas;
  },
  getOffscreenCanvas: function () {
    return this._offscreenCanvas;
  },
  copyToScreen: function () {
    var screenCtx = this.getScreenCanvas().getContext("2d");
    var offscreenCtx = this.getOffscreenCanvas().getContext("2d");
    
    var screenWidth = this.getScreenCanvas().width;
    var screenHeight = this.getScreenCanvas().height;
    
    var offset = this._transformScreenToOffscreen(-this._left, -this._top);
    
    screenCtx.clearRect(0, 0, screenWidth, screenHeight);
    
    var offscreenWidth = Math.round(offscreenCtx.canvas.width / this._zoom);
    var offscreenHeight = Math.round(offscreenCtx.canvas.height / this._zoom);

    screenCtx.drawImage(offscreenCtx.canvas, 0, 0, offscreenWidth, offscreenHeight, 0, 0, offscreenWidth / this._zoom, offscreenHeight / this._zoom);
  },
  clearCanvas: function (canvas) {
    var ctx = canvas.getContext("2d");
    ctx.clearRect (0, 0, canvas.width, canvas.height);
  },
  _transformScreen: function (x, y) {
    var offset = this._screenCanvas.cumulativeOffset();
    return {
      x: (x - offset.left) - this._left,
      y: (y - offset.top) - this._top
    };
  },
  _transformScreenToOffscreen: function (x, y) {
    return {
      x: (x + this._offscreenOffsetLeft),
      y: (y + this._offscreenOffsetTop)
    };
  },
  _transformOffscreenToScreen: function (x, y) {
    return {
      x: x - this._offscreenOffsetLeft,
      y: y - this._offscreenOffsetTop
    };
  },
});
**/
MapSVGLayer = Class.create(MapLayer, {
  initialize: function ($super, map, options) {
    $super(map, options);
    
    this._svgImage = null;
    this._svgDocument = null;
    this._svgWidth = null;
    this._svgHeight = null;
    
    this._mapMouseMoveListener = this._onMapMouseMove.bindAsEventListener(this);

    map.addListener("mouseMove", this._mapMouseMoveListener);

    /**
    this._dragging = false;
    
    this._svgDocument = new fi.foyt.svg.svgdom.FNISVGDocument();
    this._svgContainer = new Element("div", {
      className: "mapSvgContainer"
    }); 
    this._disableSelection();
    
    this._svgContainer.appendChild(this._svgDocument.getRootElement());
    this.domNode.appendChild(this._svgContainer);
    
    this._windowMouseUpListener = this._onWindowMouseUp.bindAsEventListener(this);
 
    // TODO: Release listeners
    var _this = this;
    this._svgDocument.getRootElement().addEventListener("mousemove", function(event){
      _this._onSVGDocumentMouseMove(event);
    }, false);
    
    this._svgDocument.getRootElement().addEventListener("mousedown", function(event){
      _this._onSVGDocumentMouseDown(event);
    }, false);
    
    Event.observe(window, "mouseup", this._windowMouseUpListener);
    **/
    this.load();
  },
  render: function ($super, screenContext) {
    if (this._svgImage == null) {
      this._prepareSvgImage(0, 0, this._svgWidth, this._svgHeight);
    }
  
    screenContext.putImageData(this._svgImage, 0, 0);
  },
  moveBy: function ($super, x, y) {
    $super(x, y);
    /**
    var viewBox = this._svgDocument.getViewBox();
    
    this._svgDocument.setViewBox(viewBox.x - x, viewBox.y - y, viewBox.width, viewBox.height);
    **/
  },
  load: function () {
    var _this = this;
    new Ajax.Request(CONTEXTPATH + '/v1/materials/vectorImages/' + this._options.vectorImageId, {
      method: 'GET',
      onSuccess: function (transport) {
        _this._loadSvg(transport.responseText);
      }      
    });
  },
  
  getType: function ($super) {
    return "SVG";  
  }, 
  
  _loadSvg: function (xmlData) {
    // this._svgXml = xmlData;

    if (Prototype.Browser.IE) {
      this._svgDocument = new ActiveXObject("Microsoft.XMLDOM");
      this._svgDocument.async = false;
      this._svgDocument.loadXML(xmlData);                                 
    } else {
      parser = new DOMParser();
      this._svgDocument = parser.parseFromString(xmlData, "text/xml");
    }

    this._svgWidth = this._svgDocument.documentElement.getAttribute("width");
    this._svgHeight = this._svgDocument.documentElement.getAttribute("height");
    
    this.fire("load");

/**    
    if (width && height)
      this._svgDocument.setPageSize(width, height);
    
    // this._svgDocument.setViewBox(100, 100, width, height);
    
    for (var l = svgDocument.documentElement.childNodes.length - 1; l >= 0; l++) {
      var svgElement = svgDocument.documentElement.childNodes[l];
      this._appendSVGElement(null, svgElement);
    }
    **/
  },
  _prepareSvgImage: function (x, y, width, height) {
    var tempCanvas = new Element("canvas", {
      width: width,
      height: height
    });
  
    var tempContext = tempCanvas.getContext("2d");
    tempContext.drawSvg(this._svgDocument, 0, 0, width, height);
    var imageData = tempContext.getImageData(0, 0, width, height);
    this._svgImage = imageData;
  },
  _onMapMouseMove: function (event) {
  }
  
  
  
  /**,
  _appendSVGElement: function(parentElement, svgElement) {
    if (svgElement.nodeType == 1) {
      var nodeType = 'normal';
      if (svgElement.tagName.toUpperCase() == 'G') {
        if (svgElement.getAttribute('fnigrouptype') == 'layer')
          nodeType = 'layer';
        else if (svgElement.getAttribute('inkscape:groupmode') == 'layer')
          nodeType = 'inkscapeLayer';
        else {
          nodeType = 'group';
        }
      }
  
      switch (nodeType) {
        case 'normal':
          var normalElement = this._svgDocument.importElement(svgElement);
          this._svgDocument.appendElement(parentElement, normalElement);
          
          for ( var i = 0; i < svgElement.childNodes.length; i++)
            this._appendSVGElement(normalElement, svgElement.childNodes[i]);
        break;
        case 'layer':
          var layer = this._svgDocument.createLayer(svgElement.getAttribute('fnilayertitle'));
          
          for (var i = 0, l = svgElement.attributes.length; i < l; i++) {
            layer.setAttribute(svgElement.attributes[i].nodeName, svgElement.attributes[i].nodeValue);
          }
          
          for ( var i = 0; i < svgElement.childNodes.length; i++)
            this._appendSVGElement(layer, svgElement.childNodes[i]);
        break;
        case 'inkscapeLayer':
          var layer = this._svgDocument.createLayer(svgElement.getAttribute('inkscape:label'));
          
          for (var i = 0, l = svgElement.attributes.length; i < l; i++) {
            layer.setAttribute(svgElement.attributes[i].nodeName, svgElement.attributes[i].nodeValue);
          }
          
          for ( var i = 0; i < svgElement.childNodes.length; i++)
            this._appendSVGElement(layer, svgElement.childNodes[i]);
        break;
        case 'group':
          var groupElement = this._svgDocument.createElement("g");
          
          for (var i = 0, l = svgElement.attributes.length; i < l; i++) {
            groupElement.setAttribute(svgElement.attributes[i].nodeName, svgElement.attributes[i].nodeValue);
          }

          for ( var i = 0; i < svgElement.childNodes.length; i++)
            this._appendSVGElement(groupElement, svgElement.childNodes[i]);
          
          this._svgDocument.appendElement(parentElement, groupElement);
        break;
      }
    } else {
      var normalElement = this._svgDocument.importElement(svgElement);
      this._svgDocument.appendElement(parentElement, normalElement);
      
      for ( var i = 0; i < svgElement.childNodes.length; i++)
        this._appendSVGElement(normalElement, svgElement.childNodes[i]);
    }
  },
  _disableSelection: function(){
    // safari 1.0+
    this._svgDocument.getRootElement().onselectstart = function(){
      return false;
    };
    // ie 5.5+
    this._svgDocument.getRootElement().unselectable = "on";
    // mozilla
    this._svgDocument.getRootElement().style.MozUserSelect = "none";
  },
  _translateScreenToCanvas: function (x, y) {
    var offset = this.domNode.cumulativeOffset();
    return {
      x: (x - offset.left) - this._left,
      y: (y - offset.top) - this._top
    };
  },
  _onSVGDocumentMouseDown: function (event) {
    this._dragging = true;
    this._mouseScreenX = null;
    this._mouseScreenY = null;
    this._mouseLastScreenX = null;
    this._mouseLastScreenY = null;
  },
  _onSVGDocumentMouseMove: function (event) {
    this._mouseLastScreenX = this._mouseScreenX;
    this._mouseLastScreenY = this._mouseScreenY;
    
    this._mouseScreenX = Event.pointerX(event);
    this._mouseScreenY = Event.pointerY(event);
    var mouseChangeX = 0;
    var mouseChangeY = 0;
    
    if ((this._mouseLastScreenX != null) && (this._mouseLastScreenY != null)) {
      mouseChangeX = (this._mouseScreenX - this._mouseLastScreenX);
      mouseChangeY = (this._mouseScreenY - this._mouseLastScreenY);
    }
    
    var canvasPoints = this._translateScreenToCanvas(this._mouseScreenX, this._mouseScreenY);
    
    this.fire("mouseMove", {
      mouseScreenX: this._mouseScreenX,
      mouseScreenY: this._mouseScreenY,
      mouseChangeX: mouseChangeX,
      mouseChangeY: mouseChangeY,
      canvasX: canvasPoints.x,
      canvasY: canvasPoints.y
    });
    
    if (this._dragging === true) {
      this.fire("mouseDrag", {
        mouseScreenX: this._mouseScreenX,
        mouseScreenY: this._mouseScreenY,
        mouseChangeX: mouseChangeX,
        mouseChangeY: mouseChangeY,
        canvasX: canvasPoints.x,
        canvasY: canvasPoints.y
      });
    }
  },
  _onWindowMouseUp: function (event) {
    this._dragging = false;
  }**/
});

MapTokenLayer = Class.create(MapLayer, {
  initialize: function ($super, map, options) {
    $super(map, options);
    
    this._selectedToken = null;
    this._draggingToken = false;
    this._x = 0;
    this._y = 0;
    
    this._tokens = new Array();
    this._events = new Array();
    this._pendingEvents = new Array();
    this._activeEvents = new Array();
    this._loadingTokenCount = 0;

    this._addToken('/fni/materials/1/pentagon_1/tokens/liftarn_su-27_silhou', null, 32);
    this._addToken('/fni/materials/1/pentagon_1/tokens/carrier2.png', null, 32);
    
    this._mouseDownListener = this._onMouseDown.bindAsEventListener(this);
    this._mouseDragStartListener = this._onMouseDragStart.bindAsEventListener(this);
    this._mouseDragListener = this._onMouseDrag.bindAsEventListener(this);
    this._mouseUpListener = this._onMouseUp.bindAsEventListener(this);
    this._timeStartListener = this._onTimeStart.bindAsEventListener(this);
    this._timeChangeListener = this._onTimeChange.bindAsEventListener(this);
    
    map.addListener("mouseDown", this._mouseDownListener);
    map.addListener("mouseDragStart", this._mouseDragStartListener);
    map.addListener("mouseDrag", this._mouseDragListener);
    map.addListener("mouseUp", this._mouseUpListener);
    map.addListener("mouseMove", this._mouseMoveListener);
    document.observe("fnimaptime:start", this._timeStartListener);
    document.observe("fnimaptime:change", this._timeChangeListener);
    
    this._tokens[0].moveTo(100, 100);
    this._tokens[1].moveTo(200, 200);

    this._addEvent(new MapTokenMoveEvent(this._tokens[0], 0, 3 * 1000, new MapLocation(200, 200)));
    this._addEvent(new MapTokenMoveEvent(this._tokens[0], 3.5 * 1000, 8 * 1000, new MapLocation(300, 12)));
    this._addEvent(new MapTokenMoveEvent(this._tokens[0], 8 * 1000, 11 * 1000, new MapLocation(300, 512)));
  },
  render: function ($super, screenContext) {
    for (var i = 0, l = this._tokens.length; i < l; i++) {
      this._tokens[i].render(screenContext, this._x, this._y);
    }
  },
  moveBy: function ($super, x, y) {
    this._x += x;
    this._y += y;
  },
  getType: function ($super) {
    return "Token";  
  },
  _addToken: function (imageUrl, width, height) {
    this._loadingTokenCount++;

    var token = new MapTokenLayerToken();
    var _this = this;
    token.load(imageUrl, width, height, function () {
      _this._loadingTokenCount--;
      if (_this._loadingTokenCount <= 0) {
        _this._loadingTokenCount = 0;
        _this.fire("load");
      }
    });

    this._tokens.push(token);
  },
  _addEvent: function (event) {
    this._events.push(event);
  },
  _setSelectedToken: function (token) {
    if (this._selectedToken) {
      this._selectedToken.setSelected(false);
    }
    
    if (token) {
      token.setSelected(true);
    }
    
    this._selectedToken = token;
  },
  _getSelectedToken: function () {
    return this._selectedToken;
  },
  
  _selectToken: function (canvasX, canvasY) {
    var found = false;
    var render = false;
    var selected = null;

    for (var i = 0, l = this._tokens.length; i < l; i++) {
      var token = this._tokens[i];
      var boundingBox = token.getBoundingBox();
      if ((canvasX > boundingBox.x) && (canvasX < (boundingBox.x + boundingBox.width)) && (canvasY > boundingBox.y) && (canvasY < (boundingBox.y + boundingBox.height))) {
        found = true;
        if (!token.isSelected()) {
          selected = token;
          render = true;
          break;
        }
      }
    }
    
    if (found == false) {
      this._setSelectedToken(null);
      render = true;
    } else {
      if (selected) {
        this._setSelectedToken(selected);
        render = true;
      }
    }

    //if (render == true)
      this.getMap().render();
  },
  _addEvent: function (event) {
    this._events.push(event);
  },
  _checkEvents: function (time) {
    for (var i = 0, l = this._activeEvents.length; i < l; i++) {
      var event = this._activeEvents[i];
      if ((event.getEnd() <= time)) {
        this._activeEvents.splice(i, 1)[0].update(1);
      } 
    }
    
    for (var i = 0, l = this._pendingEvents.length; i < l; i++) {
      var event = this._pendingEvents[i];
      if ((event.getStart() <= time)) {
        this._pendingEvents.splice(i, 1);
        this._activeEvents.push(event);
        event.start();
      } 
    }
  },
  _onMouseDown: function (event) {
    var canvasX = event.memo.canvasX;
    var canvasY = event.memo.canvasY;
    this._selectToken(canvasX, canvasY);
  },
  _onMouseDragStart: function (event) {
    var canvasX = event.memo.canvasX;
    var canvasY = event.memo.canvasY;
    if (!this._getSelectedToken()) {
      this._selectToken(canvasX, canvasY);
    }

    if (this._getSelectedToken()) {
      this._draggingToken = true;
    }    
  },
  _onMouseDrag: function (event) {
    if (this._draggingToken) {
      var canvasX = event.memo.canvasX;
      var canvasY = event.memo.canvasY;
    
      var boundingBox = this._getSelectedToken().getBoundingBox();
      this._getSelectedToken().moveTo(canvasX - (boundingBox.width / 2), canvasY - (boundingBox.height / 2));
      this.getMap().render();
    }
  },
  _onMouseUp: function (event) {
    if (this._draggingToken) {
      var canvasX = event.memo.canvasX;
      var canvasY = event.memo.canvasY;
      
      this._draggingToken = false;

      var boundingBox = this._getSelectedToken().getBoundingBox();
      this._getSelectedToken().moveTo(canvasX - (boundingBox.width / 2), canvasY - (boundingBox.height / 2));
      this.getMap().render();
    }
  },
  
  _onTimeStart: function (event) {
    var time = event.memo.time;
    
    for (var i = 0, l = this._events.length; i < l; i++) {
      this._pendingEvents.push(this._events[i]);
    }
  },
  _onTimeChange: function (event) {
    var time = event.memo.time;
    
    this._checkEvents(time);
    
    for (var i = 0, l = this._activeEvents.length; i < l; i++) {
      var event = this._activeEvents[i];
      var eventEnd = event.getEnd();
      var eventStart = event.getStart();
      var eventLength = eventEnd - eventStart;
      
      var pct = (time - eventStart) / eventLength;
      if (pct > 1) 
        pct = 1;
      
      event.update(pct);
    }
    
    this.getMap().render();
  }
});

MapTokenEvent = Class.create({
  initialize: function (token, start, end) {
    this._token = token;
    this._start = start;
    this._end = end;
  },
  getToken: function () {
    return this._token;
  },
  getStart: function () {
    return this._start;
  },
  getEnd: function () {
    return this._end;
  },
  start: function () {
    
  },
  update: function (pct) {
    // 
  }
});

MapTokenMoveEvent = Class.create(MapTokenEvent, {
  initialize: function ($super, token, start, end, to) {
    $super(token, start, end);
    
    this._token = token;
    this._to = to;
  },
  getTo: function () {
    return this._to;
  },
  start: function ($super) {
    this._originalX = this._token.getLocation().getX();
    this._originalY = this._token.getLocation().getY();
    this._changeX = (this.getTo().getX() - this._originalX);
    this._changeY = (this.getTo().getY() - this._originalY);
  },
  update: function ($super, pct) {
    this._token.moveTo(
        this._originalX + (this._changeX * pct),
        this._originalY + (this._changeY * pct)
    );
  }
});

MapTokenLayerToken = Class.create({
  initialize: function (canvas) {
    this._location = new MapLocation(0, 0);
    this._selected = false;
  },
  load: function (imageUrl, width, height, callback) {
    this._tokenImage = new Image();  
    
    var _this = this;
    this._tokenImage.onload = function () {
      if (width != null && height != null) {
        _this._width = width;
        _this._height = height;
      } else if (width == null && height == null) {
        _this._width = this.naturalWidth;
        _this._height = this.naturalHeight;
      } else if (width == null) {
        _this._width = Math.round( this.naturalWidth / (this.naturalHeight / height) ); 
        _this._height = height;
      } else {
        _this._width = width;
        _this._height = Math.round( this.naturalHeight / (this.naturalWidth / width) ); 
      }
      
      callback();
    };
    
    this._tokenImage.src = imageUrl;
  },
  render: function (screenContext, x, y) {
    var imageX = x + this._location.getX();
    var imageY = y + this._location.getY();
    var imageWidth = this._width;
    var imageHeight = this._height;
    
    if (this._selected) {
      screenContext.fillStyle = 'rgba(0, 192, 128, 0.25)';
      screenContext.lineWidth = '1px';
      
      var radius = Math.max(imageWidth, imageHeight) / 2;
      
      screenContext.beginPath();
      screenContext.arc(imageX + (imageWidth / 2), imageY + (imageHeight / 2), radius, 0, Math.PI * 2 ,true); 
      screenContext.fill();
      screenContext.closePath();
    }

    screenContext.drawImage(this._tokenImage, imageX, imageY, imageWidth, imageHeight);
  },
  moveTo: function (x, y) {
    this._location.set(x, y);
  },
  setSelected: function (selected) {
    this._selected = selected;
  },
  isSelected: function () {
    return this._selected;
  },
  getLocation: function () {
    return this._location;
  },
  getBoundingBox: function () {
    return { 
      x: this._location.getX(),
      y: this._location.getY(),
      width: this._width,
      height: this._height
    };
  }
});

MapTimeManager = Class.create({
  initialize: function () {
    this._time = 0;
  
    this._heartBeat = new S2.FX.Heartbeat();
    
    this._heartBeatListener = this._onHeartBeat.bindAsEventListener(this);
    document.observe('effect:heartbeat', this._heartBeatListener);
  },
  getTime: function () {
    return this._time;
  },
  start: function () {
    this._heartBeat.start();
    document.fire("fnimaptime:start", {
      time: this._time
    });
  },
  pause: function () {
    this._heartBeat.stop();    
    document.fire("fnimaptime:pause", {
      time: this._time
    });
  },
  _setTime: function (time) {
    this._time = time;
    document.fire("fnimaptime:change", {
      time: this._time
    });
  },
  _advance: function (ms) {
    this._setTime(this._time + ms);
  },
  _onHeartBeat: function (event) {
    currentBeat = this._heartBeat.getTimestamp();
    
    if (this._lastBeat) {
      this._advance(currentBeat - this._lastBeat);
    }

    this._lastBeat = currentBeat;
  }
});

Map = Class.create({
  initialize: function (options) {
    this._controls = new MapControls(this);
    this._timeManager = new MapTimeManager();
    this._scale = new MapScale(NaN);

    this._options = options;
    this._activeLayer = null;
    this._activeTool = null;
    this._layers = new Array();
    this._loadingLayerCount = 0;
    
    if (options.millimetersPerPoint) {
      this._scale.setMillimetersPerPoint(options.millimetersPerPoint);
    }
    
    this.domNode = new Element("div", {
      className: "map"
    });
    
    if (options.container) {
      options.container.appendChild(this.domNode);
    }
    
    this.addTool(new MapPointerTool(this));
    this.addTool(new MapMoveTool(this));
    this.addTool(new MapMeasureTool(this));
    
    this._screenCanvas = new Element("canvas", {
      className: "mapScreenCanvas",
      width: this.domNode.getLayout().get("width"),
      height: this.domNode.getLayout().get("height")
    });
    
    this.domNode.appendChild(this._screenCanvas);
    
    this._screenCanvasMouseMoveListener = this._onScreenCanvasMouseMove.bindAsEventListener(this);
    this._screenCanvasMouseDownListener = this._onScreenCanvasMouseDown.bindAsEventListener(this);
    this._windowMouseUpListener = this._onWindowMouseUp.bindAsEventListener(this);
    this._layerLoadListener = this._onLayerLoad.bindAsEventListener(this);

    Event.observe(this._screenCanvas, "mousemove", this._screenCanvasMouseMoveListener);
    Event.observe(this._screenCanvas, "mousedown", this._screenCanvasMouseDownListener);
    Event.observe(window, "mouseup", this._windowMouseUpListener);
  },
  
  render: function () {
	  var screenContext = this._screenCanvas.getContext("2d");

	  this.clearCanvas(screenContext, this._screenCanvas.width, this._screenCanvas.height);

    for (var i = 0, l = this._layers.length; i < l; i++) {
      this._layers[i].render(screenContext);
    }
  },
  
  clearCanvas: function (screenContext, width, height) {
    screenContext.clearRect (0, 0, width, height);
  },
  
  moveBy: function (x, y) {
    for (var i = 0, l = this._layers.length; i < l; i++) {
      this._layers[i].moveBy(x, y);
    }
  },
  
  getTimeManager: function () {
    return this._timeManager;
  },
  
  getScale: function () {
    return this._scale;
  },
  
  getDisplayHeight: function () {
    return this.domNode.getLayout().get("height");
  },
  
  getDisplayWidth: function () {
    return this.domNode.getLayout().get("width");
  },
  
  /* Cursor */
  
  _setCursor: function (cursor) {
    if (this._cursor) {
      this.domNode.removeClassName(this._getCursorClass(this._cursor));
    }
    
    this._cursor = cursor;
    
    this.domNode.addClassName(this._getCursorClass(this._cursor));
  },

  _getCursorClass: function (cursor) {
    return "mapCursor" + cursor;
  },
  
  /* Paint */
  
  setPaintColor: function () {
    // 
  },
  setFillTileImage: function (tileId) {
    var _this = this;
    this._loadTileImage(tileId, function () {
      _this._fillColorButton.setStyle({
        backgroundImage: 'url(' + CONTEXTPATH + "/v1/map/tile/22x22/" + tileId + ')'
      });
      _this._fillTileId = tileId;
    });
  },
  getFillColor: function (x, y) {
    var tileX = (x % this._tileWidth);
    var tileY = (y % this._tileHeight);
    var offset = (tileX + (tileY * this._tileWidth)) * 4;
  
    var r = this._tileData[offset + 0];
    var g = this._tileData[offset + 1];
    var b = this._tileData[offset + 2];
    var a = this._tileData[offset + 3];
    
    return "rgba(" + r + "," + g + "," + b + "," + a + ")";
  },
  
  /* Events */
  
  fire: function (eventName, eventData) {
    var event = this.domNode.fire("fnimap:" + eventName, Object.extend(eventData||{}, {map: this}));
    return !event.stopped;
  },
  addListener: function (eventName, listener) {
    this.domNode.observe("fnimap:" + eventName, listener);
  },
  removeListener: function (eventName, listener) {
    this.domNode.stopObserving("fnimap:" + eventName, listener);
  }, 
  
  /* Tools */
  
  addTool: function (tool) {
    this._controls.addTool(tool);
  },
  
  setActiveTool: function (tool) {
    if (this._activeTool) {
      this._activeTool.deactivate();
    }

    this._controls.setActiveTool(tool);
    this._setCursor(tool.getName());
    
    tool.activate();

    this._activeTool = tool;
  },
  
  /* Add layers */
  
  addVectorImageLayer: function (layerId, layerName, vectorImageId) {
    var layer = new MapSVGLayer(this, {
      id: layerId,
      name: layerName, 
      vectorImageId: vectorImageId
    });
    this._controls.addLayer(layer);
    
    this._addLayer(layer);
  },
  
  addTokenLayer: function (layerId, layerName) {
    var layer = new MapTokenLayer(this, {
      id: layerId,
      name: layerName
    });
    this._controls.addLayer(layer);
 
    this._addLayer(layer);
  },
  
  /* Drawing */
  
  drawLine: function (x1, y1, x2, y2, width, strokeStyle) {
    var screenContext = this._screenCanvas.getContext("2d");
    
    screenContext.beginPath();
    screenContext.strokeStyle = strokeStyle;
    screenContext.lineWidth = width;
	screenContext.moveTo(x1, y1);
    screenContext.lineTo(x2, y2);
    screenContext.stroke();
    screenContext.closePath();
  },
  
  /* Private */
  
  _addLayer: function (layer) {
    this._layers.push(layer);
    this._setActiveLayer(layer);
    layer.addListener("load", this._layerLoadListener);
    this._loadingLayerCount++;
  },
  
  _setActiveLayer: function (layer) {
	this._activeLayer = layer;  
  },
  _translateScreenToCanvas: function (x, y) {
    var layout = this.domNode.cumulativeOffset();
    return {
      x: (x - layout.left),
      y: (y - layout.top)
    };
  },
  _onLayerLoad: function (event) {
    this._loadingLayerCount--;
    if (this._loadingLayerCount <= 0) {
      this._loadingLayerCount = 0;
      this.render();
    }
  },
  _onScreenCanvasMouseMove: function (event) {
    this._mouseScreenX = Event.pointerX(event);
    this._mouseScreenY = Event.pointerY(event);
    
    var mouseChangeX = 0;
    var mouseChangeY = 0;
    
    if ((this._mouseLastScreenX != null) && (this._mouseLastScreenY != null)) {
      mouseChangeX = (this._mouseScreenX - this._mouseLastScreenX);
      mouseChangeY = (this._mouseScreenY - this._mouseLastScreenY);
    }

    this._mouseLastScreenX = this._mouseScreenX;
    this._mouseLastScreenY = this._mouseScreenY;
    var canvasPosition = this._translateScreenToCanvas(this._mouseScreenX, this._mouseScreenY);
    
    this.fire("mouseMove", {
      mouseScreenX: this._mouseScreenX,
      mouseScreenY: this._mouseScreenY,
      mouseChangeX: mouseChangeX,
      mouseChangeY: mouseChangeY,
      canvasX: canvasPosition.x,
      canvasY: canvasPosition.y      
    });
    
    if (this._mouseDown === true) {
      this.fire("mouseDrag", {
        mouseScreenX: this._mouseScreenX,
        mouseScreenY: this._mouseScreenY,
        mouseChangeX: mouseChangeX,
        mouseChangeY: mouseChangeY,
	    canvasX: canvasPosition.x,
	    canvasY: canvasPosition.y  
      });
    }
  },
  _onScreenCanvasMouseDown: function (event) {
    var x = Event.pointerX(event);
    var y = Event.pointerY(event);
    var canvasPosition = this._translateScreenToCanvas(x, y);
    
    if (this._mouseDown === false) {
      this.fire("mouseDragStart", {
        mouseScreenX: x,
        mouseScreenY: y,
	    canvasX: canvasPosition.x,
	    canvasY: canvasPosition.y 
      });
    }
    
    this._mouseDown = true;
    
    this.fire("mouseDown", {
      mouseScreenX: x,
      mouseScreenY: y,
	  canvasX: canvasPosition.x,
	  canvasY: canvasPosition.y 
    });
  },
  _onWindowMouseUp: function (event) {
    this._mouseDown = false;
    
    var x = Event.pointerX(event);
    var y = Event.pointerY(event);
    var canvasPosition = this._translateScreenToCanvas(x, y);
    
    this.fire("mouseUp", {
      mouseScreenX: x,
      mouseScreenY: y,
	  canvasX: canvasPosition.x,
	  canvasY: canvasPosition.y 
    });
  }
  
  /**
  _loadTileImage: function (tileImageId, callback) {
    var tileImage = new Image();  
    
    var _this = this;
    tileImage.onload = function () {
      _this._tileWidth = this.naturalWidth;
      _this._tileHeight = this.naturalHeight;
      
      
      var tempCanvas = new Element("canvas", {
        width: _this._tileWidth,
        height: _this._tileHeight
      });
      
      var ctx = tempCanvas.getContext("2d");
      ctx.drawImage(this, 0, 0); // , _this._tileWidth, _this._tileHeight
      var imageData = ctx.getImageData(0, 0, _this._tileWidth, _this._tileHeight);
      _this._tileData = imageData.data;
      
      if (Object.isFunction(callback))
        callback();
    };
    
    tileImage.src = CONTEXTPATH + "/v1/map/tile/ORIGINAL/" + tileImageId;
  },
  _onLayerMouseDragStart: function (event) {
    if (this.fire("layerMouseDragStart", {
      mouseScreenX: event.memo.mouseScreenX,
      mouseScreenY: event.memo.mouseScreenY,
      canvasX: event.memo.canvasX,
      canvasY: event.memo.canvasY
    })) {
        
    }
  },
  _onLayerMouseDrag: function (event) {
    if (this.fire("layerMouseDrag", {
      mouseScreenX: event.memo.mouseScreenX,
      mouseScreenY: event.memo.mouseScreenY,
      canvasX: event.memo.canvasX,
      canvasY: event.memo.canvasY,
      mouseChangeX: event.memo.mouseChangeX,
      mouseChangeY: event.memo.mouseChangeY      
    })) {

    }
  },
  _onLayerMouseMove: function (event) {
    if (this.fire("layerMouseMove", {
      mouseScreenX: event.memo.mouseScreenX,
      mouseScreenY: event.memo.mouseScreenY,
      canvasX: event.memo.canvasX,
      canvasY: event.memo.canvasY,
      mouseChangeX: event.memo.mouseChangeX,
      mouseChangeY: event.memo.mouseChangeY      
    })) {
      
    }
  },
  _onLayerMouseDown: function (event) {
    if (this.fire("layerMouseDown", {
      mouseScreenX: event.memo.mouseScreenX,
      mouseScreenY: event.memo.mouseScreenY,
      canvasX: event.memo.canvasX,
      canvasY: event.memo.canvasY
    })) {
      
    }
  },
  _onLayerMouseUp: function (event) {
    if (this.fire("layerMouseUp", {
      mouseScreenX: event.memo.mouseScreenX,
      mouseScreenY: event.memo.mouseScreenY,
      canvasX: event.memo.canvasX,
      canvasY: event.memo.canvasY
    })) {
      
    }
  }**/
});

MapTool = Class.create({
  initialize: function (map) {
    this._map = map;
  },
  getName: function () {
    
  },
  getMap: function () {
    return this._map;
  },
  activate: function () {
    
  },
  deactivate: function () {
    
  }
});

MapPointerTool = Class.create(MapTool, {
  initialize: function ($super, map) {
    $super(map);
  },
  getName: function ($super) {
    return "Pointer";
  }
});

MapMoveTool = Class.create(MapTool, {
  initialize: function ($super, map) {
    $super(map);
    
    this._mapLayerMouseDragListener = this._onMapLayerMouseDrag.bindAsEventListener(this);
  },
  getName: function ($super) {
    return "Move";
  },
  activate: function ($super) {
    $super();
    
    this.getMap().addListener("layerMouseDrag", this._mapLayerMouseDragListener);
  },
  deactivate: function ($super) {
    $super();

    this.getMap().removeListener("layerMouseDrag", this._mapLayerMouseDragListener);
  },
  _onMapLayerMouseDrag: function (event) {
    var map = this.getMap();
    map.moveBy(event.memo.mouseChangeX, event.memo.mouseChangeY);
  }
});

MapMeasureTool = Class.create(MapTool, {
  initialize: function ($super, map) {
    $super(map);
    
    this._dragging = false;
    
    this._mapMouseDragListener = this._onMapMouseDrag.bindAsEventListener(this);
    this._mapMouseUpListener = this._onMapMouseUp.bindAsEventListener(this);
  },
  getName: function ($super) {
    return "Measure";
  },
  activate: function ($super) {
    $super();
    
    this.getMap().addListener("mouseDrag", this._mapMouseDragListener);
    this.getMap().addListener("mouseUp", this._mapMouseUpListener);
  },
  deactivate: function ($super) {
    $super();

    this.getMap().removeListener("mouseDrag", this._mapMouseDragListener);  
    this.getMap().removeListener("mouseUp", this._mapMouseUpListener);
  },
  _onMapMouseDrag: function (event) {
    if (this._dragging != true) {
      this._dragging = true;
      this._dragStartX = event.memo.canvasX;
      this._dragStartY = event.memo.canvasY;
    }

    var map = event.memo.map;
    map.render();
    map.drawLine(this._dragStartX, this._dragStartY, event.memo.canvasX, event.memo.canvasY, 1, 'rgba(255, 128, 0, 0.8)');
  },
  _onMapMouseUp: function (event) {
    if (this._dragging) {
      var dragDistance = this._calculateDistance(this._dragStartX, this._dragStartY, event.memo.canvasX, event.memo.canvasY);

      if (isNaN(this.getMap().getScale().getMillimetersPerPoint())) {
	      this.getMap().getScale().setMegametersPerPoint(dragDistance);
	      
	      console.log(dragDistance + ' = ' + '1000km');
	      console.log('mm/px ratio:' + this.getMap().getScale().getMillimetersPerPoint());
	      console.log('1px = ' + this.getMap().getScale().getMeters(1) + 'm');
	      console.log('1px = ' + this.getMap().getScale().getKilometers(1) + 'km');
	      
	      alert(this.getMap().getScale().getKilometers(dragDistance / 1000));
	    } else {
	      alert(this.getMap().getScale().getKilometers(dragDistance) + 'km');
	    }
    }
    
    this._dragging = false;
  },
  _calculateDistance: function (x1, y1, x2, y2) {
    var dx = Math.abs(x1 - x2);
    var dy = Math.abs(y1 - y2);
    return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
  }
});

MapControls = Class.create({
  initialize: function (map) {
    this._map = map;
    this._domNode = new Element("div", {
      className: "mapControls"
    });
    
    this._toolsGroup = new Element("div", {
      className: "mapControlsTools"
    });
    
    this._domNode.appendChild(this._toolsGroup);
    
    this._layersGroup = new Element("div", {
      className: "mapControlsLayers"
    });
      
    this._domNode.appendChild(this._layersGroup);

    this._paintGroup = new Element("div", {
      className: "mapControlsPaint"
    });
    
    this._domNode.appendChild(this._paintGroup);
    
    this._timeGroup = new Element("div", {
      className: "mapControlsTime"
    });
    
    this._timeButtonRewind = new Element("div", {
      className: "mapControlTimeButton mapControlTimeButtonRewind"
    });
    this._timeGroup.appendChild(this._timeButtonRewind);
    
    this._timeButtonPlay = new Element("div", {
      className: "mapControlTimeButton mapControlTimeButtonPlay"
    });
    this._timeGroup.appendChild(this._timeButtonPlay);

    this._timeButtonPause = new Element("div", {
      className: "mapControlTimeButton mapControlTimeButtonPause",
      style: "display: none"
    });
    this._timeGroup.appendChild(this._timeButtonPause);
    
    this._timeButtonForward = new Element("div", {
      className: "mapControlTimeButton mapControlTimeButtonForward"
    });
    this._timeGroup.appendChild(this._timeButtonForward);

    this._timeButtonTime = new Element("div", {
      className: "mapControlTimeButton mapControlTimeButtonTime"
    });
    this._timeGroup.appendChild(this._timeButtonTime);
    this._domNode.appendChild(this._timeGroup);

    document.body.appendChild(this._domNode);
    
    this._lastBeat = null;
    
    this._toolButtonClickListener = this._onToolButtonClick.bindAsEventListener(this);
    this._timeButtonPlayClickListener = this._onTimeButtonPlayClick.bindAsEventListener(this);
    this._timeButtonPauseClickListener = this._onTimeButtonPauseClick.bindAsEventListener(this);
    
    Event.observe(this._timeButtonPlay, "click", this._timeButtonPlayClickListener);
    Event.observe(this._timeButtonPause, "click", this._timeButtonPauseClickListener);
  },
  addLayer: function (layer) {
    var layerElement = new Element("div", {
      className: "mapControlsLayer"	
    });
    
    var layerIcon = new Element("div", {
      className: "mapControlsLayerIcon mapControlsLayerIcon" + layer.getType() 
    });

    var layerName = new Element("div", {
      className: "mapControlsLayerName"
    }).update(layer.getName());
    
    layerElement.appendChild(layerIcon);
    layerElement.appendChild(layerName);
    
	  this._layersGroup.appendChild(layerElement);
  },
  addTool: function (tool) {
	  var toolClass = "mapControlTool mapControlTool" + tool.getName();
	  var toolElement = new Element("div", {
	    className: toolClass
	  });
	  
	  toolElement.store("tool", tool);
	  
	  this._toolsGroup.appendChild(toolElement);
	  
	  // TODO: release listener 
	  Event.observe(toolElement, "click", this._toolButtonClickListener);
  },
  setActiveTool: function (tool) {
    this._toolsGroup.select('.mapControlActiveTool').each(function (e) {
      e.removeClassName('mapControlActiveTool');
    });
    
    var toolElement = this._toolsGroup.down('.mapControlTool' + tool.getName());
    toolElement.addClassName('mapControlActiveTool');
  },
  _onToolButtonClick: function (event) {
    var toolButton = Event.element(event);
    this._map.setActiveTool(toolButton.retrieve("tool"));
  },
  _onTimeButtonPlayClick: function (event) {
    this._timeButtonPause.show();
    this._timeButtonPlay.hide();
    
    this._map.getTimeManager().start();
  },
  _onTimeButtonPauseClick: function (event) {
    this._map.getTimeManager().pause();
    
    this._timeButtonPause.hide();
    this._timeButtonPlay.show();
  }
});