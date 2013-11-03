(function() {
  'use strict';

  $(document).ready(function() {
    $.cookiesDirective({
      duration: 30,
      position: 'bottom',
      privacyPolicyUri: CONTEXTPATH + '/about.jsf#cookies',
      cookieScripts: 'Piwik',
      scriptWrapper: function() {
        if ((typeof PIWIK_BASEURL) != 'undefined') {
          $.cookiesDirective.loadScript({
            uri: '//' + PIWIK_BASEURL + '/piwik.js'
          });
        };
      }  
    });
  });
  
}).call(this);