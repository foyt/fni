(function() {
  
  $.widget("custom.notifications", {
    
    _create: function () {
      $(this.element)
        .addClass('notifications')
        .append(
            $('<div>').addClass('notifications-container')
        );
    },
    
    notification: function (status, message) {
      var element = $('<div>').notification({
        status: status,
        text: message
      }).click($.proxy(this._onNotificationClick, this));
      
      $(this.element).find('.notifications-container').append(element);
      return element;
    },
    
    _onNotificationClick: function (event) {
      $(event.target).notification("hide");
    },
    
    _destroy : function() {
      
    }
  });
  
  $.widget("custom.notification", {
    _create: function () {
      $(this.element)
        .addClass('notification')
        .addClass('notification-' + this.options.status)
        .text(this.options.text);
    },
    
    hide: function () {
      this.element.hide('blind');
    },
    
    _destroy : function() {
      
    }
  });
  
  $(document).ready(function() {
    $('.notifications').notifications();
  });
    
}).call(this);