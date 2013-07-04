(function() {
  
  function reloadProductList() {
    loadProductList($('#store-index').data('category-type'), $('#store-index').data('tag'));
  }
  
  function loadProductList(categoryType, tag) {
    var api = getFnIApi();
    
    $('#store-index').data('category-type', categoryType);
    $('#store-index').data('tag', tag);

    switch (categoryType) {
      case 'recent':
        api.store(false).products.read().done(function (data) {
          renderProductList(data);
        });
      break;
      case 'unpublished':
        api.store(false).products.read({published:false}).done(function (data) {
          renderProductList(data);
        });
      break;
      case 'tag':
        api.store(false).products.read({tag:tag}).done(function (data) {
          renderProductList(data);
        });
      break;
    }
  }
  
  function renderProductList(data) {
    var api = getFnIApi();
    
    renderDustTemplate('/store/dust/productlist.dust', {
      locale: LOCALE,
      baseUrl: api.basePath,
      products: data
    }, function(err, out) {
      $('#store-index').html(out);
      
      /**
       * Thumbnails
       */
      
      var imageRequests = new Object();
      $.each(data, function (index, product) {
        if (product.defaultImage != null) {
          imageRequests[product.id] = api.store(false).products.images.read(product.id);
        }
      });
      
      api.batch(imageRequests).done(function (err, data) {
        $.each(data, function (productId, images) {
          var galleryItems = new Array();

          $.each(images, function (index, image) {
            var thumbnailUrl = api.basePath + image.downloadUrl + '?width=32&height=32';
            var imageUrl = api.basePath + image.downloadUrl;
            var thumbnailsContainer = $('.store-product[data-product-id="' + productId + '"] .store-product-thumbnails-container');
            thumbnailsContainer.has('img').show();
            
            $('<div class="store-product-thumbnail-container"><a href="javascript:void(null)"><img src="{1}" data-url="{0}"/></a></div>'.replace("{0}", imageUrl).replace("{1}", thumbnailUrl))
              .appendTo(thumbnailsContainer);
            
            galleryItems.push({
              src: imageUrl
            });
          });
          
          /**
           * Image popup
           */
          
          $('.store-product[data-product-id="' + productId + '"] .store-product-images-container a').magnificPopup({ 
            type: 'image',
            gallery: {
              enabled: true
            },
            items: galleryItems
          });
        });
      });
    });
  }
  
  var valueExtractor = {
    getInputValue: function (form, name) {
      var inputElement = form.find('input[name="' + name + '"]'); 
      if (inputElement.length == 0) {
        inputElement = form.find('select[name="' + name + '"]'); 
      }
      
      return inputElement.length == 0 ? null : $(inputElement).val();
    },
    getInputInt: function (form, name) {
      var value = this.getInputValue(form, name);
      if (value !== null) {
        return parseInt(value);
      }
      
      return null;
    },
    getInputFloat: function (form, name) {
      var value = this.getInputValue(form, name);
      if (value !== null) {
        return parseFloat(value.replace(',', '.'));
      }
      
      return null;
    },
    getCheckboxValue: function (form, name) {
      var inputElement = form.find('input[name="' + name + '"]'); 
      return inputElement.prop('checked');
    },
    getTextList: function (form, name) {
      var result = new Array();
      
      var inputElements = form.find('input[name="' + name + '"]'); 
      $.each(inputElements, function (index, inputElement) {
        result.push($(inputElement).val());
      });
      
      return result;
    },
    getPostfixMap: function (form, prefix, postfixes) {
      var result = new Object();
      var _this = this;
      $.each(postfixes, function (index, postfix) {
        result[postfix] = _this.getInputValue(form, prefix + postfix);
      });
      return result;
    }
  };
  
  $(document).ready(function(){
    var api = getFnIApi();

    /**
     * Product list
     */
    loadProductList('recent', '');
 
    /**
     * Category links
     */
    
    $('#storeCategories .storeCategory a').click(function (e) {
      loadProductList($(this).data('category-type'), $(this).data('tag'));
    });
    
    /**
     * Product tags
     */
    
    $(document).on('click', '.store-product-tag a', function (event) {
      event.preventDefault();
      loadProductList('tag', $(this).data('tag'));
    });
    
    /**
     * Product Admin Actions / Attach book file
     */
    $(document).on('click', '.attach-book-file-action', function (e) {
      
      var productId = $(this).data('product-id');
      var uploadUrl = getFnIApi().basePath + '/store/products/' + productId + '/files';
      
      renderDustTemplate('/store/dust/attachbookfiledialog.dust', {
        url: uploadUrl
      }, function(err2, out) {
        if (err2) {
          // Proper error handling
          alert(err2);
        } else {
          var buttons = {
          };
          
          buttons['Cancel'] = function() {
            $(this).dialog("close");
          };
          
          var dialog = $(out).dialog({
            modal: true,
            width: 500,
            maxHeight: 600,
            buttons: buttons
          });
          /***
          dialog.find('input[name="file"]').fileupload({
            dataType: 'json',
            progressall: function (e, data) {
              var progress = parseInt(data.loaded / data.total * 100, 10);
              dialog.find('#progress .bar').css(
                'width',
                progress + '%'
              );
              dialog.find('.progress .text').html(progress + ' %');
            },
            done: function (e, data) {
              $(dialog).dialog("close");
            }
          }); 
          ***/
        }
      });
    });
    
    /**
     * Product Admin Actions / Add product images
     */
    $(document).on('click', '.product-add-images-action', function (e) {
      var productId = $(this).data('product-id');
      var uploadUrl = getFnIApi().basePath + '/store/products/' + productId + '/images';

      renderDustTemplate('/store/dust/addproductimagesdialog.dust', {
        url: uploadUrl
      }, function(err2, out) {
        if (err2) {
          // Proper error handling
          alert(err2);
        } else {
          var buttons = {
          };
          
          buttons['Close'] = function() {
            $(this).dialog("close");
          };
          
          var dialog = $(out).dialog({
            modal: true,
            width: 500,
            maxHeight: 600,
            buttons: buttons
          });
          
          dialog.find('input[name="files"]').fileupload({
            dataType: 'json',
            progressall: function (e, data) {
              var progress = parseInt(data.loaded / data.total * 100, 10);
              dialog.find('#progress .bar').css(
                'width',
                progress + '%'
              );
              dialog.find('.progress .text').html(progress + ' %');
            },
            disableImageResize: /Android(?!.*Chrome)|Opera/.test(window.navigator && navigator.userAgent),
            imageMaxWidth: 800,
            imageMaxHeight: 800,
            previewCrop: true,
            done: function (e, data) {
              $(dialog).dialog("close");
              reloadProductList();
            }
          }); 
        }
      });
    });
    
    /**
     * Product Admin Actions / Edit Product Details
     */
    $(document).on('click', '.edit-details', function (e) {
      var productId = $(this).data('product-id');
      api.batch({
        productDetails: api.store(false).products.details.read(productId),
        storeDetails: api.store(false).details.read()
      }).done(function (err, data) {
        if (err) {
          // Proper error handling
          alert(err);
        } else {
          renderDustTemplate('/store/dust/productdetailsdialog.dust', data, function(err2, out) {
            if (err2) {
              // Proper error handling
              alert(err2);
            } else {
              var buttons = { };
             
              buttons['Save'] = function() {
                var productDetails = new Object();
                $.each(dialog.find('.edit-product-details-table').find('input'), function (index, input) {
                  productDetails[$(input).attr('name')] = $(input).val();
                });
                
                api.store(true).products.details.update(productId, productDetails).done($.proxy(function (data) {
                  $(this).dialog("close");
                  reloadProductList();
                }, this));
              };
              
              buttons['Close'] = function() {
                $(this).dialog("close");
              };
              
              var dialog = $(out).dialog({
                modal: true,
                width: 500,
                maxHeight: 600,
                buttons: buttons
              });
              
              $.each(data.storeDetails, function (index, storeDetail) {
                var name = storeDetail.name;
                var value = data.productDetails[name]||'';
                $('<tr><td>{0}</td><td><input name="{1}" type="text" value="{2}" class="max-width"/></td></tr>'.replace('{0}', name).replace('{1}', name).replace('{2}', value))
                  .appendTo(dialog.find('.edit-product-details-table'));
              });
              
              /**
              var detailsTable = dialog.find('.edit-product-details-table').dataTable( {
                "bPaginate": false,
                "bFilter": false,
                "bSort": false,
                "bInfo": false
              });
              
              console.log(data);
              **/
              /**
              
              var addRow = function () {
                var index = detailsTable.dataTable().fnAddData([
                  '<input type="text" class="max-width" name="name"/>',
                  '<input type="text" class="max-width" name="value"/>'   
                ])[0]; 
              };
              
              
              
              
                                                            
                                                            var row = detailsTable.dataTable().fnGetNodes(index);
                                                            $(row).find('input[name="name"]').autocomplete({
                                                              source: availableTags
                                                            });
              
              dialog.find('.edit-product-details-add').click(function (event) {
              });
              
              **/
            }
          });
        }
      });
    });
    
    /**
     * Thumbnail images
     */
    
    $(document).on('mouseenter', '.store-product-thumbnails-container img', function (e) {
      var product = $(this).closest('.store-product');
      var productId = product.data('product-id');
      var imageUrl = $(this).data('url');
      var thumbnailUrl = imageUrl + '?width=128&height=128';
      
      $('.store-product[data-product-id="' + productId + '"] .store-product-image-container a').attr("href", imageUrl);
      $('.store-product[data-product-id="' + productId + '"] .store-product-image-container img').attr("src", thumbnailUrl);
    });
    
    /**
     * Admin / Create Product
     */
    $('#storeAdminPanel>a').click(function (e) {
      api.batch({
        localizedLanguages: api.system(false).languages.read({ localized : true }),
        tags: api.store(false).tags.read()
      }).done(function (err, data) {
        if (err) {
          // Proper error handling
          alert(err);
        } else {
          renderDustTemplate('/store/dust/newproductdialog.dust', data, function(err2, out) {
            if (err2) {
              // Proper error handling
              alert(err2);
            } else {
              var buttons = {
              };
              
              var languages = $.map(data.localizedLanguages, function(localizedLanguage) { 
                return localizedLanguage["iso2"]; 
              });
              
              buttons['Create'] = function() {
                var form = $(this).find('form');
                
                var type = valueExtractor.getInputValue(form, 'type');
                
                var product = {
                  type: type,
                  price: valueExtractor.getInputFloat(form, 'price'),
                  names: valueExtractor.getPostfixMap(form, 'name-', languages),
                  descriptions: valueExtractor.getPostfixMap(form, 'description-', languages),
                  tags: valueExtractor.getTextList(form, 'tags')
                };
                
                switch (type) {
                  case 'BOOK':
                    product['downloadable'] = valueExtractor.getCheckboxValue(form, 'downloadable');
                  break;
                  case 'PREMIUM_ACCOUNT':
                    product['months'] = valueExtractor.getInputInt(form, 'months');
                  break;
                }
                
                api.store(true).products.create(product);
              };
              
              buttons['Cancel'] = function() {
                $(this).dialog("close");
              };
              
              var dialog = $(out).dialog({
                modal: true,
                width: 500,
                maxHeight: 600,
                buttons: buttons
              });
              
              dialog.find('select[name="type"]').change(function (event) {
                $(this.form).find('div[data-type]').hide();
                $(this.form).find('div[data-type="' + $(this).val() + '"]').show();
              });
              
              var createTagElement = function (tagsElement, inputText) {
                var text = inputText.toLowerCase();
                
                var existing = tagsElement.find('input[value="' + text + '"]');
                if (existing.length == 0) {
                  var tagElement = $('<span class="tag">{0}<input name="tags" type="hidden" value="{1}"/></span>'.replace('{0}', text).replace('{1}', text));
                  var removeLink = $('<a class="remove-tag" href="javascript:void(null);"></a>');
                  removeLink.appendTo(tagElement);
                  
                  removeLink.click(function () {
                    tagElement.remove();
                  });
                  
                  tagElement.appendTo(tagsElement);
                }
              };
              
              dialog.find('select[name="tag"]').change(function (event) {
                var text = $(this).val();
                if (text == 'new') {
                  $(this.form).find('.new-tag-container').show();
                } else {
                  $(this.form).find('.new-tag-container').hide();
                  
                  var section = $(this).closest('.dialog-section');
  
                  if (text) {
                    createTagElement(section.find('.tags'), text);
                  }
                }
              });
              
              dialog.find('a.add-new-tag').click(function (event) {
                var section = $(this).closest('.dialog-section');
                var tagInput = section.find('input[name="new-tag"]');
                var text = tagInput.val();
                if (text) {
                  createTagElement(section.find('.tags'), text);
                  tagInput.val('');
                }
              });
              
            }
          });
        }
      });
  
    });
  });
  
}).call(this);

