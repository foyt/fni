(function() {

  CKEDITOR.plugins.add('coops-rest', {
    requires : [ 'coops' ],
    init : function(editor) {
      
      RestConnector = CKEDITOR.tools.createClass({
        base : CKEDITOR.coops.Feature,
        $ : function(editor) {
          this.base(editor);
          
          editor.on('CoOPS:Join', this._onCoOpsJoin, this);
          editor.on("CoOPS:BeforeSessionStart", this._onBeforeSessionStart, this, null, 9999);
        },
        proto : {
          getName: function () {
            return 'rest';
          },
          
          _onCoOpsJoin: function (event) {
            var protocolVersion = event.data.protocolVersion;
            var algorithms = event.data.algorithms;
            var editor = event.editor;
            
            this._fileJoin(algorithms, protocolVersion, CKEDITOR.tools.bind(function (status, responseJson, error) {
              if (error) {
                // TODO: Proper error handling
                alert('Could not join:' + error);
              } else {
                editor.fire("CoOPS:Joined", responseJson);
              }
            }, this));
          },

          _onBeforeSessionStart : function(event) {
            if (!event.data.isConnected()) {
              var joinData = event.data.joinData;
              
              this._useMethodOverride = joinData.extensions['x-http-method-override'] != undefined;
              this._revisionNumber = joinData.revisionNumber;
              this._sessionId = joinData.sessionId;

              this.getEditor().on("CoOPS:ContentPatch", this._onContentPatch, this);
              this.getEditor().on("CoOPS:ContentRevert", this._onContentRevert, this);
              this.getEditor().on("propertiesChange", this._onPropertiesChange, this);
    
              this._startUpdatePolling();

              event.data.markConnected();
            }
          },

          _onContentPatch : function(event) {
            if (this.getEditor().config.coops.readOnly === true) {
              return;
            } 
            
            var patch = event.data.patch;
            this.getEditor().getChangeObserver().pause();
            
            this._doPatch(this._editor.config.coops.serverUrl, { patch: patch, revisionNumber : this._revisionNumber, sessionId: this._sessionId }, CKEDITOR.tools.bind(function (status, responseJson, responseText) {
              switch (status) {
                case 204:
                  // Request was ok
                break;
                case 409:
                  this.getEditor().getChangeObserver().resume();
                  this.getEditor().fire("CoOPS:PatchRejected");
                break;
                default:
                  // TODO: Proper error handling
                  alert('Unknown Error');
                break;
              }
              
            }, this));
          },
          
          _onPropertiesChange: function (event) {
            if (this.getEditor().config.coops.readOnly === true) {
              return;
            } 

            this.getEditor().getChangeObserver().pause();
            
            var changedProperties = event.data.properties;
            var properties = {};
            
            for (var i = 0, l = changedProperties.length; i < l; i++) {
              properties[changedProperties[i].property] = changedProperties[i].currentValue;
            };
            
            this._doPatch(this._editor.config.coops.serverUrl, { properties: properties, revisionNumber : this._revisionNumber, sessionId: this._sessionId  }, CKEDITOR.tools.bind(function (status, responseJson, responseText) {
              switch (status) {
                case 204:
                  // Request was ok
                break;
                case 409:
                  this.getEditor().getChangeObserver().resume();
                  this.getEditor().fire("CoOPS:PatchRejected");
                break;
                default:
                  // TODO: Proper error handling
                  alert('Unknown Error');
                break;
              }
              
            }, this));
          },
          
          _onContentRevert: function(event) {
            this.getEditor().getChangeObserver().pause();
            
            this._doGet(this._editor.config.coops.serverUrl, { }, CKEDITOR.tools.bind(function (status, responseJson, responseText) {
              switch (status) {
                case 200:
                  // Content reverted

                  this.getEditor().getChangeObserver().reset();
                  this.getEditor().getChangeObserver().resume();
                  
                  var content = responseJson.content;
                  this._revisionNumber = responseJson.revisionNumber;

                  this.getEditor().fire("CoOPS:RevertedContentReceived", {
                    content: content
                  });
                break;
                default:
                  // TODO: Proper error handling
                  alert('Unknown Error');
                break;
              }
              
            }, this));
          },
          
          _fileJoin: function (algorithms, protocolVersion, callback) {
            var parameters = new Array();
            for (var i = 0, l = algorithms.length; i < l; i++) {
              parameters.push({
                name: 'algorithm',
                value: algorithms[i]
              });
            };
            
            parameters.push({
              name: 'protocolVersion',
              value: protocolVersion
            });
          
            var url = this._editor.config.coops.serverUrl + '/join';
      
            this._doGet(url, parameters, callback);
          },
          
          _startUpdatePolling: function () {
            this._pollUpdates();
          },
          
          _stopUpdatePolling: function () {
            if (this._timer) {
              clearTimeout(this._timer);
            }

            this._timer = null;
          },
          
          _pollUpdates : function(event) {
            var url = this._editor.config.coops.serverUrl + '/update?revisionNumber=' + this._revisionNumber;
            this._doGet(url, {}, CKEDITOR.tools.bind(function (status, responseJson, responseText) {
              if (status == 200) {
                this._applyPatches(responseJson);
              } else if ((status == 204) || (status == 304)) {
                // Not modified
              } else {
                // TODO: Proper error handling
                alert(responseText);
              }
              
              this._timer = CKEDITOR.tools.setTimeout(this._pollUpdates, 500, this);
            }, this));
          },
          
          _applyPatches: function (patches) {
            var patch = patches.splice(0, 1)[0];
            this._applyPatch(patch, CKEDITOR.tools.bind(function () {
              if (patches.length > 0) {
                this._applyPatches(patches);
              }
            }, this));
          },
          
          _applyPatch: function (patch, callback) {
            if (this._sessionId != patch.sessionId) {
              // Received a patch from other client
              if (this._editor.fire("CoOPS:PatchReceived", {
                patch : patch.patch,
                checksum: patch.checksum,
                revisionNumber: patch.revisionNumber,
                properties: patch.properties
              })) {
                this._revisionNumber = patch.revisionNumber;
                callback();
              };
            } else {
              // Our patch was accepted, yay!
              this._revisionNumber = patch.revisionNumber;

              this.getEditor().getChangeObserver().resume();
              this.getEditor().fire("CoOPS:PatchAccepted", {
                revisionNumber: this._revisionNumber
              });
            }
          },

          _doGet: function (url, parameters, callback) {
            this._doGetRequest(url, parameters, function (status, responseText) {
              if ((status == 200) && (!responseText)) {
                // Request was probably aborted...
                return;
              }
              
              if (status != 200) {
                callback(status, null, responseText);
              } else {
                callback(status, JSON.parse(responseText), null);
              }
            });
          },
          
          _doPost: function (url, object, callback) {
            this._doJsonPostRequest("POST", url, object, callback);
          },
          
          _doPut: function (url, object, callback) {
            this._doJsonPostRequest("PUT", url, object, callback);
          },
          
          _doPatch: function (url, object, callback) {
            this._doJsonPostRequest("PATCH", url, object, callback);
          },
          
          _doDelete: function (url, object, callback) {
            this._doJsonPostRequest("DELETE", url, object, callback);
          },
          
          _doJsonPostRequest: function (method, url, object, callback) {
            var data = JSON.stringify(object);
            
            this._doPostRequest(method, url, data, 'application/json', function (status, responseText) {
              if ((status == 200) && (!responseText)) {
                // Request was probably aborted...
                return;
              }
              
              try {
                if (status != 200) {
                  callback(status, null, responseText);
                } else {
                  var responseJson = JSON.parse(responseText);
                  callback(status, responseJson, null);
                }
              } catch (e) {
                callback(status, null, e);
              }
            });
          },
      
          _processParameters: function (parameters) {
            var result = '';
            if ((parameters) && (parameters.length > 0)) {
              for (var i = 0, l = parameters.length; i < l; i++) {
                if (i > 0) {
                  result += '&';
                }
                result += encodeURIComponent(parameters[i].name) + '=' + encodeURIComponent(parameters[i].value);  
              }
            }
            
            return result;
          }, 
          
          _doGetRequest: function (url, parameters, callback) {
            var xhr = this._createXMLHttpRequest();
            var async = true;
            
            xhr.open("get", url + ((parameters.length > 0) ? '?' + this._processParameters(parameters) : ''), async);
            
            if (async == true) {
              xhr.onreadystatechange = function() {
                if (xhr.readyState==4) {
                  callback(xhr.status, xhr.responseText);
                }
              };
            }
            
            xhr.send(null);

            if (async == false) {
              callback(xhr.status, xhr.responseText);
            }
          },
              
          _doPostRequest: function (method, url, data, contentType, callback) {
            var xhr = this._createXMLHttpRequest();
            var async = true;
            if (this._useMethodOverride && (method != 'POST')) {
              xhr.open("POST", url, async);
              xhr.setRequestHeader("x-http-method-override", method);
            } else {
              xhr.open(method, url, async);
            }
            
            xhr.setRequestHeader("Content-type", contentType);
            
            if (!CKEDITOR.env.webkit) {
              // WebKit refuses to send these headers as unsafe
              xhr.setRequestHeader("Content-length", data ? data.length : 0);
              xhr.setRequestHeader("Connection", "close");
            }
            
            if (async == true) {
              xhr.onreadystatechange = function() {
                if (xhr.readyState==4) {
                  callback(xhr.status, xhr.responseText);
                }
              };
            }
            
            xhr.send(data);
            
            if (async == false) {
              callback(xhr.status, xhr.responseText);
            }
          },
          
          _createXMLHttpRequest: function() {
            if ( !CKEDITOR.env.ie || location.protocol != 'file:' )
            try { return new XMLHttpRequest(); } catch(e) {}
            try { return new ActiveXObject( 'Msxml2.XMLHTTP' ); } catch (e) {}
            try { return new ActiveXObject( 'Microsoft.XMLHTTP' ); } catch (e) {}
            return null;
          }
        }
      });
      
      editor.on('CoOPS:BeforeJoin', function(event) {
        event.data.addConnector(new RestConnector(event.editor));
      });

    }
  });
  
}).call(this);