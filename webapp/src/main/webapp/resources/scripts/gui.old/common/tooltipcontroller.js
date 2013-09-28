TooltipController = Class.create({
  initialize: function () {
    this._tooltip = new Tooltip({
      opacity: 0.8,
      appearDuration: 0.3,
      fadeDuration: 0.3,
      offsetX: 20,
      offsetY: -10
    });
    
    this._domSubtreeModifiedListener = this._onDomSubtreeModified.bindAsEventListener(this);
    this._replaceTitles();
    
    if (Prototype.Browser.WebKit||Prototype.Browser.Gecko) {
      Event.observe(document, 'DOMSubtreeModified', this._domSubtreeModifiedListener);
      this._checkOriginals = false;
    } else {
      this._checkOriginals = true;
    }
    
    this._mouseMoveListener = this._onMouseMove.bindAsEventListener(this);
    
    Event.observe(Prototype.Browser.IE ? document : window, "mousemove", this._mouseMoveListener);
  },
  _replaceTitles: function () {
    if (Prototype.Browser.WebKit||Prototype.Browser.Gecko) {
      Event.stopObserving(document, 'DOMSubtreeModified', this._domSubtreeModifiedListener);
    }
    
    try {
      var titleElements = $$('[title]');
      if (titleElements) {
        for (var i = titleElements.length - 1; i >= 0; i--) {
          var titleElement = titleElements[i]; 
          var titleText = titleElement.readAttribute('title');
          if (titleText) {
            titleElement.writeAttribute({
              title: null,
              fnititle: titleText
            });
          }
        }
      }
    } finally {
      if (Prototype.Browser.WebKit||Prototype.Browser.Gecko) {
        Event.observe(document, 'DOMSubtreeModified', this._domSubtreeModifiedListener);
      }
    }
  },
  _onMouseMove:function(event) {
    var element = Event.element(event);
    
    if (typeof element.up != 'function') {
      this._tooltip.hide();
      return;
    }
    
    var pointerX = Event.pointerX(event); 
    var pointerY = Event.pointerY(event);
    
    var titleElement = undefined;
    var fniTitleElement = undefined;
    
    var titleFound = false; 
    
    if (element.hasAttribute('fnititle')) {
      fniTitleElement = element;
      titleFound = true;      
    } else { 
      if (this._checkOriginals && element.hasAttribute('title')) {
        titleElement = element;
        titleFound = true;
      }
    }
    
    if (!titleFound) {
      fniTitleElement = element.up('[fnititle]');
      if (!fniTitleElement) {
        if (this._checkOriginals) {
          titleElement = element.up('[title]');
          if (titleElement)
            titleFound = true;
        }
      } else {
        titleFound = true;
      }
    }
    
    if (!titleFound) {
      fniTitleElement = this._findDescendantTitle(element, 'fnititle', pointerX, pointerY);
      if (!fniTitleElement) {
        if (this._checkOriginals) {
          titleElement = this._findDescendantTitle(element, 'title', pointerX, pointerY);
          if (titleElement)
            titleFound = true;
        }
      } else {
        titleFound = true;
      }
    }
    
    if (titleFound) {
      var titleText = undefined;
      
      if (titleElement) {
        titleText = titleElement.readAttribute('title');
        titleElement.writeAttribute({
          title: null,
          fnititle: titleText
        });

      } else if (fniTitleElement) {
        titleText = fniTitleElement.readAttribute('fnititle');
      }
      
      if ((titleText != undefined) && (titleText.length > 0)) {
        titleText = titleText.strip();
      
        if ((titleText != undefined) && (titleText.length > 0)) {
          this._tooltip.setText(titleText);
          this._tooltip.show();
          this._tooltip.setPosition(pointerX, pointerY);
        } else {
          this._tooltip.hide();
        }
        
      }
    } else {
      this._tooltip.hide();      
    }
  },
  _findDescendantTitle: function (element, attributeName, pointerX, pointerY) {
    var titleElements = Element.select(element, '[' + attributeName + ']');
    if (titleElements) {
      for (var i = 0, l = titleElements.length; i < l; i++){
        var titleElement = titleElements[i];
        var dimensions = titleElement.getDimensions();
        var offset = titleElement.positionedOffset();
        
        var top = offset.top;
        var left = offset.left;
        var bottom = (offset.top + dimensions.height);
        var right = (offset.left + dimensions.width);
        
        if ((pointerY >= top) && (pointerY <= bottom) && (pointerX >= left) && (pointerX <= right)) {
          return titleElement;
        }  
      }
    }
    
    return null;
  },
  _onDomSubtreeModified: function (event) {
    this._replaceTitles();
  }
});

function initTooltip() {
  new TooltipController();
}