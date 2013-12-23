$(function() {
  'use strict';

  $.widget("custom.tosstable", {
    options : {},
    _create : function() {
      this.element
        .addClass('tosstable')
        .droppable({
          scope: 'tosstable'
        })
        .on('drop', $.proxy(this._onDrop, this))
        .disableSelection();
    },
    _onDrop: function (event, ui) {
      ui.draggable.tossable("tabled", event.target);
    },
    _destroy : function() {
    }
  });
  
  $.widget("custom.tossable", {
    options : {
      width: 64,
      height: 64,
      tableWidth: 96,
      tableHeight: 96
    },
    
    _create : function() {
      this._tabled = false;
      this._table = null;
      
      this.element
        .addClass('tossable')
        .css({
          width: this.options.width + 'px',
          height: this.options.height + 'px',
          position: 'relative'
        })
        .draggable({
          scope: 'tosstable'
        })
        .on("dragstart", $.proxy(this._dragStart, this))
        .on("dragstop", $.proxy(this._dragStop, this))
        .on("mousedown", $.proxy(function (event, data) {
          if (!$(this.element).data('originals')) {
            $(this.element).data('originals', {
              parent: $(this.element).parent(),
              position: $(this.element).css('position'),
              display: $(this.element).css('display'),
              margin: {
                left: $(this.element).css('marginLeft'),
                top: $(this.element).css('marginTop'),
                right: $(this.element).css('marginRight'),
                bottom: $(this.element).css('marginBottom')
              }
            });
          }
          
          var offset = $(this.element).offset();

          $(this.element).css({
            margin: 0,
            position: 'absolute',
            top: offset.top + 'px',
            left: offset.left + 'px'
          }).appendTo($(document.body));
          
          this._enlarge();
        }, this))
        .on("mouseup", $.proxy(function (event, data) {
          this._shrink();
        }, this))
        .on("tossabletossed", $.proxy(function (event, data) {
          this._revert();
        }, this))
        .on("tossableremovedfromtable", $.proxy(function (event, data) {
          this._revert();
        }, this))
        
        // changedtable
        
        .disableSelection();
    },
    _dragStart: function (event, ui) {
      this._oldTable = this._table;
      this._table = null;
      
      this._trigger("picked", event, {
        oldTable: this._oldTable,
        table: this._table
      });
    },
    _dragStop: function (event, ui) {
      if (this._table != null) {
        if (this._table === this._oldTable) {
          // Moved within table
          this._trigger("movedontable", event, {
            table: this._table
          });
        } else {
          if (this._oldTable === null) {
            // Tabled
            this._trigger("tabled", event, {
              table: this._table
            });
          } else {
            // Moved to another table
            this._trigger("changedtable", event, {
              oldTable: this._oldTable,
              table: this._table
            });
          }
          
          var tableOffset = $(this._table).offset();
          var itemOffset = $(this.element).offset();
          
          $(this._table).append(
            $(this.element).css({
              top: (itemOffset.top - tableOffset.top) + 'px',
              left: (itemOffset.left - tableOffset.left) + 'px'
            })
          );
        }
      } else {
        if (this._oldTable === null) {
          // Just tossed
          this._trigger("tossed", event, {});
        } else {
          // Removed from table
          this._trigger("removedfromtable", event, {
            oldTable: this._oldTable
          });
        }
      }
    },
    table: function () {
      return this._table;
    },
    tabled: function (table) {
      this._table = table;
    },
    _enlarge: function (callback) {
      $(this.element)
        .stop(true, false)
        .animate({
          width: this.options.tableWidth + 'px', 
          height: this.options.tableHeight + 'px'
        }, {
          easing: 'easeOutBounce',
          duration: 1000,
          complete: function () {
            if (callback)
              callback();
          }
        })
        .css('overflow', 'visible');
    },
    _shrink: function (callback) {
      $(this.element)
        .stop(true, false)
        .animate({
          width: this.options.width + 'px', 
          height: this.options.height + 'px'
        }, {
          easing: 'easeOutBounce',
          duration: 1000,
          complete: $.proxy(function () {
            if (callback)
              callback();
          }, this)
        })
        .css('overflow', 'visible');
    },
    
    _revert: function (callback) {
      var duration = 1200;
      var easing = 'easeOutBounce';
      
      var originals = $(this.element).data('originals');
      var parent = originals.parent;
      var placeHolder = $('<div>')
        .css({
          position: originals.position,
          display: originals.display,
          width: 0,
          height: 0,
          marginLeft: originals.margin.left,
          marginRight: originals.margin.right,
          marginTop: originals.margin.top,
          marginBottom: originals.margin.bottom,
          paddingLeft: originals.margin.left
        })
        .appendTo(parent);
      var offset = placeHolder.offset();
      //placeHolder.remove();
      
      placeHolder.animate({
        width: this.options.width + 'px', 
        height: this.options.height + 'px'
      }, {
        easing: easing,
        duration: duration
      });
      
      $(this.element)
        .stop(true, false)
        .animate({
          width: this.options.width + 'px', 
          height: this.options.height + 'px',
          top: offset.top + 'px',
          left: offset.left + 'px'
        }, {
          easing: easing,
          duration: duration,
          complete: $.proxy(function () {
            placeHolder.remove();
            $(this.element)
              .appendTo(parent)
              .css({
                top: 0,
                left: 0,
                position: 'relative',
                marginLeft: originals.margin.left,
                marginRight: originals.margin.right,
                marginTop: originals.margin.top,
                marginBottom: originals.margin.bottom
              });
            
            if (callback)
              callback();
          }, this)
        })
        .css('overflow', 'visible');
    },
    _destroy : function() {
    }
  });
});