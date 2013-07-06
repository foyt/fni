(function() {
  
  var BASE_PATH = CONTEXTPATH + '/v1/2';
  
  function createBatch(items) {
    var batchCallbacks = new Array();
    
    $.each(items, function(key, item) {
      batchCallbacks.push(function (callback) {
        item.done(function (data) {
          var result = new Object();
          result[key] = data;
          callback(null, result);
        });
      });
    });
    
    var result = {
      error: function (callback) {
        if (this._err) {
          callback(null, this._err, this._err);
        } else {
          this._errorCallback = callback;
        }

        return this;
      },
      done: function (callback) {
        if (this._done) {
          callback(this._done);
        } else {
          this._doneCallback = callback;
        }

        return this;
        
        /**
        async.parallel(batchCallbacks, function(err, results) {
          var resultObject = new Object();
          $.each(results, function (index, result) {
            $.each(result, function (key, value) {
              resultObject[key] = value;
            });
          });
          
          var err = null;
          callback(err, resultObject);
        });
        
        **/
      }
    };
    
    async.parallel(batchCallbacks, $.proxy(function(err, results) {
      console.log(["done", err, results]);
      
      if (err) {
        if (this._errorCallback) {
          this._errorCallback(null, err, _err);
        } else {
          this._err = err;
        }
      } else {
        var resultObject = new Object();
        $.each(results, function (index, result) {
          $.each(result, function (key, value) {
            resultObject[key] = value;
          });
        });
        
        if (this._doneCallback) {
          this._doneCallback(resultObject);
        } else {
          this._done = resultObject;
        }
        
      }
    }, result));
    
    return result;
  }
  
  function createRestClient(json, path) {
    var options = {};
    if (json) {
      options = {
        stringifyData: true,
        ajax: {
          headers: {
            'Content-Type': 'application/json'
          }
        }
      };
    }
    
    return new $.RestClient(BASE_PATH + path, options);
  }
  
  function createSystemService(json) {
    var restClient = createRestClient(json, '/system/');
    restClient.add('languages');
    return restClient;
  }
  
  function createStoreService(json) {
    var restClient = createRestClient(json, '/store/');
    restClient.add('tags');
    restClient.add('products');
    restClient.add('details');
    restClient.products.add('files');
    restClient.products.add('images');
    restClient.products.add('details');
    
    return restClient;
  }
  
  window.getFnIApi = function () {
    return {
      basePath: BASE_PATH,
      batch: createBatch,
      system: createSystemService,
      store: createStoreService
    };
  };
  
}).call(this);