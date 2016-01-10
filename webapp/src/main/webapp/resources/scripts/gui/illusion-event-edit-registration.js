(function() {
  'use strict';
  
  // TODO: Localize 
  
  $.widget("custom.registerFormEditor", {
    options: {
    
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
          locales: {
            edit: this.element.find('.visual-editor').attr('data-edit-field-locale'),
            editDialog: {
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
      this._createForm(); 
      this.element.on('click', '.item-editor .edit', $.proxy(this._onEditClick, this));
    },
    
    refresh: function () {
      $(this.element).alpaca('destroy');
      this._createForm();
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
    
    _createForm: function () {
      var extraOptions = {
        "view": {
          "parent": "web-create",
          "locale": LOCALE == 'fi' ? "fi_FI" : 'en_US',
          "displayReadonly": true
        },
        "postRender": $.proxy(this._onFormPostRender, this)
      };
        
      var options = $.extend(true, $(document.body).registerFormEditor("form"), extraOptions);
      
      $(this.element).alpaca(options);
    },
    
    _onEditClick: function (event) {
      // TODO: Separate to basic and advanced tabs
      var removeProperties = ['title', 'description', 'dependencies'];
      var addOptions = ['label'];
      
      var fieldElement = $(event.target).closest('.alpaca-container-item').find('.alpaca-field');
      var fieldId = fieldElement.attr('data-alpaca-field-id');
      var field = Alpaca.fieldInstances[fieldId];
      
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
      
      var extraOptions = {
        "view": {
          "parent": "web-edit",
          "displayReadonly": false,
          "locale": LOCALE == 'fi' ? "fi_FI" : 'en_US'
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
        }, this)
      };
      
      $.each(schemaOptions.fields, function (fieldName, field) {
        if (field.type == 'array') {
          schemaOptions.fields[fieldName] = $.extend(field, {
            animate: false,
            collapsible: false,
            toolbarSticky: true
          });
        }
      });

      var options = $.extend(true, {
        data: fieldData,
        schema: $.extend(true, schemaExtra, schema),
        options: $.extend(true, optionsExtra, schemaOptions)
      }, extraOptions);
            
      $('<div>').alpaca(options);
    }
  });
  
  $(document).ready(function () {
    $(document.body)
      .registerFormEditor();
  });
  
}).call(this);