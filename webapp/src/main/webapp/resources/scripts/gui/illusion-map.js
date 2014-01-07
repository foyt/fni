(function() {
  'use strict';
  
  var TRANSPARENT_BACKGROUND= "data:image/png;base64,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";
  
  function RGBAsToInts (rgbas) {
    var ints = new Array(rgbas.length >> 2);
    for (var i = 0, l = rgbas.length; i < l; i += 4) {
      ints[i >> 2] = ((rgbas[i] << 24) + (rgbas[i + 1] << 16) + (rgbas[i + 2] << 8) + (rgbas[i + 3] << 0)) >>> 0;
    }
    
    return ints;
  };

  function IntsToRGBAs (ints) {
    var result = new Array(ints.length << 2);
    
    for (var i = 0, l = ints.length; i < l; i++) {
      var ri = i << 2;
      result[ri] = (ints[i] & 4278190080) >>> 24;
      result[ri + 1] = (ints[i] & 16711680) >>> 16;
      result[ri + 2] = (ints[i] & 65280) >>> 8;
      result[ri + 3] = (ints[i] & 255) >>> 0;
    }
    
    return result;
  };
  
  function RGBAToInt (r, g, b, a) {
    return RGBAsToInts([r, g, b, a]);
  };
  
  function IntToRGBA (int) {
    return IntsToRGBAs([int]);
  };
  
  function indexToCoords(index, screenWidth) {
    var y = Math.floor(index / screenWidth);
    var x = index - (y * screenWidth);
    return [x, y];
  };
  
  var Paint = function (type, value) {
    this._type = type;
    this._value = value;
  };
  
  Paint.prototype.getType = function () {
    return this._type;
  };
  
  Paint.prototype.getValue = function () {
    return this._value;
  };
  
  Paint.prototype.toCSS = function () {
    switch (this._type) {
      case 'color':
        return this._value;
      break;
      case 'pattern':
        return 'url(' + this._value + ')';        
      break;
    }
  };
  
  Paint.prototype.toCanvasStyle = function () {
    switch (this._type) {
      case 'color':
        return this._value;
      break;
      case 'pattern': 
        var patternImage = new Image();
        patternImage.src = this._value;
        return $('<canvas>')[0]
          .getContext("2d").createPattern(patternImage, 'repeat');
      break;
    }
  };
  
  $.widget("custom.illusionMapButtonTool", {
    options : {},
    _create : function() {
      this.element
        .on("click", $.proxy(this._onClick, this))
        .addClass('map-tool map-button-tool');
    },
    
    _onClick: function (event) {
      this.element.closest('.map-tools').find('.map-button-tool-selected').illusionMapButtonTool("deactivate");
      this.element.illusionMapButtonTool("activate");
    },
    
    activate: function () {
      this.element.addClass('map-button-tool-selected');
      if (this.options.activate) {
        this.options.activate.call(this.element);
      }
    },
    
    deactivate: function () {
      this.element.removeClass('map-button-tool-selected');
      if (this.options.activate) {
        this.options.deactivate.call(this.element);
      }
    },
    
    applyStyle: function (context) {
      this.element.closest('.map').find('.map-tool-paints').illusionMapToolPaints("applyStyle", context);
    },
    
    _destroy : function() {
    }
  });
  
  $.widget("custom.illusionMapToolBrush", {
    options : {},
    _create : function() {
      this._canvasMouseMoveListener = $.proxy(this._onCanvasMouseMove, this);
      this._canvasMouseDragListener = $.proxy(this._onCanvasMouseDrag, this);
      this._canvasAfterRedrawListener = $.proxy(this._onCanvasAfterRedraw, this);
      
      this._canvas = this.element.closest('.map').find('.map-canvas');
      this._cursorX = null;
      this._cursorY = null;
      
      this.element
        .illusionMapButtonTool({
          activate: $.proxy(this._onActivate, this),
          deactivate: $.proxy(this._onDeactivate, this)
        })
        .addClass('map-tool-brush');
    },
    
    _getRadius: function () {
      // TODO: Move to tool options
      return 10;
    },
    
    _onActivate: function (event) {
      this._canvas.on("canvas.mousemove", this._canvasMouseMoveListener);
      this._canvas.on("canvas.mousedrag", this._canvasMouseDragListener);
      this._canvas.on("canvas.afterredraw", this._canvasAfterRedrawListener);
    },
    
    _onDeactivate: function (event) {
      this._canvas.off("canvas.mousemove", this._canvasMouseMoveListener);
      this._canvas.off("canvas.mousedrag", this._canvasMouseDragListener);
      this._canvas.off("canvas.afterredraw", this._canvasAfterRedrawListener);
    },
    
    _onCanvasAfterRedraw: function (event) {
      this._canvas.illusionMapCanvas('drawScreen', $.proxy(function (screenCtx) {
        $(this.element).illusionMapButtonTool("applyStyle", screenCtx);
        
        screenCtx.beginPath();
        screenCtx.arc(this._cursorX, this._cursorY, this._getRadius(), 0, 2 * Math.PI, false);
        screenCtx.closePath();  
        
        screenCtx.fill();
      }, this));
    },
    
    _onCanvasMouseMove: function (event) {
      this._cursorX = event.canvasX;
      this._cursorY = event.canvasY;
    },
    
    _onCanvasMouseDrag: function (event) {
      this._canvas.illusionMapCanvas('drawOffscreen', $.proxy(function (offscreenCtx) {
        $(this.element).illusionMapButtonTool("applyStyle", offscreenCtx);
        offscreenCtx.beginPath();
        offscreenCtx.arc(event.canvasX, event.canvasY, this._getRadius(), 0, 2 * Math.PI, false);
        offscreenCtx.closePath();
        offscreenCtx.fill();
      }, this));
    },
    
    _destroy : function() {
    }
  });
  
  $.widget("custom.illusionMapToolArea", {
    options : {},
    _create : function() {
      this._canvasMouseUpListener = $.proxy(this._onCanvasMouseUp, this);
      this._canvasMouseDownListener = $.proxy(this._onCanvasMouseDown, this);
      this._canvasMouseDragListener = $.proxy(this._onCanvasMouseDrag, this);
      this._canvasAfterRedrawListener = $.proxy(this._onCanvasAfterRedraw, this);
      
      this._canvas = this.element.closest('.map').find('.map-canvas');
      this._dragPath = null;
      
      this.element
        .illusionMapButtonTool({
          activate: $.proxy(this._onActivate, this),
          deactivate: $.proxy(this._onDeactivate, this)
        })
        .addClass('map-tool-area');
    },
    
    _onActivate: function (event) {
      this._canvas.on("mousedown", this._canvasMouseDownListener);
      this._canvas.on("mouseup", this._canvasMouseUpListener);
      this._canvas.on("canvas.mousedrag", this._canvasMouseDragListener);
      this._canvas.on("canvas.afterredraw", this._canvasAfterRedrawListener);
    },
    
    _onDeactivate: function (event) {
      this._canvas.off("mousedown", this._canvasMouseDownListener);
      this._canvas.off("mouseup", this._canvasMouseUpListener);
      this._canvas.off("canvas.mousedrag", this._canvasMouseDragListener);
      this._canvas.off("canvas.afterredraw", this._canvasAfterRedrawListener);
    },

    _onCanvasMouseDown: function (event) {
      this._dragPath = new Array();
    },

    _onCanvasMouseDrag: function (event) {
      this._dragPath.push({
        x: event.canvasX,
        y: event.canvasY
      });
      
      this._dragPath = simplify(this._dragPath, 1, true);
    },

    _onCanvasMouseUp: function (event) {
      this._canvas.illusionMapCanvas('drawOffscreen', $.proxy(function (offscreenCtx) {
        this._drawPath(offscreenCtx);
      }, this));
      
      this._dragPath = null;
    },
    
    _onCanvasAfterRedraw: function (event) {
      if (this._dragPath != null) {
        this._canvas.illusionMapCanvas('drawScreen', $.proxy(function (screenCtx) {
          this._drawPath(screenCtx);
        }, this));
      }
    },
    
    _drawPath: function (context) {
      $(this.element).illusionMapButtonTool("applyStyle", context);
      
      context.beginPath();
      if (this._dragPath.length > 0) {
        for (var i = 0, l = this._dragPath.length; i < l; i++) {
          var p = this._dragPath[i];
          if (p) {
            context.lineTo(p.x, p.y);
          }
        }
      }

      context.closePath(); 
      context.fill();
    },
    
    _destroy : function() {
    }
  });
  
  $.widget("custom.illusionMapToolPath", {
    options : {},
    _create : function() {
      this.element
        .illusionMapButtonTool()
        .addClass('map-tool-path');
    },
    
    _destroy : function() {
    }
  });
 
  $.widget("custom.illusionMapPaintPicker", {
    _create : function() {
      this._tabNamePrefix = new Date().getTime() + '-';
      
      var _this = this;
      this.element
        .dialog({
          width: 400,
          height: 337,
          buttons: [{
            'text': 'Ok',
            'click': function () {
              $(this).dialog("close"); 
              if (_this.options.ok) {
                _this.options.ok(_this._paint);
              }
            }
          }, {
            'text': 'Cancel',
            'click': function () {
              $(this).dialog("close"); 
            }
          }]
        });

      this._tabs = $('<div>')
        .css({
          'display': 'inline-block',
          'width': '297px'        
        });
      this._tabLabels = $('<ul>').appendTo(this._tabs);
      this._tabContents = $('<div>').appendTo(this._tabs);
      
      this._colorTab = this._addTab('color', 'Color');
      
      $('<input>')
        .appendTo(this._colorTab)
        .spectrum({
          flat: true,
          showInput: false,
          showButtons: false,
          showAlpha: true,
          clickoutFiresChange: true,
          move: $.proxy(function(color) {
            this._selectPaint("color", color.toRgbString());
          }, this)
        });
      
      this._patternTab = this._addTab('pattern', 'Pattern');
      var patternImages = $('<div>')
        .appendTo(this._patternTab);
      
      for (var i = 0, l = this.options.patterns.length; i < l; i++) {
        var pattern = this.options.patterns[i];
        $('<img>')
          .css({
            'maxWidth': '48px',
            'maxHeight': '48px'
          })
          .attr("src", pattern.url)
          .attr("title", pattern.name)
          .appendTo(patternImages)
          .click(function () {
            var canvas = $('<canvas>')
              .attr({
                width: $(this).prop('naturalWidth'),
                height: $(this).prop('naturalHeight')
              })[0];
            var ctx = canvas.getContext("2d");
            ctx.drawImage($(this)[0], 0, 0);
            _this._selectPaint('pattern', canvas.toDataURL());
          });
      }
      
      this._preview = $('<div>')
        .css({
          'width': '64px',
          'marginLeft': '0.5em',
          'display': 'inline-block',
          'vertical-align': 'top'
        });

      this._previewPaint = $('<div>')
        .addClass('ui-widget-content ui-corner-all')
        .css({
          'width': '64px',
          'height': '64px',
          "marginBottom": "0.5em"
        }).appendTo(this._preview);
      
      this._tabs.appendTo(this.element);
      this._preview.appendTo(this.element);

      this._tabs.tabs();
      
      this._selectPaint(this.options.type, this.options.value);
    },
    
    _selectPaint: function (type, value) {
      this._paint = new Paint(type, value);
      this._previewPaint.css("background", this._paint.toCSS());    
    },
   
    _addTab: function (name, label) {
      $('<li>')
        .append(
          $('<a>')
            .text(label)
            .attr('href', '#' + this._tabNamePrefix + name))
        .appendTo(this._tabLabels);
      
      return $('<div>')
        .css({
          'height': '186px',
          "padding": "0.5em"
        })
        .attr('id', this._tabNamePrefix + name)
        .appendTo(this._tabContents);
    },
    
    _destroy : function() {
    }
  });
  
  $.widget("custom.illusionMapToolPaints", {
    options : {},
    _create : function() {
      this.element
        .addClass('map-tool map-tool-paints');

      this._fill = new Paint(this.options.fill.type, this.options.fill.value);
      this._stroke = new Paint(this.options.stroke.type, this.options.stroke.value);
      
      this._fillPaint = $('<div>')
        .addClass('map-fill-paint')
        .click($.proxy(this._onFillClick, this));
      
      this._strokePaint = $('<div>')
        .addClass('map-stroke-paint')
        .click($.proxy(this._onStrokeClick, this));

      $('<div>')
        .append(this._fillPaint)
        .css({
          'background': TRANSPARENT_BACKGROUND
        }).appendTo(this.element);
        
      $('<div>')
        .append(this._strokePaint)
        .css({
          'background': TRANSPARENT_BACKGROUND
        }).appendTo(this.element);
    },
    
    applyStyle: function (context) {
      context.fillStyle = this._fill.toCanvasStyle();
      context.strokeStyle = this._stroke.toCanvasStyle();
    },
    
    _loadPatterns: function (callback) {
      this.options.patterns(function (patterns) {
        callback(patterns);
      });
    },
    
    _onFillClick: function () {
      this._loadPatterns($.proxy(function (patterns) {
        $('<div>')
          .illusionMapPaintPicker({
            type: this._fill.getType(),
            value: this._fill.getValue(),
            patterns: patterns,
            ok: $.proxy(function (paint) {
              this._fill = paint;
              this._fillPaint.css("background", paint.toCSS());
            }, this)
          });
      }, this));
    },
    
    _onStrokeClick: function () {
      this._loadPatterns($.proxy(function (patterns) {
        $('<div>')
          .illusionMapPaintPicker({
            type: this._stroke.getType(),
            value: this._stroke.getValue(),
            patterns: patterns,
            ok: $.proxy(function (type, value, cssPaint) {
              this._stroke = paint;
              this._strokePaint.css("background", paint.toCSS());
            }, this)
          });
      }, this));
    },
    
    _destroy : function() {
    }
  });
  
  $.widget("custom.illusionMapTools", {
    options : {},
    _create : function() {
      this.element.addClass("map-tools");
    },
    _destroy : function() {
    }
  });
  
  $.widget("custom.illusionMapCoOps", {
    options: {
      changePollInternal: 200,
      changeFlushInternal: 200
    },
    _create : function() {
      this._pollingChanges = false;
      this._unsavedChanges = null;
      
      this.element
        .on("coops.join", $.proxy(this._onCoOpsJoin, this));
      
      $.ajax(this.options.serverUrl + '/join', {
        data: {
          'algorithm': 'uint2darr-lw',
          'protocolVersion': '1.0.0draft2'
        },
        success: $.proxy(this._onJoinRequstSuccess, this)
      });
    },
    
    _pollChanges: function () {
      if (this._pollingChanges == true) {
        var data = this.element.illusionMapCanvas("offscreenData");

        var diff = this._diffImageData(this._currentData, data);
        if (!diff.matches) {
          this.element.trigger(jQuery.Event("coops.changed"), {
            changes: diff.changes
          });
          
          this._currentData = data;
        }

        this._changePollTimeoutId = setTimeout($.proxy(this._pollChanges, this), this.options.changePollInternal); 
      }
    },
    
    _startChangePolling: function () {
      if (this._pollingChanges == false) {
        console.log("START");
        this._pollingChanges = true;
        this._pollChanges();
      }
    },
    
    _stopChangePolling: function () {
      if (this._pollingChanges == true) {
        console.log("STOP");
        clearTimeout(this._changePollTimeoutId);
        this._pollingChanges = false;
      }
    },

    _diffImageData: function (data1, data2) {
      var changes = new Array();
      var matches = true;
      
      for (var i = 0; i < data1.length; i++) {
        if (data1[i] !== data2[i]) {
          var coordinate = indexToCoords(i, this.element.width());          
          changes.push(coordinate[0], coordinate[1], data2[i]);
          matches = false;
        }
      };
      
      return {
        changes: changes,
        matches: matches
      };
    },
    
    _onJoinRequstSuccess: function (data, textStatus, jqXHR) {
      var imageUrl = 'data:' + data.contentType + ';base64,' + data.content;
      $(this.element).illusionMapCanvas("loadImage", imageUrl, $.proxy(function () {
        this._sessionId = data.sessionId;
        this._revisionNumber = data.revisionNumber;
        this.element.trigger("coops.join", data);
      }, this));
    },
    
    _onCoOpsJoin: function (event, data) {
      this.element
        .on("coops.changed", $.proxy(this._onCoOpsChanged, this))
        .on("coops.patchaccepted", $.proxy(this._onCoOpsPatchAccepted, this))
        .on("coops.patchapplied", $.proxy(this._onCoOpsPatchApplied, this));

      this._pollUpdates();
      this._flushChanges();
      this._currentData = this.element.illusionMapCanvas("offscreenData");
      this._startChangePolling();
    },
    
    _onCoOpsChanged: function (event, data) {
      if (this._unsavedChanges) {
// TODO: Remove duplicates        
//        var keys = new Array();
//        for (var i = 0, l = data.changes.length; i < l; i += 3) {
//          keys.push(data.changes[i] + ',' + data.changes[i + 1]);
//        }
//        
//        for (var i = this._unsavedChanges.length - 3; i >= 0; i -= 3) {
//          var key = this._unsavedChanges[i] + ',' + this._unsavedChanges[i + 1];
//          if (keys.indexOf(key) != -1) {
//            this._unsavedChanges.splice(i, 3);
//          }
//        }
        this._unsavedChanges.push(data.changes);
      } else {
        this._unsavedChanges = data.changes;
      }
    },
    
    _onCoOpsPatchAccepted: function (event, data) {
      this._revisionNumber = data.revisionNumber;
      this._unsavedChanges = null;
    },
    
    _onCoOpsPatchApplied: function (event, data) {
      this._revisionNumber = data.revisionNumber;
    },
    
    _pollUpdates : function() {
      $.ajax(this.options.serverUrl + '/update', {
        data: {
          'revisionNumber': this._revisionNumber
        },
        complete: $.proxy(function (jqXHR, textStatus) {
          var status = jqXHR.status;
          switch (status) {
            case 200:
              var data = $.parseJSON(jqXHR.responseText);
              this._stopChangePolling();
              this._applyPatches(data, $.proxy(function () {
                this._startChangePolling();
              }, this));
            break;
            case 204:
            case 304:
              // Not modified
            break;
            default:
              // TODO: Proper error handling
              alert(textStatus);
            break;
          }
          
          setTimeout($.proxy(function () {
            this._pollUpdates();
          }, this), this.options.changePollInternal);
        }, this)
      });
      
    },
    
    _flushChanges : function() {
      if (this._unsavedChanges) {
        var patches = this._unsavedChanges;
        
        this._stopChangePolling();
        
        $.ajax(this.options.serverUrl, {
          type: 'PATCH',
          data: JSON.stringify({
            'patch': JSON.stringify(patches),
            'revisionNumber': this._revisionNumber, 
            'sessionId': this._sessionId 
          }),
          complete: $.proxy(function (jqXHR, textStatus) {
            var status = jqXHR.status;
            switch (status) {
              case 204:
                // Request was ok
              break;
              case 409:
                // Patch was rejected
              break;
              default:
                // TODO: Proper error handling
                alert('Unknown Error');
              break;
            }
            
            setTimeout($.proxy(function () {
              this._flushChanges();
            }, this), this.options.changeFlushInternal);
          }, this)
        });
      } else {
        setTimeout($.proxy(function () {
          this._flushChanges();
        }, this), this.options.changeFlushInternal);
      }

    },
    
    _applyPatches: function (patches, callback) {
      var patch = patches.splice(0, 1)[0];
      this._applyPatch(patch, $.proxy(function () {
        if (patches.length > 0) {
          this._applyPatches(patches, callback);
        } else {
          if (callback) {
            callback.call(this);
          }
        }
      }, this));
    },
    
    _applyPatch: function (patch, callback) {
      if (this._sessionId != patch.sessionId) {
        this.element.illusionMapCanvas('drawOffscreen', $.proxy(function (offscreenCtx) {
          var changes = JSON.parse(patch.patch);
          for (var i = 0, l = changes.length; i < l; i += 3) {
            var x = changes[i];
            var y = changes[i + 1];
            var v = changes[i + 2];
            var rgba = IntToRGBA(v);
            offscreenCtx.fillStyle = 'rgba(' + rgba.join(',') + ')';
            offscreenCtx.fillRect(x, y, 1, 1);
          }
        }, this));

        this.element.trigger(jQuery.Event("coops.patchapplied"), {
          revisionNumber: patch.revisionNumber
        });

        if (callback) {
          callback.call(this);
        }
      } else {
        this.element.trigger(jQuery.Event("coops.patchaccepted"), {
          revisionNumber: patch.revisionNumber
        });

        if (callback) {
          callback.call(this);
        }
      }
    }
    
  });
  
  $.widget("custom.illusionMapCanvas", {
    options : {
      redrawInternal: 5
    },
    _create : function() {
      this._mouseDown = false;
      this._offscreenDirty = false;
      this._screenDirty = false;

      this.element
        .attr({
          width: this.options.width,
          height: this.options.height
        })
        .css({
          'background': 'url(' + TRANSPARENT_BACKGROUND + ')'
        })
        .on("mousedown", $.proxy(this._onMouseDown, this))
        .on("mouseup", $.proxy(this._onMouseUp, this))
        .on("mousemove", $.proxy(this._onMouseMove, this))
        .addClass('map-canvas');
      
      this._offscreenCanvas = $('<canvas>')
        .attr({
          width: this.options.width,
          height: this.options.height
        });
      
      this._scheduleRedraw();
    },
    
    offscreenCtx: function () {
      return this._offscreenCanvas[0].getContext("2d");
    },
    
    screenCtx: function () {
      return this.element[0].getContext("2d");
    },

    screenDirty: function (value) {
      if (value !== undefined) {
        this._screenDirty = value;
      } else {
        return this._screenDirty;
      }
    },
    
    offscreenDirty: function (value) {
      if (value !== undefined) {
        this._offscreenDirty = value;
      } else {
        return this._offscreenDirty;
      }
    },

    drawScreen: function (func) {
      func.call(this, this.screenCtx());
      this.screenDirty(true);
    },
    
    drawOffscreen: function (func) {
      func.call(this, this.offscreenCtx());
      this.offscreenDirty(true);
    },
    
    flipToScreen: function () {
      var screenCtx = this.screenCtx();
      screenCtx.clearRect(0, 0, this.options.width, this.options.height); 
      screenCtx.drawImage(this._offscreenCanvas[0], 0, 0, this.options.width, this.options.height);
    },
    
    loadImage: function (url, callback) {
      var imageObj = new Image();
      imageObj.onload = $.proxy(function() {
        this.drawOffscreen($.proxy(function (context) {
          context.drawImage(imageObj, 0, 0);
          if (callback) {
            callback.call(this);
          }
        }, this));
      }, this);
      imageObj.src = url;
    },
    
    _scheduleRedraw: function () {
      this.element.trigger(jQuery.Event("canvas.beforeredraw"));

      if (this.offscreenDirty() || this.screenDirty()) {
        this.flipToScreen();
        this.screenDirty(false);
        this.offscreenDirty(false);
      }
      
      this.element.trigger(jQuery.Event("canvas.afterredraw"));

      this._redrawTimeoutId = setTimeout($.proxy(this._scheduleRedraw, this), this.options.redrawInternal); 
    },
    
    offscreenData: function () {
       var imageData = this.offscreenCtx().getImageData(0, 0, this.options.width, this.options.height);
       return RGBAsToInts(imageData.data);
    },
    
    _onMouseDown: function (e) {
      var offset = this.element.offset();
      
      this.element.trigger(jQuery.Event("canvas.mousedown", {
        originalEvent: e,
        canvasX: e.pageX - offset.left,
        canvasY: e.pageY - offset.top,
      }));
      
      this._mouseDown = true;
    },
    
    _onMouseUp: function (e) {
      var offset = this.element.offset();
      
      this.element.trigger(jQuery.Event("canvas.mouseup", {
        originalEvent: e,
        canvasX: e.pageX - offset.left,
        canvasY: e.pageY - offset.top,
      }));
      
      this._mouseDown = false;
    },
    
    _onMouseMove: function (e) {
      var offset = this.element.offset();
      
      this.element.trigger(jQuery.Event("canvas.mousemove", {
        originalEvent: e,
        canvasX: e.pageX - offset.left,
        canvasY: e.pageY - offset.top,
      }));
      
      if (this._mouseDown) {
        this.element.trigger(jQuery.Event("canvas.mousedrag", {
          originalEvent: e,
          canvasX: e.pageX - offset.left,
          canvasY: e.pageY - offset.top,
        }));
      }
    },
    _destroy : function() {
    }
  });
  
  $.widget("custom.illusionMap", {
    options : {},
    _create : function() {
      this.element.addClass('map');
    },
    _destroy : function() {
    }
  });
  
  $(document).ready(function() {
    var map = $('<div>')
      .appendTo($('.map-container'))
      .illusionMap();
    
    $('<canvas>')
      .appendTo(map)
      .illusionMapCanvas({
        width: 860,
        height: 300
      })
      .illusionMapCoOps({
        serverUrl: CONTEXTPATH + '/forge/coops/' + '3004',
        changePollInternal: 1000,
        changeFlushInternal: 1000
      });
    
    var tools = $('<div>').illusionMapTools()
      .appendTo(map);
    
    $('<div>')
      .appendTo(tools)
      .illusionMapToolBrush();
       
    $('<div>')
      .appendTo(tools)
      .illusionMapToolArea();
       
    $('<div>')
      .appendTo(tools)
      .illusionMapToolPath();
       
    $('<div>')
      .appendTo(tools)
      .illusionMapToolPaints({
        fill: {
          type: 'color',
          value: '#fff'
        },
        stroke: {
          type: 'color',
          value: '#000'
        },
        patterns: function(response) {
          response([{
            name: 'Water 1',
            url: CONTEXTPATH + '/illusion/mapImage?url=http://ubuntuone.com/5H1ofuBc7XVIPWMzYjZntO',
          }, {
            name: 'Grass 1',
            url: CONTEXTPATH + '/illusion/mapImage?url=http://ubuntuone.com/0q7Bor5Ttk1ZALAhweBTF1'
          }]);
        }
      });
  });

}).call(this);