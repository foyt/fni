(function() {
  
  $(document).ready(function () {
    if (!!navigator.userAgent.match(/Trident\/7\./)) {
      // IE 11
      $(document.head)
        .append($('<link>').attr({
          'href': '//static.forgeandillusion.net/libs/fni-flexgrid/1.0.0/flexgrid-ie.css',
          'rel': 'stylesheet'
        }));
    }
  });

}).call(this);