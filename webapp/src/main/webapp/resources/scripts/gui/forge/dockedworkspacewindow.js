ForgeDockedWorkspaceWindow = Class.create(GUIComponent, {
  initialize: function($super, options) {
    $super(options);
    this._illuminated = false;
    this.domNode = new Element("div", {className: "forgeWorkspaceDockedWindow"});
    
    this._clickListener = this._onClick.bindAsEventListener(this);
    Event.observe(this.domNode, "click", this._clickListener);
    
    var window = options.window;
    if (window.getComponentOptions().className) {
      this.domNode.addClassName(window.getComponentOptions().className);
    }
    
    this._titleElement = new Element("span", {className: "forgeWorkspaceDockedWindowTitle"}).update(window.getTitle()); 
    
    this._contentElement = new Element("div", {className: 'forgeWorkspaceDockedWindowContent'});
    this._contentElement.appendChild(new Element("span", {className: "forgeWorkspaceDockedWindowIcon"}));
    this._contentElement.appendChild(this._titleElement);

    this.domNode.appendChild(this._contentElement);
  },
  destroy: function($super){
    Event.stopObserving(this.domNode, "click", this._clickListener);
    this._illuminated = false;
    $super();
  },
  setTitle: function (title) {
    this._titleElement.update(title);
  },
  _onClick: function (event) {
    this.fire("click", {
      window: this.getComponentOptions().window
    });
  },
  illuminate: function () {
    if (this._illuminated == false) {
      this._originalBgColor = this._contentElement.getStyle("backgroundColor");
      this._illuminated = true;
      this._illuminate();
    }
  },
  _illuminate: function () {
    if (this._illuminated == true) {
      var _this = this;
      this._contentElement.morph("background-color:#ffaa75", {
        duration: 2,
        after: function () {
          _this._deluminate();
        }
      }); 
    }
  },
  _deluminate: function () {
    var _this = this;
    this._contentElement.morph("background-color:" + this._originalBgColor, {
      duration: 2,
      after: function () {
        _this._illuminate();
      }
    });
  }
});