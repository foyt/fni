(function() {
  'use strict';
  
  window.formatJavaLocale = function (pattern) {
    var params = [];
    
    for (var i = 1, l = arguments.length; i < l; i++) {
      params.push(arguments[i]);
    }
    
    var formatDateTime = function (formatString, value) {
      if (!$.isFunction(moment)) {
        console.warning("Cannot format date, moment.js is not loaded");
        return null;
      }

      moment.locale(LOCALE);
      var momentValue = moment(value);
      
      if (!$.isFunction(momentValue.formatWithJDF)) {
        console.warning("Cannot format date, moment-jdateformatparser is not loaded");
        return null;
      }
      
      // Workaround for https://github.com/MadMG/moment-jdateformatparser/issues/7
      var fixed = formatString.replace(/\'/g, $.proxy(function(match) {
        return (this.end = !!!this.end) ? '[' : ']';
      }, {}));
      
      return momentValue.formatWithJDF(fixed);
    };
    
    var formatters = {
      date: function (options, value) {
        var formatString = DATE_FORMATS[options[2]||''];
        return formatDateTime(formatString, value);
      },
      time: function (options, value) {
        var formatString = TIME_FORMATS[options[2]||''];
        return formatDateTime(formatString, value);
      }
    };
    
    return pattern.replace(/{[0-9, a-z]*}/g, function(match){
      var result = match.replace(/ /g, '');
      result = result.substring(1, result.length - 1).split(',');
      
      var index = parseInt(result[0]);
      var value = '';
      
      if (index < params.length) { 
        var value = params[index];
        if (result.length > 1) {
          var formatter = formatters[result[1]];
          if ($.isFunction(formatter)) {
            value = formatter(result, value);
          }
        }
      }
      
      return value;
    }); 
  }
  
}).call(this);