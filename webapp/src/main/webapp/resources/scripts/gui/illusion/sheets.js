SheetBuilder = Class.create({
  initialize: function (sheet) {
    this._sheet = sheet;
    
    this._sheetBuilderToolbar = new SheetBuilderToolbar(this);
    
    this._cellEditListener = this._onCellEdit.bindAsEventListener(this);
    Event.observe(this._sheet.domNode, "fni:cellEdit", this._cellEditListener);
  },
  setup: function () {
    document.body.appendChild(this._sheetBuilderToolbar.domNode);
  },
  getSheet: function () {
    return this._sheet;
  },
  _onCellEdit: function (event) {
    var cell = this.getSheet().getCell(event.memo.row, event.memo.column);
    
    var dialog = new SheetBuilderPropertyController(cell);
    dialog.open();
  }
});

SheetBuilderPropertyController = Class.create(ModalDialogController, {
  initialize: function ($super, cell) {
    this._cell = cell;
    this._componentEditor = null;

    this._componentEditors = new Hash();
    this._componentEditors.set(SheetCellTextComponent.prototype.NAME, SheetBuilderTextComponentEditor);
    this._componentEditors.set(SheetCellNumberComponent.prototype.NAME, SheetBuilderNumberComponentEditor);
    this._componentEditors.set(SheetCellSelectComponent.prototype.NAME, SheetBuilderSelectComponentEditor);
    this._componentEditors.set(SheetCellMemoComponent.prototype.NAME, SheetBuilderMemoComponentEditor);
    this._componentEditors.set(SheetCellTextListComponent.prototype.NAME, SheetBuilderListComponentEditor);
    
    $super({
      title: getLocale().getText('illusion.sheetBuilder.propertiesDialog.title'),
      width: 600,
      position: 'fixed',
      content: this._buildContent()
    });
  },
  destroy: function ($super) {
    $super();
  },
  setup: function ($super) {
    $super(function () {
      
    });
  },
  _createComponentEditor: function (fieldsContainer) {
    var editorClass = this._componentEditors.get(this._cell.getComponent().__proto__.NAME);
    return new editorClass(fieldsContainer, this._cell);
  },
  _buildContent: function () {
    var contentContainer = new Element("div", {
      className: "dialogContent sheetBuilderPropertiesDialogContent"
    });
    
    var form = new Element("form");
    Event.observe(form, "submit", function (event) {
      Event.stop(event);
    });
    
    var fieldsetsContainer = new Element("div", { 
      className: "stackedLayoutContainer"
    });
    
    var fieldsContainer = new Element("fieldset", {
      className: "stackedLayoutFieldsContainer"
    }); 
    
    var buttonsContainer = new Element("fieldset", {
      className: "stackedLayoutButtonsContainer"
    });
    
    fieldsetsContainer.appendChild(fieldsContainer);
    fieldsetsContainer.appendChild(buttonsContainer);
    
    fieldsContainer.appendChild(new Element("div", {
      className: "confirmDialogText"
    }).update(getLocale().getText('illusion.sheetBuilder.propertiesDialog.helpText')));
    
    var component = this._cell.getComponent();
    if (component) {
      this._componentEditor = this._createComponentEditor(fieldsContainer);
    }

    var _this = this;
    buttonsContainer.appendChild(this._createButton(getLocale().getText('illusion.sheetBuilder.propertiesDialog.applyLabel'), 'ok', function () {
      _this._onOkButtonClick();
    }, true));
    
    buttonsContainer.appendChild(this._createButton(getLocale().getText('illusion.sheetBuilder.propertiesDialog.cancelLabel'), 'cancel', null, false));

    form.appendChild(fieldsetsContainer);
    contentContainer.appendChild(form);
    
    return contentContainer;
  },
  _onOkButtonClick: function () {
    this._componentEditor.commit();
    this.close();
  }
});

