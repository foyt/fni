(function() {
  'use strict';

  $.widget("custom.illusionMapToolPaint", {
    options : {},
    _create : function() {
      this.element
        .click($.proxy(this._onClick, this));
    },
    _onClick: function (event) {
      var dialogElement = $('<div>');
      var tabsElement = $('<div>');

      var prefix = new Date().getTime() + '-';
      var colorTabId = prefix + 'color';
      var patternTabId = prefix + 'pattern';

      this._tabLabels = $('<ul>');
      this._tabs = $('<div>');

      tabsElement.append(this._tabLabels);
      tabsElement.append(this._tabs);
      dialogElement.append(tabsElement);
      
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
      
      this._addPattern(patterns, 'http://dev.forgeandillusion.net:8080/fni/test/Tiles/Wood/woodchip_medic-e.png');
      this._addPattern(patterns, 'http://dev.forgeandillusion.net:8080/fni/test/Tiles/Cave/00473-Cave_Floor_1.png');
      patternTabContent.append(patterns);
      
      tabsElement.tabs();
      dialogElement.dialog();

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
    _setPaint: function () {
      
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
        });
    },
    _destroy : function() {
    }
  });
  
  $.widget("custom.illusionMapTools", {
    options : {},
    _create : function() {
      this._foregroundPaint = this.element.find('.map-foreground-paint')
        .illusionMapToolPaint();
      
      this._backgroundPaint = this.element.find('.map-background-paint')
        .illusionMapToolPaint();
    },
    _destroy : function() {
    }
  });
  
  $.widget("custom.illusionMap", {
    options : {},
    _create : function() {
      this._mouseDown = false;
      
      this.element
        .on("mousedown", $.proxy(this._onMouseDown, this))
        .on("mouseup", $.proxy(this._onMouseUp, this))
        .on("mousemove", $.proxy(this._onMouseMove, this))
        .on("illusionmapmousedragstart", $.proxy(this._onMouseDragStart, this))
        .on("illusionmapmousedragend", $.proxy(this._onMouseDragEnd, this))
        .on("illusionmapmousedrag", $.proxy(this._onMouseDrag, this));
    },
    _onMouseDown: function (e) {
      if (!this._mouseDown) {
        this._trigger("mousedragstart", e);
      }
      
      this._mouseDown = true;
    },
    _onMouseUp: function (e) {
      if (this._mouseDown) {
        this._trigger("mousedragend", e);
      }
      
      this._mouseDown = false;
    },
    _onMouseMove: function (e) {
      if (this._mouseDown) {
        this._trigger("mousedrag", e);
      }
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
    
    $('.map-tools').illusionMapTools({
      paint: {
        foreground: {
          type: 'color',
          value: '#fff'
        },
        background: {
          type: 'color',
          value: '#000'
        }
      }
    });
  });
  
}).call(this);