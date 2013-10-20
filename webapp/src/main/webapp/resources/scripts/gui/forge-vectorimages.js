(function() {
  'use strict';
  
  $(document).ready(function () {
    $('.forge-vector-image-container iframe').load(function () {
      var frameDocument = this.contentDocument||this.contentWindow.document;

      var injectStyle = frameDocument.createElement('link');
      injectStyle.setAttribute("type", "text/css");
      injectStyle.setAttribute("rel", "stylesheet");
      injectStyle.setAttribute("href", CONTEXTPATH + '/uresources/forge-svgeditor-embedded.css');
      frameDocument.getElementsByTagName('head')[0].appendChild(injectStyle);

      var svgContent = $(this).data('content');
      $(this).data('content', null);
      var editorApi = new embedded_svg_edit(this);
      editorApi.setSvgString(svgContent);
    });
  });

}).call(this);