SheetBuilderToolbar = Class.create({
  initialize: function (sheetBuilder) {
    this.domNode = new Element("div", {
      className: "sheetBuilderToolbar"
    });
    this.domNode.onselectstart = function(){
      return false;
    };
    this.domNode.unselectable = "on";
    this.domNode.style.MozUserSelect = "none";
    
    this._draggingButton = null;
    this._sheetBuilder = sheetBuilder;
    
    this._buttonMouseDownClickListener = this._onButtonMouseDownClick.bindAsEventListener(this);
    this._windowMouseUpClickListener = this._onWindowMouseUpClick.bindAsEventListener(this);
    this._windowMouseMoveListener = this._onWindowMouseMove.bindAsEventListener(this);
    
    this._textButton = this._addButton(SheetCellTextComponent);
    this._selectButton = this._addButton(SheetCellSelectComponent);
    this._numberButton = this._addButton(SheetCellNumberComponent);
    this._memoButton = this._addButton(SheetCellMemoComponent);
    this._listButton = this._addButton(SheetCellTextListComponent);
    
    Event.observe(window, "mousemove", this._windowMouseMoveListener);
    Event.observe(window, "mouseup", this._windowMouseUpClickListener);
  },
  _addButton: function (componentClass) {
    var button = new SheetBuilderToolbarButton(componentClass);
    this.domNode.appendChild(button.domNode);
    
    button.domNode.store("button", button);
    button.domNode.store("componentName", componentClass.prototype.NAME);
    Event.observe(button.domNode, "mousedown", this._buttonMouseDownClickListener);

    return button;
  },
  _onButtonMouseDownClick: function (event) {
    var button = Event.element(event).retrieve("button");
    var componentName = Event.element(event).retrieve("componentName");
    
    this._buttonDragElement = new Element("div", {
      className: "sheetBuilderDraggingButton"
    });
    $(document.body).appendChild(this._buttonDragElement);
    
    this._draggingButton = button;
    var posX = Event.pointerX(event);
    var posY = Event.pointerY(event);
    var dims = this._buttonDragElement.getDimensions();
    
    this._buttonDragElement.setStyle({
      display: 'block',
      top: (posY - (dims.height / 2)) + 'px',
      left: (posX - (dims.width / 2)) + 'px'
    });
    
    this._buttonDragElement.addClassName("sheetBuilderToolbarButton" + componentName);
  },
  _onWindowMouseMove: function (event) {
    if (this._draggingButton != null) {
      var posX = Event.pointerX(event);
      var posY = Event.pointerY(event);
      var dims = this._buttonDragElement.getDimensions();
      
      this._buttonDragElement.setStyle({
        top: (posY - (dims.height / 2)) + 'px',
        left: (posX - (dims.width / 2)) + 'px'
      });      
    } 
  },
  _onWindowMouseUpClick: function (event) {
    if (this._draggingButton != null) {
      var posX = Event.pointerX(event);
      var posY = Event.pointerY(event);
      
      var cell = this._sheetBuilder.getSheet().getCellByCoordinates(posX, posY);
      this._buttonDragElement.remove();

      if (cell) {
        var component = this._draggingButton.createComponent();
        cell.setComponent(component);
        cell.editCell();
      }

      this._draggingButton = null;
    }
  }
});

SheetBuilderToolbarButton = Class.create({
  initialize: function (componentClass) {
    this.domNode = new Element("div", {
      className: "sheetBuilderToolbarButton sheetBuilderToolbarButton" + componentClass.prototype.NAME
    });
    this._componentClass = componentClass;
  },
  createComponent: function () {
    return new this._componentClass({
      label: 'Title'
    });
  }
});

SheetBuilderEditor = Class.create({
  initialize: function (options) {
    this.domNode = new Element("div", {
      className: "sheetBuilderEditor"
    });
    this.domNode.appendChild(new Element("div", {
      className: "sheetBuilderEditorTitle"
    }).update(options.title));
  }
});

SheetBuilderEditorText = Class.create(SheetBuilderEditor, {
  initialize: function ($super, value, options) {
    $super(Object.extend({
    }, options));
    
    this._editor = new Element("input", {
      className: "sheetBuilderEditorText",
      type: "text",
      value: value||''
    });
    
    this.domNode.appendChild(this._editor);
  },
  getValue: function () {
    return this._editor.value; 
  }
});

