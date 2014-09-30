(function() {
  'use strict';
  
  function getQueryParameters() {
    var result = {};
    
    var location = window.document.location;
    var search = location.search;
    if (search) {
    var params = search.substring(1).split('&');
      for (var i = 0, l = params.length; i < l; i++) {
        var param = params[i].split('=', 2);
        result[param[0]] = param[1];
      }
    }
    
    return result;
  }
    
  var SHEETDATA = null;
  var SETTINGID = null;
  var DATAURL = null; 
  
  function createSetting(callback) {
    $.ajax(DATAURL, {
      type: 'POST',
      contentType: "application/json",
      dataType : "json",
      accepts: {
        'json' : 'application/json'
      },
      data: JSON.stringify({
        'key': 'CHARACTER_SHEET_DATA',
        'value': '{}'
      }),
      success : function(data, textStatus, jqXHR) {
        callback(data);
      },
      error: function ( jqXHR, textStatus, errorThrown) {
        // TODO: Proper error handling
        alert(textStatus);
      }
    });    
  }
  
  function findSetting(callback) {
    $.ajax(DATAURL, {
      data: {
        'key': 'CHARACTER_SHEET_DATA'
      },
      success : function(data, textStatus, jqXHR) {
        if (jqXHR.status == 204) {
          createSetting(callback);      
        } else {
          callback(data[0]);
        }
      },
      error: function ( jqXHR, textStatus, errorThrown) {
        // TODO: Proper error handling
        
        alert(textStatus);
      }
    });
  }
  
  function loadSheetData(callback) {
    if (!DATAURL) {
      // Preview mode
      return callback({});
    }
    
    if (SHEETDATA) {
      callback(SHEETDATA);
    } else {
      if (!SETTINGID) {
        findSetting(function (setting) {
          SETTINGID = setting.id;
          var data = setting.value;
          SHEETDATA = data ? JSON.parse(data) : {};
          callback(SHEETDATA);
        });
      } else {
        $.ajax(DATAURL + '/' + SETTINGID, {
          success : function(setting, textStatus, jqXHR) {
            var data = setting.value;
            SHEETDATA = data ? JSON.parse(data) : {};
            callback(SHEETDATA);
          },
          error: function ( jqXHR, textStatus, errorThrown) {
            // TODO: Proper error handling
            
            alert(textStatus);
          }
        });
      }
    }
  }
  
  function saveSheetData(sheetData, callback) {
    if (!DATAURL) {
      // Preview mode
      return callback({});
    }
    
    SHEETDATA = null;
    
    $.ajax(DATAURL + '/' + SETTINGID, {
      type: 'PUT',
      contentType: "application/json",
      dataType : "json",
      accepts: {
        'json' : 'application/json'
      },
      data: JSON.stringify({
        'key': 'CHARACTER_SHEET_DATA',
        'value': JSON.stringify(sheetData)
      }),
      success : function(data, textStatus, jqXHR) {
        callback();
      },
      error: function ( jqXHR, textStatus, errorThrown) {
        // TODO: Proper error handling
        alert(textStatus);
      }
    });    
  }

  $.widget("custom.illusionField", {
    options: {
      sum: [],
      store: false,
    },
    _create : function() {
      if (this.options.sum.length > 0) {
        $(this.element).attr('readOnly', 'readOnly');

        $.each(this.options.sum, $.proxy(function (index, field) {
          $(field).change($.proxy(function (event) {
            this._updateSum();
          }, this));
        }, this));

        this._updateSum();
      }

      if (this.options.store) {
        if (!$(this.element).attr('name')) {
          throw new Error("Stored field does not have a name");
        }

        var storedValue = this._retrievedValue($.proxy(function (storedValue) {
          this.element.val(storedValue);
          this.element.trigger("change");
        }, this));
        
        this.element.change($.proxy(function (event) {
          this._storeValue($(event.target).val());
        }, this));
      }
    },
    _updateSum: function () {
      var sum = 0;
      $.each(this.options.sum, $.proxy(function (index, field) {
        switch ($(field).attr('type')) {
          case 'number':
            sum += parseInt($(field).val());
          break;
          default:
            throw new Error("Can not sum of filed typed: " + $(field).attr('type'));
          break;
        }
      }, this));
      
      $(this.element).val(sum);
    },

    _retrievedValue: function (callback) {
      loadSheetData($.proxy(function (sheetJson) {
        var name = $(this.element).attr('name');
        callback(sheetJson[name]);
      }, this));
    },

    _storeValue: function (value) {
      loadSheetData($.proxy(function (sheetData) {
        sheetData[$(this.element).attr('name')] = value;
        saveSheetData(sheetData, function () {
          // Update data links
          $('.i-data-link').attr('href', '?d=' + btoa(JSON.stringify(sheetData)));
        });
      }, this));
    }
  });
  
  $.widget("custom.illusionRoll", {
    _create : function() {
      
    },

    fields: function() {
      var result = [];
      
      var fields = $(this.element).attr('data-fields');
      if (fields) {
        $.each(fields.split(','), function (index, field) {
          result.push(field);
        });
      }

      return result; 
    },

    fieldValues: function () {
      return $.map(this.fields(), function (field) {
        return parseInt($(field).val()||'0');
      });      
    },

    parseRoll: function (vars) {
      var roll = $(this.element).attr('data-roll');
      if (vars) {
        for (var i = 0, l = vars.length; i < l; i++) {
          roll = roll.replace(new RegExp('\\{' + i + '\\}', 'g'), vars[i]);
        }
      }
      return roll;
    },

    roll: function (vars) {
      var roll = this.parseRoll(vars||this.fieldValues());
      return [this.evalRoll(roll), roll];
    },

    evalRoll: function (roll) {
      var evil = eval;
      return evil('Math.round(' + roll
        .replace(/([0-9]{1,})([\*]{0,1})(d)([0-9]{1,})/g, "($1*(1 + (Math.random()*($4 - 1))))")
        .replace(/(d)([0-9]{1,})/g, "(1 + (Math.random()*($2 - 1)))") + ')');
    }
  });
  
  $(document).ready(function () {
    
    var params = getQueryParameters();
    if (params['d']) {
      if (window.atob) {
        var data = atob(params['d']);
        // TODO: Load sheet data
      }
    }
    
    DATAURL = params['dataUrl'];
    if (!DATAURL) {
      alert('dataUrl parameter is missing');
    }
    
    loadSheetData(function (sheetData) {
      // Initialize data links
      
      $('.i-data-link').attr('href', '?d=' + btoa(JSON.stringify(sheetData)));
      
      // Initialize fields
      
      $('.i-field').each(function (index, field) {
        if ($(field).hasClass('i-field-sum')) {
          $(field).illusionField({
            sum: $(field).attr('data-fields').split(',')
          });
        } else {
          $(field).illusionField({
            store: true
          });   
        }
      });    
      
      // Initilize progressbars
      
      $('.i-progressbar').each(function (index, field) {
        var targetField = $($(field).attr('data-field'));
        
        $(field).progressbar({
          value: parseInt(targetField.val())
        });

        targetField.change($.proxy(function (event) {
          $(this).progressbar("value", parseInt($(event.target).val()));
        }, field));
      });
      
      // Initialize rolls
      
      $('.i-roll')
        .illusionRoll()
        .click(function () {
          var roll = $(this).illusionRoll('roll');
          $(this).trigger("roll", {
            result: roll[0],
            roll: roll[1]
          });
        });
    });
    
    $('.properties .property-exp').change(function (event) {
      var currentExp = parseInt($(this).val()||'0');
      if (currentExp >= 100) {
        var expFieldName = $(this).attr('name');
        var fieldName = expFieldName.substring(0, expFieldName.length - 4);
        
        var currentLevel = parseInt($('input[name="' + fieldName + '"]').val()||'0');
        var raiseLevels = Math.floor(currentExp / 100);
        if (raiseLevels > 0) {
          $(this)
            .val(currentExp - (raiseLevels * 100))
            .trigger('change');
          
          $('input[name="' + fieldName + '"]')
            .val(currentLevel + raiseLevels)
            .trigger("change");
        }
      }
    });
  }); 
  
}).call(this);