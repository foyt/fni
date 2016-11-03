(function() {
  
  $(document).ready(function () {
    if (!!navigator.userAgent.match(/Trident\/7\./)) {
      // IE 11
      $(document.head)
        .append($('<link>').attr({
          'href': '//cdn.metatavu.io/libs/fni-flexgrid/1.0.0/flexgrid-ie.css',
          'rel': 'stylesheet'
        }));
    }
  });

}).call(this);