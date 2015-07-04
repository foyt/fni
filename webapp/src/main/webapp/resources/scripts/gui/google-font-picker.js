(function() {
  'use strict';
  
  $.widget("custom.googleFontPicker", $.ui.autocomplete, {
    _create : function() {
      this._loadValues($.proxy(function (values) {
        this.option("source", values);
        $.ui.autocomplete.prototype._create.apply(this, arguments);
      }, this));
    },
    
    _renderItem: function (ul, item) {
      WebFont.load({
        google: {  
          families: [item.label]
        } 
      });
      
      return $("<li>")
        .css('font-family', item.label)
        .append(item.label)
        .appendTo(ul);
    },
    
    _loadValues: function (callback) {
      $.ajax('https://www.googleapis.com/webfonts/v1/webfonts?key=' + this.options.apiKey, {
        async: true,
        success: function (data, textStatus, jqXHR) {
          callback($.map(data.items, function (item) {
            var variants = $.map(item.files, function (value, key) {
              return key;
            });
            
            return {
              label: item.family,
              value: "http://fonts.googleapis.com/css?family=" + encodeURIComponent(item.family + ':' + variants.join(','))
            };
          }));
        }
      });
    }
  });

}).call(this);