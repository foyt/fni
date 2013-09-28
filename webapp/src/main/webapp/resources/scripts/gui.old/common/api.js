API = {
  get: function (url, options) {
    return this.doGet(url, options);
  },
  doGet: function(url, options) {
    return this._request('GET', url, options);
  },
  post: function (url, options) {
    return this.doPost(url, options);
  },
  doPost: function (url, options) {
    return this._request('POST', url, options);
  },
  put: function (url, options) {
    return this.doPut(url, options);
  },
  doPut: function (url, options) {
    return this._request('PUT', url, options);
  },
  doDelete: function (url, options) {
    return this._request('DELETE', url, options);
  },
  showMessages: function (messages) {
    for (var i = 0, l = messages.length; i < l; i++) {
      var message = messages[i];
      var className = 'errorMessage';
      
      switch (message.severity) {
        case 'INFO':
          className = 'infoMessage';
        break;
        case 'WARNING':
          className = 'warningMessage';
        break;
        case 'SERIOUS':
          className = 'errorMessage';
        break;
        case 'CRITICAL':
          className = 'errorMessage';
        break;
      }
      
      getNotificationQueue().addNotification(new NotificationMessage({
        text: message.message,
        className: className 
      }));
    }
  },
  _request: function (method, url, options) {
	this._queue.push({
	   method: method,
	   url: url,
	   options: options
	 });
	 
	 if (this._queueStopped) {
	   this._executeNext();
	 }
  },
  _executeNext: function () {
	if (this._queue.length > 0) {
      var requestMeta = this._queue.pop();
	  this._executeRequest(requestMeta.method, requestMeta.url, requestMeta.options);
	}

	this._queueStopped = !(this._queue.length > 0);
  },
  _executeRequest: function (method, url, options) {
    var opts = options||{};
    
    var params = new Hash();
    var origParams = $H(opts.parameters);
    var origKeys = origParams.keys();
    for (var i = 0; i < origKeys.length; i++) {
      var key = origKeys[i];
      var value = origParams.get(key);
      if (Object.isNumber(value))
        params.set(key, value.toString());
      else
        params.set(key, value);
    }
 
    var _this = this;
    return new Ajax.Request(url, {  
      method: method,
      parameters: params,
      onSuccess: function(transport){
        if (!transport.responseText) {
          // Request was probably aborted...
          return;
        }
        
        try {
          var jsonResponse = transport.responseText.evalJSON();
          if (jsonResponse.messages)
            API.showMessages(jsonResponse.messages);

          if (jsonResponse.status) {
            // Old deprecated syntax
            var code = jsonResponse.status.code;
            if (code == 200) {
              if (opts.onSuccess) {
                opts.onSuccess(jsonResponse);
              }
            } else {
              var message = null;
              if (!jsonResponse.messages) {
                message = jsonResponse.status.reason;
                if (!message) {
                  message = 'Unknown error occured in "' + url + '" call.'; 
                }
              }
              
              API._handleFailure(opts, message, code, false);
            }
          } else {
            if (opts.onSuccess) {
              opts.onSuccess(jsonResponse);
            }
          }
        } catch (e) {
          API._handleFailure(opts, e, -1, false);
        }
      },
      on403: function (transport) {
        var message = transport.responseText; 
        API._handleFailure(opts, message, 403, true);
      },
      onFailure: function(transport){
        API._handleFailure(opts, transport.responseText, transport.status, true);
      },
      onComplete: function () {
       _this._executeNext();
    	  
        if (opts.onComplete) {
          opts.onComplete();
        }
      },
      asynchronous: opts.asynchronous == false ? false : true,
      requestHeaders: {
        'Authorization': 'Token ' + Cookie.get('accessToken'),
        'Accept-Language': getLocale().getLanguage()  
      }        
    });
  },
  _handleFailure: function (opts, message, code, httpError) {
    var defaultHandler = function (message, code, httpError) {
      if (message) {
        getNotificationQueue().addNotification(new NotificationMessage({
          text: message,
          className: "errorMessage"
        }));
      }
    };
    
    if (Object.isFunction(opts.onFailure)) {
      opts.onFailure(message, code, httpError, defaultHandler);
    } else {
      defaultHandler(message, code, httpError);
    }
  },
  _queueStopped: true,
  _queue: new Array()
};