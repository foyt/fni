/**
 * @class Tree node component
 * @extends GUIComponent
 * @constructor 
 */
TreeNode = Class.create(GUIComponent, 
/**
  * @scope TreeNode.prototype
  */
{
  /**
   * Constructor
   * 
   * @param {Class} $super superclass
   * @param {Hash} options options
   * @config {String} nodeText node text
   */
  initialize : function($super, options) {
    $super(options);
    this.domNode = new Element("div", {
      className: "treeNode"
    });

    this.structureContainer = new Element("div");
    this.nodeContainer = new Element("div");
    this.childNodesContainer = new Element("div");

    this.domNode.appendChild(this.structureContainer);
    this.domNode.appendChild(this.nodeContainer);
    this.domNode.appendChild(this.childNodesContainer);

    this._childless = options.childless || false;
    this._isDisabled = false;
    this._isOpen = false;
    this._isSelected = false;
    this._children = new Array();

    this._statusIconClickListener = this._onStatusIconClick.bindAsEventListener(this);
    this._nodeContainerClickListener = this._onNodeContainerClick.bindAsEventListener(this);

    this.childNodesContainer.hide();

    this.statusIcon = new Element('div');
    this.nodeContainer.appendChild(this.statusIcon);

    this.textNode = new Element('div', {
      className: "treeTextNode"
    }).update(this.getComponentOptions().nodeText);

    Event.observe(this.statusIcon, "click", this._statusIconClickListener);
    Event.observe(this.nodeContainer, "click", this._nodeContainerClickListener);

    this._iconNode = new Element("div", {
      className: "treeNodeIcon"
    });
    
    this.nodeContainer.appendChild(this._iconNode);
    this.nodeContainer.appendChild(this.textNode);

    // this.disableSelection();

    this.nodeContainer.addClassName('treeNodeNodeContainer');
    this.childNodesContainer.addClassName('treeNodeChildNodesContainer');
    this.statusIcon.addClassName('treeStatusNode');
    this.structureContainer.addClassName('treeStructureContainer');

    this.state = "Unknown";
    this.position = "Unknown";
    
    this._updateStatusIcon();
  },
  /**
   * Component deinitialization.
   * 
   * @param {Class}
   *          $super superclass
   */
  deinitialize : function($super) {
    Event.stopObserving(this.statusIcon, "click", this._statusIconClickListener); 
    Event.stopObserving(this.textNode, "click", this._nodeContainerClickListener);
    $super();
  },
  /**
   * node parent if any
   * 
   * @return {TreeNode}
   */
  getParentNode : function() {
    return this._parent;
  },
  /**
   * returns true if node is open
   * 
   * @return {Boolean}
   */
  isOpen : function() {
    return this._isOpen;
  },
  /**
   * returns true if node has children
   * 
   * @return {Boolean}
   */
  hasChildren : function(callback) {
    if (this._childless != true) {
      this._loadChildren(function () {
        callback.call(this, this._children.length > 0);
      });
    } else {
      callback.call(this, false);
    }
  },
  /**
   * returns true if node has next sibling node
   * 
   * @return {Boolean}
   */
  hasNextSibling : function() {
    var parent = this.getParentNode();
    if (!parent)
      return false;
    else {
      return parent.isLastChild(this) == false;
    }
  },
  /**
   * Adds new child node under node. This method should not be used directly. Use TreeComponent.addChildNode -method instead.
   * 
   * @param {TreeNode}
   *          child
   */
  addChildNode : function(child) {
    child._parent = this;

    this._children.push(child);

    this.addGUIComponent(this.childNodesContainer, child);
    this._updateStatusIcon();
  },
  removeChildNode : function(child) {
    child._parent = undefined;
    for ( var i = 0, l = this._children.length; i < l; i++) {
      if (this._children[i] == child) {
        this._children.splice(i, 1);
        break;
      }
    }

    this.removeGUIComponent(child);
    this._updateStatusIcon();
  },
  /**
   * returns true if node is last child
   * 
   * @return {Boolean}
   */
  isLastChild : function(child) {
    return !child.domNode.nextSibling;
  },
  /**
   * Sets node selected / nonselected
   * 
   * fires "selected" or "deselected" event when node is selected or deselected
   * 
   * @param {Boolean}
   *          selected
   */
  setSelected : function(selected) {
    if (this._isSelected != selected) {
      this._isSelected = selected;
      if (selected) {
        this.domNode.addClassName('treeNodeSelected');
        this.fire("selected", {});
      } else {
        this.domNode.removeClassName('treeNodeSelected');
        this.fire("deselected", {});
      }
    }
  },
  /**
   * Returns whatever node is selected or not
   * 
   * @return {Boolean}
   */
  isSelected : function() {
    return this._isSelected;
  },
  /**
   * Returns owner tree
   * 
   * @return {TreeComponent}
   */
  getTree : function() {
    return this._tree;
  },
  /**
   * Closes node
   */
  close : function() {
    this.childNodesContainer.hide();
    this.statusIcon.removeClassName('treeStatusNode' + this.position + this.state);
    this.state = 'Closed';
    if (this.position && this.state)
      this.statusIcon.addClassName('treeStatusNode' + this.position + this.state);
    this._isOpen = false;
  },
  /**
   * Opens node
   */
  open : function(callback) {
    this.statusIcon.addClassName('nodeOpening');

    this.hasChildren(function (hasChildren) {
      if (hasChildren) {
        this._isOpen = true;
        this._updateStatusIcon();
        this.childNodesContainer.show();
      }
      
      if (Object.isFunction(callback)) {
        callback();
      }

      this.statusIcon.removeClassName('nodeOpening');
    });
  },
  setDisabled : function(disabled) {
    this._disabled = disabled;
    if (disabled) {
      this.domNode.addClassName("treeNodeDisabled");
    } else {
      this.domNode.removeClassName("treeNodeDisabled");
    }
  },
  isDisabled : function() {
    return this._disabled;
  },
  /**
   * Method launched on first attempt to open the node. Ment to be implemented in derived class (for lazy loading trees)
   */
  doLoadChildren : function(callback) {
  },
  /**
   * Returns icon node
   * 
   * @return {HTMLDOMNode}
   */
  getIconNode : function() {
    return this._iconNode;
  },
  getText: function () {
    return this.textNode.innerHTML;
  },
  /**
   * @private
   */
  _getNodeState : function() {
    if (this._childless == true) {
      return 'Childless';
    } else {
      if (this._children.length > 0) {
        if (this.isOpen())
          return 'Open';
        else
          return 'Closed';
      } else {
        if (this._childrenLoaded != true)
          return 'Closed';
        else
          return 'Childless';
      }
    }
  },
  /**
   * @private
   */
  _getNodePosition : function() {
    if (!this.getParentNode()) {
      return 'Root';
    } else {
      if (this.hasNextSibling())
        return 'Cross';
      else
        return 'Last';
    }
  },
  /**
   * @private
   */
  _updateStatusIcon : function() {
    var oldState = this.state;
    var oldPosition = this.position;

    this.position = this._getNodePosition();
    this.state = this._getNodeState();

    while (this.structureContainer.firstChild)
      this.structureContainer.removeChild(this.structureContainer.firstChild);

    var parentTreeNodes = new Array();

    var parentNode = this.getParentNode();
    while (parentNode) {
      parentTreeNodes.push(parentNode);
      parentNode = parentNode.getParentNode();
    }

    while (parentTreeNodes.length > 0) {
      var treeNode = parentTreeNodes.pop();
      var structureNode = new Element("div");
      structureNode.addClassName('TreeStructureNode');
      if (treeNode.hasNextSibling() == true)
        structureNode.addClassName('TreeStructureLineNode');
      this.structureContainer.appendChild(structureNode);
    }

    if ((oldState != this.state) || (oldPosition != this.position)) {
      if (oldPosition && oldState)
        this.statusIcon.removeClassName('treeStatusNode' + oldPosition + oldState);
      if (this.position && this.state)
        this.statusIcon.addClassName('treeStatusNode' + this.position + this.state);
    }
  },
  /**
   * @private
   */
  _loadChildren : function(callback) {
    if (this._childrenLoaded != true) {
      this._childrenLoaded = true;
      var _this = this;
      this.domNode.addClassName("treeNodeLoading");
      this.doLoadChildren(function () {
        _this.domNode.removeClassName("treeNodeLoading");
        
        for ( var i = 0; i < _this._children.length; i++)
          _this._children[i]._updateStatusIcon();
        
        _this._updateStatusIcon();

        callback.call(_this);
      });
      
    } else {
      callback.call(this);
    }
  },
  /**
   * @private
   */
  _onStatusIconClick : function(event) {
    if (this.isOpen()) {
      this.close();
    } else {
      this.open();
    }
  },
  /**
   * @private
   */
  _onNodeContainerClick : function(event) {
    if (!this._disabled) {
      this.fire("click", {});
    }
  }
});

