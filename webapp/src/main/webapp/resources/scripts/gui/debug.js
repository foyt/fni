(function() {
  'use strict';
  
  var messges = [];
  
  console.log = function (message) {
    messages.push(message);
  };
  
  $(document).ready(function () {
    $('<div>')
      .attr('id', 'log')
      .css({
        'color': '#0a0',
        'background': 'rgba(0, 0, 0, 0.9)',
        'position': 'fixed',
        'left': '0px',
        'right': '0px',
        'bottom': '0px'
      })
      .appendTo(document.body);
    
    console.log = function (message) {
      $('#log').append(
        $('<pre>')
          .text($.type(message) == 'string' ? message : JSON.stringify(message))    
      );
    };  
    
    console.log('Debug logging is enabled');

    for (var i = 0, l = messages.length; i < l; i++) {
      console.log(messages[i]);
    }
  });

}).call(this);