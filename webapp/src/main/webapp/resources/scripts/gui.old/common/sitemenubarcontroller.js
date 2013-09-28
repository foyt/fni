SiteMenuBarController = Class.create({
  initialize: function () {
    this._openMenu = null;
    this._widgets = new Hash();
    
    this._windowMouseDownListener = this._onWindowMouseDown.bindAsEventListener(this);
  },
  setup: function () {    
    this._messagesWidget = this._registerWidget(new MessagesMenuBarWidget(),$('siteMenuBarWidgetMessages'), $('siteMenuBarWidgetMessagesContent'));
    this._chatWidget = this._registerWidget(new ChatMenuBarWidget(),$('siteMenuBarWidgetChat'), $('siteMenuBarWidgetChatContent'));
    this._friendsWidget = this._registerWidget(new SiteMenuBarWidgetFriends(),$('siteMenuBarWidgetFriends'), $('siteMenuBarWidgetFriendsContent'));
    this._calendarWidget = this._registerWidget(new CalendarMenuBarWidget(),$('siteMenuBarWidgetCalendar'), $('siteMenuBarWidgetCalendarContent'));
    this._searchWidget = this._registerWidget(new SiteMenuBarWidgetSearch(),$('siteMenuBarWidgetSearch'), $('siteMenuBarWidgetSearchContent'));
    this._localeWidget = this._registerWidget(new SiteMenuBarWidgetLanguage(),$('siteMenuBarWidgetLocale'), $('siteMenuBarWidgetLocaleContent'));

    if (isLoggedIn()) {
      this._accountWidget = this._registerWidget(new SiteMenuBarWidgetAccount(),$('siteMenuBarWidgetAccount'), $('siteMenuBarWidgetAccountContent'));
    }

    Event.observe(window, "mousedown", this._windowMouseDownListener);
  },
  destroy: function () {
    
  },
  getWidget: function (name) {
    return this._widgets.get(name);
  },
  openMenuWidget: function (menuWidget) {
    if (this._openMenu) {
      this.closeMenuWidget();
    }
    
    this._openMenu = menuWidget;
    menuWidget.show();
  },
  closeMenuWidget: function () {
    if (this._openMenu) {
      this._openMenu.hide();
      this._openMenu = null;
    }
  },
  _registerWidget: function (widget, menuElement, contentElement) {
    widget.setup(menuElement, contentElement);
    widget.addListener("menuClick", this, this._onWidgetMenuClick);
    this._widgets.set(widget.getName(), widget);
    return widget;
  },
  _onWidgetMenuClick: function (event) {
    this.openMenuWidget(event.menuWidget);
  },
  _onWindowMouseDown: function (event) {
    if (this._openMenu) {
      var element = Event.element(event);
      
      if (element.hasClassName('siteMenuBarWidget'))
        return false;
      
      if (element.hasClassName('siteMenuBarWidgetContent'))
        return false;
      
      if (element.up('.siteMenuBarWidget')) 
        return false;
        
      if (element.up('.siteMenuBarWidgetContent')) 
        return false;
      
      this.closeMenuWidget();
    }
  }
});

