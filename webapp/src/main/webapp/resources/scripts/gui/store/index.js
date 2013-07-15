(function() {
  
  function showError(jqXHR, textStatus, errorThrown) {
    alert(errorThrown);
  }
  
  function reloadProductList() {
    loadProductList($('#store-index').data('category-type'), $('#store-index').data('tag'));
  }
  
  function loadProductList(categoryType, tag) {
    var api = getFnIApi();
    
    $('#store-index').data('category-type', categoryType);
    $('#store-index').data('tag', tag);

    switch (categoryType) {
      case 'recent':
        api.store(false).products.read()
          .done(function (data) {
            renderProductList('Most Recent Products', data);
          })
          .error(function (jqXHR, textStatus, errorThrown) {
            showError(jqXHR, textStatus, errorThrown);
          });
      break;
      case 'unpublished':
        api.store(false).products.read({published:false})
          .done(function (data) {
            renderProductList('Unpublished Products', data);
          })
          .error(function (jqXHR, textStatus, errorThrown) {
            showError(jqXHR, textStatus, errorThrown);
          });
      break;
      case 'tag':
        api.store(false).products.read({tag:tag})
          .done(function (data) {
            renderProductList('Products tagged as \'' + tag + "'", data);
          })
          .error(function (jqXHR, textStatus, errorThrown) {
            showError(jqXHR, textStatus, errorThrown);
          });
      break;
    }
  }
  
  function renderProductList(title, data) {
    var api = getFnIApi();
    
    renderDustTemplate('/store/dust/productlist.dust', {
      locale: LOCALE,
      title: title,
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
      
      api.batch(imageRequests)
        .done(function (data) {
          $.each(data, function (productId, images) {
            var galleryItems = new Array();
  
            $.each(images, function (index, image) {
              var thumbnailUrl = api.basePath + image.downloadUrl + '?width=32&height=32';
              var imageUrl = api.basePath + image.downloadUrl;
              var thumbnailsContainer = $('.store-product[data-product-id="' + productId + '"] .store-product-thumbnails-container');
              thumbnailsContainer.has('img').show();
              
              $('<div class="store-product-thumbnail-container"><a href="javascript:void(null)"><img src="{1}" data-url="{0}"/></a></div>'.replace("{0}", imageUrl).replace("{1}", thumbnailUrl))
                .appendTo(thumbnailsContainer.find('.store-product-thumbnails-inner-container'));
              
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
        })
        .error(function (jqXHR, textStatus, errorThrown) {
          showError(jqXHR, textStatus, errorThrown);
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
    getCheckboxNumberList: function (form, name) {
      var result = new Array();
      
      var inputElements = form.find('input[name="' + name + '"]'); 
      $.each(inputElements, function (index, inputElement) {
        if ($(inputElement).prop('checked'))
          result.push(parseInt($(inputElement).val()));
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
    
    $('#store-categories .store-category a').click(function (e) {
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
     * Product Admin Actions / Publish Product
     */
    $(document).on('click', '.store-product-add-to-cart-action', function (event) {
      var productId = $(this).closest('.store-product').data('product-id');
      var operatorForm = $('#shopping-cart-operator-container form');
      var prefix = operatorForm.attr('name');
      operatorForm.find('input[name="' + prefix + ':productId"]').val(productId);
      operatorForm.find('input[type="submit"]').click();
    });

    /**
     * Product Admin Actions / Publish Product
     */
    $(document).on('click', '.product-publish', function (event) {
      var productId = $(this).data('product-id');

      api.store(false).products.read(productId)
        .done(function (product) {
          renderDustTemplate('/store/dust/productpublishdialog.dust', {
            title: product.names[LOCALE]
          }, function(err, out) {
            if (err) {
              // Proper error handling
              alert(err);
            } else {
              var buttons = new Object();
              
              buttons['Publish'] = function() {
                product.published = true;
                api.store(true).products.update(productId, product)
                  .done($.proxy(function (data) {
                    loadProductList('recent');
                    $(this).dialog("close");
                  }, this))
                  .error(function (jqXHR, textStatus, errorThrown) {
                    showError(jqXHR, textStatus, errorThrown);
                  });
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
            }
          });
        })
        .error(function (jqXHR, textStatus, errorThrown) {
          showError(jqXHR, textStatus, errorThrown);
        });
    });
    
    /**
     * Product Admin Actions / Unpublish Product
     */
    $(document).on('click', '.product-unpublish', function (event) {
      var productId = $(this).data('product-id');

      api.store(false).products.read(productId)
        .done(function (product) {
          renderDustTemplate('/store/dust/productunpublishdialog.dust', {
            title: product.names[LOCALE]
          }, function(err, out) {
            if (err) {
              // Proper error handling
              alert(err);
            } else {
              var buttons = new Object();
              
              buttons['Unpublish'] = function() {
                product.published = false;
                api.store(true).products.update(productId, product)
                  .done($.proxy(function (data) {
                    loadProductList('unpublished');
                    $(this).dialog("close");
                  }, this))
                  .error(function (jqXHR, textStatus, errorThrown) {
                    showError(jqXHR, textStatus, errorThrown);
                  });
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
            }
          });
        })
        .error(function (jqXHR, textStatus, errorThrown) {
          showError(jqXHR, textStatus, errorThrown);
        });
    });
    
    /**
     * Product Admin Actions / Attach book file
     */
    $(document).on('click', '.product-attach-book-file-action', function (e) {
      
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
        }
      });
    });
    
    /**
     * Product Admin Actions / Edit Product
     */
    $(document).on('click', '.product-edit', function (event) {
      var productId = $(this).data('product-id');
      
      api.batch({
        localizedLanguages: api.system(false).languages.read({ localized : true }),
        product: api.store(false).products.read(productId),
        tags: api.store(false).tags.read()
      })
      .done(function (data) {
        renderDustTemplate('/store/dust/editproductdialog.dust', {
          locale: LOCALE,
          localizedLanguages: data.localizedLanguages,
          product: data.product,
          tags: data.tags
        }, function(err, out) {
          if (err) {
            // TODO: Proper error handling
            alert(err);
          } else {
            var buttons = new Object();
            
            buttons['Save'] = function() {
              var form = $(this).find('form');
              
              var type = data.product.type;
              var languages = $.map(data.localizedLanguages, function(localizedLanguage) { 
                return localizedLanguage["iso2"]; 
              });

              var product = {
                id: data.product.id,
                type: type,
                price: valueExtractor.getInputFloat(form, 'price'),
                names: valueExtractor.getPostfixMap(form, 'name-', languages),
                descriptions: valueExtractor.getPostfixMap(form, 'description-', languages),
                tags: valueExtractor.getTextList(form, 'tags'),
                published: false,
                requiresDelivery: valueExtractor.getCheckboxValue(form, 'requires-delivery')
              };

              switch (type) {
                case 'BOOK':
                  product['downloadable'] = valueExtractor.getCheckboxValue(form, 'downloadable');
                break;
                case 'PREMIUM_ACCOUNT':
                  product['months'] = valueExtractor.getInputInt(form, 'months');
                break;
              }
              
              api.store(true).products.update(data.product.id, product).done($.proxy(function () {
                $(this).dialog("close");
                reloadProductList();
              }, this));
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
            
            $(dialog).on("click", 'a.remove-tag', function (event) {
              $(this).closest('.tag').remove();
            });
            
            var createTagElement = function (tagsElement, inputText) {
              var text = inputText.toLowerCase();
              var existing = tagsElement.find('input[value="' + text + '"]');
              if (existing.length == 0) {
                $('<span class="tag"><span>{0}</span><input name="tags" type="hidden" value="{1}"/><a class="remove-tag" href="javascript:void(null);"></a></span>'.replace('{0}', text).replace('{1}', text))
                  .appendTo(tagsElement);
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
      })
      .error(function (jqXHR, textStatus, errorThrown) {
        showError(jqXHR, textStatus, errorThrown);
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
    $(document).on('click', '.product-edit-details', function (e) {
      var productId = $(this).data('product-id');
      api.batch({
        productDetails: api.store(false).products.details.read(productId),
        storeDetails: api.store(false).details.read()
      })
      .done(function (data) {
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
          }
        });
      })
      .error(function (jqXHR, textStatus, errorThrown) {
        showError(jqXHR, textStatus, errorThrown);
      });
    });
    
    /**
     * Product Admin Actions / Delete Product
     */
    $(document).on('click', '.product-delete', function (event) {
      var productId = $(this).data('product-id');
      var productTitle = $(this).closest('.store-product').find('.storeProductListProductName').html();
      
      renderDustTemplate('/store/dust/productdeletedialog.dust', {
        title: productTitle
      }, function(err, out) {
        if (err) {
          // Proper error handling
          alert(err);
        } else {
          var buttons = new Object();
          
          buttons['Delete'] = function() {
            api.store(false).products.destroy(productId)
              .done($.proxy(function (data) {
                reloadProductList();
                $(this).dialog("close");
              }, this))
              .error(function (jqXHR, textStatus, errorThrown) {
                showError(jqXHR, textStatus, errorThrown);
              });
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
    $('#store-admin-panel>a').click(function (e) {
      api.batch({
        localizedLanguages: api.system(false).languages.read({ localized : true }),
        tags: api.store(false).tags.read()
      })
      .done(function (data) {
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
                tags: valueExtractor.getTextList(form, 'tags'),
                requiresDelivery: valueExtractor.getCheckboxValue(form, 'requires-delivery')
              };
              
              switch (type) {
                case 'BOOK':
                  product['downloadable'] = valueExtractor.getCheckboxValue(form, 'downloadable');
                break;
                case 'PREMIUM_ACCOUNT':
                  product['months'] = valueExtractor.getInputInt(form, 'months');
                break;
              }
              
              api.store(true).products.create(product)
                .done($.proxy(function (data) {
                  $(this).dialog("close");
                  loadProductList('unpublished');
                }, this))
                .error(function (jqXHR, textStatus, errorThrown) {
                  showError(jqXHR, textStatus, errorThrown);
                });
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
            
            $(dialog).on("click", 'a.remove-tag', function (event) {
              $(this).closest('.tag').remove();
            });
            
            var createTagElement = function (tagsElement, inputText) {
              var text = inputText.toLowerCase();
              var existing = tagsElement.find('input[value="' + text + '"]');
              if (existing.length == 0) {
                $('<span class="tag"><span>{0}</span><input name="tags" type="hidden" value="{1}"/><a class="remove-tag" href="javascript:void(null);"></a></span>'.replace('{0}', text).replace('{1}', text))
                  .appendTo(tagsElement);
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
      })
      .error(function (jqXHR, textStatus, errorThrown) {
        showError(jqXHR, textStatus, errorThrown);
      });
    });
  });
  
}).call(this);

