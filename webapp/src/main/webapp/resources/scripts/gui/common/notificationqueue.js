NotificationQueue = Class.create({
  initialize: function () {
    this.domNode = new Element("div", {
      className: "notificationQueue"
    });
    
    this._notifications = new Array();
  },
  destroy: function () {
  },
  addNotification: function (notification) {
    notification._setQueue(this);
    this.domNode.appendChild(notification.domNode);
    var lastItem = this._notifications.last();
    if (lastItem)
      lastItem.domNode.removeClassName("notificationLast");
    
    notification.domNode.addClassName("notificationLast");
    this._notifications.push(notification);
  },
  _removeItem: function (item) {
    this._notifications.splice(this._notifications.indexOf(item), 1);
    var lastItem = this._notifications.last();
    if (lastItem)
      lastItem.domNode.addClassName("notificationLast");
    
    item._hide(function() {
      item.destroy();
    });
  }
});

Notification = Class.create({
  initialize: function (options) {
    this.domNode = new Element("div", {
      className: "notification"
    });
    
    this._removeButton = new Element("div", {
      className: "notificationRemoveButton"
    });
    
    this.domNode.appendChild(this._removeButton);
    this._queue = null;
    this._options = options;
    
    if (options.duration) {
      var _this = this;
      setTimeout(function () {
        _this._hide(function () {
          _this.destroy();
        });
      }, options.duration);
    }
    
    Event.observe(this._removeButton, 'click', this._onRemoveButtonClick.bind(this));
  },
  destroy: function () {
    this._removeButton.purge();
    this.domNode.remove();
  },
  getOptions: function () {
    return this._options;
  },
  _hide: function (callback) {
    var height = this.domNode.getDimensions().height;
    
    var engine = Prototype.Browser.WebKit ? 'css-transition' : 'javascript';

    this.domNode.setStyle({
      zIndex: 0
    });
    
    new S2.FX.Morph(this.domNode, {
      style: "margin-top: -" + height + 'px',
      engine: engine,
      duration: 0.65,
      after: function(){
        callback();
      }
    }).play();

  },
  _setQueue: function (queue) {
    this._queue = queue;
  },
  _onRemoveButtonClick: function (event) {
    this._queue._removeItem(this);
  }
});

NotificationMessage = Class.create(Notification, {
  initialize: function ($super, options) {
    $super(options);
    
    this.domNode.addClassName("notificationMessage");

    if (options.className) {
      this.domNode.addClassName(options.className);
    }
    
    this.domNode.appendChild(new Element("div", {
      className: "notificationMessageText"
    }).update(options.text));
  },
  destroy: function ($super) {
    $super();
  }
});

function getNotificationQueue() {
  if (!window._notificationQueue) {
    window._notificationQueue = new NotificationQueue();
    document.body.appendChild(window._notificationQueue.domNode);
  }
  
  return window._notificationQueue;
}

