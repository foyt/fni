dust.onLoad = function(name, callback) {
  $.ajax(CONTEXTPATH + '/resources/dust/' + name + '-' + LOCALE + '.dust', {
    success : function(data, textStatus, jqXHR) {
      callback(false, data);
    }
  });
};