EditableTreeNode = Class.create(TreeNode, {
  initialize : function($super, options) {
    $super(options);
    
    this._edit = false;
    this._textEditBlurListener = this._onTextEditBlur.bindAsEventListener(this);
    this._textEditKeyDownListener = this._onTextEditKeyDown.bindAsEventListener(this);
  },
  setEdit: function (edit) {
    if (edit == true) {
      if (this.fire("textEdit")) {
        var text = this.getText();
        
        this._editElement = new Element("input", {
          type: "text",
          className: "treeTextEdit",
          value: text
        }); 
        
        this.textNode.insert({
          before: this._editElement
        });
        
        this.textNode.hide();
        
        // TODO: Cross Browser selection support
        this._editElement.selectionStart = 0;
        this._editElement.selectionEnd = text.length;
        this._editElement.focus();
        
        Event.observe(this._editElement, "keydown", this._textEditKeyDownListener);
        Event.observe(this._editElement, "blur", this._textEditBlurListener);
      }
    } else {
      Event.stopObserving(this._editElement, "keydown", this._textEditKeyDownListener);
      Event.stopObserving(this._editElement, "blur", this._textEditBlurListener);
      
      this.textNode.update(this._editElement.value).show();
      this._editElement.remove();
    }

    this._edit = edit;
  },
  _onTextEditBlur: function (event) {
    if (this.fire("textEditCommit", {
      text: this._editElement.value
    })) {
      this.setEdit(false);
    }
  },
  _onTextEditKeyDown: function (event) {
    if (this._edit == true) {
      if (event.keyCode === Event.KEY_RETURN) {
        Event.stop(event);
        if (this.fire("textEditCommit", {
          text: this._editElement.value
        })) {
          this.setEdit(false);
        }
      } else if (event.keyCode === Event.KEY_ESC) {
        Event.stop(event);
        if (this.fire("textEditCancel", {
          text: this._editElement.value
        })) {
          this.setEdit(false);
        }
      }
    } 
  }
});

