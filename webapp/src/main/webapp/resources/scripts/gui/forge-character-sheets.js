(function() {
  'use strict';
  
  var updateTimeout = 300;
  
  function utf8_to_b64( str ) {
    return window.btoa(unescape(encodeURIComponent( str )));
  }
   
  function b64_to_utf8( str ) {
    return decodeURIComponent(escape(window.atob( str )));
  }
  
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
  
  $(document).ready(function () {
    var previewTimeout = null;
    
    function updatePreview(title, styles, contents, scripts) {
      dust.render("character-sheet", { 
        "title": title,
        "styles": styles,
        "contents": contents,
        "scripts": scripts,
        "mode": "preview"
      }, function(err, html) {
        var previewFrame = $('.forge-chracter-sheet-editor-preview');
        previewFrame.attr("src", 'data:text/html;base64,' + utf8_to_b64(html));
      });
    }
    
    function onChange() {
      if (previewTimeout) {
        clearTimeout(previewTimeout);  
      }
      
      previewTimeout = setTimeout(function () {
        updatePreview(
          $('.forge-chracter-sheet-title').val(), 
          $('.forge-chracter-sheet-editor-styles').codeMirror('value'), 
          $('.forge-chracter-sheet-editor-contents').codeMirror('value'), 
          $('.forge-chracter-sheet-editor-scripts').codeMirror('value')
        ); 
      }, updateTimeout);
    }
    
    $('.forge-chracter-sheet-editor-preview').load(function () {
      var contentDocument = this.contentDocument || this.contentWindow.document;
      var scrollHeight = $(contentDocument).find('html').prop('scrollHeight');
      $(this).css("height", scrollHeight);
    });

    updatePreview(
      $('.forge-chracter-sheet-title').val(), 
      $('.forge-chracter-sheet-editor-styles').val(), 
      $('.forge-chracter-sheet-editor-contents').val(),
      $('.forge-chracter-sheet-editor-scripts').val()
    ); 
    
    $('textarea.forge-chracter-sheet-editor-contents')
      .codeMirror({
        mode: "text/html"
      })
      .on('change', onChange);
    
    $('textarea.forge-chracter-sheet-editor-styles')
      .codeMirror({
        mode: "text/css"
      })
      .on('change', onChange);
    
    $('textarea.forge-chracter-sheet-editor-scripts')
      .codeMirror({
        mode: "text/javascript"
      })
      .on('change', onChange);
  });
  
}).call(this);