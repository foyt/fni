(function() {
  'use strict';
  
  var illusionClient = new $.RestClient(CONTEXTPATH + '/rest/illusion/');
  illusionClient.add('events');
  
  $(document).ready(function() { 
    $( "#tabs" ).tabs();
    
    illusionClient.events.read().done(function (events, textStatus, xhrObject){
      switch (xhrObject.status) {
        case 200:
          var source = $.map(events, function (event) {
            return {
              title: event.name,
              start: new Date(Date.parse(event.start)),
              end: new Date(Date.parse(event.end)),
              url: CONTEXTPATH + '/illusion/event/' + event.urlName
            };
          });
          
          $('.calendar-view').fullCalendar({
            eventSources: [ {
              events: source,
              color: '#000',
              textColor: '#fff'
            }]
          });
        break;
        case 204:
        break;
        default:
          $('.notifications').notifications('notification', 'error', textStatus);
        break;
      }
    });
  });

}).call(this);