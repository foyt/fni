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
  
  $.widget("custom.bookPublisherDialog", {
    options: {
      templateName: null,
      templateOptions: {}
    },
    
    _create : function() {
      this.dialogElement = null;
      
      dust.render(this.options.templateName, this.options.templateOptions, $.proxy(function (err, html) {
        if (err) {
          $('.notifications').notifications('notification', 'error', err);
        } else {
          this.dialogElement = $(html);
          this.element.trigger("beforeDialogOpen");
          
          this.dialogElement.find('input[type="color"]')
            .attr({
              'type': 'text',
              'data-original-type': 'color'
            })
            .spectrum();
          
          this.dialogElement.on("change","input,select,textarea", $.proxy(this._onFormFieldChange, this));
          
          var buttons = $.map(this.options.dialogButtons, $.proxy(function (dialogButton) {
            return {
              'require-valid': dialogButton['require-valid']||false, 
              'text': this.dialogElement.attr(dialogButton['text-attribute']),
              'click': $.proxy(dialogButton['click'], this)
            };
          }, this));
          
          this.dialogElement
            .addClass('forge-publisher-dialog')
            .dialog({
              modal: true,
              width: this.options.dialogWidth,
              buttons: buttons
            });
          
          this.element.trigger("afterDialogOpen");
        }
      }, this));
    },
    
    _close: function () {
      this.dialogElement.find('input[data-original-type="color"]').spectrum('destroy').remove();
      this.dialogElement.dialog('close').remove();
      this.destroy();
    },
    
    _onFormFieldChange: function (event) {
      var valid = event.target.checkValidity();
      
      var buttons = $.map(this.dialogElement.dialog("option", "buttons"), function (button) {
        if (button['require-valid'] == true) {
          if (valid) {
            delete button['disabled'];
          } else {
            button['disabled'] = true;
          }
        }
        
        return button;
      });
      
      this.dialogElement.dialog("option", "buttons", buttons);
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
          'font-size': '12pt',
          'margin-top': '14pt',
          'margin-bottom': '14pt'          
        }
      }, {
        "name":"Otsikko 1",
        "selector":"h1",
        "rules":{
          'font-family': 'Tinos',
          'font-size': '24pt',  
          'margin-top': '18pt',  
          'margin-bottom': '18pt',  
          'font-weight': 'bold'
        }
      }, {
        "name":"Otsikko 2",
        "selector":"h2",
        "rules":{
          'font-family': 'Tinos',
          'font-size': '22pt',  
          'margin-top': '16pt',  
          'margin-bottom': '16pt',  
          'font-weight': 'bold'
        }
      }, {
        "name":"Otsikko 3",
        "selector":"h3",
        "rules":{
          'font-family': 'Tinos',
          'font-size': '20pt', 
          'margin-top': '14pt',
          'margin-bottom': '14pt',  
          'font-weight': 'bold'
        }
      }, {
        "name":"Otsikko 4",
        "selector":"h4",
        "rules":{
          'font-family': 'Tinos',
          'font-size': '18pt',
          'margin-top': '12pt',
          'margin-bottom': '12pt',  
          'font-weight': 'bold'
        }
      }, {
        "name":"Otsikko 5",
        "selector":"h5",
        "rules":{
          'font-family': 'Tinos',
          'font-size': '16pt',  
          'margin-top': '10pt',  
          'margin-bottom': '10pt',  
          'font-weight': 'bold'
        }
      }, {
        "name":"Otsikko 6",
        "selector":"h6",
        "rules":{
          'font-family': 'Tinos',
          'font-size': '14pt',
          'margin-top': '8pt',
          'margin-bottom': '8pt',  
          'font-weight': 'bold'
        }
      }, {
        "name":"Luettelo",
        "selector":"ul",
        "rules":{
          'font-family': 'Tinos',
          'font-size': '12pt',
          'margin-left':'30pt',
          'list-style-type': 'disc'
        }
      }, {
        "name":"Numerolista",
        "selector":"ol",
        "rules":{  
          'font-family': 'Tinos',
          'font-size': '12pt',
          'margin-left':'30pt',
          'list-style-type': 'decimal'
        }
      }],
      
      blockTags: ['p', 'h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'ul', 'ol', 'div', 'img'],
      defaultPageType: 'Contents',
      
      pageTypes: [{
        name: "Contents",
        header: {},
        footer: {
          text: '[[PAGE]]',
          rules: {
            'display': 'block',
            'text-align': 'center'
          }
        }
      }, {
        name: "Table of contents",
        header: {},
        footer: {}
      }, {
        name: "Front cover",
        header: {},
        footer: {}
      }, {
        name: "Back cover",
        header: {},
        footer: {}
      }]

    },
    
    _create : function() {
      this.fonts(this.options.fonts);
      this.styles(this.options.styles);
      this.pageTypes(this.options.pageTypes);
      
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
      this.element.on("pageTypesChanged", $.proxy(this._onPageTypesChanged, this));
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
      if (val !== undefined) {
        var styles = val;
        
        if (!val) {
          styles = [];
        } else {
          if ($.type(styles) == 'string') {
            styles = $.parseJSON(val);
          } else {
            styles = val;
          }
        }
        
        this._styles = $.map(styles, function (style) {
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
      if (val !== undefined) {
        if (!val) {
          this._fonts = [];
        } else {
          if ($.type(val) == 'string') {
            this._fonts = $.parseJSON(val);
          } else {
            this._fonts = val;
          }
        }
        
        $(this.element).trigger("fontsChanged", {
          fonts: val
        });
      } else {
        return this._fonts;
      }
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
      
      $(this.element).trigger("fontsChanged", {
        fonts: this.fonts()
      });
    },
        
    removeFont: function (name) {
      this._fonts = $.grep(this._fonts, function(font, index) {
        return font.name != name;
      });

      $(this.element).trigger("fontsChanged", {
        fonts: this.fonts()
      });
    },
    
    pageTypes: function (val) {
      if (val !== undefined) {
        if (!val) {
          this._pageTypes = [];
        } else {
          if ($.type(val) == 'string') {
            this._pageTypes = $.parseJSON(val);
          } else {
            this._pageTypes = val;
          }
        }
        
        $(this.element).trigger("pageTypesChanged", {
          pageTypes: val
        });
      } else {
        return this._pageTypes;
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
      var page = $('<section>')
        .attr('data-type', this.options.defaultPageType)
        .addClass('forge-book-publisher-page')
        .append($('<header>'))
        .append($('<main>'))
        .append($('<footer>'))
        .appendTo(this.element.find('.forge-book-publisher-pages'));
      
      this._updateHeadersAndFooters(page);
      
      return page;
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
      
      lastPage.find('main').append(block);
    },
    
    _measurePageContentHeight: function (page) {
      var children = page.find('main').children();
      
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
    
    _updateHeadersAndFooters: function (pages) {
      var typeMap = {};

      $.each(this.pageTypes(), function (index, pageType) {
        typeMap[pageType.name] = pageType;
      });
      
      $(pages).each(function (index, section) {
        var pageType = typeMap[$(section).attr('data-type')];
        var header = $(section).find('header');
        var footer = $(section).find('footer');
        
        header
          .css(pageType.header.rules||{})
          .text(pageType.header.text||'');
         
        footer
          .css(pageType.footer.rules||{})
          .text(pageType.footer.text||'');
        
        $(section).find('main').css({
          height: 'calc(100% - ' + (footer.outerHeight(true) + header.outerHeight(true)) + 'px)'
        });
      });
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
        }, 
        offset: '10px'
      });

      var bookToolGroup = $('<div>')
        .addClass('forge-book-publisher-tool-group forge-book-publisher-tool-group-book')
        .appendTo(tools);
      
      var pageToolGroup = $('<div>')
        .addClass('forge-book-publisher-tool-group forge-book-publisher-tool-group-page')
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
      
      $('<a>') 
        .addClass('forge-book-publisher-tool')
         .attr('title', this.options.locales['page-types-button-tooltip'])
        .click($.proxy(this._onPageTypesClick, this))
        .append($('<span>').addClass('fa fa-bookmark-o'))
        .appendTo(bookToolGroup);
      
      this._createToolButton("change-page-type", pageToolGroup, this.options.locales['change-page-type-button-tooltip'], { 
        icon: 'fa fa-bookmark',
        items: $.map(this._pageTypes, function (pageType) {
          return {
            name: pageType.name,
            action: 'changePageType'
          };
        })
      });
      
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

      pageToolGroup.find('.forge-book-publisher-tool').attr({
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
              this._changePageType(this.element.find('.forge-book-publisher-page-selected'), item.name);
            break;
          }
        }, this));
        
        menuItems.append(menuItem);
      }, this));
    },
    
    _createCss: function (callback) {
      dust.render("forge/book-publisher/css", {
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
        .bookPublisherStylesDialog({
          fonts: this.fonts(),
          styles: this.styles()
        })
        .on("applyStyles", $.proxy(function (event, data) {
          this.styles(data.styles);
        }, this));
    },
    
    _onPageTypesClick: function (event) {
      $("<div>") 
        .bookPublisherPageTypesDialog({
          pageTypes: this.pageTypes(),
          fonts: this.fonts()
        })
        .on("applyTypes", $.proxy(function (event, data) {
          this.pageTypes(data.types);
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
    
    _onPageTypesChanged: function (event, data) {
      this._createToolButtonItems("change-page-type", $.map(data.pageTypes, function (pageType) {
        return {
          name: pageType.name,
          action: 'changePageType'
        };
      }));
      
      this._updateHeadersAndFooters(this.element.find('section'));
    },

    _onBlockSelect: function (event, data) {
      this.element.find('.forge-book-publisher-tool-group-block .forge-book-publisher-tool').attr({
        'data-disabled': data.block.length ? 'false' : 'true'
      });
    },
    
    _onPageSelect: function (event, data) {
      this.element.find('.forge-book-publisher-tool-group-page .forge-book-publisher-tool').attr({
        'data-disabled': data.page.length ? 'false' : 'true'
      }); 
    }
  });
  
  $.widget("custom.bookPublisherStylesDialog", $.custom.bookPublisherDialog, {
    options: {
      dialogWidth: 650,
      dialogButtons: [{
        'require-valid': true,
        'text-attribute': 'data-apply-button',
        'click': function () {
          this.element.trigger("applyStyles", {
            styles: this.styles()
          });
          
          this._close(); 
        }
      }, {
        'text-attribute': 'data-cancel-button',
        'click' : function() {
          this._close();
        }
      }],
      templateName: "forge/book-publisher/styles-dialog"
    },
        
    _create : function() {
      this.option("templateOptions", {
        fonts: this.options.fonts,
        styles: this.options.styles
      });
      
      this.element.on("beforeDialogOpen", $.proxy(function (event) {
        this.dialogElement.find('ul').parent()
        .tabs({ 
          beforeActivate: $.proxy(function (event, ui) {
            
            this.dialogElement.find('.forge-publisher-style-dialog-style').each($.proxy(function (index, style) {
              var rulesAttr = $(style).attr('data-rules');
              if (rulesAttr) {
                var rules = $.parseJSON(rulesAttr);
                
                $.each(rules, $.proxy(function (key, value) {
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
                }, this));
                
                this._refreshPreview(style);
              }
            }, this));
            
            var href = $(ui.newTab).find('a').attr("href");
            
            if (href == '#style-tab-new') {
              event.preventDefault();
              event.stopPropagation();
              
              var newIndex = $(ui.newTab).closest('ul').find('li').index(ui.newTab);
              var newId = 'style-tab-' + newIndex;
              var selector = '.style-' + newIndex;
              
              var label = $('<li>').append(
                $('<a>').attr({
                  'href': '#' + newId
                }).text(this.dialogElement.attr('data-empty-tab-label'))
              );
              
              var panel = $('<div>')
                .attr('id', newId)
                .html($(ui.newPanel).html());
             
              panel.find('.sp-replacer').remove();
              panel.find('input[data-original-type="color"]').spectrum();
              
              $(ui.newPanel).before(panel);
              $(ui.newTab).before(label);
              
              panel.find('.forge-publisher-style-dialog-style').attr({
                'data-selector': selector,
                'data-rules': '{}'
              });
              
              $(event.target).tabs("refresh");
              $(event.target).tabs("option", "active", newIndex);
            }
          }, this)
        });

        this.dialogElement.on("change", '.forge-publisher-style-dialog-style-name', $.proxy(this._onNameChange, this));
        this.dialogElement.on("change", 'input', $.proxy(this._onInputChange, this));
        this.dialogElement.on("change", 'select', $.proxy(this._onSelectChange, this));
        this.dialogElement.on("click", '.forge-publisher-style-dialog-style-setting-font-action-add', $.proxy(this._onAddFontClick, this));
        this.dialogElement.on("click", '.forge-publisher-style-dialog-style-setting-font-action-remove', $.proxy(this._onRemoveFontClick, this));
      }, this));
      
      this._super();
    },
    
    styles: function () {
      return $.map(this.dialogElement.find('.ui-tabs-panel:not([id="style-tab-new"]) .forge-publisher-style-dialog-style'), function (style) {
        return {
          name: $(style).find('input[name="name"]').val(),
          selector: $(style).attr('data-selector'),
          rules: $.parseJSON($(style).attr('data-rules')||'{}')
        }
      });
    },
    
    _onAddFontClick: function (event) {
      $('<div>')
        .bookPublisherAddFontDialog()
        .on("addFont", $.proxy(function (event, data) {
          $('.book-publisher').bookPublisher('addFont', data.name, data.url);

          $.each(this.dialogElement.find('select[name="font-family"]'), function (index, select) {
            $(select).append($('<option>').attr('value', data.name).text(data.name));
          });
        }, this));
    },
    
    _onRemoveFontClick: function (event) {
      var name = $(event.target)
        .closest('.forge-publisher-style-dialog-style-setting-font')
        .find('select[name="font-family"]').val();
      
      $('<div>')
        .bookPublisherRemoveFontDialog({
          name: name
        })
        .on("removeFont", $.proxy(function (event, data) {
          $('.book-publisher').bookPublisher('removeFont', data.name);
          this.dialogElement.find('select[name="font-family"] option[value="' + name + '"]').remove();
        }, this));
    },
    
    _onNameChange: function (event) {
      this.dialogElement.find('.ui-tabs-active a')
        .text($(event.target).val()||this.dialogElement.attr('data-empty-tab-label'));
    },
    
    _onInputChange: function (event) {
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
    
    _onSelectChange: function (event) {
      var select = $(event.target);
      var style = $(select).closest('.forge-publisher-style-dialog-style');
      this._updateRule(style, select.attr('name'), select.val() + (select.attr('data-unit')||''));
    },
    
    _refreshPreview: function (style) {
      var rules = $.parseJSON($(style).attr('data-rules')||'{}');
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
      var rules = $.parseJSON($(style).attr('data-rules')||'{}');
      rules[rule] = value;
      $(style).attr('data-rules', JSON.stringify(rules));
      this._refreshPreview(style);
    }
    
  });
  
  $.widget("custom.bookPublisherAddFontDialog", $.custom.bookPublisherDialog, {
    options: {
      dialogWidth: 500,
      dialogButtons: [{
        'require-valid': true,
        'text-attribute': 'data-add-button',
        'click': function () {
          this.element.trigger("addFont", {
            name: $(this.dialogElement).find('input[name="name"]').val(),
            url: $(this.dialogElement).find('input[name="url"]').val()
          });

          this._close(); 
        }
      }, {
        'text-attribute': 'data-cancel-button',
        'click' : function() {
          this._close();
        }
      }],
      templateName: "forge/book-publisher/add-font-dialog"
    },
        
    _create : function() {
      this.element.on("beforeDialogOpen", $.proxy(function (event) {
        this.dialogElement.find('input[name="google-fonts-search"]').googleFontPicker({
          apiKey: $('.google-api-public-key').val(),            
          minLength: 3,
          select: $.proxy(function(event, ui ) {
            event.preventDefault();
            event.stopPropagation();
            $(event.target).val('');
            this.dialogElement.find('input[name="name"]').val(ui.item.label);
            this.dialogElement.find('input[name="url"]').val(ui.item.value);
          }, this)
        });
      }, this));
      
      this._super();
    }
    
  });
  
  $.widget("custom.bookPublisherRemoveFontDialog", $.custom.bookPublisherDialog, {
    options: {
      dialogWidth: 500,
      dialogButtons: [{
        'require-valid': true,
        'text-attribute': 'data-remove-button',
        'click': function () {
          this.element.trigger("removeFont", {
            name: this.options.name
          });

          this._close(); 
        }
      }, {
        'text-attribute': 'data-cancel-button',
        'click' : function() {
          this._close();
        }
      }],
      templateName: "forge/book-publisher/remove-font-dialog"
    },
    
    _create : function() {
      this.option("templateOptions", {
        name: this.options.name
      });
      
      this._super();
    }
    
  });
  
  $.widget("custom.bookPublisherPageTypesDialog", $.custom.bookPublisherDialog, {
    
    options: {
      dialogWidth: 650,
      dialogButtons: [{
        'require-valid': true,
        'text-attribute': 'data-apply-button',
        'click': function () {
          this.element.trigger("applyTypes", {
            types: this.types()
          });

          this._close(); 
        }
      }, {
        'text-attribute': 'data-cancel-button',
        'click' : function() {
          this._close();
        }
      }],
      templateName: "forge/book-publisher/page-types-dialog"
    },
    
    _create : function() {

      this.option("templateOptions", {
        types: this.options.pageTypes,
        fonts: this.options.fonts
      });
      
      this.element.on("beforeDialogOpen", $.proxy(function (event) {
        $.each(this.dialogElement.find('.forge-publisher-page-types-dialog-type'), $.proxy(function (index, type) {
          $.each(['header', 'footer'], $.proxy(function (groupIndex, group) {
            var rulesAttr = $(type).attr('data-' + group + '-rules');
            if (rulesAttr) {
              var rules = $.parseJSON(rulesAttr);
              
              $.each(rules, $.proxy(function (key, value) {
                var input = $(type).find('input[name="' + group + ':' + key + '"]');
                if (input.length) {
                  if (input.attr('data-unit')) {
                    input.val((new LengthUnitConverter(value)).to(input.attr('data-unit')));
                  } else {
                    input.attr('value', value);
                  }
                } else {
                  $(type).find('input[data-style="' + key + '"][data-on="' + value + '"][name^="' + group + ':"]').prop('checked', true);
                  $(type).find('select[name="' + group + ':' + key + '"]').val(value);
                }
              }, this));
              
              $(type).find('textarea[name="' + group + ':text"]').css(rules);
            }
          }, this));
        }, this));
        
        autosize(this.dialogElement.find('textarea'));
        autosize.update(this.dialogElement.find('textarea'));
        
        this.dialogElement.find('ul').parent()
          .tabs({
            beforeActivate: $.proxy(function (event, ui) {
              var href = $(ui.newTab).find('a').attr("href");
              
              if (href == '#type-tab-new') {
                event.preventDefault();
                event.stopPropagation();
                
                var newIndex = $(ui.newTab).closest('ul').find('li').index(ui.newTab);
                var newId = 'type-tab-' + newIndex;
                
                var label = $('<li>').append(
                  $('<a>').attr({
                    'href': '#' + newId
                  }).text(this.dialogElement.attr('data-empty-tab-label'))
                );
                
                var panel = $('<div>')
                  .attr('id', newId)
                  .html($(ui.newPanel).html());
                
                panel.find('.sp-replacer').remove();
                panel.find('input[data-original-type="color"]').spectrum();
                
                $(ui.newPanel).before(panel);
                $(ui.newTab).before(label);
                
                $(event.target).tabs("refresh");
                
                $(event.target).tabs("option", "active", newIndex);
              }
            }, this)
          });

        this.dialogElement.on("change", '.forge-publisher-page-types-dialog-type-name', $.proxy(this._onNameChange, this));
        this.dialogElement.on("change", 'input', $.proxy(this._onInputChange, this));
        this.dialogElement.on("change", 'select', $.proxy(this._onSelectChange, this));
        this.dialogElement.on("click", '.forge-publisher-page-types-dialog-type-setting-font-action-add', $.proxy(this._onAddFontClick, this));
        this.dialogElement.on("click", '.forge-publisher-page-types-dialog-type-setting-font-action-remove', $.proxy(this._onRemoveFontClick, this));
        
      }, this));
      
      this._super();
    },
    
    types: function () {
      return $.map(this.dialogElement.find('.ui-tabs-panel:not([id="type-tab-new"]) .forge-publisher-page-types-dialog-type'), function (type) {
        var headerRules = $(type).attr('data-header-rules');
        var footerRules = $(type).attr('data-footer-rules');
        
        return {
          name: $(type).find('input[name="name"]').val(),
          header: {
            text: $(type).find('textarea[name="header:text"]').val(),
            rules: headerRules ? $.parseJSON(headerRules) : {}
          }, 
          footer: {
            text: $(type).find('textarea[name="footer:text"]').val(),
            rules: footerRules ? $.parseJSON(footerRules) : {}
          }
        }
      });
    },

    _updateRule: function (type, group, rule, value) {
      var rulesAttr = $(type).attr('data-' + group + '-rules');
      var rules = rulesAttr ? $.parseJSON(rulesAttr) : {};
      rules[rule] = value;
      $(type).attr('data-' + group + '-rules', JSON.stringify(rules));
      
      var disabled = rules['display'] == 'none';
      delete rules['display'];
      
      var textarea = type.find('textarea[name="' + group + ':text"]');
      
      textarea.css(rules);
      if (disabled) {
        textarea.attr('disabled', 'disabled');
      } else {
        textarea.removeAttr('disabled');
      }
      
      autosize.update(textarea);
    },
    
    _onNameChange: function (event) {
      this.dialogElement.find('.ui-tabs-active a')
        .text($(event.target).val()||this.dialogElement.attr('data-empty-tab-label'));
    },
    
    _onAddFontClick: function (event) {
      $('<div>')
        .bookPublisherAddFontDialog()
        .on("addFont", $.proxy(function (event, data) {
          $('.book-publisher').bookPublisher('addFont', data.name, data.url);

          $.each(this.dialogElement.find('select[name*="font-family"]'), function (index, select) {
            $(select).append($('<option>').attr('value', data.name).text(data.name));
          });
        }, this));
    },
    
    _onRemoveFontClick: function (event) {
      var name = $(event.target)
        .closest('.forge-publisher-page-types-dialog-type-setting-font')
        .find('select[name*="font-family"]').val();
      
      $('<div>')
        .bookPublisherRemoveFontDialog({
          name: name
        })
        .on("removeFont", $.proxy(function (event, data) {
          $('.book-publisher').bookPublisher('removeFont', data.name);
          this.dialogElement.find('select[name*="font-family"] option[value="' + name + '"]').remove();
        }, this));
    },

    _onInputChange: function (event) {
      var input = $(event.target);
      var type = input.closest('.forge-publisher-page-types-dialog-type');
      var name = input.attr('name');
      if (name.indexOf(':')) {
        var nameParts = name.split(':');
        
        if (input.attr('type') == 'checkbox') {
          if (nameParts[1] == 'show') {
            if (input.prop('checked')) {
              type.find('textarea[name="' + nameParts[0] + ':text"]').removeAttr('disabled');
            } else {
              type.find('textarea[name="' + nameParts[0] + ':text"]').attr('disabled', 'disabled');
            }
          } else {
            var mutuallyExculusive = input.attr('data-mutually-exclusive');
            if (mutuallyExculusive && input.prop('checked')) {
              var mutuallyExculusiveInput = type.find('input[name="' + mutuallyExculusive + '"]');
              if (mutuallyExculusiveInput.prop('checked')) {
                mutuallyExculusiveInput
                  .prop('checked', false)
                  .trigger('change');
              }
            }
            
            var value = input.attr(input.prop('checked') ? 'data-on' : 'data-off');
            this._updateRule(type, nameParts[0], input.attr('data-style'), value);
          }
        } else {
          this._updateRule(type, nameParts[0], nameParts[1], input.val() + (input.attr('data-unit')||''));
        }
      }
    },
    
    _onSelectChange: function (event) {
      var select = $(event.target);
      var name = select.attr('name');
      if (name.indexOf(':')) {
        var nameParts = name.split(':');
        var type = select.closest('.forge-publisher-page-types-dialog-type');
        this._updateRule(type, nameParts[0], nameParts[1], select.val() + (select.attr('data-unit')||''));
      }
    }
    
  });
  
  $(document).ready(function () {
    $('.book-publisher')
      .bookPublisher({
        locales: {
          'save-button-tooltip': $('.book-publisher').attr('data-save-button-tooltip'),
          'import-button-tooltip': $('.book-publisher').attr('data-import-button-tooltip'),
          'styles-button-tooltip': $('.book-publisher').attr('data-styles-button-tooltip'),
          'page-types-button-tooltip': $('.book-publisher').attr('data-page-types-button-tooltip'),
          'change-page-type-button-tooltip': $('.book-publisher').attr('data-change-page-type-button-tooltip'),
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
    
    var fonts = $('.book-layout-fonts').val();
    var styles = $('.book-layout-styles').val();
    var data = $('.book-layout-data').val();
    
    if (fonts) {
      $('.book-publisher').bookPublisher('fonts', fonts);
    }
    
    if (styles) {
      $('.book-publisher').bookPublisher('styles', styles);
    }
    
    if (data) {
      $('.book-publisher').bookPublisher('data', data);
    }
  });
  
}).call(this);