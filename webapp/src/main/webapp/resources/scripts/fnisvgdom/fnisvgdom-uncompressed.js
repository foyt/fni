if (!window.fi) {
  /**
   * @namespace fi.foyt package
   */
  window.fi = {};
};if (!window.fi.foyt) {
  /**
   * @namespace fi.foyt package
   */
  window.fi.foyt = {};
};if (!window.fi.foyt.svg) {
  /**
   * @namespace fi.foyt.svg package
   */
  window.fi.foyt.svg = {};
};if (!window.fi.foyt.svg.svgdom) {
  /**
   * @namespace fi.foyt.svg.svgdom package
   */
  window.fi.foyt.svg.svgdom = {};
};fi.foyt.svg.svgdom.FNISVGDocument = Class.create( {
  initialize : function() {
    this.svgNamespace = 'http://www.w3.org/2000/svg';
    this.elements = null;
    this._viewbox = null;

    this._svgNode = document.createElementNS(this.svgNamespace, "svg");
    this._svgDocument = this._svgNode.ownerDocument;
    this._svgWindow = window;
    this._svgNode.setAttribute("xmlns:svg", this.svgNamespace);
    this._svgNode.setAttribute("xmlns:fni", "http://www.foyt.fi/fnins");
    this._svgNode.setAttribute("xmlns", this.svgNamespace);
    this._initLayers();
  },
  getCurrentLayer : function() {
    return this._currentLayer;
  },
  setCurrentLayer : function(layer) {
    this._currentLayer = layer;
    this.fire("currentLayerChanged", {
      layer : layer
    });
  },
  getLayers : function() {
    var result = new Array();
    var childNodes = this._getElementChildNodes(this.getRootElement());
    for ( var i = 0; i < childNodes.length; i++) {
      var child = childNodes[i];
      if (child) {
        if ((child.tagName.toUpperCase() == 'G') && (child.getAttribute('fnigrouptype') == 'layer')) {
          result.push(childNodes[i]);
        }
      }
    }

    return result;
  },
  raiseLayer : function(layer) {
    var layers = this.getLayers();
    for ( var i = 0; i < layers.length; i++) {
      if (layers[i] == layer) {
        if (i < (layers.length - 2))
          this.getRootElement().insertBefore(layer, layers[i + 2]);
        else
          this.getRootElement().appendChild(layer);

        this.fire("layerRaised", {
          layer : layer
        });
        break;
      }
    }
  },
  lowerLayer : function(layer) {
    var layers = this.getLayers();
    for ( var i = 0; i < layers.length; i++) {
      if (layers[i] == layer) {
        if (i > 0)
          this.getRootElement().insertBefore(layer, layers[i - 1]);

        this.fire("layerLowered", {
          layer : layer
        });
        break;
      }
    }
  },
  getRootElement : function() {
    return this._svgNode;
  },
  getBackgroundGroup : function() {
    return this._background;
  },
  createElement : function(name) {
    return this._svgDocument.createElementNS(this.svgNamespace, name);
  },
  importElement : function(node) {
    try {
      return this._svgDocument.importNode(node, false);
    } catch (e) {

      var imported = this.createElement(node.tagName);
      for ( var i = 0; i < node.attributes.length; i++)
        imported.setAttribute(node.attributes[i].name, node.attributes[i].value);

      return imported;
    }

  },
  appendElement : function(parentNode, element) {
    if (this.elements == null)
      this.elements = new Array();

    this.elements.push(element);

    if (parentNode == null)
      this.getRootElement().appendChild(element);
    else
      parentNode.appendChild(element);
  },
  appendTemporaryElement : function(element) {
    this.getRootElement().appendChild(element);
    element._temporary = true;
  },
  removeTemporaryElement : function(element) {
    if (element != null) {
      if (element.parentNode != null)
        element.parentNode.removeChild(element);
      else
        addDebugText('Could not remove temporary node ' + element.tagName + ', cannot find parent...');
    }
    ;
  },
  removeElement : function(element) {
    for ( var i = 0; i < this.elements.length; i++) {
      if (this.elements[i] == element) {
        this.elements.splice(i, 1);

        while (element.childNodes.length > 0)
          this.removeElement(element.firstChild);

        element.parentNode.removeChild(element);
        break;
      }
    }
  },
  getElements : function() {
    return this.elements;
  },
  setPageSize: function (width, height) {
    var rootElement = this.getRootElement();
    rootElement.setAttribute("width", width);
    rootElement.setAttribute("height", height);
  },
  setViewBox : function(x, y, width, height) {
    this._viewbox = {
      x : x,
      y : y,
      width : width,
      height : height
    };

    var rootElement = this.getRootElement();

    rootElement.setAttribute("viewBox", x + ' ' + y + ' ' + width + ' ' + height);
  },
  getViewBox : function() {
    if (this._viewbox == null) {
      var rootElement = this.getRootElement();
      var width = rootElement.getAttribute("width");
      var height = rootElement.getAttribute("height");
      
      this._viewbox = {
        x : 0,
        y : 0,
        width : width,
        height : height
      };
    }
    
    return this._viewbox;
  },
  getElementAt : function(x, y) {
    var elements = this.getElements();
    if (elements != null) {
      for ( var i = 0; i < elements.length; i++) {
        var element = elements[i];
        var eBBox = element.getBBox();

        if ((eBBox.x <= x) && (eBBox.y <= y) && ((eBBox.x + eBBox.width) >= x) && ((eBBox.y + eBBox.height) >= y))
          return element;
      }
    }

    return null;
  },
  removeAllElements : function() {
    if (this.elements) {
      while (this.elements.length > 0) {
        var element = this.elements.pop();
        element.parentNode.removeChild(element);
      }
    }
  },
  createLayer : function(title) {
    var groupElement = this.createElement("g");
    groupElement.setAttribute("fnigrouptype", "layer");
    groupElement.setAttribute("fnilayertitle", title);

    this.getRootElement().appendChild(groupElement);

    this.fire("layerCreated", {
      layer : groupElement
    });

    return groupElement;
  },
  removeLayer : function(layer) {
    while (layer.childNodes.length > 0) {
      this.removeElement(layer.firstChild);
    }

    this.getRootElement().removeChild(layer);

    this.fire("layerRemoved", {
      layer : layer
    });
  },
  inspect : function() {
    return "[FNISVGDocument]";
  },
  getAsXML : function(width, height) {
    var xmlData = this._getSVGHeader();

    xmlData += '<svg xmlns:fni="http://www.foyt.fi/fnins" xmlns:svg="' + this.svgNamespace + '" xmlns="' + this.svgNamespace + '" ' + 'width="' + width
        + '" height="' + height + '">';

    var layers = this.getLayers();
    for (var i = 0; i < layers.length; i++)
      xmlData += this._getElementAsXML(layers[i]);

    return xmlData + "</svg>";
  },
  _getElementChildNodes: function (element) {
    return element.childNodes;
  },
  _initLayers : function() {
    this._background = this.createElement("g");
    this._background.setAttribute("fnigrouptype", "background");
    this.getRootElement().appendChild(this._background);
    this._currentLayer = null;
  },
  _getSVGHeader : function() {
    return '<?xml version="1.0" encoding="UTF-8" standalone="no"?>';
  },
  _getElementAsXML : function(element) {
    var result = '';
    
    if (element._temporary != true) {
      if (FNISVGDOMUtils.isValidSVGElement(element)) {
        result = '<' + element.tagName;
  
        var attributeNames = FNISVGDOMUtils.getElementAttributeNames(this, element);
        for (var i = 0; i < attributeNames.length; i++) {
          var attrName = attributeNames[i];
          var attrValue = element.getAttribute(attributeNames[i]);
          result += ' ' + attrName + '="' + attrValue + '"';
        }
        
        var childNodes = this._getElementChildNodes(element);
        var descendantXML = '';
        for ( var i = 0; i < childNodes.length; i++)
          descendantXML += this._getElementAsXML(childNodes[i]);
  
        if (descendantXML == '')
          result += '/>';
        else
          result += '>' + descendantXML + '</' + element.tagName + '>';
      }
    }

    return result;
  }
});

