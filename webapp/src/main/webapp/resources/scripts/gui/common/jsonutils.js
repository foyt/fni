JSONUtils = {
  jsonRequest: function(url, options) {
    var params = new Hash();
    var origParams = $H(options.parameters);
    var origKeys = origParams.keys();
    for (var i = 0; i < origKeys.length; i++) {
      var key = origKeys[i];
      var value = origParams.get(key);
      if (Object.isNumber(value))
        params.set(key, value.toString());
      else
        params.set(key, value);
    }
 
    new Ajax.Request(url, {  
      method: options.method ? options.method : 'post',
      parameters: params,
      onSuccess: function(transport){
        try {
          var jsonResponse = transport.responseText.evalJSON();
          if (options.onSuccess) {
            options.onSuccess(jsonResponse);
          }
        }
        catch (e) {
          if (options.onFailure) {
            options.onFailure(e, -1, false);
          } else {
            showError(e, -1, false);
          }
        }
      },
      onFailure: function(transport){
        if (options.onFailure)
          options.onFailure(transport.responseText, transport.status, true);
        else
          showError(transport.responseText, transport.status, true);
      },
      onComplete: function () {
        if (options.onComplete) {
          options.onComplete();
        }
      },
      asynchronous: options.asynchronous == false ? false : true,
      requestHeaders: {
// TODO: Locale
//        'Accept-Language': getLocale().getLanguage()  
      }        
    });
  }
}
