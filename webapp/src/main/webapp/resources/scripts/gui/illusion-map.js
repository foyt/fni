(function() {
  'use strict';
  
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
    
    _getCanvas: function () {
      return this._canvas;
    },
    
    _getRadius: function () {
      // TODO: Move to tool options
      return 10;
    },
    
    _setStyle: function (context) {
      this.element.closest('.map').find('.map-tool-paints').illusionMapToolPaints("setForegroundStyle", context);
    },
    
    _onActivate: function (event) {
      this._getCanvas().on("canvas.mousemove", this._canvasMouseMoveListener);
      this._getCanvas().on("canvas.mousedrag", this._canvasMouseDragListener);
      this._getCanvas().on("canvas.afterredraw", this._canvasAfterRedrawListener);
    },
    
    _onDeactivate: function (event) {
      this._getCanvas().off("canvas.mousemove", this._canvasMouseMoveListener);
      this._getCanvas().off("canvas.mousedrag", this._canvasMouseDragListener);
      this._getCanvas().off("canvas.afterredraw", this._canvasAfterRedrawListener);
    },
    
    _onCanvasAfterRedraw: function (event) {
      this._getCanvas().illusionMapCanvas('drawScreen', $.proxy(function (screenCtx) {
        this._setStyle(screenCtx);
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
      this._getCanvas().illusionMapCanvas('drawOffscreen', $.proxy(function (offscreenCtx) {
        this._setStyle(offscreenCtx);
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
      this.element
        .illusionMapButtonTool()
        .addClass('map-tool-area');
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
      
      this._addPattern(patterns, 'http://ubuntuone.com/5H1ofuBc7XVIPWMzYjZntO');
      this._addPattern(patterns, 'http://ubuntuone.com/0q7Bor5Ttk1ZALAhweBTF1');
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
  
  $.widget("custom.illusionMapCanvas", {
    options : {
      redrawInternal: 5,
      changePollInternal: 200
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
      this._currentData = this._getOffscreenData();
      
      this._scheduleRedraw();
      this._scheduleChangePolling();
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
    
    _scheduleRedraw: function () {
      this.element.trigger(jQuery.Event("canvas.beforeredraw"));

      if (this.offscreenDirty() || this.screenDirty()) {
        /**
        if (this.offscreenDirty()) {
          this._currentData = this._getOffscreenData();
        } 
        **/
        this.flipToScreen();
        this.screenDirty(false);
        this.offscreenDirty(false);
      }
      
      this.element.trigger(jQuery.Event("canvas.afterredraw"));

      this._redrawTimeoutId = setTimeout($.proxy(this._scheduleRedraw, this), this.options.redrawInternal); 
    },
    
    _scheduleChangePolling: function () {
      var currentData = this._getOffscreenData();

      var diff = this._diffImageData(this._currentData, currentData);
      if (!diff.matches) {
        this._currentData = currentData;
        
        this.element.trigger(jQuery.Event("canvas.changed"), {
          changes: diff.changes
        });
      }

      this._redrawTimeoutId = setTimeout($.proxy(this._scheduleChangePolling, this), this.options.changePollInternal); 
    },
    
    _getOffscreenData: function () {
      return this.offscreenCtx().getImageData(0, 0, this.options.width, this.options.height);
    },
    
    _diffImageData: function (data1, data2) {
      var changes = new Object();
      var matches = true;
      
      for (var i = 0; i < data1.data.length; i+= 4) {
        if (data1.data[i] !== data2.data[i]) {
          changes[i] = data2.data[i];
          matches = false;
        }
      }
      
      return {
        changes: changes,
        matches: matches
      };
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
  
/**  
  $.widget("custom.illusionMap", {
    options : {},
    _create : function() {
      
      
      this.element
        
        .on("illusionmapmousedragstart", $.proxy(this._onMouseDragStart, this))
        .on("illusionmapmousedragend", $.proxy(this._onMouseDragEnd, this))
        .on("illusionmapmousedrag", $.proxy(this._onMouseDrag, this));
    },
    _onMouseDragStart: function (e) {
      this._dragPath = new Array();
      
      console.log("Dragstart");
    },
    _onMouseDrag: function (e) {
      // var ctx = this.element.context.getContext('2d');
      // ctx.clearRect(0, 0, 800, 400);

      var offset = this.element.offset();
      
      this._dragPath.push({
        x: e.pageX - offset.left,
        y: e.pageY - offset.top
      });
      this._dragPath = simplify(this._dragPath, 1, true);
      
      this._plotPath(this._dragPath, "#f00");
    },
    _onMouseDragEnd: function (e) {
      console.log("path " + this._dragPath.length);
      console.log("Dragend");
    },
    _destroy : function() {
    },
    _plotPath: function (path, color) {
      var ctx = this.element.context.getContext('2d');
      
      ctx.strokeStyle = color;
      ctx.lineWidth = 2;
      ctx.lineCap = 'round';
      ctx.lineJoin = 'round';
      
      ctx.fillStyle = ctx.createPattern(this.options.patternImage, "repeat");
      ctx.beginPath();

      if (path.length > 0) {
        for (var i = 0, l = path.length; i < l; i++) {
          var p = path[i];
          if (p) {
            ctx.lineTo(p.x, p.y);
          }
        }
        
        ctx.lineTo(path[0].x, path[0].y);
      }

      // ctx.stroke();
      ctx.fill();
      ctx.closePath();
    }
  });
  **/
  $(document).ready(function() {
//    var image = new Image();
//    image.src = CONTEXTPATH + '/test/forest03.png';
//    image.onload = function (e) {

//    $('.select').autocomplete({
//      source: function( request, response ) {
//        var term = this.term.toLowerCase();
//        
//        $.getJSON(CONTEXTPATH + "/test/files.json", {
//        }, function (data, status, xhr) {
//          var images = data['images'];
//          
//          for (var i = images.length - 1; i >= 0; i--) {
//            if (images[i].toLowerCase().indexOf(term) == -1) {
//              images.splice(i, 1);
//            }
//          }
//          
//          response( $.map( images, function( image ) {
//            return {
//              label: image,
//              value: 'http://dev.forgeandillusion.net:8080/fni/test/Tiles/' + image
//            };
//          }));
//        });
//      },
//      select: function( event, ui ) {
//        var image = $('<img>')
//         .attr('src', ui.item.value)
//         .css({
//           'maxWidth': '32px'
//         });
//        
//        $('.images').append(image);
//        
//        /**
//        var users = $(dialog).find('.forge-share-material-users');
//        var userId = ui.item.value;
//        if (users.find('input[value="' + userId + '"]').length == 0) {
//          users.append(
//            $('<div class="forge-share-material-user">')
//              .append($('<input name="user" type="hidden">').val(userId))
//              .append($('<label>').text(ui.item.label))
//              .append(createRoleSelect())); 
//        }
//        
//        **/
//      }
//    });
//
//    $('#map').illusionMap({
//      
//    });
    
    var map = $('<div>')
      .appendTo($('.map-container'))
      .illusionMap();
    
    $('<canvas>')
      .appendTo(map)
      .illusionMapCanvas({
        width: 800,
        height: 800
      })
      .on("canvas.changed", function (event, data) {
        console.log(["changed", data, data.changes]);
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