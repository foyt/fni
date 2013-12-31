(function() {
  'use strict';
  
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
    
    styleContext: function (context) {
      this.element.closest('.map').find('.map-tool-paints').illusionMapToolPaints("setForegroundStyle", context);
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
        $(this.element).illusionMapButtonTool("styleContext", screenCtx);
        
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
        $(this.element).illusionMapButtonTool("styleContext", offscreenCtx);
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
      $(this.element).illusionMapButtonTool("styleContext", context);
      
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

  $.widget("custom.illusionMapToolPaint", {
    options : {},
    _create : function() {
      this.element
        .click($.proxy(this._onClick, this));
      
      this._setPaint(this.options.type, this.options.value);
    },
    _onClick: function (event) {
      this._dialog = $('<div>');
      var tabsElement = $('<div>');

      var prefix = new Date().getTime() + '-';
      var colorTabId = prefix + 'color';
      var patternTabId = prefix + 'pattern';

      this._tabLabels = $('<ul>');
      this._tabs = $('<div>');

      tabsElement.append(this._tabLabels);
      tabsElement.append(this._tabs);
      this._dialog.append(tabsElement);
      
      $('<li>')
        .append(
          $('<a>')
            .text('Color')
            .attr('href', '#' + colorTabId))
        .appendTo(this._tabLabels);
      
      $('<li>')
        .append(
          $('<a>')
            .text('Pattern')
            .attr('href', '#' + patternTabId))
        .appendTo(this._tabLabels);

      var colorTabContent = $('<div>')
        .attr('id', colorTabId)
        .appendTo(tabsElement);
      
      var patternTabContent = $('<div>')
        .attr('id', patternTabId)
        .appendTo(tabsElement);
      
      var patterns = $('<div>')
        .addClass('map-paint-fav-patterns');
      
      this._addPattern(patterns, CONTEXTPATH + '/illusion/mapImage?url=http://ubuntuone.com/5H1ofuBc7XVIPWMzYjZntO');
      this._addPattern(patterns, CONTEXTPATH + '/illusion/mapImage?url=http://ubuntuone.com/0q7Bor5Ttk1ZALAhweBTF1');
      patternTabContent.append(patterns);
      
      tabsElement.tabs();
      this._dialog.dialog();

      var colorInput = $('<input>').appendTo(colorTabContent);
      var colorPreview = $('<div>')
        .addClass('map-paint-color-preview')
        .append('<span>')
        .appendTo(colorTabContent);
      
      colorInput.spectrum({
        flat: true,
        showInput: false,
        showButtons: false,
        showAlpha: true,
        clickoutFiresChange: true,
        move: function(color) {
          colorPreview.find('span').css("backgroundColor", color.toRgbString());
        }
      });
    },
    setStyle: function (context) {
      switch (this._paintType) {
        case 'color':
          context.fillStyle = this._paintValue;
        break;
        case 'pattern':
          context.fillStyle = this._paintPattern;
        break;
      }
    },
    
    _setPaint: function (type, value) {
      switch (type) {
        case 'color':
          this.element.css({
            'backgroundColor': value, 
            'backgroundImage': 'none'
          });

          this._paintType = type;
          this._paintValue = value;
        break;
        case 'pattern':
          var image = new Image();
          image.onload = $.proxy(function () {
            var canvas = this.element.closest('.map').find('.map-canvas');
            this._paintPattern = canvas.illusionMapCanvas("offscreenCtx")
              .createPattern(image, "repeat");
            
            this.element.css({
              'backgroundColor': null, 
              'backgroundImage': 'url(' + value + ')'
            });
            
            this._paintType = type;
            this._paintValue = value;
          }, this);          
          image.src = value;
        break;
      }
    },
    
    _addPattern: function (patterns, src) {
      var lastSlashIndex = src.lastIndexOf('/');
      var fileName = lastSlashIndex > -1 ? src.substring(lastSlashIndex + 1) : src;
      var _this = this;
      
      $('<div>')
        .addClass('map-paint-fav-pattern')
        .data('src', src)
        .append(
            $('<img>')
              .attr('src', src)
        )
        .append(
          $('<label>').text(fileName)
        )
        .appendTo(patterns)
        .click(function (e) {
          _this._setPaint("pattern", $(this).data('src'));
          _this._dialog.dialog("close");
        });
    },
    _destroy : function() {
    }
  });
  
  $.widget("custom.illusionMapToolPaints", {
    options : {},
    _create : function() {
      this._foregroundPaint = $('<div>')
        .addClass('map-foreground-paint')
        .illusionMapToolPaint({
          type: this.options.foreground.type,
          value: this.options.foreground.value
        });
      
      this._backgroundPaint = $('<div>')
        .addClass('map-background-paint')
        .illusionMapToolPaint({
          type: this.options.background.type,
          value: this.options.background.value
        });
    
      this.element
        .addClass('map-tool map-tool-paints')
        .append(this._foregroundPaint)
        .append(this._backgroundPaint);  
    },
    
    setForegroundStyle: function (context) {
      this._foregroundPaint.illusionMapToolPaint("setStyle", context);
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
      changePollInternal: 200
    },
    _create : function() {
      this._pollingChanges = false;
      
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
        var currentData = this.element.illusionMapCanvas("offscreenData");
        
        var diff = this._diffImageData(this._currentData, currentData);
        if (!diff.matches) {
          this._currentData = currentData;
          
          this.element.trigger(jQuery.Event("coops.changed"), {
            changes: diff.changes
          });
        }
          
        this._changePollTimeoutId = setTimeout($.proxy(this._pollChanges, this), this.options.changePollInternal); 
      }
    },
    
    _startChangePolling: function () {
      if (this._pollingChanges == false) {
        this._pollingChanges = true;
        this._pollChanges();
      }
    },
    
    _stopChangePolling: function () {
      if (this._pollingChanges == true) {
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
          
          changes.push({
            x: coordinate[0],
            y: coordinate[1],
            v: data2[i]
          });
          
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
      $(this.element).illusionMapCanvas("loadImage", imageUrl);
      this._sessionId = data.sessionId;
      this._revisionNumber = data.revisionNumber;
      this.element.trigger("coops.join", data);
    },
    
    _onCoOpsJoin: function (event, data) {
      this.element
        .on("coops.changed", $.proxy(this._onCoOpsChanged, this));
      
      this._pollUpdates();
      this._currentData = this.element.illusionMapCanvas("offscreenData");
      this._startChangePolling();
    },
    
    _onCoOpsChanged: function (event, data) {
      this._stopChangePolling();

      $.ajax(this.options.serverUrl, {
        type: 'PATCH',
        data: JSON.stringify({
          'patch': JSON.stringify(data.changes),
          'revisionNumber': this._revisionNumber, 
          'sessionId': this._sessionId 
        }),
        done: $.proxy(function (data, textStatus, jqXHR) {
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
        }, this)
      });
    },
    _pollUpdates : function() {
      $.ajax(this.options.serverUrl + '/update', {
        data: {
          'revisionNumber': this._revisionNumber
        }
      })
      .done($.proxy(function (data, textStatus, jqXHR) {
        var status = jqXHR.status;
        switch (status) {
          case 200:
            this._applyPatches(data);
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
        }, this), 500);
      }, this));
      
    },
    
    _applyPatches: function (patches) {
      var patch = patches.splice(0, 1)[0];
      this._applyPatch(patch, $.proxy(function () {
        if (patches.length > 0) {
          this._applyPatches(patches);
        }
      }, this));
    },
    
    _applyPatch: function (patch, callback) {
      if (this._sessionId != patch.sessionId) {
        this.element.illusionMapCanvas('drawOffscreen', $.proxy(function (offscreenCtx) {
          var changes = JSON.parse(patch.patch);
          for (var i = 0, l = changes.length; i < l; i++) {
            var change = changes[i];
            var rgba = IntToRGBA(change.v);
            
            offscreenCtx.fillStyle = 'rgba(' + rgba.join(',') + ')';
            offscreenCtx.fillRect(change.x, change.y, 1, 1);
          }
        }, this));

        this._revisionNumber = patch.revisionNumber;
      } else {
        // Our patch was accepted, yay!
        this._revisionNumber = patch.revisionNumber;
        this._startChangePolling();
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
    
    loadImage: function (url) {
      var imageObj = new Image();
      imageObj.onload = $.proxy(function() {
        this.drawOffscreen(function (context) {
          context.drawImage(imageObj, 0, 0);
        });
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
        serverUrl: CONTEXTPATH + '/forge/coops/' + '3003'
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
        foreground: {
          type: 'color',
          value: '#fff'
        },
        background: {
          type: 'color',
          value: '#000'
        }
      });
  });
  
}).call(this);