SheetBuilderEditorNumber = Class.create(SheetBuilderEditor, {
  initialize: function ($super, value, options) {
    $super(Object.extend({
    }, options));
    
    this._editor = new Element("input", {
      className: "sheetBuilderEditorNumber number",
      type: "text",
      value: value||''
    });
    
    if (options.required) {
      this._editor.addClassName("required"); 
    }
    
    if (options.min) {
      this._editor.addClassName("min"); 
      this._editor.addClassName("min-" + options.min); 
    }
    
    if (options.max) {
      this._editor.addClassName("max"); 
      this._editor.addClassName("max-" + options.max);  
    }
    
    this.domNode.appendChild(this._editor);
  },
  getValue: function () {
    return parseInt(this._editor.value); 
  }
});

SheetBuilderEditorOptionList = Class.create(SheetBuilderEditor, {
  initialize: function ($super, value, options) {
    $super(Object.extend({
    }, options));
    
    var columns = new Array();

    columns.push({
      header : '',
      left : 0,
      right:  22 + 8 + 4 + 22 + 8 + 22 + 8 + 22 + 4,
      measurementUnit: 'px',
      dataType : 'text',
      editable: true
    }, {
      width : 22,
      right : 22 + 8 + 4 + 22 + 8 + 22 + 8,
      dataType : 'button',
      imgsrc : THEMEPATH + '/gfx/icons/16x16/actions/sheet-row-add.png',
      onclick : function(event) {
        var dataGrid = event.dataGridComponent;
        dataGrid.insertRow(['', '', '', '', ''], event.row + 1, true);
      }
    }, {
      width : 22,      
      right : 22 + 8 + 4 + 22 + 8,
      dataType : 'button',
      imgsrc : THEMEPATH + '/gfx/icons/16x16/actions/sheet-row-remove.png',
      onclick : function(event) {
        var dataGrid = event.dataGridComponent;
        dataGrid.deleteRow(event.row);
      }
    }, {
      width : 22,
      right : 4,
      dataType : 'button',
      imgsrc : THEMEPATH + '/gfx/icons/16x16/actions/sheet-row-down.png',
      onclick : function(event) {
        var dataGrid = event.dataGridComponent;
        var row = event.row;
        dataGrid.switchRows(row, row + 1);
      }
    }, {
      width : 22,
      right : 22 + 8 + 4,
      dataType : 'button',
      imgsrc : THEMEPATH + '/gfx/icons/16x16/actions/sheet-row-up.png',
      onclick : function(event) {
        var dataGrid = event.dataGridComponent;
        var row = event.row;
        dataGrid.switchRows(row, row - 1);
      }
    });

    this._dataGrid = new DataGrid(this.domNode, {
      id: 'dg',
      columns : columns
    });
    
    var rowDatas = new Array();
    
    for (var i = 0; i < value.length; i++) {
      rowDatas.push([value[i], '', '', '', '']);
    }

    this._dataGrid.addRows(rowDatas);
  },
  getValue: function () {
    var result = new Array();
    for (var rowIndex = 0, rowCount = this._dataGrid.getRowCount(); rowIndex < rowCount; rowIndex++) {
      result.push(this._dataGrid.getCellValue(rowIndex, 0));
    }
    return result;
  }
});


