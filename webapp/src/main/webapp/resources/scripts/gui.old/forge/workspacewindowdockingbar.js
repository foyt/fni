ForgeWorkspaceWindowDockingBar = Class.create(GUIComponent, {
  initialize: function($super, options){
    $super(options);
    this.domNode = new Element("div", {className: 'forgeWorkspaceWindowDockingBar'});
    this._windows = new Hash();    
  },
  destroy: function ($super) {
    // TODO: Clear this._windows
    
    $super();
  },
  dockWindow: function (window) {
    if (window.fire("dock", {})) {
      var dockedWindow = new ForgeDockedWorkspaceWindow({
        window: window
      });
      
      this._windows.set(window.getComponentId(), dockedWindow);
      window.dockedTo(this);
      
      this.addGUIComponent(this.domNode, dockedWindow);
      
      dockedWindow.addListener("click", this, this._onDockedWindowClick);
      
      this._transitionEffect(window.domNode, dockedWindow.domNode);
      
      this.fire("windowDocked", {
        window: window
      });
    }
  },
  undockWindow: function (window) {
    if (window.fire("undock", {})) {
      var dockedWindow = this.getDockedWindow(window);
      window.show();
      
      var _this = this;
      this._transitionEffect(dockedWindow.domNode, window.domNode, function () {
        _this.removeGUIComponent(dockedWindow);
        window.undocked();
        _this.fire("windowUndocked", {
          window: window
        });
        
        window.fire("resize", {});
      });
    }
  },
  getDockedWindow: function (window) {
    return this._windows.get(window.getComponentId());
  },
  illuminateDockedWindow: function (window) {
    var dockedWindow = this.getDockedWindow(window);
    dockedWindow.illuminate();    
  },
  _onDockedWindowClick: function (event) {
    this.undockWindow(event.window);
  },
  _transitionEffect: function (fromElement, toElement, afterFinish) {
    var documentBody = document.getElementsByTagName('body')[0];
    
    toElement.setStyle({
      opacity: 0,
      display: ''
    });
    
    var fDims = Element.getDimensions(fromElement);
    var fOffs = Element.cumulativeOffset(fromElement);    
    var tDims = Element.getDimensions(toElement);
    var tOffs = Element.cumulativeOffset(toElement);    

    fromElement.hide();
    
    var scaleEffectDiv = new Element("div", {className:"forgeWorkspaceWindowMinimizing"});
    scaleEffectDiv.setStyle({
      width: (fDims.width - 2) + 'px',
      height: (fDims.height - 2) + 'px',
      top: fOffs.top + 'px',
      left: fOffs.left + 'px'
    });
    
    var morphStyle = 
      "width:" + tDims.width + 'px;' + 
      "height:" + tDims.height + 'px;' +
      "top:" + tOffs.top + 'px;' +
      "left:" + tOffs.left + 'px;';
      
    documentBody.appendChild(scaleEffectDiv);
    scaleEffectDiv.morph(morphStyle, {
      duration: 0.75,
      after: function () {
        scaleEffectDiv.remove();
        toElement.morph("opacity: 1", {
          duration: 0.1,
          after: function(){
            if (afterFinish)
              afterFinish();
          } 
        });
      } 
    });
  }
});
