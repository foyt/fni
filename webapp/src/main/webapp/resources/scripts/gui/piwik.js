(function() {
  'use strict';
  
  if (PIWIK_BASEURL) {
    try {
      var piwikTracker = Piwik.getTracker(PIWIK_BASEURL + "piwik.php", 1);
      piwikTracker.trackPageView();
      piwikTracker.enableLinkTracking();
    } catch (err) {}
  }
  
}).call(this);