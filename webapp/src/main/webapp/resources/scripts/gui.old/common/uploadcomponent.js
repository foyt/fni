UploadComponent = Class.create(GUIComponent, {
  initialize : function($super, options) {
    $super(options);

    this.domNode = new Element("div", {
      className : "uploadComponent",
    });
    
    if (options && options.required == true) {
      this.domNode.addClassName("uploadComponentRequired");
    }

    this._fieldCount = 0;
    this._disabled = false;

    this._uploadFieldChangeListener = this._onUploadFieldChange.bindAsEventListener(this);
    this._queueFileRemoveLinkClickListener = this._onQueueFileRemoveLinkClick.bindAsEventListener(this);

    this._fieldContainer = new Element("div", {
      className : "uploadComponentField"
    });
    this._queueContainer = new Element("div", {
      className : "uploadComponentQueue"
    });

    if (options.queueHeight) {
      this._queueContainer.setStyle({
        height : options.queueHeight + 'px'
      });
    }

    this.domNode.appendChild(this._fieldContainer);
    this.domNode.appendChild(this._queueContainer);
    
    this.domNode.store("uploadComponent", this);

    this._createUploadField();
  },
  setProgressBackgroundColor : function(fieldName, backgroundColor) {
    var progressElement = $(this.getComponentId() + '_' + fieldName + '.progress');
    if (progressElement != null) {
      progressElement.setStyle({
        backgroundColor : backgroundColor
      });
    }
  },
  showProgressBar : function(fieldName) {
    var progressLayputElement = $(this.getComponentId() + '_' + fieldName + '.progressoverlay');
    if (progressLayputElement != null) {
      progressLayputElement.setStyle({
        display : ''
      });
    }
  },
  hideProgressBar : function(fieldName) {
    var progressLayputElement = $(this.getComponentId() + '_' + fieldName + '.progressoverlay');
    if (progressLayputElement != null) {
      progressLayputElement.setStyle({
        display : 'none'
      });
    }
  },
  setProgress : function(fieldName, progress) {
    var progressLayputElement = $(this.getComponentId() + '_' + fieldName + '.progressoverlay');
    if (progressLayputElement != null) {
      var offset = -18 + (progress * ((progressLayputElement.getWidth() + 18) / 100));
      progressLayputElement.setStyle({
        backgroundPosition : offset + 'px 0px'
      });
    }
  },
  setProgressText : function(fieldName, text) {
    var progressTextElement = $(this.getComponentId() + '_' + fieldName + '.progresstext');
    if (text.blank()) {
      if (progressTextElement != null) {
        progressTextElement.update(text);
        progressTextElement.hide();
      }
    } else {
      if (progressTextElement != null) {
        progressTextElement.show();
        progressTextElement.update(text);
      }
    }
  },
  getFileSize : function(fieldName) {
    var queueElement = $(this.getComponentId() + '_' + fieldName);
    if (queueElement != null) {
      return queueElement._fileSizeBytes;
    }
  },
  clearQueue : function() {
    var files = this._queueContainer.select("div.uploadComponentQueueFile");
    for ( var i = 0, l = files.length; i < l; i++) {
      files[i].remove();
    }
    
    if (this.getComponentOptions().required == true) {
      this.domNode.validate(true);
    } 
  },
  hasFields: function () {
    return this._queueContainer.select("div.uploadComponentQueueFile").length > 0;
  },
  setDisabled: function (disabled) {
    this._disabled = disabled;
    
    if (this._disabled) {
      this.domNode.addClassName("uploadComponentDisabled");
      this._uploadField.setAttribute("disabled", "disabled");
    } else {
      this._uploadField.removeAttribute("disabled");
      this.domNode.removeClassName("uploadComponentDisabled");
    }
  },
  _onUploadFieldChange : function(event) {
    var field = Event.element(event);
    this._moveFieldToQueue(field);
    this._createUploadField();
    
    if (this.getComponentOptions().required == true) {
      this.domNode.validate(true);
    } 
  },
  _onQueueFileRemoveLinkClick : function(event) {
    if (this._disabled == false) {
      var removeButton = Event.element(event);
      Event.stopObserving(removeButton, "click", this._queueFileRemoveLinkClickListener);
      var fieldElement = removeButton.up(".uploadComponentQueueFile");
      fieldElement.remove();
  
      if (this.getComponentOptions().required == true) {
        this.domNode.validate(true);
      } 
    }
  },
  _createUploadField : function() {
    this._uploadField = new Element("input", {
      type : "file",
      name : this.getComponentOptions().name + this._fieldCount
    });
    Event.observe(this._uploadField, "change", this._uploadFieldChangeListener);
    this._fieldContainer.appendChild(this._uploadField);
    this._fieldCount++;
  },
  _moveFieldToQueue : function(field) {
    var uploadFiles = field.files;
    var fieldName = field.name;
    var fileName = '';
    var fileSize = '';
    var fileSizeBytes = -1;

    if (uploadFiles && (uploadFiles.length > 0) && uploadFiles[0]) {
      fileName = field.files[0].fileName||field.files[0].name;
      fileSizeBytes = field.files[0].fileSize||field.files[0].size;
    } else {
      var fullpath = field.value;
      fileName = fullpath.substring(fullpath.lastIndexOf('\\') + 1);
    }

    if (fileSizeBytes > -1) {
      fileSize = this._getHumanReadableFileSize(fileSizeBytes);
    } else {
      fileSize = '';
    }

    field.addClassName("uploadComponentQueueFileHidden");
    Event.stopObserving(field, "change", this._uploadFieldChangeListener);

    var queueElement = new Element("div", {
      className : "uploadComponentQueueFile",
      id : this.getComponentId() + '_' + fieldName
    });
    queueElement._fileSizeBytes = fileSizeBytes;
    var progressElement = new Element("div", {
      className : "uploadComponentQueueFileProgress",
      id : this.getComponentId() + '_' + fieldName + '.progress'
    });
    var progressOverlayElement = new Element("div", {
      className : "uploadComponentQueueFileProgressOverlay",
      style : "display: none",
      id : this.getComponentId() + '_' + fieldName + '.progressoverlay'
    });
    var progressTextContainer = new Element("div", {
      className : "uploadComponentQueueFileProgressTextContainer"
    });
    var progressText = new Element("div", {
      className : "uploadComponentQueueFileProgressText",
      id : this.getComponentId() + '_' + fieldName + '.progresstext'
    });
    var fileNameElement = new Element("div", {
      className : "uploadComponentQueueFileName"
    }).update(fileName);
    var fileSizeElement = new Element("div", {
      className : "uploadComponentQueueFileSize"
    }).update(fileSize);
    var removeButton = new Element("a", {
      className : "uploadComponentQueueFileRemoveButton"
    });
    // TODO: Tooltip
    Event.observe(removeButton, "click", this._queueFileRemoveLinkClickListener);

    queueElement.appendChild(fileNameElement);
    queueElement.appendChild(fileSizeElement);
    queueElement.appendChild(removeButton);
    queueElement.appendChild(field);
    queueElement.appendChild(progressElement);
    queueElement.appendChild(progressOverlayElement);
    progressTextContainer.appendChild(progressText);
    queueElement.appendChild(progressTextContainer);

    progressText.hide();

    this._queueContainer.appendChild(queueElement);
  },
  _getHumanReadableFileSize : function(bytes) {
    if (bytes > (1024 * 1024))
      return this._roundTo(bytes / (1024 * 1024), 2) + ' MB';
    else if (bytes > 1024)
      return this._roundTo(bytes / 1024, 2) + ' kB';
    else
      return bytes + ' B';
  },
  _roundTo: function (value, digits) {
    var mul = Math.pow(10, digits);
    return Math.round(value * mul) / mul;
  }
});

RequiredUploadComponentFieldValidator = Class.create(fi.internetix.validation.FieldValidator, {
  initialize : function($super) {
    $super();
  },
  validate: function ($super, field) {
    var uploadComponent = field.retrieve("uploadComponent");
    if (uploadComponent) {
      if (field.form === undefined) {
        field.form = field.up('form');
      }
      
      if (uploadComponent.hasFields())
        return fi.internetix.validation.FieldValidator.STATUS_VALID;
    }
    
    return fi.internetix.validation.FieldValidator.STATUS_INVALID;
  },
  getType: function ($super) {
    return fi.internetix.validation.FieldValidator.TYPE_MANDATORY;
  },
  getClassName: function () {
    return 'uploadComponentRequired';
  }
});

fi.internetix.validation.FieldValidatorVault.registerValidator(new RequiredUploadComponentFieldValidator());