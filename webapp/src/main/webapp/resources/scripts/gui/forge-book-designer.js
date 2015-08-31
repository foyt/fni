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
  
  function generateUUID(prefix) {
    return prefix + (new Date().getTime().toString(16)) + '-' + (Math.floor((Math.random() * 1000000000) + 1000000000).toString(16));
  }
  
  $.widget("custom.bookDesignerDialog", {
    options: {
      closable: true,
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
            .addClass('forge-designer-dialog')
            .dialog($.extend({
              modal: true,
              dialogClass: this.options.closable ? "" : "no-close",
              buttons: buttons
            }, this.options.dialogOptions||{}));
          
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
  
  $.widget("custom.bookDesigner", {
    options: {
      scrollDuration: 1000,
      scrollOffset: 100,
      blockTags: ['p', 'h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'ul', 'ol', 'div', 'img'],
      styles: [],
      pageTypes: [],
      fonts: [],
      data: ''
    },
    
    _create : function() {
      this.styles(this.options.styles);
      this.pageTypes(this.options.pageTypes);
      this.fonts(this.options.fonts);

      this._materialClient = new $.RestClient(CONTEXTPATH + '/rest/material/');
      this._materialClient.add("documents");
      this._materialClient.add("images");
      
      this._styleSheet = $('<style>').attr({ 'type': 'text/css' }).appendTo(document.head);
      
      this._createTools();
      
      $('<article>')
        .addClass('forge-book-designer-pages')
        .appendTo(this.element);
      
      this._refreshCss($.proxy(function () {
        this.addPage();
        this.data(this.options.data);

        this._updatePageNumbers();
        this._updateHeadersAndFooters(this.element.find('section'));

        this.element.on("click", '.forge-book-designer-page', $.proxy(this._onPageClick, this));
        this.element.on("stylesChanged", $.proxy(this._onStylesChanged, this));
        this.element.on("fontsChanged", $.proxy(this._onFontsChanged, this));
        this.element.on("pageTypesChanged", $.proxy(this._onPageTypesChanged, this));
        this.element.on("pageRemove", $.proxy(this._onPageRemove, this));
        this.element.on("pageMove", $.proxy(this._onPageMove, this));
        this.element.on("blockSelect", $.proxy(this._onBlockSelect, this));
        this.element.on("pageSelect", $.proxy(this._onPageSelect, this));
      }, this));
    },
    
    data: function (val) {
      if (val !== undefined) {
        var parsed = $(val);
        
        parsed.find('img').each(function (imageIndex, image) {
          $(image)
            .attr('data-original', $(image).attr('src'))
            .removeAttr('src')
            .lazyload();
        });
        
        this.element.find('.forge-book-designer-pages').empty().append(parsed);
        
        this._toolsWaypoint[0].context.refresh();
      } else {
        var cloned = $('<pre>').html( this.element.find('.forge-book-designer-pages').html() );

        cloned.find('.forge-book-designer-page-selected').removeClass('forge-book-designer-page-selected');
        cloned.find('.forge-book-designer-block-selected').removeClass('forge-book-designer-block-selected');
        cloned.find('*[contenteditable]').removeAttr('contenteditable');
        cloned.find('*[spellcheck]').removeAttr('spellcheck');
        cloned.find('img[data-original]').each(function (imageIndex, image) {
          $(image)
            .attr('src', $(image).attr('data-original'))
            .removeAttr('data-original');
        });

        return cloned.html();
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
          styles: this._styles
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
        
        WebFont.load({
          custom: {
            families: $.map(this._fonts, function (font) {
              return font.name;
            }),
            urls: $.map(this._fonts, function (font) {
              return font.url;
            })
          }
        });
        
        $(this.element).trigger("fontsChanged", {
          fonts: this._fonts
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
          
          this._pageTypes = $.map(this._pageTypes, function (pageType) {
            if (!pageType.id) {
              pageType.id = generateUUID('pt-');
            }
            
            return pageType;
          });
        }
        
        $(this.element).trigger("pageTypesChanged", {
          pageTypes: this._pageTypes
        });
      } else {
        return this._pageTypes;
      }
    },
    
    insertHtmlBefore: function (data, block) {
      var elements = this._parseHtml(data);
      $(block).before(elements);
      return elements;
    },

    insertHtmlAfter: function (data, block) {
      var elements = this._parseHtml(data);
      $(block).after(elements);
      return elements;
    },

    appendHtmlToPage: function (data, page) {
      var elements = this._parseHtml(data);
      $(page).find('main').append(elements);
      return elements;
    },

    prependHtmlToPage: function (data, page) {
      var elements = this._parseHtml(data);
      $(page).find('main').prepend(elements);
      this._scrollToElement(elements);
      return elements;
    },

    addPage: function () {
      var page = this._createPage(this.pageTypes()[0])
        .appendTo(this.element.find('.forge-book-designer-pages'));

      this._updatePageNumbers();
      this._updateHeadersAndFooters(page);
      this._toolsWaypoint[0].context.refresh();
      
      return page;
    },

    addPageBefore: function (refPage) {
      var page = this._createPage(this.pageTypes()[0]);
      refPage.before(page);

      this._updatePageNumbers();
      this._updateHeadersAndFooters(page);
      this._toolsWaypoint[0].context.refresh();
      
      return page;
    },

    addPageAfter: function (refPage) {
      var page = this._createPage(this.pageTypes()[0]);
      refPage.after(page);

      this._updatePageNumbers();
      this._updateHeadersAndFooters(page);
      this._toolsWaypoint[0].context.refresh();
      
      return page;
    },
    
    appendBlockAfter: function (html, after) {
      var block = $(html);
      $(after).after(block);
      return block;
    },
    
    appendBlock: function (html, page) {
      var block = $(html);
      $(page).find('main').append(block);
      return block;
    },
    
    selectBlock: function (block) {
      var page = $(block).closest('.forge-book-designer-page');
      this._selectPage(page);
      this._selectBlock(block);
    },
    
    selectPage: function (page) {
      this._selectPage(page);
      this._selectBlock(null);
    },
    
    autoLayout: function () {
      var pages = this.element.find('.forge-book-designer-page');
      var currentPage = pages.splice(0, 1);
      var remainingHeight = $(currentPage).find('main').height();
      var blockElements = this.element.find('.forge-book-designer-page>main>*');
      var tempPage = $('<section>')
        .addClass('forge-book-designer-page')
        .css({
          'opacity': 0
        })
        .append(blockElements)
        .appendTo(this.element.find('.forge-book-designer-pages'));
      
      blockElements.each($.proxy(function (index, block) {
        var blockHeight = this._measureBlockOuterHeight(block, true, false);
        if (remainingHeight >= blockHeight) {
          remainingHeight -= blockHeight;
          $(currentPage).find('main').append(block);
        } else {
          currentPage = pages.length ? pages.splice(0, 1) : this.addPage();
          $(currentPage).find('main').append(block);
          remainingHeight = $(currentPage).find('main').height() - blockHeight;
        }
      }, this));
      
      tempPage.remove();
    },
    
    _parseHtml: function (data) {
      var elements = [];
      
      $('<pre>')
        .html(data)
        .children()
        .each($.proxy(function (index, child) {
          $(child).css('page-break-after', '');
          $(child).find('img').each(function (imageIndex, image) {
            $(image)
              .attr('data-original', $(image).attr('src'))
              .removeAttr('src')
              .lazyload();
          });
          
          elements.push(child);
        }, this));
      
      return $(elements);
    },
    
    _scrollToElement: function (element) {
      if ($(element).length) {
        $('html, body').animate({
          scrollTop: $(element).offset().top - this.options.scrollOffset
        }, this.options.scrollDuration);
      }
    },
    
    _createPage: function (type) {
      return $('<section>')
        .attr({
          'data-type-id': type ? type.id : 'error',
          'data-type-name': type ? type.name : 'Error'
        })
        .addClass('forge-book-designer-page')
        .append($('<header>'))
        .append($('<main>'))
        .append($('<footer>'));
    },
    
    _measurePageContentHeight: function (page) {
      var children = $(page).find('main').children();
      
      var result = 0;
      for (var i = 0, l = children.length; i < l; i++) {
        result += $(children[i]).outerHeight(true);
      }
      
      return result;
    },
    
    _measureBlockOuterHeight: function (block, includeMargin, useTempPage) {
      var tempPage = null;
      
      if (useTempPage) {
        tempPage = $('<section>')
          .addClass('forge-book-designer-page')
          .css({
            'opacity': 0
          })
          .append(block)
          .appendTo(this.element.find('.forge-book-designer-pages'));
      }
      
      var height = $(block).outerHeight(includeMargin);
      var imgs = $(block).find('img');
      
      if (imgs.length) {
        for (var i = 0, l = imgs.length; i < l; i++) {
          var img = $(imgs[i]);
          var float = img.css('float');
          
          if ((float == 'left')||(float == 'right')) {
            height = Math.max(img.outerHeight(includeMargin), height);
          }
        }
      }
      
      if (useTempPage) {
        tempPage.remove();
      }
      
      return height;
    },
    
    _updateHeadersAndFooters: function (pages) {
      var typeMap = {};

      $.each(this.pageTypes(), function (index, pageType) {
        typeMap[pageType.id] = pageType;
      });
      
      $(pages).each($.proxy(function (index, page) {
        var pageType = typeMap[$(page).attr('data-type-id')];
        if (pageType) {
          var header = $(page).find('header');
          var footer = $(page).find('footer');
          
          header.text(this._processHeaderFooterText(page, pageType.header.text||''));
          footer.text(this._processHeaderFooterText(page, pageType.footer.text||''));
  
          var headerHeight = header.is(':visible') ? header.outerHeight(true) : 0;
          var footerHeight = footer.is(':visible') ? footer.outerHeight(true) : 0;
          
          $(page).find('main').css({
            height: 'calc(100% - ' + (headerHeight + footerHeight) + 'px)'
          });
        }
      }, this));
    },
    
    _processHeaderFooterText: function (page, text) {
      return text.replace('[[PAGE]]', $(page).attr('data-page-number'));
    },
    
    _updatePageNumbers: function () {
      this.element.find($.map(this.pageTypes(), function (pageType) {
        return !pageType.numberedPage ? "section[data-type-id='" + pageType.id + "']" : null;
      }).join(',')).each(function (index, page) {
        $(page).removeAttr('data-page-number');
      });
      
      this.element.find($.map(this.pageTypes(), function (pageType) {
        return pageType.numberedPage ? "section[data-type-id='" + pageType.id + "']" : null;
      }).join(',')).each(function (index, page) {
        $(page).attr('data-page-number', index + 1);
      });
    },
    
    _loadContent: function (type, id, callback) {
      switch (type) {
        case 'IMAGE':
          this._materialClient.images.read(id).done($.proxy(function (image, message, xhr){
            if (xhr.status !== 200) {
              $('.notifications').notifications('notification', 'error', message);
            } else {
              callback($('<img>').attr({
                'src': CONTEXTPATH + '/materials/' + image.path
              }));
            }
          }, this));
        break;
        case 'DOCUMENT':
          this._materialClient.documents.read(id).done($.proxy(function (document, message, xhr){
            if (xhr.status !== 200) {
              $('.notifications').notifications('notification', 'error', message);
            } else {
              callback(document.data);
            }
          }, this));
        break;
      }
      
    },
    
    _changePageType: function (page, typeId, typeName) {
      $(page).attr({
        'data-type-id': typeId,
        'data-type-name': typeName
      });
      
      this._updatePageNumbers();
      this._updateHeadersAndFooters(page);
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
    
    _changeBlockAlign: function (element, align) {
      if (element.prop('tagName').toLowerCase() == 'img') {
        element.css({
          'display': align ? 'block' : '',
          'margin-left': align == 'center' || align == 'right' ? 'auto' : '',
          'margin-right': align == 'center' || align == 'left' ? 'auto' : '',
          'text-align':''
        });          
      } else {
        element.css({
          'display': '',
          'margin-left': '',
          'margin-right': '',
          'text-align': align
        });  
      }

      this.element.trigger("blockAlignChange");
    },
    
    _changeBlockFloat: function (element, float) {
      element.css({
        'float': float
      });  

      this.element.trigger("blockFloatChange");
    },
    
    _moveBlock: function (block, direction) {
      switch (direction) {
        case 'up':
          var previousBlock = block.prev();
          if (previousBlock.length) {
            previousBlock.before(block);
            this.element.trigger("blockMove");
          } else {
            var previousPage = block.closest('.forge-book-designer-page').prev();
            if (previousPage) {
              previousPage.find('main').append(block);
              this.element.trigger("blockMove");
            }
          }
        break;
        case 'down':
          var nextBlock = block.next();
          if (nextBlock.length) {
            nextBlock.after(block);
            this.element.trigger("blockMove");
          } else {
            var nextPage = block.closest('.forge-book-designer-page').next();
            if (nextPage) {
              nextPage.find('main').prepend(block);
              this.element.trigger("blockMove");
            }
          }
        break;
      }
    },
        
    _removeBlock: function (element) {
      $(element).remove();
      this.element.trigger("blockRemoved");
    },
    
    _movePage: function (page, direction) {
      switch (direction) {
        case 'up':
          var previousPage = page.prev('.forge-book-designer-page');
          if (previousPage.length) {
            previousPage.before(page);
            this.element.trigger("pageMove");
          }
        break;
        case 'down':
          var nextPage = page.next('.forge-book-designer-page');
          if (nextPage.length) {
            nextPage.after(page);
            this.element.trigger("pageMove");
          }
        break;
      }
    },
        
    _removePage: function (page) {
      $(page).remove();
      this.element.trigger("pageRemove");
    },
    
    _createTools: function () {
      var tools = $('<div>')
        .addClass('forge-book-designer-tools')
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
        .addClass('forge-book-designer-tool-group forge-book-designer-tool-group-book')
        .appendTo(tools);
      
      var pageToolGroup = $('<div>')
        .addClass('forge-book-designer-tool-group forge-book-designer-tool-group-page')
        .appendTo(tools);
      
      var blockToolGroup = $('<div>')
        .addClass('forge-book-designer-tool-group forge-book-designer-tool-group-block')
        .appendTo(tools);
      
      $('<a>') 
        .addClass('forge-book-designer-tool')
        .attr('title', this.options.locales['save-button-tooltip'])
        .click($.proxy(this._onSaveClick, this))
        .append($('<span>').addClass('fa fa-save'))
        .appendTo(bookToolGroup);
      
      $('<a>') 
        .addClass('forge-book-designer-tool')
        .attr('title', this.options.locales['print-button-tooltip'])
        .click($.proxy(this._onPrintClick, this))
        .append($('<span>').addClass('fa fa-print'))
        .appendTo(bookToolGroup);

//      $('<a>') 
//        .addClass('forge-book-designer-tool')
//        .attr('title', this.options.locales['publish-button-tooltip'])
//        .click($.proxy(this._onPublishClick, this))
//        .append($('<span>').addClass('fa fa-globe'))
//        .appendTo(bookToolGroup);
      
      this._createToolButton("add-contents", bookToolGroup, this.options.locales['add-contents-button-tooltip'], { 
        icon: 'fa fa-plus',
        items: [{
          name: this.options.locales['import-button'],
          action: 'importMaterial'
        }, {
          name: this.options.locales['add-blank-page-button'],
          action: 'addBlankPage'
        }, {
          name: this.options.locales['add-blank-block-button'],
          action: 'addBlankBlock'
        }]
      });
      
      $('<a>') 
        .addClass('forge-book-designer-tool')
        .attr('title', this.options.locales['auto-layout-button-tooltip'])
        .click($.proxy(this._onAutoLayoutClick, this))
        .append($('<span>').addClass('fa fa-magic'))
        .appendTo(bookToolGroup);
            
      $('<a>') 
        .addClass('forge-book-designer-tool')
        .attr('title', this.options.locales['styles-button-tooltip'])
        .click($.proxy(this._onStyleClick, this))
        .append($('<span>').addClass('fa fa-font'))
        .appendTo(bookToolGroup);
      
      $('<a>') 
        .addClass('forge-book-designer-tool')
        .attr('title', this.options.locales['page-types-button-tooltip'])
        .click($.proxy(this._onPageTypesClick, this))
        .append($('<span>').addClass('fa fa-bookmark-o'))
        .appendTo(bookToolGroup);
      
      this._createToolButton("change-page-type", pageToolGroup, this.options.locales['change-page-type-button-tooltip'], { 
        icon: 'fa fa-bookmark',
        items: $.map(this._pageTypes, function (pageType) {
          return {
            name: pageType.name,
            action: 'changePageType',
            id: pageType.id
          };
        })
      });
      
      this._createToolButton("move-page", pageToolGroup, this.options.locales['move-page-button-tooltip'], {
        icon: 'fa fa-arrows',
        items: [{
          icon: 'fa fa-arrow-up',
          action: 'movePage',
          direction: 'up'
        }, {
          icon: 'fa fa-arrow-down',
          action: 'movePage',
          direction: 'down'
        }]
      });
      
      $('<a>') 
        .addClass('forge-book-designer-tool')
        .attr('title', this.options.locales['remove-page-button-tooltip'])
        .click($.proxy(this._onRemovePageClick, this))
        .append($('<span>').addClass('fa fa-trash'))
        .appendTo(pageToolGroup);
      
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
      
      this._createToolButton("aligns", blockToolGroup, this.options.locales['change-block-align-button-tooltip'], {
        icon: 'fa fa-align-left',
        items: [{
          name: this.options.locales['align-left'],
          action: 'changeAlign',
          align: ''
        }, {
          name: this.options.locales['align-right'],
          action: 'changeAlign',
          align: 'right'
        }, {
          name: this.options.locales['align-center'],
          action: 'changeAlign',
          align: 'center'
        }, {
          name: this.options.locales['align-justify'],
          action: 'changeAlign',
          align: 'justify'
        }]
      });
      
      this._createToolButton("floats", blockToolGroup, this.options.locales['change-block-float-button-tooltip'], {
        icon: 'fa fa-magnet',
        items: [{
          name: this.options.locales['float-none'],
          action: 'changeAlign',
          align: ''
        }, {
          name: this.options.locales['float-left'],
          action: 'changeFloat',
          float: 'left'
        }, {
          name: this.options.locales['float-right'],
          action: 'changeFloat',
          float: 'right'
        }]
      });
      
      this._createToolButton("move-block", blockToolGroup, this.options.locales['move-block-button-tooltip'], {
        icon: 'fa fa-arrows',
        items: [{
          icon: 'fa fa-arrow-up',
          action: 'moveBlock',
          direction: 'up'
        }, {
          icon: 'fa fa-arrow-down',
          action: 'moveBlock',
          direction: 'down'
        }]
      });
      
      $('<a>') 
        .addClass('forge-book-designer-tool')
        .attr('title', this.options.locales['remove-block-button-tooltip'])
        .click($.proxy(this._onRemoveBlockClick, this))
        .append($('<span>').addClass('fa fa-trash'))
        .appendTo(blockToolGroup);
      
      blockToolGroup.find('.forge-book-designer-tool').attr({
        'data-disabled': 'true'
      });

      pageToolGroup.find('.forge-book-designer-tool').attr({
        'data-disabled': 'true'
      });
    },
    
    _createToolButton: function (toolId, toolGroup, tooltip, toolOptions) {
      var menuItems = $('<div>')
        .addClass('forge-book-designer-tool-items forge-book-designer-tool-items-text')
        .addClass('forge-book-designer-tool-items-' + toolId)
        .appendTo(toolGroup)
        .hide();
      
      this._createToolButtonItems(toolId, toolOptions.items);
     
      var menuButton = $('<div>')
        .addClass('forge-book-designer-tool')
        .attr('title', tooltip)
        .append($('<span>').addClass(toolOptions.icon))
        .appendTo(toolGroup);
      
      $(window).click(function () {
        $('.forge-book-designer-tool-items').hide();
      });
      
      menuButton.click(function (event) {
        event.stopPropagation();
        
        if ($(this).attr('data-disabled') != 'true') {
          $('.forge-book-designer-tool-items').hide();
          menuItems.show();
          menuItems.css({
            'margin-top': ((-menuItems.height() / 2) + ($(this).height() / 2)) + 'px'
          });
        }
      });
    },
    
    _createToolButtonItems: function (toolId, items) {
      var menuItems = this.element.find('.forge-book-designer-tool-items-' + toolId).empty();
      
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
          
          var element = $(this.element).find('.forge-book-designer-block-selected');
         
          switch (item.action) {
            case 'changeStyle':
              this._changePageElementStyle(element, item.element, item.className);
            break;
            case 'changeAlign':
              this._changeBlockAlign(element, item.align);
            break;
            case 'changeFloat':
              this._changeBlockFloat(element, item.float);
            break;
            case 'moveBlock':
              this._moveBlock(element, item.direction);
            break;
            case 'movePage':
              this._movePage(this.element.find('.forge-book-designer-page-selected'), item.direction);
            break;
            case 'changePageType':
              this._changePageType(this.element.find('.forge-book-designer-page-selected'), item.id, item.name);
            break;
            case 'importMaterial':
              var browser = $('<div>')
                .forgeMaterialBrowser({
                  types: ['FOLDER', 'DOCUMENT', 'IMAGE']
                })
                .on('materialSelect', $.proxy(function (event, materialSelectData) {
                  $('<div/>')
                    .bookDesignerAddContentDialog({
                      pageSelected: this.element.find('.forge-book-designer-page-selected').length > 0,
                      blockSelected: this.element.find('.forge-book-designer-block-selected').length > 0
                    })
                    .on("addContent", $.proxy(function (event, addContentData) {
                      this._loadContent(materialSelectData.type, materialSelectData.id, $.proxy(function (content) {
                        var block = null;
                        
                        switch (addContentData.position) {
                          case 'after-selection':
                            block = this.insertHtmlAfter(content, this.element.find('.forge-book-designer-block-selected')).first();
                          break;
                          case 'before-selection':
                            block = this.insertHtmlBefore(content, this.element.find('.forge-book-designer-block-selected')).first();
                          break;
                          case 'page-top':
                            block = this.prependHtmlToPage(content, this.element.find('.forge-book-designer-page-selected')).first();
                          break;
                          case 'beginning':
                            block = this.prependHtmlToPage(content, this.element.find('section').first()).first();
                          break;
                          case 'bottom':
                            block = this.appendHtmlToPage(content, this.element.find('section').last()).first();
                          break;
                        }
    
                        this._scrollToElement(block);
                        this.selectBlock(block);
                      }, this));
                    }, this));
                }, this));
            break;
            case 'addBlankPage':
              $('<div/>')
                .bookDesignerAddPageDialog({
                  pageSelected: this.element.find('.forge-book-designer-page-selected').length != 0
                })
                .on("addPage", $.proxy(function (event, data) {
                  switch (data.position) {
                    case 'last':
                      this.addPage();
                    break;
                    case 'first':
                      var firstPage = this.element.find('.forge-book-designer-page').first();
                      if (!firstPage.length) {
                        this.addPage();
                      } else {
                        this.addPageBefore(firstPage);
                      }
                    break;
                    case 'before-selected':
                      this.addPageBefore(this.element.find('.forge-book-designer-page-selected'));
                    break;
                    case 'after-selected':
                      this.addPageAfter(this.element.find('.forge-book-designer-page-selected'));
                    break;
                  }
                }, this));
            break;
            case 'addBlankBlock':
              $('<div/>')
                .bookDesignerAddContentDialog({
                  pageSelected: this.element.find('.forge-book-designer-page-selected').length != 0,
                  blockSelected: $(this.element).find('.forge-book-designer-block-selected').length > 0
                })
                .on("addContent", $.proxy(function (event, data) {
                  var block = null;
                  var content = $('<p>').html('&nbsp;');
                  
                  switch (data.position) {
                    case 'after-selection':
                      block = this.insertHtmlAfter(content, $(this.element).find('.forge-book-designer-block-selected'));
                    break;
                    case 'before-selection':
                      block = this.insertHtmlBefore(content, $(this.element).find('.forge-book-designer-block-selected'));
                    break;
                    case 'page-top':
                      block = this.prependHtmlToPage(content, this.element.find('.forge-book-designer-page-selected'));
                    break;
                    case 'beginning':
                      block = this.prependHtmlToPage(content, this.element.find('section').first());
                    break;
                    case 'bottom':
                      block = this.appendHtmlToPage(content, this.element.find('section').last());
                    break;
                  }

                  this._scrollToElement(block);
                  this.selectBlock(block);
                }, this));
            break;
          }
        }, this));
        
        menuItems.append(menuItem);
      }, this));
    },
    
    _createCss: function (callback) {
      dust.render("forge/book-designer/css", {
        styles: this.styles(),
        pageTypes: this.pageTypes()
      }, $.proxy(function(err, css) {
        if (err) {
          $('.notifications').notifications('notification', 'error', err);
        } else {
          callback(css);
        }
      }, this));
    },
    
    _refreshCss: function (callback) {
      this._createCss($.proxy(function (css) {
        $(this._styleSheet).text(css);
        if ($.isFunction(callback)) {
          callback();
        }
      }, this));
    },
    
    _selectBlock: function (block) {
      this.element
        .find('.forge-book-designer-block-selected')
        .removeClass('forge-book-designer-block-selected');
      
      $(block)
        .attr({
          'contenteditable':'true',
          'spellcheck':'false'
        })
        .addClass('forge-book-designer-block-selected');
      
      if (block && block.length && $.isFunction(block[0].focus)) {
        block[0].focus();
      }
       
      this.element.trigger("blockSelect", {
        block: block||[]
      });
    },
    
    _selectPage: function (page) {
      this.element
        .find('.forge-book-designer-page-selected')
        .removeClass('forge-book-designer-page-selected');
      
      if (page) {
        page.addClass('forge-book-designer-page-selected');
      }
      
      this.element.trigger("pageSelect", {
        page: page||[]
      });
    },
    
    _onPageClick: function (event) {
      var page = $(event.target).closest('.forge-book-designer-page');
      var block = $(event.target).closest($.map(this.options.blockTags, function (blockTag) {
        return '.forge-book-designer-page ' + blockTag; 
      }).join(','), page);

      if (!block.hasClass('forge-book-designer-block-selected')) {
        this._selectBlock(block);
      }
      
      if (!page.hasClass('forge-book-designer-page-selected')) {
        this._selectPage(page);
      }
    },
    
    _onSaveClick: function () {
      $(this.element).trigger("save", {
        "data": this.data(),
        "styles": this.styles(),
        "fonts": this.fonts(),
        "pageTypes": this.pageTypes()
      });
    },
    
    _onPrintClick: function () {
      $(this.element).trigger("print", { });
    },
    
    _onPublishClick: function () {
      var dialog = $("<div>") 
        .bookDesignerPublishTemplateDialog({})
        .on("publish", $.proxy(function (event, data) {
          dialog.bookDesignerPublishTemplateDialog('destroy').remove();

          $(this.element).trigger("publishTemplate", {
            "templateName": data.name,
            "data": this.data(),
            "styles": this.styles(),
            "fonts": this.fonts(),
            "pageTypes": this.pageTypes()
          });
        }, this));
    },
    
    _onAutoLayoutClick: function (event) {
      this.autoLayout();
    },
    
    _onStyleClick: function (event) {
      var dialog = $("<div>") 
        .bookDesignerStylesDialog({
          fonts: this.fonts(),
          styles: this.styles()
        })
        .on("applyStyles", $.proxy(function (event, data) {
          this.styles(data.styles);
          dialog.bookDesignerStylesDialog('destroy').remove();
        }, this));
    },
    
    _onPageTypesClick: function (event) {
      $("<div>") 
        .bookDesignerPageTypesDialog({
          pageTypes: this.pageTypes(),
          fonts: this.fonts()
        })
        .on("applyTypes", $.proxy(function (event, data) {
          this.pageTypes(data.types);
        }, this));
    }, 
    
    _onRemoveBlockClick: function (event) {
      var element = $(this.element).find('.forge-book-designer-block-selected');
      this._removeBlock(element);
      this.selectBlock(null);
    },
    
    _onRemovePageClick: function (event) {
      this._removePage($(this.element).find('.forge-book-designer-page-selected'));
      this.selectPage(null);
    },
    
    _onStylesChanged: function (event, data) {
      this._refreshCss($.proxy(function () {
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

        this._updateHeadersAndFooters(this.element.find('section'));
      }, this));
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
    }, 
    
    _onPageTypesChanged: function (event, data) {
      this._refreshCss($.proxy(function () {
        this._createToolButtonItems("change-page-type", $.map(data.pageTypes, function (pageType) {
          return {
            name: pageType.name,
            action: 'changePageType',
            id: pageType.id
          };
        }));
  
        var typeMap = {};
        $.each(data.pageTypes, function (index, pageType) {
          typeMap[pageType.id] = pageType;
        });
        
        var pages = this.element.find('section');
        pages.each(function (index, page) {
          var pageType = typeMap[$(page).attr('data-type-id')];
          if (!pageType) {
            $(page).attr({
              'data-type-id': data.pageTypes[0].id,
              'data-type-name': data.pageTypes[0].name
            });
          } else {
            $(page).attr('data-type-name', pageType.name);
          }
        });
  
        this._updatePageNumbers();
        this._updateHeadersAndFooters(pages);      
        
      }, this));
    },
    
    _onPageRemove: function (event, data) {
      this._updatePageNumbers();
      this._updateHeadersAndFooters(this.element.find('section'));
      this._toolsWaypoint[0].context.refresh();
    },
    
    _onPageMove: function (event, data) {
      this._updatePageNumbers();
      this._updateHeadersAndFooters(this.element.find('section'));
      this._toolsWaypoint[0].context.refresh();
    },

    _onBlockSelect: function (event, data) {
      this.element.find('.forge-book-designer-tool-group-block .forge-book-designer-tool').attr({
        'data-disabled': data.block.length ? 'false' : 'true'
      });
    },
    
    _onPageSelect: function (event, data) {
      this.element.find('.forge-book-designer-tool-group-page .forge-book-designer-tool').attr({
        'data-disabled': data.page.length ? 'false' : 'true'
      }); 
    }
  });
  
  $.widget("custom.bookDesignerStylesDialog", $.custom.bookDesignerDialog, {
    options: {
      dialogOptions: {
        width: 650
      },
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
      templateName: "forge/book-designer/styles-dialog"
    },
        
    _create : function() {
      this.option("templateOptions", {
        fonts: this.options.fonts,
        styles: $.map(this.options.styles, function (style) {
          return $.extend(style, {
            removable: ['p', 'h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'ol', 'ul'].indexOf(style.selector) == -1
          });
        })
      });
      
      this.element.on("beforeDialogOpen", $.proxy(function (event) {
        this.dialogElement.find('ul').parent()
        .tabs({ 
          beforeActivate: $.proxy(function (event, ui) {
            
            this.dialogElement.find('.forge-designer-style-dialog-style').each($.proxy(function (index, style) {
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
              
              panel.find('.forge-designer-style-dialog-style').attr({
                'data-selector': selector,
                'data-rules': '{}'
              });
              
              $(event.target).tabs("refresh");
              $(event.target).tabs("option", "active", newIndex);
            }
          }, this)
        });

        this.dialogElement.on("change", '.forge-designer-style-dialog-style-name', $.proxy(this._onNameChange, this));
        this.dialogElement.on("change", 'input', $.proxy(this._onInputChange, this));
        this.dialogElement.on("change", 'select', $.proxy(this._onSelectChange, this));
        this.dialogElement.on("click", '.forge-designer-style-dialog-style-setting-font-action-add', $.proxy(this._onAddFontClick, this));
        this.dialogElement.on("click", '.forge-designer-style-dialog-style-setting-font-action-remove', $.proxy(this._onRemoveFontClick, this));
        this.dialogElement.on("click", '.forge-designer-style-dialog-delete-style', $.proxy(this._onDeleteClick, this));
        this.dialogElement.on("click", '.forge-designer-style-dialog-restore-style', $.proxy(this._onRestoreClick, this));
      }, this));
      
      this._super();
    },
    
    styles: function () {
      return $.map(this.dialogElement.find('.ui-tabs-panel:not([id="style-tab-new"]) .forge-designer-style-dialog-style:not([data-removed="true"])'), function (style) {
        return {
          name: $(style).find('input[name="name"]').val(),
          selector: $(style).attr('data-selector'),
          rules: $.parseJSON($(style).attr('data-rules')||'{}')
        }
      });
    },
    
    _onAddFontClick: function (event) {
      $('<div>')
        .bookDesignerAddFontDialog()
        .on("addFont", $.proxy(function (event, data) {
          $('.book-designer').bookDesigner('addFont', data.name, data.url);

          $.each(this.dialogElement.find('select[name="font-family"]'), function (index, select) {
            $(select).append($('<option>').attr('value', data.name).text(data.name));
          });
        }, this));
    },
    
    _onRemoveFontClick: function (event) {
      var name = $(event.target)
        .closest('.forge-designer-style-dialog-style-setting-font')
        .find('select[name="font-family"]').val();
      
      $('<div>')
        .bookDesignerRemoveFontDialog({
          name: name
        })
        .on("removeFont", $.proxy(function (event, data) {
          $('.book-designer').bookDesigner('removeFont', data.name);
          this.dialogElement.find('select[name="font-family"] option[value="' + name + '"]').remove();
        }, this));
    },
    
    _onNameChange: function (event) {
      this.dialogElement.find('.ui-tabs-active a')
        .text($(event.target).val()||this.dialogElement.attr('data-empty-tab-label'));
    },
    
    _onDeleteClick: function (event) {
      var style = $(event.target).closest('.forge-designer-style-dialog-style');
      style.find('.forge-designer-style-dialog-delete-style').hide();
      style.find('.forge-designer-style-dialog-restore-style').show();
      style.attr('data-removed', 'true');
      
      this.dialogElement.find('.ui-tabs-active')
        .addClass('tab-to-be-removed');
    },
    
    _onRestoreClick: function (event) {
      var style = $(event.target).closest('.forge-designer-style-dialog-style');
      style.find('.forge-designer-style-dialog-delete-style').show();
      style.find('.forge-designer-style-dialog-restore-style').hide();
      style.removeAttr('data-removed');
      
      this.dialogElement.find('.ui-tabs-active')
        .removeClass('tab-to-be-removed');
    },
    
    _onInputChange: function (event) {
      var input = $(event.target);
      var style = $(input).closest('.forge-designer-style-dialog-style');
      
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
      var style = $(select).closest('.forge-designer-style-dialog-style');
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

      $(style).find('.forge-designer-style-dialog-style-preview-margin').css(marginStyles);
      $(style).find('.forge-designer-style-dialog-style-preview-padding').css(paddingStyles);
      $(style).find('.forge-designer-style-dialog-style-preview').css(previewStyles);
    },
    
    _updateRule: function (style, rule, value) {
      var rules = $.parseJSON($(style).attr('data-rules')||'{}');
      rules[rule] = value;
      $(style).attr('data-rules', JSON.stringify(rules));
      this._refreshPreview(style);
    }
    
  });
  
  $.widget("custom.bookDesignerPublishTemplateDialog", $.custom.bookDesignerDialog, {
    options: {
      dialogOptions: {
        width: 500
      },
      dialogButtons: [{
        'require-valid': true,
        'text-attribute': 'data-publish-button',
        'click': function () {
          this.element.trigger("publish", {
            name: $(this.dialogElement).find('input[name="name"]').val()
          });

          this._close(); 
        }
      }, {
        'text-attribute': 'data-cancel-button',
        'click' : function() {
          this._close();
        }
      }],
      templateName: "forge/book-designer/publish-template-dialog"
    },
        
    _create : function() {
      this._super();
    }
    
  });
  
  $.widget("custom.bookDesignerAddFontDialog", $.custom.bookDesignerDialog, {
    options: {
      dialogOptions: {
        width: 500
      },
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
      templateName: "forge/book-designer/add-font-dialog"
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
  
  $.widget("custom.bookDesignerRemoveFontDialog", $.custom.bookDesignerDialog, {
    options: {
      dialogOptions: {
        width: 500
      },
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
      templateName: "forge/book-designer/remove-font-dialog"
    },
    
    _create : function() {
      this.option("templateOptions", {
        name: this.options.name
      });
      
      this._super();
    }
    
  });
  
  $.widget("custom.bookDesignerAddPageDialog", $.custom.bookDesignerDialog, {
    options: {
      dialogOptions: {
        width: 500
      },
      dialogButtons: [{
        'require-valid': true,
        'text-attribute': 'data-add-button',
        'click': function () {
          this.element.trigger("addPage", {
            position: this.dialogElement.find('input[name="position"]:checked').val()
          });
          
          this._close(); 
        }
      }, {
        'text-attribute': 'data-cancel-button',
        'click' : function() {
          this._close();
        }
      }],
      templateName: "forge/book-designer/add-page-dialog"
    },
    
    _create : function() {
      this.option("templateOptions", {
        pageSelected: this.options.pageSelected
      });
      
      this._super();
    }
    
  });
  
  $.widget("custom.bookDesignerAddContentDialog", $.custom.bookDesignerDialog, {
    options: {
      dialogOptions: {
        width: 500
      },
      dialogButtons: [{
        'require-valid': true,
        'text-attribute': 'data-add-button',
        'click': function () {
          this.element.trigger("addContent", {
            position: this.dialogElement.find('input[name="position"]:checked').val()
          });
          
          this._close(); 
        }
      }, {
        'text-attribute': 'data-cancel-button',
        'click' : function() {
          this._close();
        }
      }],
      templateName: "forge/book-designer/add-content-dialog"
    },
    
    _create : function() {
      this.option("templateOptions", {
        pageSelected: this.options.pageSelected,
        blockSelected: this.options.blockSelected
      });
      
      this._super();
    }
    
  });
  
  $.widget("custom.bookDesignerPageTypesDialog", $.custom.bookDesignerDialog, {
    
    options: {
      dialogOptions: {
        width: 650
      },
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
      templateName: "forge/book-designer/page-types-dialog"
    },
    
    _create : function() {

      this.option("templateOptions", {
        types: this.options.pageTypes,
        fonts: this.options.fonts
      });
      
      this.element.on("beforeDialogOpen", $.proxy(function (event) {
        $.each(this.dialogElement.find('.forge-designer-page-types-dialog-type'), $.proxy(function (index, type) {
          var pageRulesAttr = $(type).attr('data-page-rules');
          if (pageRulesAttr) {
            var pageRules = $.parseJSON(pageRulesAttr);
            
            $.each(pageRules, $.proxy(function (key, value) {
              var input = $(type).find('input[name="' + key + '"]');
              if (input.length) {
                if (input.attr('type') == 'url') {
                  input.val(value.replace(/(url\()(.*)(\))/, "$2"));
                } else if (input.attr('data-unit')) {
                  input.val((new LengthUnitConverter(value)).to(input.attr('data-unit')));
                } else {
                  input.attr('value', value);
                }
              } else {
                $(type).find('input[data-style="' + key + '"][data-on="' + value + '"]').prop('checked', true);
                $(type).find('select[name="' + key + '"]').val(value);
              }
            }, this));
          }
          
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
              } else {
                this._checkDeleteState($(ui.newPanel).find('.forge-designer-page-types-dialog-type'));
              }
            }, this)
          });

        this._checkDeleteState(this.dialogElement.find('.forge-designer-page-types-dialog-type').first());
        
        this.dialogElement.on("change", '.forge-designer-page-types-dialog-type-name', $.proxy(this._onNameChange, this));
        this.dialogElement.on("change", 'input', $.proxy(this._onInputChange, this));
        this.dialogElement.on("change", 'select', $.proxy(this._onSelectChange, this));
        this.dialogElement.on("click", '.forge-designer-page-types-dialog-type-setting-font-action-add', $.proxy(this._onAddFontClick, this));
        this.dialogElement.on("click", '.forge-designer-page-types-dialog-type-setting-font-action-remove', $.proxy(this._onRemoveFontClick, this));
        this.dialogElement.on("click", '.forge-designer-page-types-dialog-delete-type', $.proxy(this._onDeleteClick, this));
        this.dialogElement.on("click", '.forge-designer-page-types-dialog-restore-type', $.proxy(this._onRestoreClick, this));
        
      }, this));
      
      this._super();
    },
    
    types: function () {
      return $.map(this.dialogElement.find('.ui-tabs-panel:not([id="type-tab-new"]) .forge-designer-page-types-dialog-type:not([data-removed="true"])'), function (type) {
        var pageRules = $(type).attr('data-page-rules');
        var headerRules = $(type).attr('data-header-rules');
        var footerRules = $(type).attr('data-footer-rules');
        
        return {
          id: $(type).attr('data-page-type-id'),
          name: $(type).find('input[name="name"]').val(),
          numberedPage: $(type).find('input[name="numbered-page"]').prop('checked'),
          pageRules: pageRules ? $.parseJSON(pageRules) : {},
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

    _updatePageRule: function (type, rule, value) {
      var rulesAttr = $(type).attr('data-page-rules');
      var rules = rulesAttr ? $.parseJSON(rulesAttr) : {};
      rules[rule] = value;
      $(type).attr('data-page-rules', JSON.stringify(rules));
    },

    _updateGroupRule: function (type, group, rule, value) {
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
    
    _checkDeleteState: function (type) {
      var typeCount = this.dialogElement.find('.ui-tabs-nav li:not(.tab-to-be-removed) a:not([href="#type-tab-new"])').length;
      if (typeCount <= 1) {
        $(type).find('.forge-designer-page-types-dialog-delete-type').attr('disabled', 'disabled');
      } else {
        $(type).find('.forge-designer-page-types-dialog-delete-type').removeAttr('disabled');
      }
    },
    
    _onDeleteClick: function (event) {
      var type = $(event.target).closest('.forge-designer-page-types-dialog-type');
      type.find('.forge-designer-page-types-dialog-delete-type').hide();
      type.find('.forge-designer-page-types-dialog-restore-type').show();
      type.attr('data-removed', 'true');
      
      this.dialogElement.find('.ui-tabs-active')
        .addClass('tab-to-be-removed');
    },
    
    _onRestoreClick: function (event) {
      var type = $(event.target).closest('.forge-designer-page-types-dialog-type');
      type.find('.forge-designer-page-types-dialog-delete-type').show();
      type.find('.forge-designer-page-types-dialog-restore-type').hide();
      type.removeAttr('data-removed');
      
      this.dialogElement.find('.ui-tabs-active')
        .removeClass('tab-to-be-removed');
      
      this._checkDeleteState(type);
    },
    
    _onNameChange: function (event) {
      this.dialogElement.find('.ui-tabs-active a')
        .text($(event.target).val()||this.dialogElement.attr('data-empty-tab-label'));
    },
    
    _onAddFontClick: function (event) {
      $('<div>')
        .bookDesignerAddFontDialog()
        .on("addFont", $.proxy(function (event, data) {
          $('.book-designer').bookDesigner('addFont', data.name, data.url);

          $.each(this.dialogElement.find('select[name*="font-family"]'), function (index, select) {
            $(select).append($('<option>').attr('value', data.name).text(data.name));
          });
        }, this));
    },
    
    _onRemoveFontClick: function (event) {
      var name = $(event.target)
        .closest('.forge-designer-page-types-dialog-type-setting-font')
        .find('select[name*="font-family"]').val();
      
      $('<div>')
        .bookDesignerRemoveFontDialog({
          name: name
        })
        .on("removeFont", $.proxy(function (event, data) {
          $('.book-designer').bookDesigner('removeFont', data.name);
          this.dialogElement.find('select[name*="font-family"] option[value="' + name + '"]').remove();
        }, this));
    },

    _onInputChange: function (event) {
      var input = $(event.target);
      var type = input.closest('.forge-designer-page-types-dialog-type');
      var name = input.attr('name');
      var grouped = name.indexOf(':') > -1;
      var nameParts = name.split(':');
        
      if (input.attr('type') == 'checkbox') {
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
        if (grouped) {
          this._updateGroupRule(type, nameParts[0], input.attr('data-style'), value);
        } else {
          this._updatePageRule(type, input.attr('data-style'), value);
        }
      } if (input.attr('type') == 'url') { 
        if (grouped) {
          this._updateGroupRule(type, nameParts[0], nameParts[1], 'url(' + input.val() + ')');
        } else {
          this._updatePageRule(type, name, 'url(' + input.val() + ')');
        }
      } else {
        if (grouped) {
          this._updateGroupRule(type, nameParts[0], nameParts[1], input.val() + (input.attr('data-unit')||''));
        } else {
          this._updatePageRule(type, name, input.val() + (input.attr('data-unit')||''));
        }
      }
    },
    
    _onSelectChange: function (event) {
      var select = $(event.target);
      var name = select.attr('name');
      var grouped = name.indexOf(':') > -1;
      var nameParts = name.split(':');
      var type = select.closest('.forge-designer-page-types-dialog-type');
      
      if (grouped) {
        this._updateGroupRule(type, nameParts[0], nameParts[1], select.val() + (select.attr('data-unit')||''));
      } else {
        this._updatePageRule(type, nameParts[0], select.val() + (select.attr('data-unit')||''));
      }
    }
    
  });

  $.widget("custom.bookDesignerChooseTemplateDialog", $.custom.bookDesignerDialog, {
    options: {
      closable: false,
      dialogOptions: {
        closeOnEscape: false,
        width: 500
      },
      dialogButtons: [],
      templateName: "forge/book-designer/choose-template-dialog"
    },
        
    _create : function() {
      this._super();
      
      this.element.on("beforeDialogOpen", $.proxy(function (event) {
        this.dialogElement.on("click", '.template', $.proxy(function (event) {
          this.element.trigger("choose", {
            id: $(event.target).attr('data-id')
          });
        }, this));
      }, this));
    }
    
  });
  
  
  $(document).ready(function () {
    if ($('.book-use-template').val() == 'true') {
      var materialClient = new $.RestClient(CONTEXTPATH + '/rest/material/');
      materialClient.add("bookTemplates");
      
      materialClient.bookTemplates.read({
        'publicity': 'PUBLIC'
      }).done(function (templates, message, xhr){
        if (xhr.status !== 200) {
          $('.notifications').notifications('notification', 'error', message);
        } else {
          $('<div/>')
            .bookDesignerChooseTemplateDialog({
              templateOptions: {
                templates: templates
              }
            })
            .on("choose", $.proxy(function (event, data) {
              $('.book-design-template-id').val(data.id);
              $('.book-design-apply-template')[0].click();
            }, this));
        }
      });
    } else {
      var locales = {};
      var localeKeys = [
        'save-button-tooltip',
        'print-button-tooltip',
        'publish-button-tooltip',
        'add-contents-button-tooltip',
        'import-button',
        'add-blank-block-button',
        'add-blank-page-button',
        'auto-layout-button-tooltip',
        'styles-button-tooltip',
        'page-types-button-tooltip',
        'move-page-button-tooltip',
        'remove-page-button-tooltip',
        'change-page-type-button-tooltip',
        'change-block-style-button-tooltip',
        'change-block-align-button-tooltip',
        'change-block-align-button-tooltip',
        'align-left',
        'align-right',
        'align-center',
        'align-justify',
        'change-block-float-button-tooltip',
        'float-none',
        'float-left',
        'float-right',
        'move-block-button-tooltip',
        'remove-block-button-tooltip'
      ];
      
      if ($('.book-designer').length) {
        $.each(localeKeys, $.proxy(function (index, localeKey) {
          locales[localeKey] = this.attr('data-' + localeKey);
        }, $('.book-designer')));
        
        var fonts = $('.book-design-fonts').val();
        var styles = $('.book-design-styles').val();
        var data = $('.book-design-data').val();
        var pageTypes = $('.book-design-page-types').val();
        
        $('.book-designer')
          .bookDesigner({
            locales: locales,
            fonts: fonts,
            styles: styles,
            pageTypes: pageTypes,
            data: data
          })
          .on("save", function (event, data) {
            $('.book-design-fonts').val(JSON.stringify(data.fonts));
            $('.book-design-styles').val(JSON.stringify(data.styles));
            $('.book-design-page-types').val(JSON.stringify(data.pageTypes));
            $('.book-design-data').val(data.data);
            $('.book-design-save')[0].click();
          })
          .on("print", function (event, data) {
            window.location = CONTEXTPATH + '/forge/bookDesignPdf/' + $('.book-design-id').val();
          })
          .on("publishTemplate", function (event, data) {
            $('.book-design-template-name').val(data.templateName);
            $('.book-design-fonts').val(JSON.stringify(data.fonts));
            $('.book-design-styles').val(JSON.stringify(data.styles));
            $('.book-design-page-types').val(JSON.stringify(data.pageTypes));
            $('.book-design-data').val(data.data);
            $('.book-design-publish-template')[0].click();
          });
      } else {
        var fonts = $('.book-design-fonts').val();
        var styles = $('.book-design-styles').val();
        var pageTypes = $('.book-design-page-types').val();
        
        dust.render("forge/book-designer/css", {
          fonts: fonts ? $.parseJSON(fonts) : [],
          styles: styles ? $.parseJSON(styles) : [],
          pageTypes: pageTypes ? $.parseJSON(pageTypes) : []
        }, function(err, css) {
          if (err) {
            $('.notifications').notifications('notification', 'error', err);
          } else {
            $('<style>')
              .attr({ 'type': 'text/css' })
              .text(css)
              .appendTo(document.head);
          }
        });
        
      }
    }
  });
  
}).call(this);