SiteMenuBarWidget = Class.create({
  initialize: function () {
    this._menuElementClickListener = this._onMenuElementClick.bindAsEventListener(this); 
  },
  setup: function (menuElement, contentElement) {
    this._menuElement = menuElement;
    this._contentElement = contentElement;
    
    this._notificationElement = this._menuElement.down('.siteMenuBarWidgetNotification');
    
    if (!this._notificationElement) {
      this._notificationElement = new Element("div", {
        className: "siteMenuBarWidgetNotification"
      });
      this._notificationElement.hide();
      this._menuElement.appendChild(this._notificationElement);
    }
    
    Event.observe(this._menuElement, "click", this._menuElementClickListener);
  },
  destroy: function () {
    Event.stopObserving(this._menuElement, "click", this._menuElementClickListener);
  },
  getName: function () {
    return undefined;
  },
  showNotification: function (text) {
    this._notificationElement.update(text).show();
  },
  hideNotification: function () {
    this._notificationElement.hide();
  },
  getMenuElement: function () {
    return this._menuElement;
  },
  getContentElement: function () {
    return this._contentElement;
  },
  show: function () {
    if (this.isEnabled()) {
      this.getMenuElement().addClassName('siteMenuBarWidgetActive');
      this.getContentElement().addClassName('siteMenuBarWidgetContentActive');
      this.getContentElement().setStyle({
    	right: this.getMenuElement().getLayout().get("right") + 'px'
      });
      
      this.fire("show");
    }
  },
  hide: function () {
    this.getMenuElement().removeClassName('siteMenuBarWidgetActive');
    this.getContentElement().removeClassName('siteMenuBarWidgetContentActive');
    this.fire("hide");
  },
  isVisible: function () {
    return this.getMenuElement().hasClassName('siteMenuBarWidgetActive');
  },
  startLoadingAnimation: function () {
    this.getMenuElement().addClassName('siteMenuBarWidgetLoading');
  },
  stopLoadingAnimation: function () {
    this.getMenuElement().removeClassName('siteMenuBarWidgetLoading');
  },
  disableMenu: function () {
    this.getMenuElement().addClassName('siteMenuBarWidgetDisabled');
  },
  enableMenu: function () {
    this.getMenuElement().removeClassName('siteMenuBarWidgetDisabled');
  },
  isEnabled: function () {
    return !this.getMenuElement().hasClassName('siteMenuBarWidgetDisabled');
  },
  _onMenuElementClick: function (event) {
    this.fire("menuClick", {
      menuWidget: this
    }); 
  }
});

Object.extend(SiteMenuBarWidget.prototype, fni.events.FNIEventSupport);

SiteMenuBarWidgetSearch = Class.create(SiteMenuBarWidget, {
  initialize: function ($super) {
    $super();
  },
  setup: function ($super, menuElement, contentElement) {
    $super(menuElement, contentElement);
  },
  destroy: function ($super) {
    $super();
  },
  getName: function ($super) {
    return 'search';
  }
});

SiteMenuBarWidgetAccount = Class.create(SiteMenuBarWidget, {
  initialize: function ($super) {
    $super();
  },
  setup: function ($super, menuElement, contentElement) {
    $super(menuElement, contentElement);
  },
  destroy: function ($super) {
    $super();
  },
  getName: function ($super) {
    return 'account';
  }
});

SiteMenuBarWidgetLanguage = Class.create(SiteMenuBarWidget, {
  initialize: function ($super) {
    $super();
    
    this._liClickListener = this._onLiClick.bindAsEventListener();
  },
  setup: function ($super, menuElement, contentElement) {
    $super(menuElement, contentElement);
    
    var lis = this.getContentElement().select('li');
    for (var i = 0, l = lis.length; i < l; i++) {
      Event.observe(lis[i], "click", this._liClickListener);
    }
  },
  destroy: function ($super) {
    $super();
  },
  getName: function ($super) {
    return 'language';
  },
  _onLiClick: function (event) {
    var li = Event.element(event);
    if (li.tagName != 'LI')
      li = li.up('li');
    
    var locale = li.down('input[name="locale"]').value;
    if (locale) {
      var path = isLoggedIn() ? '/v1/users/SELF/changeLocale' : '/v1/users/ANONYMOUS/changeLocale';
    	
      API.put(CONTEXTPATH + path, {
	    parameters: {
	      locale: locale
	    },
	    onSuccess: function (jsonResponse) {
	      window.location.reload();
	    }
      });
    }
  }
});

