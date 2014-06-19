(function() {
  'use strict';
  
  function ping() {
    setTimeout(function() {
      $.get(CONTEXTPATH + '/rest/system/ping')
        .always(function() { 
          ping(); 
        });
    }, 60 * 1000);
  }
  
  $(document).ready(function() {
    ping();
  });
  
}).call(this);