SheetBuilderComponentEditor = Class.create({
  initialize: function (fieldsContainer, cell, editableProperties) {
    this._cell = cell;
    this._editors = new Hash();
      
    var sheet = cell.getSheet();
    var column = cell.getColumn();
    var row = cell.getRow();
    
    this._editableProperties = [{
      options: {
        title: 'Otsikko',
        required: true
      },
      type: 'text',
      name: 'label'
    }, {
      options: {
        title: 'Leveys (sarakkeina)',
        min: 1,
        max: sheet.getColumnCount() - column,
        required: true
      },
      type: 'number',
      name: 'colspan'
    }, {
      options: {
        title: 'Korkeus (riveinä)',
        min: 1,
        max: sheet.getRowCount() - row,
        required: true
      },
      type: 'number',
      name: 'rowspan'
    }].concat(editableProperties);
    
    var editableProperties = this.getEditableProperties();

    for (var i = 0, l = editableProperties.length; i < l; i++) {
      var property = editableProperties[i];
      var editor = this._createPropertyEditor(property.type, property.name, property.options);
      if (editor) {
        fieldsContainer.appendChild(editor.domNode);
        this._editors.set(property.name, editor);
      }
    }
  },
  getCell: function () {
    return this._cell;
  },
  getEditableProperties: function () {
    return this._editableProperties;
  },
  getValue: function (name) {
    switch (name) {
      case 'label':
        return this._cell.getComponent().getLabel();
      case 'colspan': 
        return this._cell.getColSpan();
      case 'rowspan':
        return this._cell.getRowSpan();
      default:
        alert('Unknown option ' + name);
      break;
    }
  },
  setValue: function (name, value) {
    switch (name) {
      case 'label':
        return this._cell.getComponent().setLabel(value);
      case 'colspan': 
        return this._cell.setColSpan(parseInt(value));
      case 'rowspan':
        return this._cell.setRowSpan(parseInt(value));
      default:
        alert('Unknown option ' + name);
      break;
    }
  },
  commit: function() {
    var editableProperties = this.getEditableProperties();

    for (var i = 0, l = editableProperties.length; i < l; i++) {
      var property = editableProperties[i];
      this.setValue(property.name, this._getEditorValue(property.name));
    }
  },
  _createPropertyEditor: function (type, name, options) {
    var editor = null;
    switch (type) {
      case 'text':
        editor = new SheetBuilderEditorText(this.getValue(name), options);
      break;
      case 'number':
        editor = new SheetBuilderEditorNumber(this.getValue(name), options);
      break;
      case 'optionlist':
        editor = new SheetBuilderEditorOptionList(this.getValue(name), options);
      break;
      default:
        alert('Unknown type: ' + type);
      break;
    }
    
    return editor;
  },
  _getEditorValue: function (name) {
    var editor = this._editors.get(name);
    if (editor) {
      return editor.getValue();
    } else {
      alert('Could not find editor: ' + name); 
    }
  }
});

SheetBuilderTextComponentEditor = Class.create(SheetBuilderComponentEditor, {
  initialize: function ($super, fieldsContainer, cell) {
    $super(fieldsContainer, cell, []);
  }
});

SheetBuilderNumberComponentEditor = Class.create(SheetBuilderComponentEditor, {
  initialize: function ($super, fieldsContainer, cell) {
    $super(fieldsContainer, cell, []);
  }
});

SheetBuilderSelectComponentEditor = Class.create(SheetBuilderComponentEditor, {
  initialize: function ($super, fieldsContainer, cell) {
    $super(fieldsContainer, cell, [{
      options: {
        title: 'Valinnat'
      },
      type: 'optionlist',
      name: 'options'
    }]);
  },
  getValue: function ($super, name) {
    switch (name) {
      case 'options':
        return this.getCell().getComponent().getOptions();
      break;
      default:
        return $super(name);
    }
  },
  setValue: function ($super, name, value) {
    switch (name) {
      case 'options':
        this.getCell().getComponent().setOptions(value);
      break;
      default:
        $super(name, value);
    }
  }
});

SheetBuilderMemoComponentEditor = Class.create(SheetBuilderComponentEditor, {
  initialize: function ($super, fieldsContainer, cell) {
    $super(fieldsContainer, cell, [{
      options: {
        title: 'Rivimäärä',
        min: 1,
        required: true
      },
      type: 'number',
      name: 'rows'
    }]);
  },
  getValue: function ($super, name) {
    switch (name) {
      case 'rows':
        return this.getCell().getComponent().getRows();
      break;
      default:
        return $super(name);
    }
  },
  setValue: function ($super, name, value) {
    switch (name) {
      case 'rows':
        this.getCell().getComponent().setRows(value);
      break;
      default:
        $super(name, value);
    }
  }
});

SheetBuilderListComponentEditor = Class.create(SheetBuilderComponentEditor, {
  initialize: function ($super, fieldsContainer, cell) {
    $super(fieldsContainer, cell, [{
      options: {
        title: 'Rivimäärä',
        min: 1,
        required: true
      },
      type: 'number',
      name: 'rows'
    }]);
  },
  getValue: function ($super, name) {
    switch (name) {
      case 'rows':
        return this.getCell().getComponent().getRows();
      break;
      default:
        return $super(name);
    }
  },
  setValue: function ($super, name, value) {
    switch (name) {
      case 'rows':
        this.getCell().getComponent().setRows(value);
      break;
      default:
        $super(name, value);
    }
  }
});

