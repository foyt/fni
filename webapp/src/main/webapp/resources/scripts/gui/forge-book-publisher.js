(function() {
  'use strict';

  var LengthUnitConverter = function(length) {
    this.init(length);
  }
  
  $.extend(LengthUnitConverter.prototype, {
    
    init: function (length) {
      var match = /([0-9.]{1,})(.*)/.exec(length);
      if (match && match.length == 3) {
        var value = parseFloat(match[1]);
        var unit = match[2];
        var dpi = 96;
        
        switch (unit) {
          case 'pt':
            this.value = value / 0.75;
          break;
          case 'px':
            this.value = value;
          break;
          case 'mm':
            this.value = (value / 25.4) * dpi;
          break;
          case 'cm':
            this.value = (value / 2.54) * dpi;
          break;
          case 'in':
            this.value = value * dpi;
          break;
        }
      }
      
    },
    
    add: function (value) {
      if (value) {
        var px = (new LengthUnitConverter(value)).toPx();
        this.value += px||0;
      }
      
      return this;
    },
    
    multiply: function (value) {
      this.value *= value;
      return this;
    },
    
    to: function (unit, includeUnit) {
      switch (unit) {
        case 'px':
          return this.toPx(includeUnit);
        case 'pt':
          return this.toPt(includeUnit);
      }
      
      throw new Error("Unknown unit " + unit);
    },
    
    toPx: function (includeUnit) {
      if (includeUnit === true) {
        return this.toPx(false) + 'px'
      } else {
        return this.value;
      }
    },
    
    toPt: function (includeUnit) {
      if (includeUnit === true) {
        return this.toPt(false) + 'pt'
      } else {
        return this.value * 0.75;
      }
    }
    
  });
  
  $.widget("custom.bookPublisher", {
    options: {
      fonts: [{
        "name": "Tinos",
        "url": "http://fonts.googleapis.com/css?family=Tinos:400,400italic,700,700italic"
       }, {
       "name": "Open Sans",
       "url": "http://fonts.googleapis.com/css?family=Open+Sans:400,300,300italic,600,800italic,800,700,700italic,600italic"
      }, {
        "name": "PT Serif",
        "url": "http://fonts.googleapis.com/css?family=PT+Serif:400,400italic,700,700italic"
      }],
      
      styles: [{
        "name":"Leipäteksti",
        "selector":"p",
        "rules":{
          'font-family': 'Tinos',
          'margin-top': '17.9167px',
          'margin-bottom': '17.9167px'  
        }
      }, {
        "name":"Otsikko 1",
        "selector":"h1",
        "rules":{
          'font-family': 'Tinos',
          'font-size': '32px',  
          'margin-top': '21.4333px',  
          'margin-bottom': '21.4333px',  
          'font-weight': 'bold'
        }
      }, {
        "name":"Otsikko 2",
        "selector":"h2",
        "rules":{
          'font-family': 'Tinos',
          'font-size': '24px',  
          'margin-top': '18px',  
          'margin-bottom': '18px',  
          'font-weight': 'bold'
        }
      }, {
        "name":"Otsikko 3",
        "selector":"h3",
        "rules":{
          'font-family': 'Tinos',
          'font-size': '18.7167px', 
          'margin-top': '15.5333px',
          'margin-bottom': '15.5333px',  
          'font-weight': 'bold'
        }
      }, {
        "name":"Otsikko 4",
        "selector":"h4",
        "rules":{
          'font-family': 'Tinos',
          'margin-top': '18px',
          'margin-bottom': '17.9167px',  
          'font-weight': 'bold'
        }
      }, {
        "name":"Otsikko 5",
        "selector":"h5",
        "rules":{
          'font-family': 'Tinos',
          'font-size': '13.2833px',  
          'margin-top': '24px',  
          'margin-bottom': '24px',  
          'font-weight': 'bold'
        }
      }, {
        "name":"Otsikko 6",
        "selector":"h6",
        "rules":{
          'font-family': 'Tinos',
          'font-size': '13px',
          'margin-top': '20.0333px',
          'margin-bottom': '20.0333px',  
          'font-weight': 'bold'
        }
      }, {
        "name":"Luettelo",
        "selector":"ul",
        "rules":{
          'font-family': 'Tinos',
          'margin-left':'40px',
          'list-style-type': 'disc'
        }
      }, {
        "name":"Numerolista",
        "selector":"ol",
        "rules":{  
          'font-family': 'Tinos',
          'margin-left':'40px',
          'list-style-type': 'decimal'
        }
      }],
      
      blockTags: ['p', 'h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'ul', 'ol', 'div', 'img'],
      defaultPageType: 'CONTENT',
    },
    
    _create : function() {
      this.fonts(this.options.fonts);
      this.styles(this.options.styles);
      
      this._documentClient = new $.RestClient(CONTEXTPATH + '/rest/material/');
      this._documentClient.add("documents");
      
      this._styleSheet = $('<style>').attr({ 'type': 'text/css' }).appendTo(document.head);
      
      this._createTools();
      
      $('<article>')
        .addClass('forge-book-publisher-pages')
        .appendTo(this.element);
      
      this.element.on("click", '.forge-book-publisher-page', $.proxy(this._onPageClick, this));
      this.element.on("stylesChanged", $.proxy(this._onStylesChanged, this));
      this.element.on("fontsChanged", $.proxy(this._onFontsChanged, this));
      this.element.on("blockSelect", $.proxy(this._onBlockSelect, this));
      this.element.on("pageSelect", $.proxy(this._onPageSelect, this));

      this.addPage();
    },
    
    data: function (val) {
      if (val) {
        this.element.find('.forge-book-publisher-pages').html(val);
        this._toolsWaypoint[0].context.refresh();
      } else {
        var cloned = $('<pre>').html( this.element.find('.forge-book-publisher-pages').html() );

        cloned.find('.forge-book-publisher-block-selected').removeClass('forge-book-publisher-block-selected');
        
        var result = cloned.html();
        
        return result;
      }
    },
    
    styles: function (val) {
      if (val) {
        this._styles = $.map(val, function (style) {
          var selectorParts = style.selector.split('.');
          
          if (selectorParts.length == 1) {
            // just the element 
            style._element = selectorParts[0];
          } else if (selectorParts.length == 2) {
            style._element = selectorParts[0]||'p';
            style._className = selectorParts[1];
          } else {
            $('.notifications').notifications('notification', 'error', "Could not recognize style selector " + style.selector);
          }
          
          return style;
        });
        
        $(this.element).trigger("stylesChanged", {
          styles: val
        });
      } else {
        return $.map(this._styles, function (style) {
          return {
            name: style.name || style.selector,
            selector: style.selector,
            rules: style.rules
          };
        });
      }
    },
    
    fonts: function (val) {
      if (val) {
        this._fonts = val;
        
        $(this.element).trigger("fontsChanged", {
          fonts: val
        });
      } else {
        return this._fonts;
      }
    },
    
    appendHtml: function (data) {
      $('<pre>')
        .html(data)
        .children()
        .each($.proxy(function (index, child) {
          $(child).css('page-break-after', '')
          this.appendBlock(child.outerHTML);
        }, this));
    },

    addPage: function () {
      return $('<section>')
        .attr('data-type', this.options.defaultPageType)
        .addClass('forge-book-publisher-page')
        .appendTo(this.element.find('.forge-book-publisher-pages'));
    },
    
    appendBlock: function (html) {
      var block = $(html);
      var lastPage = this.element 
        .find('section')
        .last();
      
      var pageHeight = lastPage.height();
      var blockHeight = this._measureBlockOuterHeight(block, true);
      var pageContentHeight = this._measurePageContentHeight(lastPage);
      
      if ((pageContentHeight + blockHeight) >= pageHeight) {
        lastPage = this.addPage();
      }
      
      lastPage.append(block);
    },
    
    _measurePageContentHeight: function (page) {
      var children = page.children();
      
      var result = 0;
      for (var i = 0, l = children.length; i < l; i++) {
        result += $(children[i]).outerHeight(true);
      }
      
      return result;
    },
    
    _measureBlockOuterHeight: function (block, includeMargin) {
      var tempPage = $('<section>')
        .addClass('forge-book-publisher-page')
        .css({
          'opacity': 0
        })
        .append(block)
        .appendTo(this.element);
      
      var height = block.outerHeight(includeMargin);
      var imgs = block.find('img');
      
      if (imgs.length) {
        for (var i = 0, l = imgs.length; i < l; i++) {
          var img = $(imgs[i]);
          var float = img.css('float');
          
          if ((float == 'left')||(float == 'right')) {
            height = Math.max(img.outerHeight(includeMargin), height);
          }
        }
      }
      
      tempPage.remove();
      
      return height;
    },
    
    _importMaterial: function (id) {
      this._documentClient.documents.read(id).done($.proxy(function (document, message, xhr){
        if (xhr.status !== 200) {
          $('.notifications').notifications('notification', 'error', message);
        } else {
          this.appendHtml(document.data);
        }
      }, this));
    },
    
    _changePageType: function (page, type) {
      $(page).attr('data-type', type);
    },
    
    _changePageElementStyle: function (element, elementName, className) {
      if (elementName != element.prop('tagName').toLowerCase()) {
        var newBlock = $('<' + elementName + '>');
        
        $.each(element[0].attributes, function (index, attribute) {
          newBlock.attr(attribute.name, attribute.value);
        }); 
        
        element.replaceWith(newBlock.html(element.html()));
        element = newBlock;
      }
      
      $.each(this._styles, function (index, style) {
        if (style._className) {
          element.removeClass(style._className);
        } 
      });
      
      if (className) {
        element.addClass(className);
      }
    
      this.element.trigger("blockStyleChange");
    },
    
    _changePageElementFloat: function (element, float) {
      element.css('float', float);
      this.element.trigger("blockFloatChange");
    },
    
    _movePageElement: function (element, direction) {
      switch (direction) {
        case 'up':
          var previousBlock = element.prev();
          if (previousBlock.length) {
            previousBlock.before(element);
            this.element.trigger("blockMove");
          } else {
            var previousPage = element.closest('.forge-book-publisher-page').prev();
            if (previousPage) {
              previousPage.append(element);
              this.element.trigger("blockMove");
            }
          }
        break;
        case 'down':
          var nextBlock = element.next();
          if (nextBlock.length) {
            nextBlock.after(element);
            this.element.trigger("blockMove");
          } else {
            var nextPage = element.closest('.forge-book-publisher-page').next();
            if (nextPage) {
              nextPage.prepend(element);
              this.element.trigger("blockMove");
            }
          }
        break;
      }
    },
        
    _createTools: function () {
      var tools = $('<div>')
        .addClass('forge-book-publisher-tools')
        .appendTo(this.element);
      
      this._toolsWaypoint = tools.waypoint({
        handler: function(direction) {
          if (direction == 'up') {
            $(this.element).css({
              'position': 'absolute',
              'top': 'initial'
            });
          } else {
            $(this.element).css({
              'position': 'fixed',
              'top': '10px'
            });
          }
        }
      }, {
        offset: '10px'
      });

      var bookToolGroup = $('<div>')
        .addClass('forge-book-publisher-tool-group forge-book-publisher-tool-group-book')
        .appendTo(tools);
      
      var blockToolGroup = $('<div>')
        .addClass('forge-book-publisher-tool-group forge-book-publisher-tool-group-block')
        .appendTo(tools);
      
      $('<a>') 
        .addClass('forge-book-publisher-tool')
        .attr('title', this.options.locales['save-button-tooltip'])
        .click($.proxy(this._onSaveClick, this))
        .append($('<span>').addClass('fa fa-save'))
        .appendTo(bookToolGroup);
            
      $('<a>') 
        .addClass('forge-book-publisher-tool')
        .attr('title', this.options.locales['import-button-tooltip'])
        .click($.proxy(this._onImportClick, this))
        .append($('<span>').addClass('fa fa-plus'))
        .appendTo(bookToolGroup);
      
      $('<a>') 
        .addClass('forge-book-publisher-tool')
         .attr('title', this.options.locales['styles-button-tooltip'])
        .click($.proxy(this._onStyleClick, this))
        .append($('<span>').addClass('fa fa-font'))
        .appendTo(bookToolGroup);
      
      this._createToolButton("styles", blockToolGroup, this.options.locales['change-block-style-button-tooltip'], { 
        icon: 'fa fa-header',
        items: $.map(this._styles, function (style) {
          return {
            name: style.name,
            action: 'changeStyle',
            selector: style.selector,
            rules: style.rules,
            element: style._element,
            className: style._className
          };
        })
      });
      
      this._createToolButton("floats", blockToolGroup, this.options.locales['change-block-float-button-tooltip'], {
        icon: 'fa fa-align-left',
        items: [{
          icon: 'fa fa-align-justify',
          action: 'changeFloat',
          float: 'none'
        }, {
          icon: 'fa fa-align-left',
          action: 'changeFloat',
          float: 'left'
        }, {
          icon: 'fa fa-align-right',
          action: 'changeFloat',
          float: 'right'
        }]
      });
      
      this._createToolButton("move", blockToolGroup, this.options.locales['move-block-button-tooltip'], {
        icon: 'fa fa-arrows',
        items: [{
          icon: 'fa fa-arrow-up',
          action: 'move',
          direction: 'up'
        }, {
          icon: 'fa fa-arrow-down',
          action: 'move',
          direction: 'down'
        }]
      });
      
      blockToolGroup.find('.forge-book-publisher-tool').attr({
        'data-disabled': 'true'
      });
    },
    
    _createToolButton: function (toolId, toolGroup, tooltip, toolOptions) {
      var menuItems = $('<div>')
        .addClass('forge-book-publisher-tool-items forge-book-publisher-tool-items-text')
        .addClass('forge-book-publisher-tool-items-' + toolId)
        .appendTo(toolGroup)
        .hide();
      
      this._createToolButtonItems(toolId, toolOptions.items);
     
      var menuButton = $('<div>')
        .addClass('forge-book-publisher-tool')
        .attr('title', tooltip)
        .append($('<span>').addClass(toolOptions.icon))
        .appendTo(toolGroup);
      
      $(window).click(function () {
        $('.forge-book-publisher-tool-items').hide();
      });
      
      menuButton.click(function (event) {
        event.stopPropagation();
        
        if ($(this).attr('data-disabled') != 'true') {
          $('.forge-book-publisher-tool-items').hide();
          menuItems.show();
          menuItems.css({
            'margin-top': ((-menuItems.height() / 2) + ($(this).height() / 2)) + 'px'
          });
        }
      });
    },
    
    _createToolButtonItems: function (toolId, items) {
      var menuItems = this.element.find('.forge-book-publisher-tool-items-' + toolId).empty();
      
      $.each(items, $.proxy(function (itemIndex, item) {
        var menuItem = $('<a>').addClass('text').attr({ 'href': '#' });
       
        if (item.name) {
          menuItem.text(item.name);
        }
       
        if (item.icon) {
          menuItem.addClass(item.icon);
        }
       
        $(menuItem).on('click', $.proxy(function (event) {
          event.preventDefault();
          
          var element = $(this.element).find('.forge-book-publisher-block-selected');
         
          switch (item.action) {
            case 'changeStyle':
              this._changePageElementStyle(element, item.element, item.className);
            break;
            case 'changeFloat':
              this._changePageElementFloat(element, item.float);
            break;
            case 'move':
              this._movePageElement(element, item.direction);
            break;
            case 'changePageType':
              this._changePageType(element.closest('.forge-book-publisher-page'), item.pageType);
            break;
          }
        }, this));
        
        menuItems.append(menuItem);
      }, this));
    },
    
    _createCss: function (callback) {
      dust.render("forge-publisher-css", {
        styles: this.styles()
      }, $.proxy(function(err, css) {
        if (err) {
          $('.notifications').notifications('notification', 'error', err);
        } else {
          callback(css);
        }
      }, this));
    },
    
    _selectBlock: function (block) {
      this.element
        .find('.forge-book-publisher-block-selected')
        .removeClass('forge-book-publisher-block-selected');
      
      $(block).addClass('forge-book-publisher-block-selected');
      
      this.element.trigger("blockSelect", {
        block: block
      });
    },
    
    _selectPage: function (page) {
      this.element
        .find('.forge-book-publisher-page-selected')
        .removeClass('forge-book-publisher-page-selected');
      
      page.addClass('forge-book-publisher-page-selected');
      
      this.element.trigger("pageSelect", {
        page: page
      });
    },
    
    _onPageClick: function (event) {
      var page = $(event.target).closest('.forge-book-publisher-page');
      var block = $(event.target).closest($.map(this.options.blockTags, function (blockTag) {
        return '.forge-book-publisher-page ' + blockTag; 
      }).join(','), page);

      this._selectBlock(block);
      this._selectPage(page);
    },
    
    _onSaveClick: function () {
      $(this.element).trigger("save", {
        "data": this.data(),
        "styles": this.styles(),
        "fonts": this.fonts()
      });
    },

    _onImportClick: function (event) {
      $(document)
        .forgeMaterialBrowser({
          types: ['FOLDER', 'DOCUMENT']
        })
        .on('materialSelect', $.proxy(function (event, data) {
          $(document).off('materialSelect');
          this._importMaterial(data.id);
        }, this));
    },
    
    _onStyleClick: function (event) {
      $("<div>") 
        .bookPublisherStyleDialog({
          fonts: this.fonts(),
          styles: this.styles()
        })
        .on("applyStyles", $.proxy(function (event, data) {
          this.styles(data.styles);
          this.fonts(data.fonts);
        }, this));
    },
    
    _onStylesChanged: function (event, data) {
      this._createCss($.proxy(function (css) {
        $(this._styleSheet).text(css);
      }, this));
      
      this._createToolButtonItems("styles", $.map(data.styles, function (style) {
        return {
          name: style.name,
          action: 'changeStyle',
          selector: style.selector,
          rules: style.rules,
          element: style._element,
          className: style._className
        };
      }));
    },
    
    _onFontsChanged: function (event, data) {
      var families = $.map(this.fonts(), function (font) {
        return font.name;
      });
      
      var urls = $.map(this.fonts(), function (font) {
        return font.url;
      });
      
      WebFont.load({
        custom: {
          families: families,
          urls: urls
        }
      });
      
      this._createCss($.proxy(function (css) {
        $(this._styleSheet).text(css);
      }, this));
    },

    _onBlockSelect: function (event, data) {
      this.element.find('.forge-book-publisher-tool-group-block .forge-book-publisher-tool').attr({
        'data-disabled': data.block.length ? 'false' : 'true'
      });
    },
    
    _onPageSelect: function (event, data) {
      
    }
    
  });
  
  $.widget("custom.bookPublisherStyleDialog", {
    _create : function() {
      this._fonts = this.options.fonts;
      
      this._dialog = null;
      dust.render("forge-publisher-style-dialog", {
        fonts: this._fonts,
        styles: this.options.styles
      }, $.proxy(function(err, html) {
        if (err) {
          $('.notifications').notifications('notification', 'error', err);
        } else {
          this._dialog = $(html);
          
          this._dialog.find('.forge-publisher-style-dialog-style-setting-editor input').change($.proxy(this._onStyleInputChange, this));
          this._dialog.find('.forge-publisher-style-dialog-style-setting-editor select').change($.proxy(this._onStyleSelectChange, this));
          this._dialog.find('.forge-publisher-style-dialog-style-setting-font-action-add').click($.proxy(this._onFontAddClick, this));
          this._dialog.find('.forge-publisher-style-dialog-style-setting-font-action-remove').click($.proxy(this._onFontRemoveClick, this));
          
          this._dialog.find('.forge-publisher-style-dialog-style').each($.proxy(function (index, style) {
            var rules = $.parseJSON($(style).attr('data-rules'));
            
            $.each(rules, function (key, value) {
              var input = $(style).find('input[name="' + key + '"]');
              if (input.length) {
                if (input.attr('data-unit')) {
                  input.val((new LengthUnitConverter(value)).to(input.attr('data-unit')));
                } else {
                  input.attr('value', value);
                }
              } else {
                $(style).find('input[data-style="' + key + '"][data-on="' + value + '"]').prop('checked', true);
                $(style).find('select[name="' + key + '"]').val(value);
              }
            });

            this._refreshPreview(style);
          }, this));

          this._dialog.find('input[type="color"]')
            .attr('type', 'text')
            .spectrum({
              preferredFormat: "rgb",
              allowEmpty: true,
              hideAfterPaletteSelect: true,
              showButtons: false
            });
          
          this._dialog.find('.forge-publisher-style-dialog-style').first().addClass('forge-publisher-style-dialog-style-active');
          this._dialog.find('.forge-publisher-style-dialog-style-name').click(function (event) {
            $(this).closest('.forge-publisher-style-dialog-styles')
              .find('.forge-publisher-style-dialog-style')
              .removeClass('forge-publisher-style-dialog-style-active');
            $(this)
              .closest('.forge-publisher-style-dialog-style')
              .addClass('forge-publisher-style-dialog-style-active');
          });
          
          this._dialog.dialog({
            modal: true,
            width: 650,
            buttons : [ {
              'text' : this._dialog.attr('data-apply-button'),
              'class': 'apply-button',
              'click' : $.proxy(function(event) {
                this.element.trigger("applyStyles", {
                  styles: this.styles(),
                  fonts: this.fonts()
                });

                this._close();
              }, this)
            }, {
              'text' : this._dialog.attr('data-cancel-button'),
              'class': 'cancel-button',
              'click' : $.proxy(function(event) {
                this._close();
              }, this)
            } ]
          });
        }
      }, this));
    },
    
    styles: function () {
      return $.map(this._dialog.find('.forge-publisher-style-dialog-style'), function (style) {
        return {
          name: $(style).find('input[name="name"]').val(),
          selector: $(style).attr('data-selector'),
          rules: $.parseJSON($(style).attr('data-rules'))
        }
      });
    },
    
    fonts: function () {
      return this._fonts;
    },
    
    addFont: function (name, url) {
      WebFont.load({
        custom: {
          families: [name],
          urls: [url]
        }
      });
      
      this._fonts.push({
        name: name,
        url: url
      });
      
      $.each($(this._dialog).find('select[name="font-family"]'), function (index, select) {
        $(select).append($('<option>').attr('value', name).text(name));
      });
    },
    
    removeFont: function (name) {
      this._fonts = $.grep(this._fonts, function(font, index) {
        return font.name != name;
      });
      
      $(this._dialog).find('select[name="font-family"] option[value="' + name + '"]').remove();
      $(this._dialog).find('select[name="font-family"]').trigger('change');
    },
    
    _onStyleInputChange: function (event) {
      var input = $(event.target);
      var style = $(input).closest('.forge-publisher-style-dialog-style');
      
      if (input.attr('type') == 'checkbox') {
        var mutuallyExculusive = input.attr('data-mutually-exclusive');
        if (mutuallyExculusive && input.prop('checked')) {
          var mutuallyExculusiveInput = style.find('input[name="' + mutuallyExculusive + '"]');
          if (mutuallyExculusiveInput.prop('checked')) {
            mutuallyExculusiveInput
              .prop('checked', false)
              .trigger('change');
          }
        }
        
        var value = input.attr(input.prop('checked') ? 'data-on' : 'data-off');
        this._updateRule(style, input.attr('data-style'), value);
      } else {
        this._updateRule(style, input.attr('name'), input.val() + (input.attr('data-unit')||''));
      }
    },

    _onStyleSelectChange: function (event) {
      var select = $(event.target);
      var style = $(select).closest('.forge-publisher-style-dialog-style');
      this._updateRule(style, select.attr('name'), select.val() + (select.attr('data-unit')||''));
    },
    
    _onFontAddClick: function (event) {
      var style = $(event.target).closest('.forge-publisher-style-dialog-style');
      this._openAddFontDialog($.proxy(function (data) {
        this.addFont(data.name, data.url);
        style.find('select[name="font-family"]')
          .val(data.name)
          .trigger('change');
      }, this));
    },
    
    _onFontRemoveClick: function (event) {
      var style = $(event.target).closest('.forge-publisher-style-dialog-style');
      var name = style.find('select[name="font-family"]').val();
      
      this._confirmFontRemoval(name, $.proxy(function (data) {
        this.removeFont(data.name);
        style.find('select[name="font-family"]').trigger('change');
      }, this));
    },
    
    _openAddFontDialog: function (callback) {
      dust.render("forge-publisher-style-add-font-dialog", {
      }, $.proxy(function(err, html) {
        if (err) {
          $('.notifications').notifications('notification', 'error', err);
        } else {
          var dialog = $(html);
          
          dialog.find('input[name="google-fonts-search"]').googleFontPicker({
            apiKey: $('.google-api-public-key').val(),            
            minLength: 3,
            select: function( event, ui ) {
              $(this).val('');
              dialog.find('input[name="name"]').val(ui.item.label);
              dialog.find('input[name="url"]').val(ui.item.value);
            }
          });
          
          dialog.dialog({
            modal : true,
            width : 500,
            buttons : [ {
              'text' : dialog.attr('data-add-button'),
              'class': 'select-button',
              'click' : function(event) {
                callback({
                  name: $(this).find('input[name="name"]').val(),
                  url: $(this).find('input[name="url"]').val()
                });
                
                $(this).dialog('close');
              }
            }, {
              'text' : dialog.attr('data-cancel-button'),
              'class': 'cancel-button',
              'click' : function(event) {
                $(this).dialog('close');
              }
            } ]
          });
        }
      }, this));
    },
    
    _confirmFontRemoval: function (name, callback) {
      dust.render("forge-publisher-style-remove-font-dialog", {
        name: name
      }, $.proxy(function(err, html) {
        if (err) {
          $('.notifications').notifications('notification', 'error', err);
        } else {
          var dialog = $(html);
          dialog.dialog({
            modal : true,
            width : 500,
            buttons : [ {
              'text' : dialog.attr('data-remove-button'),
              'class': 'remove-button',
              'click' : function(event) {
                callback({
                  name: name,
                });
                
                $(this).dialog('close');
              }
            }, {
              'text' : dialog.attr('data-cancel-button'),
              'class': 'cancel-button',
              'click' : function(event) {
                $(this).dialog('close');
              }
            } ]
          });
        }
      }, this));
    },
    
    _refreshPreview: function (style) {
      var rules = $.parseJSON($(style).attr('data-rules'));
      var previewStyles = {};
      var marginStyles = {};
      var paddingStyles = {};
      
      $.each(['left','top','bottom','right'], function (index, direction) {
        marginStyles['margin-' + direction] = (new LengthUnitConverter(rules['padding-' + direction]||'0pt')).multiply(-1).toPt(true);
        paddingStyles['padding-' + direction] = '0pt';
      });
      
      $.each(rules, function (key, value) {
        if ((key.indexOf('margin-') == 0)) {
          var direction = key.substring(7);
          marginStyles['margin-' + direction] = (new LengthUnitConverter(value)) 
            .add(rules['padding-' + direction])
            .multiply(-1)
            .toPt(true);
        } else if ((key.indexOf('padding-') == 0)) {
          var direction = key.substring(8);
          paddingStyles['margin-' + direction] = (new LengthUnitConverter(value)) 
            .multiply(-1)
            .toPt(true);
        } else {
          if (key.indexOf('background') == 0) {
            paddingStyles[key] = value;
          }
          
          previewStyles[key] = value;
        }
      });

      $(style).find('.forge-publisher-style-dialog-style-preview-margin').css(marginStyles);
      $(style).find('.forge-publisher-style-dialog-style-preview-padding').css(paddingStyles);
      $(style).find('.forge-publisher-style-dialog-style-preview').css(previewStyles);
    },
    
    _updateRule: function (style, rule, value) {
      var rules = $.parseJSON($(style).attr('data-rules'));
      rules[rule] = value;
      $(style).attr('data-rules', JSON.stringify(rules));
      this._refreshPreview(style);
    },
    
    _close: function () {
      this._dialog.dialog('close');
      this._dialog.find('*').remove();
      this.destroy();
    }
  });
  
  $(document).ready(function () {
    $('.book-publisher')
      .bookPublisher({
        locales: {
          'save-button-tooltip': $('.book-publisher').attr('data-save-button-tooltip'),
          'import-button-tooltip': $('.book-publisher').attr('data-import-button-tooltip'),
          'styles-button-tooltip': $('.book-publisher').attr('data-styles-button-tooltip'),
          'change-block-style-button-tooltip': $('.book-publisher').attr('data-change-block-style-button-tooltip'),
          'change-block-float-button-tooltip': $('.book-publisher').attr('data-change-block-float-button-tooltip'),
          'move-block-button-tooltip': $('.book-publisher').attr('data-move-block-button-tooltip')
        }
      })
      .on("save", function (event, data) {
        $('.book-layout-fonts').val(JSON.stringify(data.fonts));
        $('.book-layout-styles').val(JSON.stringify(data.styles));
        $('.book-layout-data').val(data.data);
        $('.book-layout-save')[0].click();
      });
    
    $('.book-publisher').bookPublisher('fonts', $.parseJSON($('.book-layout-fonts').val()));
    $('.book-publisher').bookPublisher('styles', $.parseJSON($('.book-layout-styles').val()));
    $('.book-publisher').bookPublisher('data', $('.book-layout-data').val());
  });
  
}).call(this);