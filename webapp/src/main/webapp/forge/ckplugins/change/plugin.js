/*
 * change - CKEditor plugin for detecting content changes.
 * 
 * Licensed under GNU Lesser General Public License Version 3 or later (the "LGPL")
 * http://www.gnu.org/licenses/lgpl.html
 *
 * Antti Leppä / Foyt
 * antti.leppa@foyt.fi
 * 
 * Fires a contentChange event when editor content changes. 
 */
(function() {

  /**
   * Base class for change obserers.
   * 
   * @class
   */
  ChangeObserver = CKEDITOR.tools.createClass({
    /**
     * Creates a ChangeObserver class instance.
     *
     * @constructor
     * @param {CKEDITOR.editor} editor object observer observes.
     */
    $ : function(editor) {
      this._editor = editor;
      this._oldContent = '';
      this._paused = false;
    },
    proto : /** @lends ChangeObserver.prototype */ {
      /**
       * Returns editor observer observes
       * 
       * @returns {CKEDITOR.editor} editor that observer observes
       */
      getEditor : function() {
        return this._editor;
      },
      /**
       * Checks whether editor content has changed and fires a contentChange event if it has.
       * 
       * Event contains oldContent and currentContent properties that contain content before and after the change.
       */
      checkChange : function() {
        if ((this._paused == false) && (this.getEditor().readOnly == false)) {

          var currentContent = this._getEditorContent();
          if (this._oldContent != currentContent) {
            this._editor.fire("contentChange", {
              oldContent : this._oldContent,
              currentContent : currentContent
            });

            this._oldContent = currentContent;
          }
        }
      },
      /**
       * Stops observer
       */
      disconnect : function() {
      },
      /**
       * Starts observer
       */
      start : function() {
      },
      /**
       * Resets observer state. This means that observer assumes that content has not changed.
       */
      reset : function (content) {
        this._oldContent = content||this._getEditorContent();
      },
      /**
       * Pauses observer. 
       * 
       * Fires a changeObserverPause event 
       */
      pause : function() {
        if (this._editor.fire("changeObserverPause")) {
          this._paused = true;
        }
      },
      /**
       * Resumes observer
       * 
       * Fires a changeObserverResume event
       */
      resume : function() {
        if (this._editor.fire("changeObserverResume")) {
          this._paused = false;
          this.checkChange();
        }
      },
      _getEditorContent : function() {
        // TODO: Why editor.getSnapshot() keeps returning 'true'???
        // TODO: Data should be configurable
        return this.getEditor().getData();
      }
    }
  });

  /**
   * Change observer that uses MutationObserver for change detection.
   * 
   * @class
   * @extends ChangeObserver
   */
  MutationChangeObserver = CKEDITOR.tools.createClass({
    base : ChangeObserver,
    /**
     * Creates a MutationChangeObserver class instance.
     *
     * @constructor
     * @param {CKEDITOR.editor} editor object observer observes.
     */
    $ : function(editor) {
      this.base(editor);

      var MutationObserver = window.MutationObserver || window.WebKitMutationObserver || window.MozMutationObserver;

      var _this = this;
      this._observer = new MutationObserver(function(mutations) {
        CKEDITOR.tools.setTimeout(function() {
          this.checkChange();
        }, 0, _this);
      });
    },
    proto : /** @lends MutationChangeObserver.prototype */ {
      /**
       * Starts observer
       */
      start : function() {
        var editorDocument = this._editor.document.$;

        this._observer.observe(editorDocument, {
          attributes : true,
          childList : true,
          characterData : true,
          subtree : true
        });
      },
      /**
       * Stops observer
       */
      disconnect : function() {
        this._observer.disconnect();
        this._observer = undefined;
      }
    }
  });

  /**
   * Change observer that uses DOMSubtreeModified event for change detection.
   * 
   * @class
   * @extends ChangeObserver
   */
  DOMSubtreeModifiedChangeObserver = CKEDITOR.tools.createClass({
    base : ChangeObserver,
    /**
     * Creates a DOMSubtreeModifiedChangeObserver class instance.
     *
     * @constructor
     * @param {CKEDITOR.editor} editor object observer observes.
     */
    $ : function(editor) {
      this.base(editor);

      var _this = this;
      this._domSubtreeModifiedListener = function(event) {
        _this._onDOMSubtreeModified(event);
      };
    },
    proto : /** @lends DOMSubtreeModifiedChangeObserver.prototype */ {
      /**
       * Starts observer
       */
      start : function() {
        this._editor.document.on("DOMSubtreeModified", this._domSubtreeModifiedListener);
      },
      /**
       * Stops observer
       */
      disconnect : function() {
        this._editor.document.removeListener("DOMSubtreeModified", this._domSubtreeModifiedListener);
      },
      _onDOMSubtreeModified : function(event) {
        CKEDITOR.tools.setTimeout(function() {
          this.checkChange();
        }, 0, this);
      }
    }
  });
  
  /**
   * Change observer that uses polling for change detection.
   * 
   * @class
   * @extends ChangeObserver
   */
  PollingChangeObserver = CKEDITOR.tools.createClass({
    base : ChangeObserver,
    /**
     * Creates a PollingChangeObserver class instance.
     *
     * @constructor
     * @param {CKEDITOR.editor} editor object observer observes.
     */
    $ : function(editor) {
      this.base(editor);
      this._timer = null;
    },
    proto : /** @lends PollingChangeObserver.prototype */ {
      /**
       * Starts observer
       */      
      start : function() {
        this._poll();
      },
      /**
       * Stops observer
       */
      disconnect : function() {
        if (this._timer) {
          clearTimeout(this._timer);
        }

        this._timer = null;
      },
      _poll : function(event) {
        this.checkChange();
        this._timer = CKEDITOR.tools.setTimeout(this._poll, 333, this);
      }
    }
  });

  CKEDITOR.plugins.add('change', {
    requires : [],
    init : function(editor) {
      CKEDITOR.tools.extend(CKEDITOR.editor.prototype, {
        /**
         * Returns change observer
         * 
         * @returns {ChangeObserver} change observer
         */
        getChangeObserver : function() {
          return editor._changeObserver;
        }
      });

      var changeObserver = null;
      if ((typeof (window.MutationObserver || window.WebKitMutationObserver || window.MozMutationObserver) == 'function')) {
        // Modern browsers support mutation observer
        changeObserver = new MutationChangeObserver(editor);
      }

      if (changeObserver == null) {
        if ((CKEDITOR.env.ie && CKEDITOR.env.version == 9)) {
          // IE 9 support DOMSubtreeModified
          changeObserver = new DOMSubtreeModifiedChangeObserver(editor);
        } else {
          // Otherwise we fallback to polling
          changeObserver = new PollingChangeObserver(editor);
        }
      }

      editor._changeObserver = changeObserver;

      editor.on('instanceReady', function() {
        this.getChangeObserver().start();
      });

      editor.on('beforePaste', function(event) {
        this.getChangeObserver().pause();
      });

      editor.on('paste', function(event) {
        this.getChangeObserver().resume();
      }, editor, null, 99999);
    }
  });

}).call(this);