Sheet = Class.create({
  initialize: function (settings) {
    this._container = null;
    this._settings = Object.extend({
      columns: 2,
      row: 2
    }, settings);
    
    this._rows = new Hash();
    this.domNode = new Element("table", {
      className: "sheet"
    });
    
    for (var row = 0; row < this._settings.rows; row++) {
      var sheetRow = new SheetRow();
      for (var column = 0; column < this._settings.columns; column++) {
        sheetRow.addCell(new SheetCell(this, row, column)); 
      }
      this.addRow(sheetRow);
    }
  },
  deinitialize: function () {
    var rowKeys = this._rows.keys();
    for (var i = 0, l = rowKeys.length; i < l; i++) {
      this._rows.get(i).deinitialize();
      this._rows.unset(keys[i]);
    }
    this.domNode.remove();
  },
  setup: function (container) {
    this._container = container;
    this._container.appendChild(this.domNode);
  },
  addRow: function (row) {
    this.domNode.appendChild(row.domNode); 
    this._rows.set(this._rows.keys().length, row);
  },
  getRow: function (index) {
    return this._rows.get(index);
  },
  getCell: function (row, column) {
    return this.getRow(row).getCell(column);
  },
  getColumnCount: function () {
    return this._settings.columns;
  },
  getRowCount: function () {
    return this._settings.rows;
  },
  getCellByCoordinates: function (x, y) {
    var rowKeys = this._rows.keys();
    for (var rowIndex = 0, rowCount = rowKeys.length; rowIndex < rowCount; rowIndex++) {
      var row = this.getRow(rowKeys[rowIndex]);
      var topY = row.getTopY();
      var bottomY = row.getBottomY();
      
      if ((y > topY) && (y < bottomY)) {
        return row.getCellByCoordinate(x);
      }
    }
    
    return null;
  },
  detachCells: function (row, column, rows, columns) {
    if ((rows > 0) && (columns > 0)) {
      // console.log("detach: from " + row + "," + column + "; rows: " + rows + "," + "columns:" + columns);
      for (var r = row; r < (row + rows); r++) {
        for (var c = column; c < (column + columns); c++) {
          if (!((r == row) && (c == column))) {
            // console.log("detach " + [r, c]);
            this.getCell(r, c).detach();
            // console.log("detached " + [r, c]);
          } else {
            // console.log("skipped " + [r, c]);
          }
        }
      } 
    }
  },
  reattachCells: function (row, column, rows, columns) {
    // console.log("reattach: from " + row + "," + column + "; rows: " + rows + "," + "columns:" + columns);
    for (var r = row; r < row + rows; r++) {
      for (var c = column; c < columns; c++) {
        if (r != row && c != column) {
          this.getCell(r, c).reattach();
        }
      }
    } 
  }
});

SheetRow = Class.create({
  initialize: function () {
    this.domNode = new Element("tr", {
      className: "sheetRow"
    });
    this._cells = new Hash();
  },
  deinitialize: function () {
    this.domNode.remove();
  },
  addCell: function (cell) {
    this.domNode.appendChild(cell.domNode);
    this._cells.set(this._cells.keys().length, cell);
  },
  getCell: function (index) {
    return this._cells.get(index);
  },
  getTopY: function () {
    return this.domNode.cumulativeOffset().top;
  },
  getBottomY: function () {
    return this.getTopY() + this.domNode.getHeight();
  },
  getCellByCoordinate: function (x) {
    var cellKeys = this._cells.keys();
    for (var cellIndex = 0, cellCount = cellKeys.length; cellIndex < cellCount; cellIndex++) {
      var cell = this.getCell(cellKeys[cellIndex]);
      var leftX = cell.getLeftX();
      var rightX = cell.getRightX();
      
      if ((x > leftX) && (x < rightX)) {
        return cell;
      }
    }
    
    return null;
  }
});

