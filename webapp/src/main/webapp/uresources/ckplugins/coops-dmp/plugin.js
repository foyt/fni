(function() {
  
  CKEDITOR.plugins.add( 'coops-dmp', {
    requires: ['coops'],
    init: function( editor ) { 
      CKEDITOR.coops.DmpDifferenceAlgorithm = CKEDITOR.tools.createClass({   
        base: CKEDITOR.coops.Feature,
        $: function(editor) { 
          this.base(editor);
          
          this._pendingPatches = new Array();
          this._contentCooldownTime = 200;
          this._contentCoolingDown = false;

          editor.on("CoOPS:SessionStart", this._onSessionStart, this);
        },
        proto : {
          getName: function () {
            return "dmp";
          },
          getRequiredScripts: function () {
            return [
              editor.plugins['coops-dmp'].path + 'required/diff_match_patch.js', 
              editor.plugins['coops-dmp'].path + 'required/diffxml-js.js',
              editor.plugins['coops-dmp'].path + 'required/md5.js'
            ];
          },
          
          _createChecksum: function (value) {
            return hex_md5(value);
          },
          
          _onSessionStart: function (event) {
            this._diffMatchPatch = new diff_match_patch();
            this._fmes = new Fmes();
            
            this.getEditor().on("contentChange", this._onContentChange, this);
            this.getEditor().on("CoOPS:PatchReceived", this._onPatchReceived, this);
            this.getEditor().on("CoOPS:RevertedContentReceived", this._onRevertedContentReceived, this);
          },
          
          _emitContentPatch: function (oldContent, newContent) {
            var diff = this._diffMatchPatch.diff_main(oldContent, newContent);
            this._diffMatchPatch.diff_cleanupEfficiency(diff);
            var patch = this._diffMatchPatch.patch_toText(this._diffMatchPatch.patch_make(oldContent, diff));

            this.getEditor().fire("CoOPS:ContentPatch", {
              patch: patch
            });  
          },
        
          _onContentChange: function (event) {
            if (!this._contentCoolingDown) {
              this._contentCoolingDown = true;
              
              var oldContent = event.data.oldContent;
              var currentContent = event.data.currentContent;
              
              this._emitContentPatch(oldContent, currentContent);

              CKEDITOR.tools.setTimeout(function() {
                if (this._pendingOldContent && this._pendingNewContent) {
                  this._emitContentPatch(this._pendingOldContent, this._pendingNewContent);
                  delete this._pendingOldContent;
                  delete this._pendingNewContent;
                }
                
                this._contentCoolingDown = false;
              }, this._contentCooldownTime, this);
            } else {
              if (!this._pendingOldContent) {
                this._pendingOldContent = event.data.oldContent;
              }
              
              this._pendingNewContent = event.data.currentContent;
            }
          },
          
          _applyChanges: function (text, newText) {
            // TODO: cross-browser support for document creation

            if (!text) {
              // We do not have old content so we can just directly set new content as editor data
              this.getEditor().setData(newText);
            } else {
              var newTextChecksum = this._createChecksum(newText);
              
              // Read original and patched texts into html documents
              var document1 = document.implementation.createHTMLDocument('');
              var document2 = document.implementation.createHTMLDocument('');
              document1.documentElement.innerHTML = this.getEditor().dataProcessor.toHtml( text );
              document2.documentElement.innerHTML = this.getEditor().dataProcessor.toHtml( newText );
  
              // Create delta of two created documents
              var delta = this._fmes.diff(document1, document2);
              
              // And apply delta into a editor
              (new InternalPatch()).apply(this.getEditor().document.$, delta);
  
              // Calculate checksum of patched editor content
              var patchedData = this.getEditor().getData();
              var patchedDataChecksum = this._createChecksum(patchedData);
              
              if (newTextChecksum != patchedDataChecksum) {
                if (window.console) {
                  console.log(["XmlDiffJs patching did not go well, falling back to setData", 
                               text, 
                               newText, 
                               patchedData, 
                               delta.toDUL()]);
                }
                // XmlDiffJs patching did not go well, falling back to setData 
                this.getEditor().setData(newText);
              } 
              
              var appliedChecksum = this._createChecksum(this.getEditor().getData());
              if (newTextChecksum != appliedChecksum) {
                if (window.console) {
                  console.log("appliedChecksum does not match newTextChecksum " + appliedChecksum + " != " + newTextChecksum);
                }
              }
            }
          },
          
          _lockEditor: function () {
            var editor = this.getEditor();
            var body = editor.document.getBody();
            if (!body.isReadOnly()) {
              body.data('cke-editable', 1);
            } else {
              body.data('was-readonly', 1);
            }
            
            editor.getChangeObserver().pause();
          },
          
          _unlockEditor: function () {
            var editor = this.getEditor();
            var body = editor.document.getBody();
            if (body.data('was-readonly') == 1) {
              body.data('was-readonly', false);
            } else {
              body.data('cke-editable', false);
            }
            
            editor.getChangeObserver().reset();
            editor.getChangeObserver().resume();  
          },
          
          _isPatchApplied: function (patchResult) {
            for (var j = 0, jl = patchResult[1].length; j < jl; j++) {
              if (patchResult[1][j] == false) {
                return false;
              }
            }
            
            return true;
          },
          
          _applyPatch: function (patch, patchChecksum, revisionNumber, callback) {
            this.getEditor().document.$.normalize();
            var currentContent = this.getEditor().getData();
            var patchBaseContent = this.getEditor().getCoOps().getSavedContent();
            if (patchBaseContent === null) {
              patchBaseContent = currentContent;
              if (window.console) {
                console.log("Saved content missing. Patching against current content");
              }
            }
            
            var remoteDiff = this._diffMatchPatch.patch_fromText(patch);
            var removePatchResult = this._diffMatchPatch.patch_apply(remoteDiff, patchBaseContent);
            
            if (this._isPatchApplied(removePatchResult)) {
              var remotePatchedText = removePatchResult[0];
              var remotePatchedChecksum = this._createChecksum(remotePatchedText);
              
              if (patchChecksum != remotePatchedChecksum) {
                if (window.console) {
                  console.log([
                    "Reverting document because checksum did not match", 
                    patchBaseContent, 
                    patch, 
                    revisionNumber,
                    patchChecksum,
                    remotePatchedChecksum, 
                    remotePatchedText
                  ]);
                }

                this.getEditor().fire("CoOPS:ContentRevert");
              } else {
                var localPatch = null;
                var locallyChanged = this.getEditor().getCoOps().isLocallyChanged();

                if (locallyChanged) {
                  if (window.console) {
                    console.log("Received a patch but we got some local changes");
                  }

                  var localDiff = this._diffMatchPatch.diff_main(patchBaseContent, this.getEditor().getCoOps().getUnsavedContent());
                  this._diffMatchPatch.diff_cleanupEfficiency(localDiff);
                  localPatch = this._diffMatchPatch.patch_make(patchBaseContent, localDiff);
                }
                
                if (localPatch) {
                  var localPatchResult = this._diffMatchPatch.patch_apply(localPatch, remotePatchedText);
                  if (this._isPatchApplied(localPatchResult)) {
                    var locallyPatchedText = localPatchResult[0];
                    
                    try {
                      this._applyChanges(currentContent, locallyPatchedText);
                    } catch (e) {
                      // Change applying of changed crashed, falling back to setData
                      editor.setData(locallyPatchedText);
                    }

                    callback();
                  }
                } else {
                  try {
                    this._applyChanges(currentContent, remotePatchedText);
                  } catch (e) {
                    // Change applying of changed crashed, falling back to setData
                    editor.setData(remotePatchedText);
                  }
                  
                  callback();
                }

                editor.fire("CoOPS:PatchApplied", {
                  content : remotePatchedText
                });
              }
              
            } else {
              if (window.console) {
                console.log("Reverting document because could not apply the patch");
              }

              this.getEditor().fire("CoOPS:ContentRevert");
            }
          },
          
          _applyNextPatch: function () {
            if (this._pendingPatches.length > 0) {
              // First we lock the editor, so we can do some magic without 
              // outside interference
              
              this._lockEditor();

              var pendingPatch = this._pendingPatches.shift();
              var _this = this;
              this._applyPatch(pendingPatch.patch, pendingPatch.patchChecksum, pendingPatch.revisionNumber, function () {
                _this._applyNextPatch();
              });
            } else {
              this._unlockEditor();
            }
          },

          _onPatchReceived: function (event) {
            var patch = event.data.patch;
            var patchChecksum = event.data.checksum;
            var revisionNumber = event.data.revisionNumber;
            
            if (patch && patchChecksum) {
              this._pendingPatches.push({
                patch: patch,
                patchChecksum: patchChecksum,
                revisionNumber: revisionNumber
              });
            }

            this._applyNextPatch();
          },
          
          _onRevertedContentReceived: function (event) {
            var revertedContent = event.data.content;

            var localPatch = null;
            var locallyChanged = this.getEditor().getCoOps().isLocallyChanged();

            if (locallyChanged) {
              if (window.console) {
                console.log("Content reverted but we have local changes");
              }
              
              var patchBaseContent = this.getEditor().getCoOps().getSavedContent();
              if (patchBaseContent === null) {
                patchBaseContent = currentContent;
                if (window.console) {
                  console.log("Saved content missing. Patching against current content");
                }
              }
              
              var localDiff = this._diffMatchPatch.diff_main(patchBaseContent, this.getEditor().getCoOps().getUnsavedContent());
              this._diffMatchPatch.diff_cleanupEfficiency(localDiff);
              localPatch = this._diffMatchPatch.patch_make(patchBaseContent, localDiff);
            }
            
            if (localPatch) {
              var localPatchResult = this._diffMatchPatch.patch_apply(localPatch, revertedContent);
              if (this._isPatchApplied(localPatchResult)) {
                revertedContent = localPatchResult[0];
              }
            }

            try {
              _this._applyChanges(currentContent, revertedContent);
            } catch (e) {
              // Change applying of changed crashed, falling back to setData
              editor.setData(revertedContent);
            }
            
            editor.fire("CoOPS:ContentReverted", {
              content : revertedContent
            });
            
            this._unlockEditor();
          }
        }
      });
      
      editor.on('CoOPS:BeforeJoin', function(event) {
        event.data.addAlgorithm(new CKEDITOR.coops.DmpDifferenceAlgorithm(event.editor));
      });
    }
  });

}).call(this);