(function() {
  'use strict';
  
  // TODO: Localize 
  
  $.widget("custom.registerFormEditor", {
    options: {
      fieldTypes: ['text', 'textarea', 'checkbox', 'radio', 'select', 'number', 'email', 'url']
    },
    
    _create : function() {
      this._requiresReload = false;
      
      this.element.find(".tabs")
        .tabs({
          beforeActivate: $.proxy(this._onBeforeTabActivate, this),
          activate: $.proxy(this._onTabActivate, this)
        });
      
      this.element.find('.code-editor')
        .codeMirror({
          mode: "application/json",
          lineWrapping: true
        })
        .on("change", $.proxy(this._onCodeEditorChange, this));
      
      this.element.find('.visual-editor')
        .registerFormVisualEditor({
          fieldTypes: this.options.fieldTypes,
          locales: {
            edit: this.element.find('.visual-editor').attr('data-edit-field-locale'),
            newField: this.element.find('.visual-editor').attr('data-add-new-field-locale'),
            editDialog: {
              basic: this.element.find('.visual-editor').attr('data-edit-dialog-basic-locale'),
              advanced: this.element.find('.visual-editor').attr('data-edit-dialog-advanced-locale'),
              apply: this.element.find('.visual-editor').attr('data-edit-dialog-apply-locale'),
              cancel: this.element.find('.visual-editor').attr('data-edit-dialog-cancel-locale')
            }
          }
        });
    },
    
    form: function (val) {
      if (val === undefined) {
        return this._parseForm(this.element.find('.code-editor').codeMirror("value"));
      } else {
        this.element.find('.code-editor')
          .codeMirror("value", JSON.stringify(val, null, 2));
      }
    },
    
    moveField: function (from, to) {
      var form = this.form();
      
      var fields = $.map(
        $.map(form.options.fields, function (config, field) {
          return {
            field: field,
            order: config.order ? parseInt(config.order) : Number.MIN_VALUE
          };
        })
        .sort(function (field1, field2) {
          return field1.order - field2.order;
        }), function (field) {
          return field.field;
        });
      
      var fromField = fields.splice(from, 1)[0];
      fields.splice(to, 0, fromField);
      
      $.each(fields, $.proxy(function (index, field) {
        form.options.fields[field].order = index.toString();
      }, this));
      
      this.form(form);
    },
    
    _parseForm: function (fromJson) {
      var form = $.parseJSON(fromJson);
      
      if (!form.options) {
        form.options = {};
      }
      
      if (!form.options.fields) {
        form.options.fields = {};
      }
      
      if (!form.schema) {
        form.schema = {};
      }
      
      if (!form.schema.properties) {
        form.schema.properties = {};
      }
      
      var schemaFields = $.map(form.schema.properties, function (config, field) {
        return field;
      });
      
      $.each(schemaFields, function (index, schemaField) {
        if (!form.options.fields[schemaField]) {
          form.options.fields[schemaField] = {}
        }
      });
      
      var fieldsOrders = 
        $.map(form.options.fields, function (config, field) {
          return {
            field: field,
            order: config.order ? parseInt(config.order) : Number.MIN_VALUE
          };
        })
        .sort(function (field1, field2) {
          return field1.order - field2.order;
        });
      
      $.each(fieldsOrders, function (index, field) {
        form.options.fields[field.field].order = index.toString();
      });
      
      delete form.view;
      
      return form;
    },
    
    _onCodeEditorChange: function (event) {
      this._requiresReload = true;
    },
    
    _onBeforeTabActivate: function (event, ui) {
      switch (ui.newPanel.attr('id')) {
        case 'editor':
          if (this._requiresReload) {
            this.element.find('.visual-editor')
              .registerFormVisualEditor('refresh');
            this._requiresReload = false;
          } 
        break;
      }
    },
    
    _onTabActivate: function (event, ui) {
      switch (ui.newPanel.attr('id')) {
        case 'code':
          this.element.find('.code-editor')
            .codeMirror('refresh');
        break;
      }
    }

  });
  
  $.widget("custom.registerFormVisualEditor", {
    options: {
    },
    
    _create : function() {
      this._rendered = false;
      
      $(document).on("mouseup", $.proxy(this._onDocumentMouseUp, this));
      
      $('<div>')
        .addClass('new-field-container')
        .append(
          $('<a>')
            .attr({'href': 'javascript:void(null)'})
            .addClass('new-field-link')
            .click($.proxy(this._onNewFieldLinkClick, this))
            .text(this.options.locales.newField))
        .append(
          $('<ul>')
            .hide()
            .addClass('field-types'))
        .appendTo(this.element);
      
      $('<div>')
        .addClass('form')
        .appendTo(this.element);
      
      this._createForm($.proxy(function () {
        $.each(this.options.fieldTypes, $.proxy(function (index, type) {
          var instance = new Alpaca.fieldClassRegistry[type]();
          var title = instance.getTitle();
          
          this.element.find('.new-field-container .field-types')
            .append(
              $('<li>')
                .attr({
                  'data-type': type
                })
                .text(title)
            );
        }, this));
      }, this));
      
      this.element.on('click', '.item-editor .edit', $.proxy(this._onEditClick, this));
      this.element.on('click', '.new-field-container .field-types li', $.proxy(this._onNewFieldClick, this));
      this.element.on('formPostRender', $.proxy(this._onFormPostRender, this));
    },
    
    refresh: function (callback) {
      $(this.element).find('.form').alpaca('destroy');
      this._createForm($.proxy(function () {
        if ($.isFunction(callback)) {
          callback();
        }
      }, this));
    },
    
    _openFieldEditor: function (fieldId) {
      var field = Alpaca.fieldInstances[fieldId];

      var removeProperties = ['title', 'description', 'dependencies'];
      var addOptions = ['label'];
      var basicProperties = ['label', 'readonly', 'required'];
      if (['checkbox', 'radio', 'select'].indexOf(field.type) != -1) {
        basicProperties.push('enum');
      }
      
      var schema = field.getSchemaOfSchema();
      var schemaOptions = field.getOptionsForSchema();
      var optionsSchema = field.getSchemaOfOptions();
      var optionsOptions = field.getOptionsForOptions();
      var schemaExtra = {
        properties: {}  
      };
      
      var optionsExtra = {
        fields: {}
      };
      
      var fieldData = $.extend(field.schema, field.options);
      var dialogTitle = schema.title;
      
      delete schema.title;
      delete schema.description;
      if (!schema.properties) {
        schema.properties = {};
      }
      
      $.each(removeProperties, function (index, property) {
        delete schema.properties[property];
      });
      
      $.each(addOptions, function (index, field) {
        if (optionsSchema.properties && optionsSchema.properties[field]) {
          schemaExtra.properties[field] = $.extend(true, optionsSchema.properties[field], {});
        }
        
        if (optionsOptions.properties && optionsOptions.properties[field]) {
          optionsExtra.fields[field] = $.extend(true, optionsOptions.fields[field], {});
        }
      });

      var schema = $.extend(true, schemaExtra, schema);
      var options = $.extend(true, optionsExtra, schemaOptions);
      var bindings = {};
      
      $.each(schema.properties, function (fieldName, schema) {
        bindings[fieldName] = basicProperties.indexOf(fieldName) == -1 ? '#field-advanced' : '#field-basic';
      });
      
      var template = $('<div>')
        .addClass('tabs');
      
      $('<ul>')
        .append($('<li>').append($('<a>').attr('href', '#field-basic').text(this.options.locales.editDialog.basic)))
        .append($('<li>').append($('<a>').attr('href', '#field-advanced').text(this.options.locales.editDialog.advanced)))
        .appendTo(template);
      
      $('<div>').attr({id: 'field-basic'}).appendTo(template);
      $('<div>').attr({id: 'field-advanced'}).appendTo(template);
      
      var extraOptions = {
        "view": {
          "parent": "web-edit",
          "displayReadonly": false,
          "locale": LOCALE == 'fi' ? "fi_FI" : 'en_US',
          "layout": {
            "template": template.wrap('<div>').parent().html(),
            "bindings": bindings
          }
        },
        "postRender": $.proxy(function(control) {
          var dialog = $('<div>');
          dialog
            .addClass('field-editor-dialog')
            .attr({
              title: dialogTitle
            })
            .append(control.getFieldEl())
            .appendTo(document.body)
            .dialog({
              modal: true,
              width: 600,
              buttons: [{
                'text': this.options.locales.editDialog.apply,
                'click': $.proxy(function(event) { 
                  var form = $(document.body).registerFormEditor("form");
                  
                  $.each(control.getValue(), $.proxy(function (key, value) {
                    if (!control.schema.properties[key].readonly) {
                      if ($.isArray(value) && value.length == 0) {
                        value = undefined;
                      }
                      
                      if (schema.properties[key]) {
                        form.schema.properties[field.name][key] = value;
                      } else {
                        form.options.fields[field.name][key] = value;
                      }
                      
                    }
                  }, this));
                  
                  $(document.body).registerFormEditor("form", form);
                  this.refresh();
                  
                  $(dialog).remove();
                }, this)
              }, {
                'text': this.options.locales.editDialog.cancel,
                'click': function(event) { 
                  $(dialog).remove();
                }
              }]
            });
          
            dialog.find('.tabs').tabs();

        }, this)
      };
      
      $.each(schemaOptions.fields, function (fieldName, field) {
        if (field.type == 'array') {
          schemaOptions.fields[fieldName] = $.extend(field, {
            animate: false,
            collapsible: false,
            toolbarSticky: true,
            toolbarStyle: 'link'
          });
        }
      });
      
      var options = $.extend(true, {
        data: fieldData,
        schema: schema,
        options: options
      }, extraOptions);
            
      $('<div>').alpaca(options);
    },
    
    _onNewFieldLinkClick: function (event) {
      this.element.find('.new-field-container .field-types').show();
    },
    
    _onDocumentMouseUp: function (event) {
      if ($(event.target).closest('.new-field-container').length == 0) {
        this.element.find('.new-field-container .field-types').hide();
      }
    },
    
    _onNewFieldClick: function (event) {
      this.element.find('.new-field-container .field-types').hide();
      var newField = $(event.target);
      var type = newField.attr('data-type');
      this._appendNewField(type, $.proxy(function (fieldId) {
        this._openFieldEditor(fieldId);
      }, this));
    },
    
    _appendNewField: function(type, callback) {
      var instance = new Alpaca.fieldClassRegistry[type]();
      var oldForm = $(document.body).registerFormEditor("form");
      var name = "field-" + Math.round(Math.random() * 1000).toString(16);
      var schema = {};
      var options = {};
      var fieldCount = 0;
      
      $.each(oldForm.schema.properties, function (name, option) {
        fieldCount++;
      });
      
      schema[name] = {
        "type" : instance.getType()
      };
      
      options[name] = {
        "type": instance.getFieldType(),
        "label": instance.getTitle(),
        "order": (fieldCount + 1).toString()
      };
      
      var form = $.extend(true, oldForm, {
        schema: { properties : schema },
        options: { fields: options }
      });

      $(document.body).registerFormEditor("form", form);
      this.refresh($.proxy(function () {
        if ($.isFunction(callback)) {
          callback(this.element.find('[data-alpaca-field-name="' + name + '"]').attr('data-alpaca-field-id'));
        }
      }, this));
    },
    
    _onFormPostRender: function() {
      $(this.element).find('.alpaca-container-item').each($.proxy(function (index, item) {
        $('<div>')
          .addClass('item-editor')
          .appendTo(item)
          .append(
            $('<a>')
              .addClass('edit')
              .attr({'href': 'javascript:void(null)'})
              .text(this.options.locales.edit)    
          );
      }, this));
      
      $(this.element)
        .find('.alpaca-field-object>.alpaca-container')
        .sortable({
          start: $.proxy(this._onSortableStart, this),
          stop: $.proxy(this._onSortableStop, this)
        })
        .disableSelection();
    },
    
    _onSortableStart: function( event, ui ) {
      this._sortFrom = $(ui.item).index();
    },
    
    _onSortableStop: function( event, ui ) {
      $(document.body).registerFormEditor("moveField", this._sortFrom, $(ui.item).index());
      this._sortFrom = null;
    },
    
    _createForm: function (callback) {
      var extraOptions = {
        "view": {
          "parent": "web-create",
          "locale": LOCALE == 'fi' ? "fi_FI" : 'en_US',
          "displayReadonly": true
        },
        "postRender": $.proxy(function () {
          this.element.trigger("formPostRender");
          if ($.isFunction(callback)) {
            callback();
          }
        }, this)
      };
        
      var options = $.extend(true, $(document.body).registerFormEditor("form"), extraOptions);
      
      $(this.element).find('.form').alpaca(options);
    },
    
    _onEditClick: function (event) {
      var fieldElement = $(event.target).closest('.alpaca-container-item').find('.alpaca-field');
      var fieldId = fieldElement.attr('data-alpaca-field-id');
      
      this._openFieldEditor(fieldId);
    }
  });
  
  $(document).ready(function () {
    $(document.body)
      .registerFormEditor();
  });
  
}).call(this);