Object.extend(fi.foyt.svg.svgdom.FNISVGDocument.prototype, fni.events.FNIEventSupport);fi.foyt.svg.svgdom.FNISVGDOMUtils = {
  /**
   * Utility method to calculate on screen bounding box
   *
   * source is taken from
   *
   * http://the.fuchsia-design.com/2006/12/getting-svg-elementss-full-bounding-box.html
   *
   * @param {Object} element
   */
  getScreenBBox: function(element){
    /** get the complete transformation matrix **/
    
    var matrix = this.getTransformToElement(element, element.viewportElement);
    // get the bounding box of the target element
    var box = element.getBBox();
    // create an array of SVGPoints for each corner
    // of the bounding box and update their location
    // with the transform matrix
    var corners = [];
    var point = this._createSVGPoint(element, box.x, box.y);
    corners.push(point.matrixTransform(matrix));
    point.x = box.x + box.width;
    point.y = box.y;
    corners.push(point.matrixTransform(matrix));
    point.x = box.x + box.width;
    point.y = box.y + box.height;
    corners.push(point.matrixTransform(matrix));
    point.x = box.x;
    point.y = box.y + box.height;
    corners.push(point.matrixTransform(matrix));
    var max = this._createSVGPoint(element, corners[0].x, corners[0].y);
    var min = this._createSVGPoint(element, corners[0].x, corners[0].y);
    // identify the new corner coordinates of the
    // fully transformed bounding box
    for (var i = 1; i < corners.length; i++) {
      var x = corners[i].x;
      var y = corners[i].y;
      if (x < min.x) {
        min.x = x;
      } else if (x > max.x) {
        max.x = x;
      }
      if (y < min.y) {
        min.y = y;
      } else if (y > max.y) {
        max.y = y;
      }
    }
    // return the bounding box as an SVGRect object
    return this._createSVGRect(element, min.x, min.y, max.x - min.x, max.y - min.y);
  },
  /**
   * Returns screen bounding box for array of svg elements
   *
   * @param {Array} elements Array of elements
   * @return {SVGRect}
   */
  getElementSetScreenBBox: function(elements){
    try {
      if (!elements)
      return null;
    
    if (elements.length > 0) {
        var elementBBox = FNISVGDOMUtils.getScreenBBox(elements[0]);
        var minX = elementBBox.x;
        var minY = elementBBox.y;
        var maxX = elementBBox.x + elementBBox.width;
        var maxY = elementBBox.y + elementBBox.height;
        for (var i = 1; i < elements.length; i++) {
          elementBBox = FNISVGDOMUtils.getScreenBBox(elements[i]);
          minX = Math.min(minX, elementBBox.x);
          minY = Math.min(minY, elementBBox.y);
          maxX = Math.max(maxX, elementBBox.x + elementBBox.width);
          maxY = Math.max(maxY, elementBBox.y + elementBBox.height);
        }
    
    return this._createSVGRect(elements[0], minX, minY, maxX - minX, maxY - minY);
      }
      
    return null;
    } catch (e) {
      throw new Error("FNISVGDOMUtils::getElementSetScreenBBox: " + e);
    }
  },
  createElementFromJSON: function (svgDocument, aJSON) {
    var JSON = (Object.isString(aJSON)) ? aJSON.evalJSON() : aJSON;  
    
    var element = svgDocument.createElement(JSON.element);
    
    for (var i in JSON.attributes) 
      element.setAttribute(i, JSON.attributes[i]);
    
    return element;
  },
  removeIllegalAttributes: function (svgDocument, element) {
    var attributeNames = this.getElementAttributeNames(svgDocument, element);
    for (var i = 0; i < attributeNames.length; i++) {
      var attrName = attributeNames[i];
      var found = false;
      
      var attrs = this._LegalElementAttributes[element.tagName];
      
      var j = attrs.length;
      while ((j > 0) && (found == false)) {
        j--;
        if (attrs[j] == attrName) {
          found = true;
        }        
      }
       
      if (found == false)
        element.removeAttribute(attrName);
    }
  },
  getElementAttributeNames: function (svgDocument, element) {
    var attributeNames = new Array();
  
    for (var i = 0; i < element.attributes.length; i++)
      attributeNames.push(element.attributes[i].name);
    
    return attributeNames;
  },
  isValidSVGElement: function (element) {
    for (var i = 0; i < this._ValidSVGElements.length; i++) {
      if (this._ValidSVGElements[i] == element.tagName)
        return true;
    }
    return false;
  },
  /**
   * Private method to create SVGPoint object
   *
   * source is taken from
   *
   * http://the.fuchsia-design.com/2006/12/getting-svg-elementss-full-bounding-box.html
   *
   * @param {x} point x
   * @param {y} point y
   */
  _createSVGPoint: function(element, x, y){
    var point = element.viewportElement.createSVGPoint(); 
    point.x = x;
    point.y = y;
    return point;
  },
  /**
   * Private method to create SVGRect object
   *
   * source is taken from
   *
   * http://the.fuchsia-design.com/2006/12/getting-svg-elementss-full-bounding-box.html
   *
   * @param {x} rect x
   * @param {y} rect y
   * @param {width} rect width
   * @param {height} rect height
   */
  _createSVGRect: function(element, x, y, width, height){    
    var rect = element.viewportElement.createSVGRect();    
    rect.x = x;
    rect.y = y;
    rect.width = width;
    rect.height = height;
    return rect;
  },
  _LegalElementAttributes: {
    'rect': ['id','transform','style','x','y','rx','ry','width','height'],       
    'circle': ['id','transform','style','cx','cy','r'],
    'ellipse': ['id','transform','style','cx','cy','rx','ry'],
    'line': ['id','transform','style','x1', 'y1', 'x2', 'y2'],
    'polygon': ['id','transform','style','points'],
    'polyline': ['id','transform','style','points'],
    'path': ['id','transform','style','d'],
    'g': ['id','transform','style', 'fnigrouptype', 'fnilayertitle']
  },
  _ValidSVGElements: ['rect','circle','ellipse','line','polyline','polygon','path','g', 'defs', 'linearGradient', 'radialGradient', 'stop'],
  getTransformToElement: function (element, toElement) {
    return element.getTransformToElement(toElement);
  }
}; fi.foyt.svg.svgdom.FNISVGElementController = Class.create({
  initialize: function(){
  },
  moveBy: function(element, x, y){
    throw new Error("Element does not support moveBy operation");
  },
  moveTo: function(element, x, y){
    throw new Error("Element does not support moveTo operation");
  },
  resizeBy: function(element, x, y){
    throw new Error("Element does not support resizeBy operation");
  },
  resizeTo: function(element, x, y){
    throw new Error("Element does not support resizeTo operation");
  },
  rotateBy: function(element, ang){
    throw new Error("Element does not support rotateBy operation");
  },
  rotateTo: function(element, ang){
    throw new Error("Element does not support rotateTo operation");
  },
  supports: function (operation) {
    return false;
  }
});
 
