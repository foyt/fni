(function() {
  
  $(document).ready(function () {
    if (!!navigator.userAgent.match(/Trident\/7\./)) {
      // IE 11
      $(document.head)
        .append($('<link>').attr({
          'href': CONTEXTPATH + '/theme/css/flexgrid-ie.css',
          'rel': 'stylesheet'
        }));
    }
  });

}).call(this);