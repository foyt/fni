(function() {
  'use strict';

  $.widget("custom.codeMirror", {
    options: {
      lineNumbers: true,
      foldGutter: true,
      gutters: ["CodeMirror-linenumbers", "CodeMirror-foldgutter"]
    },
    _create : function() {
      this._editor = CodeMirror.fromTextArea(this.element[0], {
        mode: this.options.mode,
        extraKeys: {"Ctrl-Space": "autocomplete"},
        lineNumbers: this.options.lineNumbers,
        foldGutter: this.options.foldGutter,
        gutters: this.options.gutters
      });

      this._editor.on("change", $.proxy(function() {
        $(this.element).trigger("change");
      }, this));
    },
    
    value: function () {
      return this._editor.getValue();
    },
    
    _destroy : function() {
    
    }
  });
  
}).call(this);