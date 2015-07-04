(function() {
  'use strict';
  
  dust.onLoad = function(name, callback) {
    $.ajax(CONTEXTPATH + '/resources/dust/' + name + '-' + LOCALE + '.dust', {
      success : function(data, textStatus, jqXHR) {
        callback(false, data);
      },
      error: function (jqXHR, textStatus, errorThrown) {
        if (jqXHR.status == 404) {
          $.ajax(CONTEXTPATH + '/resources/dust/' + name + '.dust', {
            success : function(data, textStatus, jqXHR) {
              callback(false, data);
            },
            error: function (jqXHR, textStatus, errorThrown) {
              $('.notifications').notifications('notification', 'error', textStatus);
            }
          });          
        } else {
          $('.notifications').notifications('notification', 'error', textStatus);
        }
      }
    });
  };
  
  dust.helpers.contextPath = function(chunk, context, bodies) {
    return chunk.write(CONTEXTPATH);
  };
  
  dust.helpers.baseUrl = function(chunk, context, bodies) {
    var port = window.location.port;
    var protocol = window.location.protocol;
    
    var baseUrl = protocol + '//' + window.location.hostname;
    var nativePort = (protocol === 'https' && port === 443) || (protocol === 'http' && port === 80);
    if (!nativePort) {
      baseUrl += ':' + window.location.port;
    }

    return chunk.write(baseUrl + CONTEXTPATH);
  };
  
  dust.helpers.iterate = function(chunk, context, bodies, params) {
    var obj = (params||{})['obj'];
    for (var k in obj) {
      chunk = chunk.render(bodies.block, context.push({key: k, value: obj[k]}));
    }
    
    return chunk;
  }
  
  dust.filters.lowerCase = function(value) {
    if (typeof value === 'string') {
      return value.toLowerCase();
    }
    
    return value;
  }
  
  dust.filters.upperCase = function(value) {
    if (typeof value === 'string') {
      return value.toUpperCase();
    }
    
    return value;
  }
  
}).call(this);