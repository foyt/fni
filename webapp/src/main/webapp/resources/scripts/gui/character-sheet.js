(function() {
  'use strict';
  
  function getQueryParameters() {
    var result = {};
    
    var location = window.document.location;
    var search = location.search;
    if (search) {
    var params = search.substring(1).split('&');
      for (var i = 0, l = params.length; i < l; i++) {
        var param = params[i].split('=', 2);
        result[param[0]] = param[1];
      }
    }
    
    return result;
  }

  $.widget("custom.illusionField", {
    options: {
    
    },
    _create : function() {
      this.element.addClass('i-field initialized');
    },
    
    readOnly: function () {
      return $(this.element).attr('readOnly') == 'readOnly';
    },
    
    val: function (value) {
      if (value !== undefined) {
        $(this.element).val(value);
        $(this.element).trigger('change');
      } else {
        return $(this.element).val();
      }
    }
  });

  $.widget("custom.illusionSumField", {
    _create : function() {
      this._fields = $(this.element).attr('data-fields').split(',');
      
      this.element.addClass('i-field-sum initialized');
      
      if (this._fields.length > 0) {
        $(this.element).attr('readOnly', 'readOnly');

        $.each(this._fields, $.proxy(function (index, field) {
          $(field).change($.proxy(function (event) {
            this.updateSum();
          }, this));
        }, this));

        this.updateSum();
      }
    },
    
    updateSum: function () {
      var sum = 0;
      $.each(this._fields, $.proxy(function (index, field) {
        switch ($(field).attr('type')) {
          case 'number':
            sum += parseInt($(field).val());
          break;
          default:
            throw new Error("Can not sum of field typed: " + $(field).attr('type') + " (" + $(field).attr('name') + ')');
          break;
        }
      }, this));
      
      $(this.element).val(sum);
    }
  });
  
  $.widget("custom.illusionRoll", {
    _create : function() {
      this.element
        .click($.proxy(this._onClick, this))
        .addClass('initialized');
    },

    fields: function() {
      var result = [];
      
      var fields = $(this.element).attr('data-fields');
      if (fields) {
        $.each(fields.split(','), function (index, field) {
          result.push(field);
        });
      }

      return result; 
    },

    fieldValues: function () {
      return $.map(this.fields(), function (field) {
        return parseInt($(field).val()||'0');
      });      
    },

    parseRoll: function (vars) {
      var roll = $(this.element).attr('data-roll');
      if (vars) {
        for (var i = 0, l = vars.length; i < l; i++) {
          roll = roll.replace(new RegExp('\\{' + i + '\\}', 'g'), vars[i]);
        }
      }
      return roll;
    },

    roll: function (vars) {
      var roll = this.parseRoll(vars||this.fieldValues());
      return [this.evalRoll(roll), roll];
    },

    evalRoll: function (roll) {
      var evil = eval;
      return evil('Math.round(' + roll
        .replace(/([0-9]{1,})([\*]{0,1})(d)([0-9]{1,})/g, "($1*(1 + (Math.random()*($4 - 1))))")
        .replace(/(d)([0-9]{1,})/g, "(1 + (Math.random()*($2 - 1)))") + ')');
    },
    
    _onClick: function () {
      var roll = this.roll();
      $(this.element).trigger("roll", {
        result: roll[0],
        roll: roll[1],
        label: $(this.element).attr('title')
      });
    }
  });
  
  $.widget("custom.illusionCharacterSheet", {
    options: {
      preview: false
    },
    _create : function() {
      this._receiving = false;
      this.initFields();
      
      if (!this.options.preview) {
        this._webSocketOpen = false;
        this._webSocketLastPong = null;
        var socketUrl = (window.location.protocol == 'https:' ? 'wss:' : 'ws:') + '//' + window.location.host + this.options.contextPath + '/ws/' + this.options.eventId + '/characterSheet/' + this.options.materialId + '/' + this.options.participantId + '/' + this.options.key;
        this._webSocket = this._openWebSocket(socketUrl);
        this._webSocket.onmessage = $.proxy(this._onWebSocketMessage, this);
        this._webSocket.onopen = $.proxy(this._onWebSocketOpen, this);
        $(window).on('beforeunload', $.proxy(this._onWindowBeforeUnload, this));
        this._keepalivePing();
      }
      
      $(this.element).on('roll', '.i-roll', $.proxy(this._onRoll, this));
    },
    
    initFields: function (fields) {
      if (fields === undefined) {
        fields = this.element.find('.i-field,.i-field-sum,.i-progressbar,.i-roll').filter(':not(.initialized)');
      }
      
      $(fields)
        .filter('.i-field')
        .illusionField()
        .on('change', $.proxy(this._onIllusionFieldChange, this)); 
      
      $(fields)
        .filter('.i-field-sum')
        .illusionSumField();

      // Initialize progress bars
      
      $(fields)
        .filter('.i-progressbar')
        .each(function (index, field) {
          var targetField = $($(field).attr('data-field'));
          
          $(field)
            .addClass('initialized')
            .progressbar({
              value: parseInt(targetField.val())
            });
  
          targetField.change($.proxy(function (event) {
            $(this).progressbar("value", parseInt($(event.target).val()));
          }, field));
        });
      
      // Initialize rolls
      
      $(fields)
        .filter('.i-roll')
        .illusionRoll();
    },
    
    preview: function () {
      return this.options.preview;
    },
    
    load: function (sheetData) {
      this._receiving = true;
      try {
        for (var key in sheetData) {
          var value = sheetData[key];
          $('*[name="' + key + '"]')
            .val(value)
            .trigger('change');
        }
  
        if ($(this.element).find('.i-data-link').length > 0) {
          this._updateDataLinks(sheetData);
        }
        
        $('.i-field-sum.initialized').illusionSumField('updateSum');
      } finally {
        this._receiving = false;
      }
    },
    
    set: function (key, value) {
      this._receiving = true;
      try {
        $('*[name="' + key + '"]').val(value).trigger("change");
        $('.i-field-sum.initialized').illusionSumField('updateSum');
      } finally {
        this._receiving = false;
      }
    },
    
    _keepalivePing: function () {
      var _this = this;
      
      setTimeout(function() {
        $.get(_this.options.contextPath + '/keepalive')
          .always(function() { 
            _this._keepalivePing(); 
          });
      }, 60 * 1000);
    },
    
    _sendUpdate: function (key, value) {
      if (!this._receiving && !this.options.preview) {
        this._webSocket.send(JSON.stringify({
          type: 'update',
          data: { 
            key: key, 
            value: value 
          }
        }));
      }
      
      if ($(this.element).find('.i-data-link').length > 0) {
        var sheetData = {};
        $(this.element).find('.i-field.initialized').each(function (index, field) {
          try {
            if ($(field).illusionField('readOnly')) {
              sheetData[$(field).attr('name')] = $(field).val();
            }
          } catch (e) {
            $(field).addClass('error');
          }
        });
        
        this._updateDataLinks(sheetData);
      }
    },
    
    _sendRoll: function (label, roll, result) {
      if (!this.options.preview) {
        this._webSocket.send(JSON.stringify({
          type: 'roll',
          data: { 
            label: label, 
            roll: roll,
            result: result
          }
        }));
      }
    },
    
    _updateDataLinks: function (sheetData) {
      $(this.element).find('.i-data-link').attr('href', window.document.location.search + '&d=' + btoa(JSON.stringify(sheetData)));
    },
    
    _resetSocketKeepalive: function () {
      this._webSocketLastPong = new Date().getTime();
    },
    
    _sendSocketPing: function () {
      this._webSocket.send(JSON.stringify({
        type: 'ping'
      }));
    },
    
    _startSocketPing: function () {
      this._webSocketLastPong = new Date().getTime();
      var _this = this;
      
      setInterval(function () {
        if (_this._webSocketOpen) { 
          var sincePong = new Date().getTime() - _this._webSocketLastPong;
          if (sincePong > 15000) {
            _this._closeSocket()
            window.location.reload(true);
          }
  
          _this._sendSocketPing();
        }
      }, 1000);
    },
    
    _openWebSocket: function (url) {
      if ((typeof window.WebSocket) !== 'undefined') {
        return new WebSocket(url);
      } else if ((typeof window.MozWebSocket) !== 'undefined') {
        return new MozWebSocket(url);
      }
      
      return null;
    },
    
    _closeSocket: function () {
      this._webSocketOpen = false;
      this._webSocket.onclose = function () {};
      this._webSocket.close();
    },
    
    _onWebSocketMessage: function (event) {
      var data = event.data;
      
      var message = $.parseJSON(data);
      var messageData = $.parseJSON(message.data);
      switch (message.type) {
        case 'load':
          this.load(messageData.values);
        break;
        case 'update':
          this.set(messageData.key, messageData.value);
        break;
        case 'pong':
          this._resetSocketKeepalive();
        break;
      }
    },
    
    _onWebSocketOpen: function (event) {
      this._webSocket.onclose = $.proxy(this._onWebSocketClose, this);    
      this._webSocket.onerror = $.proxy(this._onWebSocketError, this);    
      this._startSocketPing();
      this._webSocketOpen = true;
    },
    
    _onWebSocketClose: function (event) {
      this._webSocketOpen = false;
      
      if (event.code == 1000) {
        window.location.reload(true);
      } else {
        alert('Communication error with server, please try again later');
      }
    },
    
    _onWebSocketError: function (event) {
      alert('Communication error with server, please try again later');
    },
    
    _onIllusionFieldChange: function (event, data) {
      this._sendUpdate($(event.target).attr('name'), $(event.target).val());
    },
    
    _onWindowBeforeUnload: function (event) {
      this._closeSocket();
    },
    
    _onRoll: function (event, data) {
      this._sendRoll(data.label, data.roll, data.result);
    }
  });
  
  $(document).ready(function () {
    var params = getQueryParameters();

    $(document.body).illusionCharacterSheet({
      preview: (typeof PREVIEW != 'undefined') ? PREVIEW : false,
      contextPath: params.contextPath,
      participantId: params.participantId,
      eventId: params.eventId,
      materialId: params.materialId,
      key: params.key
    });
    
    if (params['d']) {
      if (window.atob) {
        var data = atob(params['d']);
        if (data) {
          $(document.body).illusionCharacterSheet('data', $.parseJSON(data));
        }
      }
    }
  }); 
  
}).call(this);