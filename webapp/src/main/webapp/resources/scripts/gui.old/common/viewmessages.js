function addViewMessage(severity, text) {
  var className = '';
  var duration = undefined;
  switch (severity) {
    case 'INFO':
      className = 'infoMessage';
      duration = 3000;
    break;
    case 'WARNING':
      className = 'warningMessage';
      duration = 3000;
    break;
    case 'SERIOUS':
      className = 'errorMessage';
    break;
    case 'CRITICAL':
      className = 'errorMessage';
    break;
  }
  
  getNotificationQueue().addNotification(new NotificationMessage({
    text: text,
    className: className,
    duration: duration
  }));
};

document.observe("dom:loaded", function() {
  var messageCount = getJsVariable('messages.count')||0;
  for (var i = 0; i < messageCount; i++) {
    var severity = getJsVariable('message.' + i + '.severity');
    var text = getJsVariable('message.' + i + '.text');
    addViewMessage(severity, text);
  }
});