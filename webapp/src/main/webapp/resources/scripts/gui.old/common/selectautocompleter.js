SelectAutocompleterChoice = Class.create({
  initialize : function(id, text) {
    this._id = id;
    this._text = text;
  },
  getId : function() {
    return this._id;
  },
  toString : function() {
    return this._text;
  },
  replace : function(needle, text) {
    return this.toString().replace(needle, text);
  }
});

SelectAutocompleter = Class.create({
  initialize: function (options) {
    var inputId = new Date().getTime();
    
    this.domNode = new Element("label", {
      className: "selectAutocompleter",
      "for": inputId
    });
    this.domNode.store("selectAutocompleter", this);
    
    if (options && options.required) {
      this.domNode.addClassName("selectAutocompleterRequired");
    }
    
    this._autocompleterContainer = new Element("div", {
      className: "selectAutocompleterContainer"
    });
    this._autocompleterContainer.appendChild(new Element("input", {
      type: "text",
      id: inputId
    }));
    
    this._selectedContainer = new Element("div", {
      className: "selectAutocompleterSelectedContainer"
    });
    
    this.domNode.appendChild(this._selectedContainer);
    this.domNode.appendChild(this._autocompleterContainer);
    
    this._autocompleter = new S2.UI.Autocompleter(this._autocompleterContainer, {
      choices: (options ? options.choices : false)||[]
    });
    
    this._autocompleter.findChoices = function() {
      var value = this._getInput();
      var choices = this.choices || [];
      var results = choices.inject([], function(memo, choice) {
        var choiseText = choice.toString().toLowerCase();
        if (choiseText.include(value.toLowerCase())) {
          memo.push(choice);
        }
        return memo;
      });
      
      this.setChoices(results); 
    };
    
    this._autocompleterSelectedListener = this._onAutocompleterSelected.bindAsEventListener(this);
    this._autocompleter.element.observe("ui:autocompleter:selected", this._autocompleterSelectedListener);
  },
  deinitialize: function () {
    this._autocompleter.element.purge();
    this._autocompleter.destory();
  },
  hasSelections: function () {
    return this._selectedContainer.select('.selectAutocompleterSelected').length > 0;
  },
  getSelected: function () {
    var result = new Array();
    this._selectedContainer.select('.selectAutocompleterSelected').each(function (element) {
      result.push(element.retrieve("choice"));
    });
    return result;
  },
  getSelectedIds: function() {
    var result = new Array();
    this.getSelected().each(function (selected) {
      result.push(selected.getId());
    });
    return result;
  },
  addSelectedById: function (id) {
    for (var i = this._autocompleter.choices.length - 1; i >= 0; i--) {
      if (this._autocompleter.choices[i].getId() == id) {
        this.addSelected(this._autocompleter.choices[i]);
        break;
      }
    }
  },
  addSelected: function (choice) {
    var selectedElementText = new Element("span", {
      className: "selectAutocompleterSelectedText"
    }).update(choice.toString().replace(/ /g, '&nbsp;'));
    
    var selectedElementRemoveButton = new Element("span", {
      className: "selectAutocompleterSelectedRemoveButton"
    });
    
    var selectedElement = new Element("span", {
      className: "selectAutocompleterSelected"
    });
    
    var _this = this;
    Event.observe(selectedElementRemoveButton, "click", function (event) {
      selectedElementRemoveButton.purge();
      selectedElement.remove();
      _this.domNode.validate(true, true);
    });
    
    selectedElement.appendChild(selectedElementText);
    selectedElement.appendChild(selectedElementRemoveButton);
    
    selectedElement.store("choice", choice);
    
    this._selectedContainer.appendChild(selectedElement);
    this._selectedContainer.appendChild(document.createTextNode(' '));
  },
  clear: function () {
    this._autocompleter.input.value = '';
    this._autocompleter.input.focus();
  },
  _onAutocompleterSelected: function (event) {
    var choice = event.memo.value;
    if (this.fire("selected", {
      choice: choice
    })) {
      event.memo.instance.choices = event.memo.instance.choices.without(choice);
      this.addSelected(choice);
      this.domNode.validate(true, true);
      setTimeout(function () {
        var inputField = event.memo.instance.input;
        inputField.clear();
        if ((typeof inputField.focus) == 'function')
          inputField.focus();
      }, 0);
    }
  }
});

Object.extend(SelectAutocompleter.prototype, fni.events.FNIEventSupport);

RequiredSelectAutocompleterFieldValidator = Class.create(fi.internetix.validation.FieldValidator, {
  initialize : function($super) {
    $super();
  },
  validate: function ($super, field) {
    var selectAutocompleter = field.retrieve("selectAutocompleter");
    if (selectAutocompleter) {
      if (field.form === undefined) {
        field.form = field.up('form');
      }
 
      if (selectAutocompleter.hasSelections())
        return fi.internetix.validation.FieldValidator.STATUS_VALID;
    }
    
    return fi.internetix.validation.FieldValidator.STATUS_INVALID;
  },
  getType: function ($super) {
    return fi.internetix.validation.FieldValidator.TYPE_MANDATORY;
  },
  getClassName: function () {
    return 'selectAutocompleterRequired';
  }
});

fi.internetix.validation.FieldValidatorVault.registerValidator(new RequiredSelectAutocompleterFieldValidator());