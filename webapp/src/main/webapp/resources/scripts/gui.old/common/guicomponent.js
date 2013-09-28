var __componentIdSequence = new Date().getTime(), __components = new Hash();

/**
 * returns GUIComponent by component id
 *
 * @param {Object} id
 */
function getComponentById(id){
  return __components.get(id);
}

Event.observe(window, "unload", function (event) {
  __components.each(function(pair) {
    if (pair.value && !pair.value._zombie) {
      pair.value.destroy();
    }
  });
  delete __components;
  delete __componentIdSequence;
});

/**
 * @class Base class for all components
 * @constructor
 */
GUIComponent = Class.create(/**
 @scope GUIComponent.prototype
 */
{
  /**
   * Component constructor.
   *
   * @param {Hash} options
   */
  initialize: function (options){
    this.componentOptions = (!options) ? {}:options;
    this.componentId = this.componentOptions.id||++__componentIdSequence;
    this.listeners = new Array();
    this.childComponents = new Hash();
    this.parentComponent = null;
    this.hidden = false;
    this._zombie = false;
    this._onDOMTree = false;
    __components.set(this.componentId, this);
  },
  destroy: function(){
    __components.unset(this.componentId);

    if (this.listeners) {
      while (this.listeners.length > 0) {
        var l = this.listeners.pop();
        l.eventName = null;
        l.listener = null;
      }
      
      this.listeners = null;
    }
    
    this._zombie = true;
    
    var childComponents = this.childComponents.values();
    for (var i = 0, l = childComponents.length; i < l; i++) {
      this.removeGUIComponent(childComponents[i]);
    }
    
    var parentElement = this.getParentElement(); 
    if (this.domNode && parentElement) 
      parentElement.removeChild(this.domNode);

    this.domNode = undefined;
  },
  /**
   * Returns true if this component has already been destroyed (but javascript garbage collection hasnt cleaned it yet)
   * @return {Boolean}
   */
  isZombie: function(){
    return this._zombie;
  },
  /**
   * Returns component options hash
   */
  getComponentOptions: function(){
    return this.componentOptions;
  },
  /**
   * Fires "parentResized" event recursively to all child components
   */
  fireParentResized: function(){
    this.fire("parentResized", {});
    
    this.childComponents.each(function(child){
      child.value.fireParentResized();
    });
  },
  /**
   * returns component id
   */
  getComponentId: function(){
    return this.componentId;
  },
  /**
   * Changes the component id to new one
   * 
   * @param newId new component id
   */
  changeComponentId: function (newId) {
    var oldId = this.getComponentId();
    __components.unset(oldId);
    this.componentId = newId;
    __components.set(this.componentId, this);
  },
  /**
   * Adds child component
   * @param {GUIComponent/HTMLDOMNode} parent parent component / node
   * @param {GUIComponent} childComponent Component to be added
   */
  addGUIComponent: function(){
    var args = $A(arguments);
    
    var parentObject = null;
    var childComponent = null;
    
    if (args.length == 2) {
      parentObject = args[0];
      childComponent = args[1];
    }
    else {
      childComponent = args[0];
      parentObject = this;
    }
    
    var parentDOMElement = parentObject.domNode != null ? parentObject.domNode : parentObject;
    parentDOMElement.appendChild(childComponent.domNode);
    childComponent.parentObject = parentObject;
    
    this._afterComponentAdded(childComponent);
  },
  /**
   * Adds child component before referenceObject
   *
   * @param {GUIComponent/HTMLDOMNode} parent parent component / node
   * @param {GUIComponent} childComponent Component to be added
   * @param {GUIComponent/HTMLDOMNode} referenceObject component or node before which childComponent is added
   */
  insertGUIComponentBefore: function(parentObject, component, referenceObject){
    var parentDOMElement = parentObject.domNode != null ? parentObject.domNode : parentObject;
    var referenceDOMElement = referenceObject.domNode != null ? referenceObject.domNode : referenceObject;
    
    parentDOMElement.insertBefore(component.domNode, referenceDOMElement);
    
    this._afterComponentAdded(component);
  },
  /**
   * Adds child component after referenceObject
   *
   * @param {GUIComponent/HTMLDOMNode} parent parent component / node
   * @param {GUIComponent} childComponent Component to be added
   * @param {GUIComponent/HTMLDOMNode} referenceObject component or node after which childComponent is added
   */
  insertGUIComponentAfter: function(parentObject, component, referenceObject){
    var parentDOMElement = parentObject.domNode != null ? parentObject.domNode : parentObject;
    var referenceDOMElement = (referenceObject.domNode != null ? referenceObject.domNode : referenceObject).nextSibling;
    
    if (referenceDOMElement != null) 
      parentDOMElement.insertBefore(component.domNode, referenceDOMElement);
    else parentDOMElement.appendChild(component.domNode);
    
    this._afterComponentAdded(component);
  },
  /**
   * removes child component from the DOM and destroys it.
   *
   * @param {GUIComponent/HTMLDOMNode} parent parent component / node
   * @param {GUIComponent/HTMLDOMNode} component component to be removed
   */
  removeGUIComponent: function(component){
    if (component.isZombie() != true) {
      this.childComponents.unset(component.getComponentId());
      component.destroy();    
    }
  },
  /**
   * Returns parent component
   */
  getParentComponent: function(){
    return this.parentComponent;
  },
  /**
   * Returns parent HTML Node
   */
  getParentElement: function(){
    return this.domNode ? this.domNode.parentNode : null;
  },
  /**
   * Returns debug information of component
   */
  inspect: function(){
    var classNames = $(this.domNode).classNames();
    return '[id: ' + this.getComponentId() + '{' + this._className + '}]';
  },
  /**  
   * Hides component
   *
   * fires "hidden" event to this component and its all descendants
   */
  hide: function(){
    $(this.domNode).hide();
    this.hidden = true;
    this._fireComponentHidden();
  },
  /**
   * Shows component
   *
   * fires "shown" event to this component and its all its visible descendants
   */
  show: function(){
    $(this.domNode).show();
    this.hidden = false;
    this._fireComponentShow();
  },
  /**
   * Returns true if component is hidden
   */
  isHidden: function(){
    return this.hidden;
  },
  /**
   * reports an error
   *
   * @param {Object} message error message
   */
  reportError: function(message){
    throw new Error(message);
  },
  /**
   * Notifies component that it has been added to HTML DOM
   */
  componentOnDOM: function(){
    if (this._onDOMTree == false) {
      this._onDOMTree = true;
      
      this.fire('DOMStatusChange', {
        action: 'added'
      });
      
      this.childComponents.each(function(child){
        child.value.componentOnDOM();
      });
    }
  },
  /**
   * Disables text selection on component
   */
  disableSelection: function(){
    var args = $A(arguments);
    var node = args.length == 1 ? args[0] : this.domNode;
    
    
    // safari 1.0+
    node.onselectstart = function(){
      return false;
    };
    // ie 5.5+
    node.unselectable = "on";
    // mozilla
    node.style.MozUserSelect = "none";
  },
  /**
   * Disables context menu on component
   */
  disableContextMenu: function(){
    Event.observe(this.domNode, "contextmenu", function(event){
      Event.stop(event)
    });
  },
  getNamedElement: function (name) {
    if (name && name.length > 0) {
      var m = this.domNode.select("*[name='" + name + "']");
      
      if (m.length != 1) {
        var q = ".named" + name[0].toUpperCase() + name.substring(1);
        m = this.domNode.select(q);
      }
      
      if (m.length == 1)
        return m[0];
    }
    
    return null;
  },
  replaceNamedElement: function (name, newElement) {
    var element = this.getNamedElement(name);
    var parent = element.parentNode;
    parent.insertBefore(newElement, element);
    parent.removeChild(element);
  },
  /**
   * @private
   */
  _fireComponentShow: function(){
    if (this.isHidden() != true) {
      this.childComponents.each(function(child){
        child.value._fireComponentShow();
      });
      this.fire("show", {});
    }
  },
  /**
   * @private
   */
  _fireComponentHidden: function(){
    this.childComponents.each(function(child){
      child.value._fireComponentHidden();
    });
    this.fire("hidden", {});
  },
  /**
   * @private
   */
  _afterDescendantAdded: function(){
    if (this._onDOMTree != false) {
      var parentComponent = this.getParentComponent();
      
      if (parentComponent != null) 
        parentComponent._afterDescendantAdded();
      
      this.fire('DOMStatusChange', {
        action: 'descendantAdded'
      });
    }
  },
  /**
   * @private
   */
  _afterComponentAdded: function(component){
    this.childComponents.set(component.getComponentId(), component);
    component.parentComponent = this;
    
    if (this._onDOMTree == true) {
      component.componentOnDOM();
      var parentComponent = this.getParentComponent();
      if (parentComponent) 
        parentComponent._afterDescendantAdded();
    }
  }
});

Object.extend(GUIComponent.prototype, fni.events.FNIEventSupport);

GUIComponent.prototype._fire = GUIComponent.prototype.fire;
GUIComponent.prototype.fire = function (eventName, event) {
  if (this.isZombie() != true) {
    return this._fire(eventName, Object.extend(event||{}, {
      component: this
    }));
  } else {
    return false;
  }
};