/**
 * @class Tree component
 * @extends GUIComponent
 * @constructor
 */
TreeComponent = Class.create(GUIComponent,
/**
 * @scope TreeComponent.prototype
 */
{
  /**
   * Constructor
   * 
   * @param {Class}
   *          $super superclass
   * @param {Hash}
   *          options options
   */
  initialize : function($super, options) {
    $super(options);
    this.domNode = new Element("div", {
      className: "tree"
    });
    this._children = new Array();
  },
  /**
   * Component deinitialization.
   * 
   * @param {Class}
   *          $super superclass
   */
  deinitialize : function($super) {
    $super();
  },
  /**
   * Appends node to tree. All tree nodes must be added thru this method or otherwise some of the features will not work properly
   * 
   * @param {TreeNode}
   *          parent Parent node (optional). If not specified new node will be added as root node
   * @param {TreeNode}
   *          parent node node to be added
   */
  addChildNode : function() {
    var args = $A(arguments);

    var parent = null;
    var child = null;

    if (args.length == 1) {
      parent = this;
      child = args[0];
    } else if (args.length == 2) {
      parent = args[0];
      child = args[1];
    }

    child._tree = this;

    if (parent == this) {
      this._children.push(child);
      this.addGUIComponent(child);
    } else {
      parent.addChildNode(child);
    }

    child.addListener("click", this, this._onChildNodeClick);
    child.addListener("selected", this, this._onChildNodeSelected);
    child._updateStatusIcon();
  },
  /**
   * @private
   */
  _onChildNodeClick : function(event) {
    this.setSelectedNode(event.component);
  },
  setSelectedNode : function(node) {
    if (this._selectedNode)
      this._selectedNode.setSelected(false);

    if (node) {
      var c = node;
      while ((c = c.getParentNode()) != null) {
        if (!c.isOpen()) {
          c.open();
        }
      }

      this._selectedNode = node;
      this._selectedNode.setSelected(true);
    }
  },
  getSelectedNode : function() {
    return this._selectedNode;
  },
  /**
   * @private
   */
  _onChildNodeSelected : function(event) {
    this.fire("nodeSelected", {
      node : event.component
    });
  }
});