SheetCell = Class.create({
  initialize: function (sheet, row, column) {
    this.domNode = new Element("td", {
      className: "sheetCell"
    });    
    
    this._editButton = new Element("div", {
      className: "sheetCellEditButton"
    });

    this._editButtonContainer = new Element("div", {
      className: "sheetCellEditButtonContainer"
    });

    this._content = new Element("div", {
      className: "sheetCellContent"
    });
    
    this._editButtonContainer.appendChild(this._editButton);
    this._content.appendChild(this._editButtonContainer);
    this.domNode.appendChild(this._content);
    
    this._sheet = sheet;
    this._row = row;
    this._column = column;
    
    this._editButtonClickListener = this._onEditButtonClick.bindAsEventListener(this);
    
    Event.observe(this._editButton, "click", this._editButtonClickListener);
  },
  deinitialize: function () {
    Event.stopObserving(this._editButton, "click", this._editButtonClickListener);
  },
  setComponent: function (component) {
    if (this._component) {
      this._component.domMode.remove();
    }

    this._component = component;
    this._content.appendChild(component.domNode);
    return this;
  },
  getComponent: function () {
    return this._component;
  },
  getSheet: function () {
    return this._sheet;
  },
  getRow: function () {
    return this._row;
  },
  getColumn: function () {
    return this._column;
  },
  getRowSpan: function () {
    var rowSpan = parseInt(this.domNode.getAttribute("rowspan"));
    if (isNaN(rowSpan) || !rowSpan) {
      rowSpan = 1;
    }
    return rowSpan;
  },
  setRowSpan: function (count) {
    var oldRowSpan = this.getRowSpan();
    var colSpan = this.getColSpan();
    this._sheet.reattachCells(this._row, this._column, oldRowSpan, colSpan);
    this._sheet.detachCells(this._row, this._column, count, colSpan);

    this.domNode.writeAttribute("rowspan", count);
    return this;
  },
  getColSpan: function () {
    var colSpan = parseInt(this.domNode.getAttribute("colspan"));
    if (isNaN(colSpan) || !colSpan) {
      colSpan = 1;
    }
        
    return colSpan;
  },
  setColSpan: function (count) {
    var rowSpan = this.getRowSpan();
    var oldColSpan = this.getColSpan();
    this._sheet.reattachCells(this._row, this._column, rowSpan, oldColSpan);
    this._sheet.detachCells(this._row, this._column, rowSpan, count);
    this.domNode.writeAttribute("colspan", count);
    return this;
  },
  getLeftX: function () {
    return this.domNode.cumulativeOffset().left;
  },
  getRightX: function () {
    return this.getLeftX() + this.domNode.getWidth();
  },
  detach: function () {
    if (this._detachedNext||this._detachedParent) {
      return;
    }
    
    this._detachedNext = this.domNode.next();
    if (!this._detachedNext) {
      this._detachedParent = this.domNode.parentNode;
    } else {
      this._detachedParent = null;
    }
    
    this.domNode.remove();
  },
  reattach: function () {
    if (this._detachedNext) {
      this._detachedNext.insert({
        before: this.domNode
      });
      this._detachedNext = null;
    } else {
      if (this._detachedParent) {
        this._detachedParent.appendChild(this.domNode);
        this._detachedParent = null;
      }
    }
  },
  editCell: function () {
    this._sheet.domNode.fire("fni:cellEdit", {
      row: this._row,
      column: this._column
    });
  },
  _onEditButtonClick: function (event) {
    this.editCell();
  }
});

SheetCellComponent = Class.create({
  initialize: function (options) {
    this.domNode = new Element("div", {
      className: "sheetCellComponent"
    });
    
    this._options = options;
  },
  deinitialize: function () {
    
  },
  setup: function (form) {
  }
});

SheetCellLabeledComponent = Class.create(SheetCellComponent, {
  initialize: function ($super, options) {
    $super(Object.extend({
      label: ''
    }, options));

    this._labelElement = new Element("div", {
      className: "sheetCellComponentLabel"
    }).update(this._options.label);

    this._content = new Element("div", {
      className: "sheetCellComponentContent"
    });
    
    if (options.contentMaxHeight) {
      this._content.setStyle({
        maxHeight: options.contentMaxHeight
      });
    }
    
    if (options.contentOverflow) {
      this._content.setStyle({
        overflow: options.contentOverflow
      });
    }

    this.domNode.appendChild(this._labelElement);
    this.domNode.appendChild(this._content);
  },
  deinitialize: function () {
    
  },
  getLabel: function () {
    return this._labelElement.innerHTML;
  },
  setLabel: function (label) {
    this._labelElement.innerHTML = label;
  },
  _getContentElement: function () {
    return this._content;
  }
});

