ConfirmDialogController = Class.create(ModalDialogController, {
  initialize: function ($super, options) {
    var opts = options||{};
    var buttons = opts.buttons||[];
    
    $super(Object.extend({
      content: this._createContent(opts.text, buttons),
      position: opts.position||'fixed'
    }, opts));
  },
  _createContent: function (text, buttons) {
    var contentContainer = new Element("div", {
      className: "dialogContent"
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
    }).update(text));
    
    for (var i = 0, l = buttons.length; i < l; i++) {
      var button = buttons[i];
      buttonsContainer.appendChild(this._createButton(button.label, button.type, button.onClick, button.formValid));
    }
    
    form.appendChild(fieldsetsContainer);
    contentContainer.appendChild(form);
    
    return contentContainer;
  }
});