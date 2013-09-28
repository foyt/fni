ShareMaterialDialogController = Class.create(ModalDialogController, {
  initialize: function ($super, options) {
    $super(Object.extend({
      title: getLocale().getText('forge.shareMaterial.shareMaterialDialogTitle'),
      contentUrl: CONTEXTPATH + '/forge/sharematerialdialog.page?materialId=' + options.materialId,
      width: 600,
      height: 486,
      position: 'fixed',
      overflow: 'visible'
    }, options));
    
    this._saveButtonClickListener = this._onSaveButtonClick.bindAsEventListener(this);
    this._cancelButtonClickListener = this._onCancelButtonClick.bindAsEventListener(this);
    this._publicityInputChangeListener = this._onPublicityInputChange.bindAsEventListener(this);
  },
  destroy: function ($super) {
    Event.stopObserving(this._saveButton, "click", this._saveButtonClickListener);
    Event.stopObserving(this._cancelButton, "click", this._cancelButtonClickListener);
    this.getDialog().content.select('input[name="publicity"]').invoke("stopObserving", "click", this._publicityInputChangeListener);
    
    $super();
  },
  setup: function ($super) {
    $super(function () {
      var dialog = this.getDialog();
      var dialogContent = dialog.content;

      this._editorsListField = dialogContent.down('.namedEditorsListField');
      this._saveButton = dialogContent.down('.namedSaveButton');
      this._cancelButton = dialogContent.down('.namedCancelButton');
      this._publicUrlInput = dialogContent.down('input[name="publicUrl"]');
      
      dialogContent.select('input[name="publicity"]').invoke("observe", "click", this._publicityInputChangeListener);
      
      if (this._getPublicity() != 'PRIVATE') {
        this._publicUrlInput.removeAttribute("disabled");
      }
      
      Event.observe(this._saveButton, "click", this._saveButtonClickListener);
      Event.observe(this._cancelButton, "click", this._cancelButtonClickListener);
      
      var _this = this;
      API.get(CONTEXTPATH + '/v1/friends/SELF/listFriends', {
        onSuccess: function (jsonResponse) {
          var friends = jsonResponse.response.friends;
          
          _this._addEditorContainer = dialogContent.down('.namedAddEditorContainer');
          var friendChoices = new Array();

          for (var i = 0, l = friends.length; i < l; i++) {
            friendChoices.push(new SelectAutocompleterChoice(friends[i].id, friends[i].fullName));
          }

          _this._addEditorAutocompleter =  new SelectAutocompleter({
            choices: friendChoices,
            required: false
          });
          _this._addEditorAutocompleter.addListener("selected", _this, _this._onAddEditorAutocompleterSelected);
          _this._addEditorContainer.appendChild(_this._addEditorAutocompleter.domNode);          
        }
      });
    });
  },
  getMaterialId: function () {
    return this._options.materialId;
  },
  _getPublicity: function () {
    return this.getDialog().content.down('input[name="publicity"]:checked').value;
  },
  _addEditor: function (id, name, role) {
    var row = new Element("div", {
      className: "formListRow"
    });
    row.appendChild(new Element("input", {
      type: "hidden",
      value: id,
      name: "userId"
    }));
    
    var nameCell = new Element("div", {
      className: "formListRowCell"
    }).update(name);
    
    var roleCell = new Element("div", {
      className: "formListRowCell formListRowCellAlignRight"
    });
    
    var roleSelect = new Element("select", {
      name: 'role'
    });
    roleSelect.appendChild(new Element("option", {
      value: "MAY_EDIT"
    }).update(getLocale().getText('forge.shareMaterial.editorRoleMayEdit')));
    roleSelect.appendChild(new Element("option", {
      value: "MAY_VIEW"
    }).update(getLocale().getText('forge.shareMaterial.editorRoleMayView')));
    roleSelect.appendChild(new Element("option", {
      value: "NONE"
    }).update(getLocale().getText('forge.shareMaterial.editorRoleNone')));
    
    roleCell.appendChild(roleSelect);

    row.appendChild(nameCell);
    row.appendChild(roleCell);
    
    this._editorsListField.down('.formFieldEditorContainer').appendChild(row);
  },
  _onSaveButtonClick: function (event) {
    var mayEdit = new Array();
    var mayView = new Array();
    var noRole = new Array();
    
    var publicity = this._getPublicity();
    
    this._editorsListField.select('.formListRow').each(function (row) {
      var userId = row.down('input[name="userId"]').value;
      var role = row.down('select[name="role"]').value;
      
      switch (role) {
        case 'MAY_EDIT':
          mayEdit.push(userId);
        break;
        case 'MAY_VIEW':
          mayView.push(userId);
        break;
        case 'NONE':
          noRole.push(userId);
        break;
      }
    });  
    
    this.startLoading();
    var _this = this;
    API.put(CONTEXTPATH + '/v1/materials/-/' + this.getMaterialId() + '/share', {
      parameters: {
        publicity: publicity,
        mayEdit: mayEdit.join(','),
        mayView: mayView.join(','),
        noRole: noRole.join(',')
      },
      onComplete: function () {
        _this.stopLoading();
      },
      onSuccess: function (jsonResponse) {
        _this.close();
        getWorkspaceController().reloadMaterialLists();
      }
    });
  },
  _onCancelButtonClick: function (event) {
    this.close();
  },
  _onAddEditorAutocompleterSelected: function (event) {
    event.stop();
    
    var choice = event.choice;
    
    this._addEditor(choice.getId(), choice.toString(), 'MAY_EDIT');

    var _this = this;
    setTimeout(function () {
      _this._addEditorAutocompleter.clear();
    }, 0);
  },
  _onPublicityInputChange: function (event) {
    if (this._getPublicity() != 'PRIVATE') {
      this._publicUrlInput.removeAttribute("disabled");
    } else {
      this._publicUrlInput.setAttribute("disabled", "disabled");
    }
  }
});