PublishArticleDialogController = Class.create(ModalDialogController, {
  initialize: function ($super, options) {
    $super(Object.extend({
      title: getLocale().getText('forge.publishArticle.publishArticleDialogTitle'),
      content: this._createContent(),
      width: 600,
      height: 212,
      position: 'fixed'
    }, options));
  },
  destroy: function ($super) {
    $super();
  },
  setup: function ($super, callback) {
    $super(callback);
  },
  getMaterialId: function () {
    return this._options.materialId;
  },
  getType: function () {
    return this.getDialog().content.down('input[name="type"]:checked').value;
  },
  _createContent: function () {
    var contentContainer = new Element("div", {
      className: "dialogContent publishArticleDialogContent"
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
      className: "formHelpField"
    }).update(getLocale().getText('forge.publishArticle.helpText')));
    
    var typeField = new Element("div", {
      className: "formField formRadioField"
    });
    
    var labelContainer = new Element("div", {
      className: "formFieldLabelContainer"
    });
    
    labelContainer.appendChild(new Element("label").update(getLocale().getText('forge.publishArticle.typeLabel')));
    typeField.appendChild(labelContainer);
    
    var fieldContainer = new Element("div", {
      className: "formFieldEditorContainer"
    });
    
    var technialRadio = new Element("input", {
      type: "radio",
      name: "type",
      value: "TECHNICAL",
      CHECKED: 'CHECKED'
    });
    technialRadio.identify();

    var articleRadio = new Element("input", {
      type: "radio",
      name: "type",
      value: "ARTICLE"
    });
    articleRadio.identify();
    
    var technicalContainer = new Element("div", {
      className: "formRadioContainer"
    });
    technicalContainer.appendChild(technialRadio);
    technicalContainer.appendChild(new Element("label", {
      "for": technialRadio.id
    }).update(getLocale().getText('forge.publishArticle.technialTypeLabel')));
    fieldContainer.appendChild(technicalContainer);

    var articleContainer = new Element("div", {
      className: "formRadioContainer"
    });
    articleContainer.appendChild(articleRadio);
    articleContainer.appendChild(new Element("label", {
      "for": articleRadio.id
    }).update(getLocale().getText('forge.publishArticle.articleTypeLabel')));
    fieldContainer.appendChild(articleContainer);
    
    typeField.appendChild(fieldContainer);

    fieldsContainer.appendChild(typeField);
    
    buttonsContainer.appendChild(this._createButton(getLocale().getText('forge.publishArticle.cancelButtonLabel'), "cancel", null, false));
    buttonsContainer.appendChild(this._createButton(getLocale().getText('forge.publishArticle.publishButtonLabel'), "ok", function (event) {
      var dialog = event.dialog;
      dialog.startLoading();
      API.post(CONTEXTPATH + '/v1/articles/publish', {
        parameters: {
          type: dialog.getType(),
          materialId: dialog.getMaterialId()
        },
        onComplete: function () {
          dialog.stopLoading();
        },
        onSuccess: function (jsonResponse) { 
          dialog.close();
          getWorkspaceController().reloadMaterialLists();
        }
      });
    }, true));

    form.appendChild(fieldsetsContainer);
    contentContainer.appendChild(form);

    return contentContainer;
  } 
});