Object.extend(fi.foyt.svg.svgdom.FNISVGElementController, {
  OPERATION_MOVE: 'move',
  OPERATION_RESIZE: 'resize',
  OPERATION_ROTATE: 'rotate'
});fi.foyt.svg.svgdom.FNISVGCircleElementController = Class.create(fi.foyt.svg.svgdom.FNISVGElementController, {
  initialize: function($super){
  }
});fi.foyt.svg.svgdom.FNISVGEllipseElementController = Class.create(fi.foyt.svg.svgdom.FNISVGElementController, {
  initialize: function($super){
  },
  moveTo: function(element, x, y){
  var cRx = parseFloat(element.getAttribute("rx"));
  if (isNaN(cRx))
    cRx = 0;
  var cRy = parseFloat(element.getAttribute("ry"));
  if (isNaN(cRy))
    cRy = 0;
  
    element.setAttribute("cx", x + cRx);
    element.setAttribute("cy", y + cRy);
  },
  moveBy: function (element, x, y) {
    var bbox = element.getBBox();
  this.moveTo(element, bbox.x + x, bbox.y + y);
  },
  resizeTo: function(element, width, height){ 
  var oRadiusX = parseFloat(element.getAttribute("rx"));
  if (isNaN(oRadiusX))
    oRadiusX = 0;
  var oRadiusY = parseFloat(element.getAttribute("ry"));
  if (isNaN(oRadiusY))
    oRadiusY = 0;
    
  var oPosX = parseFloat(element.getAttribute("cx"));
  if (isNaN(oPosX))
    oPosX = 0;
  var oPosY = parseFloat(element.getAttribute("cy"));
  if (isNaN(oPosY))
    oPosY = 0;
    
  var realPosX = oPosX - oRadiusX;
  var realPosY = oPosY - oRadiusY;  
  var w = width / 2;
  var h = height / 2;
  
  element.setAttribute("rx", w);
    element.setAttribute("ry", h);  
  element.setAttribute("cx", realPosX + w);
    element.setAttribute("cy", realPosY + h); 
  },
  resizeBy: function(element, scaleX, scaleY){
    var cRx = parseFloat(element.getAttribute("rx"));
  var cRy = parseFloat(element.getAttribute("ry"));
  var nRx = cRx * scaleX;
  var nRy = cRy * scaleY;
  
  this.moveBy(element, nRx - cRx, nRy - cRy);
  element.setAttribute("rx", nRx);        
    element.setAttribute("ry", nRy);  
  },
  supports: function (operation) {
    return ((operation == fi.foyt.svg.svgdom.FNISVGElementController.OPERATION_MOVE)||(operation == fi.foyt.svg.svgdom.FNISVGElementController.OPERATION_RESIZE));
  }
});fi.foyt.svg.svgdom.FNISVGLineElementController = Class.create(fi.foyt.svg.svgdom.FNISVGElementController, {
  initialize: function($super){
  },
  moveTo: function(element, x, y){
    try {
    var bbox = element.getBBox();
    element.setAttributeNS(null, 'x1', x + 'px');
      element.setAttributeNS(null, 'y1', y + 'px');
    element.setAttributeNS(null, 'x2', (bbox.width + x) + 'px');
      element.setAttributeNS(null, 'y2', (bbox.height + y) + 'px');
  } catch (e) {
    throw new Error("FNISVGLineElementController::moveTo: " + e);
  }
  },
  resizeTo: function(element, width, height){
  try {  
    var bbox = element.getBBox();   
    element.setAttributeNS(null, 'x2', (bbox.x + width) + 'px');
      element.setAttributeNS(null, 'y2', (bbox.y + height) + 'px');
  } catch (e) {
    throw new Error("FNISVGLineElementController::resizeTo: " + e);
  }
  },
  moveBy: function (element, x, y) {
    try {
    var bbox = element.getBBox();
    this.moveTo(element, bbox.x + x, bbox.y + y);
  } catch (e) {
    throw new Error("FNISVGLineElementController::moveBy: " + e);
  }
  },
  resizeBy: function(element, ratioX, ratioY){
  try {  
    var bbox = element.getBBox();   
    var width = bbox.width * ratioX;
    var height = bbox.height * ratioY;
    this.resizeTo(element, width, height);        
  } catch (e) {
    throw new Error("FNISVGLineElementController::resizeBy: " + e);
  }
  },
  setLine: function (element, x1, y1, x2, y2) {
    if (x1)
    element.setAttributeNS(null, 'x1', x1 + 'px');
  if (y1)
      element.setAttributeNS(null, 'y1', y1 + 'px');
  if (x2)
    element.setAttributeNS(null, 'x2', x2 + 'px');
  if (y2)
      element.setAttributeNS(null, 'y2', y2 + 'px');
  },
  supports: function (operation) {
    return ((operation == fi.foyt.svg.svgdom.FNISVGElementController.OPERATION_MOVE)||(operation == fi.foyt.svg.svgdom.FNISVGElementController.OPERATION_RESIZE));
  }
});fi.foyt.svg.svgdom.FNISVGPolygonElementController = Class.create(fi.foyt.svg.svgdom.FNISVGElementController, {
  initialize: function($super){
  }
});fi.foyt.svg.svgdom.FNISVGPolylineElementController = Class.create(fi.foyt.svg.svgdom.FNISVGElementController, {
  initialize: function($super){
  }
});fi.foyt.svg.svgdom.FNISVGRectElementController = Class.create(fi.foyt.svg.svgdom.FNISVGElementController, {
  initialize: function($super){
  },
  moveBy: function(element, x, y){
    try {  
    var elementBo = element.getBBox();
    this.moveTo(element, elementBo.x + x, elementBo.y + y);
  } catch (e) {
    throw new Error("FNISVGRectElementController::moveBy: " + e);
  } 
  },
  moveTo: function(element, x, y){
    try {  
    element.setAttribute('x', x);
      element.setAttribute('y', y);
  } catch (e) {
    throw new Error("FNISVGRectElementController::moveTo: " + e);
  }
  },
  resizeBy: function(element, scaleX, scaleY) {
    try {  
      var elementBo = element.getBBox();
    this.resizeTo(element, elementBo.width * scaleX, elementBo.height * scaleY);
  } catch (e) {
    throw new Error("FNISVGRectElementController::resizeBy: " + e);
  }
  },
  resizeTo: function(element, width, height){
    try {
    element.setAttribute('width', width);
      element.setAttribute('height', height);
  } catch (e) {
    throw new Error("FNISVGRectElementController::resizeTo: " + e);
  }
  },
  supports: function (operation) {
    return ((operation == fi.foyt.svg.svgdom.FNISVGElementController.OPERATION_MOVE)||(operation == fi.foyt.svg.svgdom.FNISVGElementController.OPERATION_RESIZE));
  }
});
 fi.foyt.svg.svgdom.FNISVGPathElementSegment = Class.create({
  initialize: function(command, absolute, parameters){
    this.command = command;
    this.absolute = absolute;
    this.parameters = parameters == null ? new Array() : parameters;
  },
  addParameter: function (x, y) {
    this.parameters.push({x: x, y: y});
  },
  getParameters: function(){
    return this.parameters;
  },
  isAbsolute: function(){
    return this.absolute;
  },
  toString: function(){     
  var result = (this.isAbsolute() ? this.command : this.command.toLowerCase()) + ((this.parameters.length > 0) ? this.parameters[0].x + ' ' + this.parameters[0].y : '');    
  for (var i = 1; i < this.parameters.length; i++) 
      result += ' ' + this.parameters[i].x + ' ' + this.parameters[i].y;
    return result;
  }
});fi.foyt.svg.svgdom.FNISVGPathParser = Class.create({
  initialize: function(){
    this.commands = {
      // moveto
      M: ['x', 'y'],
      // lineto
      L: ['x', 'y'],
      // horizontal lineto
      H: ['x'],
      // vertical lineto
      V: ['y'],
      // curveto
      C: ['x1', 'y1', 'x2', 'y2', 'x', 'y'],
      // smooth curveto
      S: ['x2', 'y2', 'x', 'y'],
      // quadratic Belzier curve
      Q: ['x1', 'y1', 'x', 'y'],
      // smooth quadratic Belzier curveto
      T: ['x', 'y'],
      // elliptical Arc
      A: ['rx', 'ry', 'x-axis-rotation', 'large-arc-flag', 'sweep-flag', 'x', 'y'],
      // closepath
      Z: []
    };
  }, 
  parsePath: function (element) {
    var path = element.getAttribute("d");
    if ((path == null) || (path.blank()))
      return new Array();
    
    var d = path.replace(/,/g, ' ');
    var tokens = d.split(" ");
    var segments = new Array();
    var segment = null;
    var tmp = true;
    var x = null;
    var y = null;
    
    for (var i = 0; i < tokens.length; i++) {
      var token = tokens[i];
      // command 
      
      if (token.charAt(0).search("[a-zA-Z]") == 0) {
        var isAbsolute = token.charAt(0).search("[A-Z]") == 0;
        var command = isAbsolute ? token.charAt(0) : token.charAt(0).toLowerCase();
        segment = new FNISVGPathElementSegment(command, isAbsolute);
        segments.push(segment);        
        pIndex = 0;
        
        if (token.length > 1) {
          x = new Number(token.substring(1));
          tmp = false;
        }
      } else {
        tmp = !tmp;
        if (tmp == true)
          y = new Number(token);
        else 
          x = new Number(token);
      }
       
    if ((x != null) && (y != null)) {
      segment.addParameter(x, y);
    x = null;
      y = null;   
    } 
  }
    
    return segments;
  }
});/*
 * Matrix: 
 * 
 * [ a c e ]
 * [ b d f ]
 * 
 * tx,ty: translation 
 * sx,sy: scaling
 *  
 * [sx 00 tx]
 * [00 sy ty]
 * [00 00 01]
 * 
 * Scale
 * 
 * [ cos(a) -sin(a)  0 ]
 * [ sin(a)  cos(a)  0 ]
 * [   0       0     1 ]
 *  // or while rotating the coordinate system axes by angle a. 
 * [  cos(a) sin(a)  0 ] 
 * [ -sin(a) cos(a)  0 ]
 * [   0       0     1 ]
 * 
 */
