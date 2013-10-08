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
  
}).call(this);