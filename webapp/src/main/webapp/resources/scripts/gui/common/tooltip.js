Tooltip = Class.create({
  initialize : function(options) {
    this._options = options;
    this._offsetX = this._options.offsetX || 0;
    this._offsetY = this._options.offsetY || 0;
    this._hidden = true;
    
    this._contentElement = new Element("div", {
      className : "tooltipContent"
    });
    
    this._opacity = this._options.opacity ? this._options.opacity : 1.0;

    this._contentInnerContainer = new Element("div", { className : "tooltipInnerContentContainer" }); 
    this._contentInnerContainer.appendChild(this._contentElement);
    this._contentContainer = new Element("div", { className : "tooltipContentContainer" });
    this._contentContainer.appendChild(this._contentInnerContainer);
    
    this.domNode = new Element("div", { className : "tooltip", style : 'opacity: ' + this._opacity });
    this.domNode.appendChild(this._contentContainer);

    if (options.text)
      this.setText(options.text);

    this._domNodeClickListener = this._onDomNodeClicked.bindAsEventListener(this);
    Event.observe(this.domNode, "click", this._domNodeClickListener);

    document.getElementsByTagName('body')[0].appendChild(this.domNode);
  },
  setPosition : function(x, y) {
    var domNodeW = this.domNode.getWidth(); 
    if ((domNodeW + x + this._offsetX) > document.viewport.getWidth()) {
      this.domNode.setStyle({
        left : (x - domNodeW - this._offsetX) + 'px',
        top : (y + this._offsetY) + 'px'
      });
    } else {
      this.domNode.setStyle({
        left : (x + this._offsetX) + 'px',
        top : (y + this._offsetY) + 'px'
      });
    }
  },
  show: function () {
    this.domNode.show();
  },
  hide: function () {
    this.domNode.hide();
  },
  remove : function() {
    this.domNode.remove();
    this.deinitialize();
  },
  setText: function (text) {
    if (text !== this._contentElement.innerHTML) {
      this._contentElement.update(text.replace(new RegExp(decodeURIComponent('%0d'), 'g'), '<br/>'));
      this._evaluateWidth();
    }
  },
  _onDomNodeClicked : function(event) {
    this.fire("clicked", {});
  },
  _evaluateWidth: function () {
    this._contentInnerContainer.setStyle({
      maxWidth: '9999'
    });
    
    var contentElements = this._contentElement.childNodes;
    var contentWidth = 0;
    for (var i = 0; i < contentElements.length; i++) {
      var element = contentElements[i];
      var elementWidth = element.nodeType == 3 ? this._getTextNodeWidth(element) : element.getWidth();
      if (elementWidth > contentWidth)
        contentWidth = elementWidth;
    }
    
    if (contentWidth > this._contentMaxWidth) {
      this._contentInnerContainer.setStyle({
        maxWidth: contentWidth + 'px'
      });
    } else {
      this._contentInnerContainer.setStyle({
        maxWidth: ''
      });
    }
  },
  _getTextNodeWidth: function (element) {
    if (element.nodeType == 3) {
      var e = $(element);
      var parent = e.parentNode;
      if (parent != null) {
        var nextSibling = e.nextSibling;
        var wrapElement = new Element("span");
        
        if (nextSibling)
          parent.insertBefore(wrapElement, nextSibling);
        else 
          parent.appendChild(wrapElement);
        wrapElement.appendChild(e);
        
        var width = wrapElement.getWidth();
        parent.insertBefore(e, wrapElement);
        wrapElement.remove();
        
        return width;
      } else {
        return 0;
      }
    } 
  }
});