fi.foyt.svg.svgdom.FNISVGPathElementController = Class.create(fi.foyt.svg.svgdom.FNISVGElementController, {
  initialize: function($super){
    this.pathParser = new fi.foyt.svg.svgdom.FNISVGPathParser();
  },
  getPathSegmentsObject: function(element){
    if (element.segments == null)
      element.segments = this.pathParser.parsePath(element);
    
    return element.segments;
  },
  appendSegment: function(element, segment){
    this.getPathSegmentsObject(element).push(segment);
    this.markPathChanged(element);
  },
  getSegments: function(element){
    return this.getPathSegmentsObject(element);
  },
  markPathChanged: function (element) {
    element.pathChanged = true;
  },
  updatePath: function(element){
    if ((element.segments != null) && (element.pathChanged == true)) {
      if (element.segments.length > 0) {
    var d = element.segments[0].toString();
    for (var i = 1; i < element.segments.length; i++) 
      d += ' ' + element.segments[i].toString();
    element.setAttributeNS(null, 'd', d);
    }
      element.pathChanged = false;
    }
  },
  reparsePath: function (element) {
    element.segments = this.pathParser.parsePath(element);
    this.markPathChanged(element);
  },
  moveBy: function(element, x, y) {
    try {
    var segments = this.getSegments(element);
    
    for (var i = 0; i < segments.length; i++) {
      var segment = segments[i];
      var parameters = segment.getParameters(); 
      for (var j = 0; j < parameters.length; j++) {
        var parameter = parameters[j];
        parameter.x += x;
        parameter.y += y; 
      }
    }
    element.pathChanged = true;
    
    this.updatePath(element);
  } catch (e) {
    throw new Error("FNISVGPathElementController::moveBy: " + e);
  }
  },
  moveTo: function(element, x, y){
    try {
    var bbox = element.getBBox();
      this.moveBy(element, x - bbox.x, y - bbox.y);
  } catch (e) {
    throw new Error("FNISVGPathElementController::moveTo: " + e);
  }
  },
  resizeTo: function (element, width, height) {
  try {
      var elementBo = element.getBBox();
    if ((elementBo.width > 0) && (elementBo.height > 0)) {
    var scaleX = width / elementBo.width;
    var scaleY = height / elementBo.height;   
    this.resizeBy(element, scaleX, scaleY);
    } 
  } catch (e) {
    throw new Error("FNISVGPathElementController::resizeTo: " + e);
  }
  },
  resizeBy: function (element, scaleX, scaleY) {
    try {
    var elementBo = element.getBBox();  
      var xTrans = (elementBo.x * scaleX) - elementBo.x;
      var yTrans = (elementBo.y * scaleY) - elementBo.y;    
      var segments = this.getSegments(element);
    
    for (var i = 0; i < segments.length; i++) {
        var segment = segments[i]; 
        var parameters = segment.getParameters(); 
        for (var j = 0; j < parameters.length; j++) {
          var parameter = parameters[j];
          parameter.x = (parameter.x * scaleX) - xTrans;
          parameter.y = (parameter.y * scaleY) - yTrans; 
        }
      }
    
    element.pathChanged = true;  
    this.updatePath(element);
  } catch (e) {
    throw new Error("FNISVGPathElementController::resizeBy: " + e);
  }
  },
  rotateBy: function (element, angle, originX, originY) {
    try {
    var sAngle = Math.sin(angle);
      var cAngle = Math.cos(angle);
    
      var segments = this.getSegments(element);
      for (var i = 0; i < segments.length; i++) {
        var segment = segments[i]; 
        var parameters = segment.getParameters(); 
        for (var j = 0; j < parameters.length; j++) {
          var parameter = parameters[j];      
          var x = parseFloat(parameter.x);
          var y = parseFloat(parameter.y);
      
          parameter.x = originX + cAngle * (x - originX) - sAngle * (y - originY);
          parameter.y = originY + sAngle * (x - originX) + cAngle *  (y - originY);
        }
      }
  
    element.pathChanged = true;  
    this.updatePath(element);
  } catch (e) {
    throw new Error("FNISVGPathElementController::rotateBy: " + e);
  }
  },
  supports: function (operation) {
    return ((operation == fi.foyt.svg.svgdom.FNISVGElementController.OPERATION_MOVE)||(operation == fi.foyt.svg.svgdom.FNISVGElementController.OPERATION_RESIZE)||(operation == fi.foyt.svg.svgdom.FNISVGElementController.OPERATION_ROTATE));
  }
});fi.foyt.svg.svgdom.FNISVGGroupElementController = Class.create(fi.foyt.svg.svgdom.FNISVGElementController, {
  initialize: function($super){
  },
  moveBy: function(element, x, y){
    try {
    var matrix = FNISVGDOMUtils.getTransformToElement(element, element.viewportElement);    
    matrix = matrix.translate(x / matrix.a, y / matrix.d);
    element.setAttribute("transform", 'matrix(' + [matrix.a, matrix.b, matrix.c, matrix.d, matrix.e, matrix.f].join(' ') + ')');    
  } catch (e) {
      throw new Error("FNISVGGroupElementController::moveBy: " + e);
    }
  },
  moveTo: function(element, x, y){
    try {
    var bbox = FNISVGDOMUtils.getScreenBBox(element);  
    this.moveBy(element, x - bbox.x, y - bbox.y);
    } catch (e) {
      throw new Error("FNISVGGroupElementController::moveTo: " + e);
    }
  },
  resizeBy: function(element, scaleX, scaleY) {
    try {
    if ((scaleX != 1) && (scaleY != 1)) {
    var bbox = element.getBBox();
    var matrix = FNISVGDOMUtils.getTransformToElement(element, element.viewportElement).scaleNonUniform(scaleX, scaleY);
    var transform = 'matrix(' +[matrix.a,matrix.b,matrix.c,matrix.d,matrix.e,matrix.f] .join(',') + ')' + ' translate(' + [(bbox.x / scaleX) - bbox.x, (bbox.y / scaleY) - bbox.y] + ')';
    element.setAttribute("transform", transform);
    }
    } catch (e) {
      throw new Error("FNISVGGroupElementController::resizeBy: " + e);
    }
  },
  resizeTo: function(element, width, height){
    try {      
    var bbox = FNISVGDOMUtils.getScreenBBox(element);
    this.resizeBy(element, width / bbox.width, height / bbox.height); 
    } catch (e) {
      throw new Error("FNISVGGroupElementController::resizeTo: " + e);
    }
  },
  supports: function (operation) {
    return ((operation == fi.foyt.svg.svgdom.FNISVGElementController.OPERATION_MOVE)||(operation == fi.foyt.svg.svgdom.FNISVGElementController.OPERATION_RESIZE));
  }
});fi.foyt.svg.svgdom.FNISVGElementControllerVaultClass = Class.create({
  initialize : function() {
    this._elementControllers = new Hash();
    this._registerElementController('rect', new fi.foyt.svg.svgdom.FNISVGRectElementController());
    this._registerElementController('circle', new fi.foyt.svg.svgdom.FNISVGCircleElementController());
    this._registerElementController('ellipse', new fi.foyt.svg.svgdom.FNISVGEllipseElementController());
    this._registerElementController('line', new fi.foyt.svg.svgdom.FNISVGLineElementController());
    this._registerElementController('polygon', new fi.foyt.svg.svgdom.FNISVGPolygonElementController());
    this._registerElementController('polyline', new fi.foyt.svg.svgdom.FNISVGPolylineElementController());
    this._registerElementController('path', new fi.foyt.svg.svgdom.FNISVGPathElementController());
    this._registerElementController('g', new fi.foyt.svg.svgdom.FNISVGGroupElementController());
  },
  getElementControllerFor : function(element) {
    if (element != null)
      return this.getElementController(element.tagName.toUpperCase());
    else
      throw new Error("Cannot return element controller for null element");
  },
  getElementController : function(elementName) {
    var controller = this._elementControllers.get(elementName.toUpperCase());
    if (!controller)
      throw new Error("Could not find elementcontroller for: " + elementName);
    else
      return controller;
  },
  _registerElementController : function(elementName, controller) {
    this._elementControllers.set(elementName.toUpperCase(), controller);
  }
});

window._svgElementControllerVault = null;

window.getSVGElementControllerVault = function() {
  if (!window._svgElementControllerVault) {
    window._svgElementControllerVault = new fi.foyt.svg.svgdom.FNISVGElementControllerVaultClass();
  }

  return window._svgElementControllerVault;
};