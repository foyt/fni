ForgeWorkspaceWindow = Class.create(GUIComponent, {
  initialize: function($super, options) {
    $super(Object.extend({
      maximizeButton: true,
      minimizeButton: true,
      closeButton: true,
      className: ''
    }, options));
	
    this.domNode = new Element("div", {
      className: 'forgeWorkspaceWindow'
    });
    
    if (this.getComponentOptions().className) {
      this.domNode.addClassName(this.getComponentOptions().className); 
    }

    this._closeButtonClickListener = this._onCloseButtonClick.bindAsEventListener(this);
    this._minimizeButtonClickListener = this._onMinimizeButtonClick.bindAsEventListener(this);
    this._maximizeButtonClickListener = this._onMaximizeButtonClick.bindAsEventListener(this);
    
    this._titleUpdating = false;
    this._maximized = false;
    this._loaded = false;
	
    this._titleBarElement = new Element("div", {className: 'forgeWorkspaceWindowTitleBar'});
    
  	this._titleElement = $(document.createElement("div"));
	  this._titleElement.addClassName('forgeWorkspaceWindowTitle');
    this._iconElement = $(document.createElement("div"));
    this._iconElement.addClassName('forgeWorkspaceWindowIcon');
//    this._iconElement.hide();
    this._titleBarElement.appendChild(this._iconElement);

    if (this.getComponentOptions().iconURL) 
      this.setIconURL(this.getComponentOptions().iconURL);

    this._titleBtnsElement = $(document.createElement("div"));
    this._titleBtnsElement.addClassName('forgeWorkspaceWindowButtons');
    this._titleBarElement.appendChild(this._titleElement);
    this._titleBarElement.appendChild(this._titleBtnsElement);
    
    if (this.getComponentOptions().closeButton === true) {
      this._closeButton = new Element("div");
      this._titleBtnsElement.appendChild(this._closeButton);
      new S2.UI.Button(this._closeButton, {
        text : false,
        icons : {
          primary : 'ui-icon-closethick'
        }
      });
      Event.observe(this._closeButton, "click", this._closeButtonClickListener);
    }
    
    if (this.getComponentOptions().minimizeButton === true) {
      this._minimizeButton = new Element("div");
      this._titleBtnsElement.appendChild(this._minimizeButton);
      new S2.UI.Button(this._minimizeButton, {
        text : false,
        icons : {
          primary : 'ui-icon-minusthick'
        }
      });
      Event.observe(this._minimizeButton, "click", this._minimizeButtonClickListener);
    }
    
    if (this.getComponentOptions().maximizeButton === true) {
      this._maximizeButton = new Element("div");
      this._titleBtnsElement.appendChild(this._maximizeButton);
      new S2.UI.Button(this._maximizeButton, {
        text : false,
        icons : {
          primary : 'ui-icon-plusthick'
        }
      });    
      Event.observe(this._maximizeButton, "click", this._maximizeButtonClickListener);
    }

    this.domNode.appendChild(this._titleBarElement);

    if (this.getComponentOptions().title)
      this.setTitle(this.getComponentOptions().title);
	
	  this._contentElement = new Element("div");
    this._contentElement.addClassName('forgeWorkspaceWindowContent');
    this.domNode.appendChild(this._contentElement);      
    
    if (this.getComponentOptions().id)
      this.domNode.id = this.getComponentOptions().id;
    
    this._windowResizeListener = this._onWindowResize.bindAsEventListener(this);
    Event.observe(window, "resize", this._windowResizeListener);
      
    this.addListener("DOMStatusChange", this, this._onDOMStatusChanged);
        
    if (options.useIframe === true) {
      this._contentFrame = new Element("iframe", {
        className: "forgeWorkspaceWindowContentFrame",
        border: "0"
      });
      this.getContentElement().appendChild(this._contentFrame);
    }
  },
  destroy: function ($super) {
	  Event.stopObserving(window, "resize", this._windowResizeListener);
    Event.stopObserving(this._closeButton, "click", this._closeButtonClickListener);
    Event.stopObserving(this._minimizeButton, "click", this._minimizeButtonClickListener);
    Event.stopObserving(this._maximizeButton, "click", this._maximizeButtonClickListener);
	  
    $super();
  },
  setIconURL: function (iconURL) {
     this._iconElement.setStyle({
       backgroundImage: 'url(' + iconURL + ')'  
     });
  },
  addContentComponent: function (component) {
    this.addGUIComponent(this.getContentElement(), component);
  },
  setTitle: function (title) {
    if (this.isDocked()) {
      this._titleElement.innerHTML = title;
      var dockedWindow = this.getDockingBar().getDockedWindow(this);
      dockedWindow.setTitle(title);
    } else {
      this._titleElement.update(title);
    }
  },
  getTitle: function () {
    return this._titleElement.innerHTML;
  },
  getContentElement: function () {
    return this._contentElement;
  },
  setSize: function (width, height) {
    this.getContentElement().setStyle({
      width: width + 'px',
      height: height + 'px'
    });
  },
  setWidth: function (width) {
    this.getContentElement().setStyle({
      width: width + 'px'
    });
  },
  setHeight: function (height) {
    this.getContentElement().setStyle({
      height: height + 'px'
    });
  },
  show: function ($super) {
    $super();
    // TODO: MaxHeight !?!?!
	  /**
    var height = this.getContentElement().getMaxHeight(); 
	  this.getContentElement().setStyle({
      height: height + 'px'
    });
    **/
    
    if (this._loaded === false) {
      
      if (this.getComponentOptions().contentUrl) {
        this.startLoading();
        
        if (this.fire("beforeContentLoad")) {
          var _this = this;
  
          if (this.getComponentOptions().useIframe === true) {
            this._contentFrame.src = this.getComponentOptions().contentUrl;
            // TODO: We really should listen for frame load
            this._loaded = true;
          } else {
            new Ajax.Request(this.getComponentOptions().contentUrl, {
              onComplete: function (transport) {
                _this._loaded = true;
                _this.stopLoading();
              },
              onSuccess: function (transport) {
                _this.getContentElement().update(transport.responseText);
                _this.fire("contentLoad");
              },
              onFailure: function (transport) {
                // TODO: Proper error handling
                alert('loading failed');
              }
            });
          }
        }
      }   
    } 
   
    this.fire("resize", { });
  },
  hide: function ($super) {
    $super();
  },
  close: function () {
    if (this.fire("close", {})) {    
      this.destroy();
    } 
  },
  minimize: function () {
    if (this.fire("minimize")) {
      this.fire("resize");
    }
  },
  maximize: function () {
    if (this.fire("maximize")) {
      this._maximized = true;
      this.fire("afterMaximize");
      this.fire("resize");
    }
  },
  restoreMaximized: function () {
    if (this.fire("restoreMaximized")) {
      this._maximized = false;
      this.fire("afterRestoreMaximized");
      this.fire("resize");
    }
  },
  addClassName: function (className) {
    this.domNode.addClassName(className); 
  },
  _onDOMStatusChanged: function (event) {
    if (event.action == 'added') {
      /**
      TODO: MaxHeight ?!?!?
    	var height = this.getContentElement().getMaxHeight(); 
    	this.getContentElement().setStyle({
        height: height + 'px'
      });
      **/
    }
  },
  _onCloseButtonClick: function (event) {
	  this.close();
  },
  _onMinimizeButtonClick: function (event) {
    this.minimize();
  },
  _onMaximizeButtonClick: function (event) {
    if (!this._maximized)
      this.maximize();
    else
      this.restoreMaximized();
  }, 
  _onWindowResize: function (event) {
	  /**
    TODO: MaxHeight ?!?!?
    var height = this.getContentElement().getMaxHeight(); 
    this.getContentElement().setStyle({
      height: height + 'px'
    });
    **/
	  
	  this.fire("resize", { });
  },
  dockedTo: function (dockingBar) {
    this._dockingBar = dockingBar;
    this._docked = true;
  },
  undocked: function () {
    this._dockingBar = undefined;
    this._docked = false;
  },
  isDocked: function () {
    return this._docked;
  },
  getDockingBar: function () {
    return this._dockingBar;
  },
  startLoading: function () {
    this._loading = true;
    if (!this._loadingPane) {
      this._loadingPane = new Element("div", {className: "forgeWorkspaceWindowLoading"});
      this.domNode.appendChild(this._loadingPane);
    }
  },
  stopLoading: function () {
    if (this._loadingPane) {
      this._loadingPane.remove();
      this._loadingPane = undefined;
    }
    
    this._loading = false;
  }
});