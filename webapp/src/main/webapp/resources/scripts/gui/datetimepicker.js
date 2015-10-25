(function() {
  'use strict';
  
  $.widget("custom.dateTimeField", {
    options: {
      
    },
    _create : function() {
      this._date = this.element.find('input.datepicker')
        .attr('type', 'text')
        .datepicker()
        .on('change blur keyup', $.proxy(this._onDatePickerChange, this));
      
      this._time = this.element.find('input.timepicker')
        .attr('type', 'text')
        .timepicker({
          timeFormat: 'G:i'
        })
        .on('change blur', $.proxy(this._onTimePickerChange, this));
      
      var value = this.element.find('input[type="hidden"]').val();
      if (value) {
        var date = new Date(Date.parse(value));
        this._date.datepicker('setDate', date);
        this._time.timepicker('setTime', date);
      } else {
        this._updateValue();
      }
    },
    
    _onDatePickerChange: function () {
      this._updateValue();
    },
    
    _onTimePickerChange: function () {
      this._updateValue();
    },
    
    dateTime: function () {
      var date = this._date.datepicker('getDate');
      if (!date) {
        return null;
      }
      
      var time = this._time.timepicker('getTime', date);
      if (time) {
        return time;
      }
      
      return date;
    },
    iso8601: function () {
      var dateTime = this.dateTime();
      return dateTime != null ? dateTime.toISOString() : null;
    },
    _updateValue: function () {
      this.element.find('input[type="hidden"]').val(this.iso8601());
    }
  });

}).call(this);