SheetCellTextComponent = Class.create(SheetCellLabeledComponent, {
  initialize: function ($super, options) {
    $super(Object.extend({
    }, options));
    
    this._inputElement = new Element("input", {
      className: "sheetCellComponentText",
      type: 'text'
    });
    this._getContentElement().appendChild(this._inputElement);
  },
  deinitialize: function () {
    
  },
  NAME: "Text"
});

SheetCellNumberComponent = Class.create(SheetCellLabeledComponent, {
  initialize: function ($super, options) {
    $super(Object.extend({
    }, options));
    
    this._inputElement = new Element("input", {
      className: "sheetCellComponentNumber",
      type: 'text'
    });
    this._getContentElement().appendChild(this._inputElement);
  },
  deinitialize: function () {
    
  },
  NAME: "Number"
});

SheetCellSelectComponent = Class.create(SheetCellLabeledComponent, {
  initialize: function ($super, options) {
    $super(Object.extend({
    }, options));
    
    this._inputElement = new Element("select", {
      className: "sheetCellComponentSelect"
    });
    this._getContentElement().appendChild(this._inputElement);
  },
  deinitialize: function () {
    
  },
  getOptions: function () {
    var result = new Array();
    
    for (var i = 0, l = this._inputElement.options.length; i < l; i++) {
      result.push(
        this._inputElement.options[i].text
      );
    }
    
    return result;
  },
  setOptions: function (options) {
    while (this._inputElement.options.length > 0) {
      this._inputElement.options.remove(this._inputElement.options.length - 1);
    }
    
    for (var i = 0, l = options.length; i < l; i++) {
      this._addOption(options[i], i);
    }
  },
  _addOption: function (text, name) {
    this._inputElement.appendChild(new Element("option", {
      name: name
    }).update(text));
  },
  NAME: "Select"
});

SheetCellMemoComponent = Class.create(SheetCellLabeledComponent, {
  initialize: function ($super, options) {
    $super(Object.extend({
      rows: 1
    }, options));
    
    this._inputElement = new Element("textarea", {
      className: "sheetCellComponentMemo",
      rows: this._options.rows
    });
    this._getContentElement().appendChild(this._inputElement);
  },
  deinitialize: function () {
    
  },
  getRows: function () {
    return parseInt(this._inputElement.getAttribute("rows")||2);
  },
  setRows: function (rows) {
    this._inputElement.writeAttribute("rows", rows);
  },
  NAME: "Memo"
});

SheetCellTextListComponent = Class.create(SheetCellLabeledComponent, {
  initialize: function ($super, options) {
    $super(Object.extend({
      rows: 1
    }, options));

    var columns = new Array();

    columns.push({
      header : '',
      left : 0,
      right: 0,
      measurementUnit: 'px',
      dataType : 'text',
      editable: true
    });

    this._dataGrid = new DataGrid(this._getContentElement(), {
      id: this._id++,
      columns : columns
    });
    
    var rowDatas = new Array();
    
    for (var i = 0; i < this._options.rows; i++) {
      rowDatas.push(['']);
    }

    this._dataGrid.addRows(rowDatas);
  },
  deinitialize: function () {
    this._dataGrid.deinitialize();
  },
  addOption: function (text, value) {
    this._inputElement.appendChild(new Element("option", {
      name: value
    }).update(text));
  },
  getRows: function () {
    return this._dataGrid.getRowCount();
  },
  setRows: function (rows) {
    var cRows = this.getRows();
    if (cRows != rows) {
      
      if (cRows < rows) {
        var rowDatas = new Array();
        while (cRows < rows) {
          rowDatas.push(['']);
          cRows++;
        }
        this._dataGrid.addRows(rowDatas);
      } else {
        while (cRows > rows) {
          this._dataGrid.deleteRow(this._dataGrid.getRowCount() - 1);
          cRows--;
        }
      }
    }
  },
  _id: new Date().getTime(),
  NAME: "List"
});