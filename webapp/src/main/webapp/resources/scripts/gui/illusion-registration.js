(function() {
  'use strict';

  $(document).ready(function () {
    var form = $('#registration-form');
    
    var formSchemaAttr = form.attr('data-form-schema')||'{}';
    var formDataAttr = form.attr('data-form-data');
    
    var formSchema = $.parseJSON(formSchemaAttr);
    var formData = formDataAttr ? { "data": $.parseJSON(formDataAttr) } : {};
    
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
      }
    };

    $('#registration-form')
      .alpaca($.extend(true, formSchema, formData, extraOptions));
    
    $('#registration-form')
      .find('.alpaca-form-buttons-container')
      .removeClass('alpaca-float-right');
  });
  
}).call(this);