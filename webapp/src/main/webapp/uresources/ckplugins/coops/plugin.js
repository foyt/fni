(function() {
  if (CKEDITOR.coops === undefined) {
    CKEDITOR.coops = {};
  }
  
  var PROTOCOL_VERSION = '1.0.0';
  
  CKEDITOR.coops.Feature = CKEDITOR.tools.createClass({   
    $: function(editor) {
      this._editor = editor;
    },
    proto : {
      getEditor: function () {
        return this._editor;
      },
      getName: function () {
        return null;
      },
      getRequiredScripts: function () {
        return null;
      }
    }
  });
  
  CKEDITOR.coops.CoOps = CKEDITOR.tools.createClass({  
    $: function(editor) {
      this._editor = editor;
      this._lastSelectionRanges = null;
      this._unsavedContent = null;
      this._savedContent = null;
    },
    proto : {
      getEditor: function () {
        return this._editor;
      },
      isLocallyChanged: function () {
        return (this._unsavedContent != null) && (this._savedContent != null) && (this._unsavedContent != this._savedContent);
      },
      getUnsavedContent: function () {
        return this._unsavedContent;
      },
      getSavedContent: function () {
        return this._savedContent;
      },
      setUnsavedContent: function (unsavedContent) {
        this._unsavedContent = unsavedContent;
      },
      setSavedContent: function (savedContent) {
        this._unsavedContent = this._savedContent = savedContent;
      }
    }
  });
  
  CKEDITOR.plugins.add( 'coops', {
    requires: ['change'],
    onLoad : function() {
      CKEDITOR.tools.extend(CKEDITOR.editor.prototype, {
        getCoOps: function () {
          return this._coOps;
        }
      });
    },
    init: function( editor ) {  
      editor.on( 'instanceReady', function(event) {
        this._coOps = new CKEDITOR.coops.CoOps(this);

        var algorithms = new Array();
        var connectors = new Array();
        
        var beforeJoinEvent = {
          addAlgorithm: function (algorithm) {
            algorithms.push(algorithm);
          },
          addConnector: function (connector) {
            connectors.push(connector);
          }
        };
        
        this.fire("CoOPS:BeforeJoin", beforeJoinEvent);

        var algorithmNames = new Array();
        var requiredScripts = new Array();
        
        for (var i = 0, l = algorithms.length; i < l; i++) {
          algorithmNames.push(algorithms[i].getName());
          if (algorithms[i].getRequiredScripts()) {
            requiredScripts = requiredScripts.concat(algorithms[i].getRequiredScripts());
          }
        }
        
        for (var i = 0, l = connectors.length; i < l; i++) {
          if (connectors[i].getRequiredScripts()) {
            requiredScripts = requiredScripts.concat(connectors[i].getRequiredScripts());
          }
        }
        
        CKEDITOR.scriptLoader.load(requiredScripts, function (completed, failed) {
          this.fire("CoOPS:Join", {
            protocolVersion: PROTOCOL_VERSION,
            algorithms: algorithmNames
          });
        }, this, true);
      });
        
      editor.on('contentChange', function(event) {
        this._coOps.setUnsavedContent(event.data.currentContent);
        editor.fire("CoOPS:ContentDirty");
      });
    
      editor.on('CoOPS:SessionStart', function(event) {
        this._coOps.setSavedContent(this.getData());
      });
    
      editor.on('CoOPS:PatchAccepted', function(event) {
        this._coOps.setSavedContent(this.getData());
      });
    
      editor.on('CoOPS:ContentReverted', function(event) {
        this._coOps.setSavedContent(event.data.content);
      });
    
      editor.on('CoOPS:PatchApplied', function(event) {
        this._coOps.setSavedContent(event.data.content);
      });
      
      editor.on("CoOPS:Joined", function (event) {
        var content = event.data.content;
        
        this.getChangeObserver().pause();
        this.getSelection().removeAllRanges();
        
        var connected = false;
        var beforeStartEvent = {
          joinData: event.data,
          isConnected: function () {
            return connected;
          },
          markConnected: function () {
            connected = true;
          }
        };

        this.fire("CoOPS:BeforeSessionStart", beforeStartEvent);

        if (beforeStartEvent.isConnected()) {
          this.fire("CoOPS:SessionStart");
          this.setData(content, function () {
            if (this.config.coops.readOnly !== true) {
              this.getChangeObserver().reset(content);
              this.getChangeObserver().resume();
              this.setReadOnly(false);  
            }
          });
        } else {
          // TODO: Proper error handling
          alert('Could not connect...');
        }
      });
  
    }
  });
  
}).call(this);