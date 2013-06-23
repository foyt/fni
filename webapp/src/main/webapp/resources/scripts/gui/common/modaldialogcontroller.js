ModalDialogController = Class.create({
  initialize: function (options) {
    this._options = Object.extend({
      content: null,
      contentUrl: null,
      useIframe: false
    }, options||{});
    
    this._dialog = null;
    
    this._beforeDialogCloseListener = this._onBeforeDialogClose.bindAsEventListener(this);
  },
  destroy: function () {
    if (this._dialog) {
      var element = this._dialog.toElement();	
      Event.stopObserving(element, "ui:dialog:before:close",  this._beforeDialogCloseListener);
      deinitializeValidation(element);
      element.select('.formSubmitField').invoke('purge');
      if (this._contentFrame)
        this._contentFrame.purge();
      this._dialog.destroy();
      
      this._dialog = null;
    }
  },
  hide: function () {
	this.getDialog().element.hide();  
  },
  show: function () {
	this.getDialog().element.show();  
  },
  open: function () {
    this.setup();
  },
  close: function () {
    this.getDialog().close();
    this.destroy();
  },
  startLoading: function () {
    this.getDialog().toElement().appendChild(new Element("div", {
      className: "dialogLoadPane" 
    }));
  },
  stopLoading: function () {
    var dialogLoadPane = this.getDialog().toElement().down('.dialogLoadPane');
    if (dialogLoadPane) {
      dialogLoadPane.remove();
    }
  },
  setup: function (callback) {
    if (this._options.content !== null) {
      this._openDialog(this._options.content, callback);
    } else {
      if (this._options.contentUrl !== null) {
        if (this._options.useIframe === true) {
          this._contentFrame = new Element("iframe", {
            style: "border: 0px; width: 100%; height: 100%; position: absolute; left: 0px; right: 0px;",
            frameBorder: "0",
            src: this._options.contentUrl
          });
          
          var _this = this;
          Event.observe(this._contentFrame, "load", function (event) {
            var contentFrame = Event.element(event);

            var frameWindow = contentFrame.contentWindow;
            if (!frameWindow) {
              var frameDocument = contentFrame.contentDocument;
              frameWindow = frameDocument.parentWindow;
              if (!frameWindow)
                frameWindow = frameDocument.defaultView;
            }
            
            if (frameWindow) {
              frameWindow.getDialog = function () {
                return _this;
              };
              
              if (Object.isFunction(frameWindow.onWindowReady)) {
                frameWindow.onWindowReady();
              }
            }
            
            if (Object.isFunction(callback))
              callback(_this);
          });
          
          var dialogContentElement = new Element("div").update(this._contentFrame);
          this._openDialogContentElement(dialogContentElement);
        } else {        
          var _this = this;
          // TODO: Error handling
          new Ajax.Request(this._options.contentUrl, {
            onSuccess: function(response) {
              _this._openDialog(response.responseText, callback);
            }
          });
        }
      }
    }
  },
  getDialog: function () {
    return this._dialog;
  },
  setTitle: function (title) {
    this.getDialog().titleText.update(title);
  },
  getOptions: function () {
    return this._options;
  },
  _openDialog: function (content, callback) {
    var dialogContentElement = new Element("div");
    
    if (Object.isElement(content))
      dialogContentElement.appendChild(content);
    else
      dialogContentElement.update(content);
    
    this._openDialogContentElement(dialogContentElement, callback);
  },
  _openDialogContentElement: function (dialogContentElement, callback) {
    var dialogElement = new Element("div");
    if (this._options.width) {
      dialogElement.setStyle({
        width: this._options.width + 'px'
      });
    }
    if (this._options.height) {
      dialogElement.setStyle({
        height: this._options.height + 'px'
      });
    }

    this._dialog = new S2.UI.Dialog(dialogElement, {
      zIndex: 2000,
      title: this._options.title,
      content: dialogContentElement,
      buttons: false,
      closeOnReturn: false
    });

    Event.observe(this._dialog.element, "ui:dialog:before:close",  this._beforeDialogCloseListener);

    this._dialog.open();
    
    this._dialog.overlay.toElement().setStyle({
      zIndex: 1999
    });
    
    if (this._options.position == 'fixed') {
      dialogElement.addClassName('dialogFixed');
      
      var vSize = document.viewport.getDimensions();
      var dialogLayout = dialogElement.getLayout();
      
      var position = {
        left: ((vSize.width  / 2) - (dialogLayout.get('width')  / 2)).round(),
        top:  ((vSize.height / 2) - (dialogLayout.get('height') / 2)).round()
      };

      dialogElement.setStyle({
        left: position.left + 'px',
        top:  position.top  + 'px'
      });
    }
    
    if (this._options.overflow === 'visible') {
      dialogElement.setStyle({
        overflow: 'visible'
      });
      
      dialogContentElement.setStyle({
        overflow: 'visible'
      });
    }
    
    if ((this._options.useIframe === true) && (this._options.height)) {
      var frameHeight = this._options.height - (
    	this._dialog.titleBar.getLayout().get("height") + 
    	this._dialog.titleBar.getLayout().get("margin-top") + 
    	this._dialog.titleBar.getLayout().get("margin-bottom") + 
    	this._dialog.titleBar.getLayout().get("padding-top") + 
    	this._dialog.titleBar.getLayout().get("padding-bottom")
      );
      
	  this._contentFrame.setStyle({
		height: frameHeight + 'px'
	  });
    }
    
    if (Object.isFunction(callback)) {
      callback.call(this);
    }
    
    initializeValidation(dialogContentElement);
  },
  _createButton: function (label, type, onClick, formValid) {
    var field = new Element("div", {
      className: "formField formSubmitField"
    });
    var input = new Element("input", {
      type: "submit",
      value: label
    });
    field.appendChild(input);
    
    var listenerAdded = false;
    
    switch (type) {
      case 'ok':
        input.addClassName("formOkButton");
      break;
      case 'save':
        input.addClassName("formSaveButton");
      break;
      case 'cancel':
        input.addClassName("formCancelButton");
        if (!Object.isFunction(onClick)) {
          Event.observe(input, "click", function (event) {
            this.close();
          }.bind(this)); 
          
          listenerAdded = true;
        }
      break;
      case 'delete':
        input.addClassName("formDeleteButton");
      break;
      default:
        alert('Unknown button type: ' + type);
      break;
    }
    
    if (listenerAdded == false && Object.isFunction(onClick)) {
      var _this = this;
      Event.observe(input, "click", function (event) {
        onClick({
          dialog:_this
        });
      }); 
    }
    
    if (formValid)
      input.addClassName('formvalid');
    
    return field;
  },
  _onBeforeDialogClose: function (event) {
	this.destroy();
  }
});

Object.extend(ModalDialogController.prototype, fni.events.FNIEventSupport);