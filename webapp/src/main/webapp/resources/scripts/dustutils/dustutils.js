dust.onLoad = function(name, callback) {
  $.ajax(CONTEXTPATH + name, {
    async: false,
    success : function(data, textStatus, jqXHR) {
      callback(false, data);
    },
    error: function (jqXHR, textStatus, errorThrown) {
      callback(errorThrown, null);
    }
  });
};

function renderDustTemplate(templateName, json, callback) {
  dust.render(templateName, json, callback);
};