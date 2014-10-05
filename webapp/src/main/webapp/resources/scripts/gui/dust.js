(function() {
  'use strict';
  
  dust.onLoad = function(name, callback) {
    $.ajax(CONTEXTPATH + '/resources/dust/' + name + '-' + LOCALE + '.dust', {
      success : function(data, textStatus, jqXHR) {
        callback(false, data);
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
  
}).call(this);