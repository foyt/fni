(function() {
  'use strict';
  
  $.alpaca.registerView({
    "id": "base",
    "messages": {
      "fi_FI": {
        "required": "Tämä tieto on pakollinen",
        "invalid": "Tämä tieto on virheellinen",
        "disallowValue": "Arvo {0} eivät ole sallittuja.",
        "notOptional": "Kenttä ei ole valinnainen",
        "invalidEmail": "Virheellinen sähköpostiosoite"
      }
    }
  });

  $(document).ready(function () {
    var form = $('#registration-form');
    
    var formSchemaAttr = form.attr('data-form-schema')||'{}';
    var formDataAttr = form.attr('data-form-data');
    
    var formSchema = $.parseJSON(formSchemaAttr);
    var formData = formDataAttr ? { "data": $.parseJSON(formDataAttr) } : {};
    var readOnlyFields = $.parseJSON(form.attr('data-readonly-fields'));
    var edit = formDataAttr||false;
    var extraOptions = {
      "options": {
        "form": {
          "buttons": {
            "register": {
              "title": form.attr('data-register-button-label'),
              "disabled": false,
              "click": function () {
                var value = this.getValue();
                $('.answers').val(JSON.stringify(value));
                $('.save')[0].click();
              }
            }
          }
        }
      },
      "view": {
        "parent": edit ? "web-edit" : "web-create",
        "locale": "fi_FI"
      }
    };
    
    $.each(readOnlyFields||[], function (index, field) {
      if (formSchema.schema && formSchema.schema.properties && formSchema.schema.properties[field]) {
        formSchema.schema.properties[field].readonly = true;
      }
    });

    $('#registration-form')
      .alpaca($.extend(true, formSchema, formData, extraOptions));
    
    $('#registration-form')
      .find('.alpaca-form-buttons-container')
      .removeClass('alpaca-float-right');
  });
  
}).call(this);