CalendarMenuBarWidget = Class.create(SiteMenuBarWidget, {
  initialize: function ($super) {
    $super();
  },
  setup: function ($super, menuElement, contentElement) {
    $super(menuElement, contentElement);

    this._datePickerInput = new Element("input", {type: 'hidden', id: new Date().getTime() + '.calendar'});
    contentElement.appendChild(this._datePickerInput);
    
    var _this = this;
    var extendedDatePicker = Class.create(DatePicker, {
      nextMonth: function ($super) {
        $super();
        _this.fire("dateChange", {
          month: this._currentMonth,
          year: this._currentYear
        });
      },
      prevMonth: function ($super) {
        $super();
        _this.fire("dateChange", {
          month: this._currentMonth,
          year: this._currentYear
        });
      },
      close: function ($super) {
        if (this._closingPrevented) {
          this.allowClose();
        } else {
          $super();
        }
      },
      load: function ($super) {
        $super();
        
        var node = $(this._id_datepicker);
        
        // safari 1.0+
        node.onselectstart = function(){
          return false;
        };
        // ie 5.5+
        node.unselectable = "on";
        // mozilla
        node.style.MozUserSelect = "none";
      },
      documentClick: function ($super, event) {
        
      },
      getCell: function (year, month, day) {
        var id  = $A([this._id_datepicker, this._df.dateToString(year, month, day, '-') ]).join('-');
        return $(id);
      },
      getDate: function () {
        var timestamp = $(this._relative).value;
        var date = new Date();
        
        if (timestamp) {
          var dt = this._df.match(timestamp);
          date.setFullYear(Number(dt[0]));
          date.setMonth(Number(dt[1]) - 1);
          date.setDate(Number(dt[2]));
          date.setHours(0);
          date.setMinutes(0);    
          date.setSeconds(0);
          date.setMilliseconds(0);
        } 
        
        return date;  
      },
      allowClose: function () {
        this._closingPrevented = undefined;
      },
      preventClose: function () {
        this._closingPrevented = true;
      }
    });
    
    // TODO: Localization
    this._dateControl = new extendedDatePicker({
      relative: this._datePickerInput.id,
      keepFieldEmpty:true,
      enableShowEffect: false,
      enableCloseEffect: false,
      language: 'fi', //getLocale().getLanguage(),
      enableShowEffect: false,
      zindex: 999,
      relativePosition: false,
      leftOffset: 0,
      topOffset: 0,
      cellCallback: function () {
        _this.hide();
      },
      clickCallback: function () {
        _this.getContentElement().appendChild(this._div);
      }
    });
  },
  show: function ($super) {
    $super();
    this._dateControl.click();
    
    // TODO: Remove test data:
    
    var now = new Date();
    var publicStart = new Date(now.getFullYear(), now.getMonth(), 15);
    var publicEnd = new Date(now.getFullYear(), now.getMonth(), 16);
    
    this._addPublicEvent(publicStart, publicEnd, true, "PTE: Public Test Event #1");
    
    var priv1Start = new Date(now.getFullYear(), now.getMonth(), 16, 12, 30, 0);
    var priv1End = new Date(now.getFullYear(), now.getMonth(), 16, 13, 30, 0);
    
    this._addPrivateEvent(priv1Start, priv1End, false, "PTE: Private Test Event #1");
    
    var priv2Start = new Date(now.getFullYear(), now.getMonth(), 20, 10, 00, 0);
    var priv2End = new Date(now.getFullYear(), now.getMonth(), 20, 16, 00, 0);
    
    this._addPrivateEvent(priv2Start, priv2End, false, "PTE: Private Test Event #2");
  },
  getName: function ($super) {
    return 'calendar';
  },
  getName: function ($super) {
    return 'calendar';
  },
  _addPublicEvent: function (startTime, endTime, allDay, summary) {
    this._addEvent(startTime, endTime, allDay, summary, true);
  },
  _addPrivateEvent: function (startTime, endTime, allDay, summary) {
    this._addEvent(startTime, endTime, allDay, summary, false);
  },
  _addEvent: function (startTime, endTime, allDay, summary, isPublic) {
    if (allDay) {
      startTime.setHours(0, 0, 0, 0);
      endTime.setHours(23, 59, 59, 999);
    }
    
    var date = startTime;
    
    while (date.getTime() < endTime.getTime()) {
      var cell = this._dateControl.getCell(date.getFullYear(), date.getMonth() + 1, date.getDate());
      if (cell) {
        if (isPublic)
          cell.addClassName('siteMenuBarWidgetCalendarCellPublicEvents');
        else
          cell.addClassName('siteMenuBarWidgetCalendarCellPrivateEvents');
        
        var title = cell._summary;
        if (title) {
          title += decodeURIComponent('%0d') + summary; 
        } else {
          title = summary;
        }
        
        cell._summary = title;
        cell.setAttribute("title", title);
      }
    
      date.setDate(date.getDate() + 1);
    }
  }
});