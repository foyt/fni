(function() {
  'use strict';
  
  function ping() {
    setTimeout(function() {
      $.get(CONTEXTPATH + '/keepalive')
        .always(function() { 
          ping(); 
        });
    }, 60 * 1000);
  }
  
  $(document).ready(function() {
    ping();
  });
  
}).call(this);