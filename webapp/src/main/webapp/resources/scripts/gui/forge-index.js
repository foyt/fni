(function() {
  'use strict';

  $(document).on('click', '.forge-menu,.forge-menu>a', function () {
    $(this).closest('.forge-menu')
      .addClass('forge-menu-open');
  });
  
  $(document).on('click', '.forge-material-icon', function () {
    $(this).closest('.forge-material')
      .addClass('forge-material-open');
  });

  $(document).on('click',  function (event) {
    if ($(event.target).closest('.forge-menu-open').length == 0) {
      $('.forge-menu-open').removeClass('forge-menu-open');
    }
    
    if ($(event.target).closest('.forge-material-open').length == 0) {
      $('.forge-material-open').removeClass('forge-material-open');
    }
  });
  
  function search() {
    var input = $('.forge-search');
    var query = $.trim(input.val());
    if (query) {
      if (input.data('searching') != true) {
        input.data('searching', true);
        $('.forge-search-loading').show();
        $.ajax({
          url : CONTEXTPATH + "/search/",
          data : {
            q: query,
            source: 'FORGE'
          },
          success : function(data) {
            input.data('searching', false);
            $('.forge-search-loading').hide();
            
            dust.render("forge-search", data, function(err, html) {
              if (!err) {
                $('.forge-search-result').show().html(html);
              } else {
                // TODO: Proper error handling...
                alert(err);
              }
            }); 
            
            if (query != $.trim(input.val())) {
              search();
            }
          }
        });
      }
    } else {
      $('.forge-search-result').hide();
    }
  }

  $(document).on('keyup', '.forge-search', function (event) {
    search();
  });
  
  $(document).on('mouseup', function (event) {
    var target = event.target;
    if ($(target).closest('.forge-search-container').length == 0) {
      $('.forge-search-result').hide(); 
    }
  });
  
  $(document).on('click', '.forge-materials-list .forge-material-title', function (event) {
    $('.forge-material-selected').removeClass('forge-material-selected');
    $(this).closest('.forge-material').addClass('forge-material-selected');
  });
  
  $(document).on('click', '.forge-material-action-delete a', function (event) {
    var materialId = $(this).data('material-id');
    var actionForm = $('#forge-action-form-container form');
    var prefix = actionForm.attr('name');

    dust.render("forge-remove-material", {
      materialName: $(this).closest('.forge-material').find('.forge-material-title').attr('title'),
      materialNameShort: $.trim($(this).closest('.forge-material').find('.forge-material-title').text())
    }, function(err, html) {
      if (!err) {
        var dialog = $(html);
        dialog.dialog({
          modal: true,
          width: 400,
          buttons: [{
            'text': dialog.data('remove-button'),
            'click': function(event) { 
              $('input[name="' + prefix + ':material-id' + '"]').val(materialId);
              $('input[name="' + prefix + ':delete' + '"]').click();
            }
          }, {
            'text': dialog.data('cancel-button'),
            'click': function(event) { 
              $(this).dialog("close");
            }
          }]
        });
      } else {
       // TODO: Proper error handling...
        alert(err);
      }
    });
  });
  
  $(document).on('click', '.forge-material-action-print-pdf a', function (event) {
    var materialId = $(this).data('material-id');
    dust.render("forge-print-material-pdf", {
    }, function(err, html) {
      if (!err) {
        var dialog = $(html);
        dialog.dialog({
          modal: true,
          width: 400,
          buttons: [{
            'text': dialog.data('print-button'),
            'click': function(event) { 
              switch (dialog.find('input[name="print-style"]:checked').val()) {
                case 'download':
                  $(this).dialog("close");
                  window.location.href = CONTEXTPATH + '/forge/pdf/' + materialId;
                break;
                case 'file':
                  var actionForm = $('#forge-action-form-container form');
                  var prefix = actionForm.attr('name');
                  $('input[name="' + prefix + ':material-id' + '"]').val(materialId);
                  $('input[name="' + prefix + ':print-file' + '"]').click();
                break;
              }
            }
          }, {
            'text': dialog.data('cancel-button'),
            'click': function(event) { 
              $(this).dialog("close");
            }
          }]
        });
      } else {
       // TODO: Proper error handling...
        alert(err);
      }
    });
  });
  
  $(document).on('click', '.forge-material-action-move a', function (event) {
    var materialId = $(this).data('material-id');
    var actionForm = $('#forge-action-form-container form');
    var prefix = actionForm.attr('name');
    
    $.ajax({
      url : CONTEXTPATH + "/forge/folderbrowser/",
      success : function(result) {
    	dust.render("forge-move-material", {
      	  materialId: materialId,
	      parents: result.parents,
	      folders: result.folders
	    }, function(err, html) {
	      if (!err) {
	        var dialog = $(html);
	        $(dialog).data('folder-id', null);
	        
	        dialog.dialog({
	          modal: true,
	          width: 600,
	          buttons: [{
	            'text': dialog.data('move-button'),
	            'click': function(event) { 
            	  $('input[name="' + prefix + ':target-folder-id' + '"]').val($(dialog).data('folder-id'));
            	  $('input[name="' + prefix + ':material-id' + '"]').val(materialId);
            	  $('input[name="' + prefix + ':move' + '"]').click();
	            }
	          }, {
	            'text': dialog.data('cancel-button'),
	            'click': function(event) { 
	              $(this).dialog("close");
	            }
	          }]
	        });
	        
	        var loadFolder = function (folderId) {
	    	  $.ajax({
	    	    url : CONTEXTPATH + "/forge/folderbrowser/",
	    	    data: folderId ? { parent: folderId } : {},
	    	    success : function(result) {
		    	  dust.render("forge-move-material-list", {
	    		    materialId: materialId,
	    	        parents: result.parents,
  	    	        folders: result.folders
		    	  }, function(err, html) {
		    		$(dialog).data('folder-id', folderId);
	      	        $(dialog).find('.forge-move-material-list').html(html);
		    	  });
	    	    }
	    	  });
	        };
	        
	        $(dialog).on('click', '.forge-move-material-list-item', function (event) {
	          if ($(this).data('folder-id') != 'DISABLED') {
	            loadFolder($(this).data('folder-id'));
	          }
	        });
	        
	        $(dialog).on('click', '.forge-move-material-parent', function (event) {
	          loadFolder($(this).data('parent-id'));
	        });
	        
	      } else {
	       // TODO: Proper error handling...
	        alert(err);
	      }
	    });    	  
      }
    });
  });

  $(document).on('click', '.forge-material-action-share a', function (event) {
    var materialId = $(this).data('material-id');
    var actionForm = $('#forge-action-form-container form');
    var prefix = actionForm.attr('name');
    
    $.ajax({
      url : CONTEXTPATH + "/forge/materialShare/",
      data: {
      	materialId: materialId 
      },
      success : function(data) {
        dust.render("forge-share-material", data, function(err, html) {
  		    if (!err) {
  		      var dialog = $(html);
     		    var createRoleSelect = function () {
  		        var roles = {
  		          'MAY_EDIT': $(dialog).data('role-may-edit'),
  		          'MAY_VIEW': $(dialog).data('role-may-view'),
  		          'NONE': $(dialog).data('role-none')
  		        };
  		        	
  		        var select = $('<select name="role">');
  		        $.each(roles, function (role, text) {
  		          select.append($('<option>').attr('value', role).text(text));
  		        });
  		        return select;
  		      };
  		        
    		    dialog.dialog({
  		        modal: true,
  		        width: 600,
  		        buttons: [{
  		          'text': dialog.data('save-button'),
  		          'click': function(event) { 
  		            var publicity = $(this).find('input[name="publicity"]:checked').val();
  		            var collaborators = new Object();
  		            
  		            $(this).find('.forge-share-material-collaborator').each(function(index, element) {
  		              collaborators[$(element).find('input[name="collaborator"]').val()] = $(element).find('select[name="role"]').val();
  		            });

                  $('input[name="' + prefix + ':material-id' + '"]').val(materialId);
                  $('input[name="' + prefix + ':material-share-publicity' + '"]').val(publicity);
                  $('input[name="' + prefix + ':material-share-collaborators' + '"]').val(JSON.stringify(collaborators));
  	              $('input[name="' + prefix + ':material-share-save' + '"]').click();
  		          }
  		        }, {
  		          'text': dialog.data('cancel-button'),
  		          'click': function(event) { 
  		            $(this).dialog("close");
  		          }
  	          }]
            });
    		    
    		    dialog.find('input[type="radio"]').change(function (event) {
    		      if ($(this).val() == 'private') {
                $(dialog).find('.forge-share-material-url input[type="text"]').attr('disabled', 'disabled');
    		      } else {
                $(dialog).find('.forge-share-material-url input[type="text"]').attr('disabled', null);
    		      }
    		    });
    		    
    		    dialog.find('.forge-share-material-invite input').autocomplete({
    		      source: data.invitables,
  		        select: function( event, ui ) {
                var collaborators = $(dialog).find('.forge-share-material-collaborators');
                var id = ui.item.value;
                if (collaborators.find('input[value="' + id + '"]').length == 0) {
                  collaborators.append(
                    $('<div class="forge-share-material-collaborator">')
                      .append($('<input name="collaborator" type="hidden">').val(id))
                      .append($('<label>').text(ui.item.label))
                      .append(createRoleSelect())); 
                }
              },
              close: function( event, ui ) {
                $(this).val('');
              }
    		    });
          } else {
            // TODO: Proper error handling...
            alert(err);
          }
        });
      }
    });
  });
  
  $(document).on('click', '.forge-new-material-folder', function (event) {
    var parentFolderId = $(this).data('parent-folder-id');
    var actionForm = $('#forge-action-form-container form');
    var prefix = actionForm.attr('name');
    
    dust.render("forge-create-folder", {
    }, function(err, html) {
      if (!err) {
        var dialog = $(html);
        dialog.dialog({
          modal: true,
          width: 400,
          buttons: [{
            'class': 'ok-button',
            'text': dialog.data('create-button'),
            'click': function(event) {
              var title = $(dialog).find('.forge-create-folder-name').val();
              $('input[name="' + prefix + ':parent-folder-id' + '"]').val(parentFolderId);
              $('input[name="' + prefix + ':new-folder-name' + '"]').val(title);
              $('input[name="' + prefix + ':new-folder' + '"]').click();
            }
          }, {
            'class': 'cancel-button',
            'text': dialog.data('cancel-button'),
            'click': function(event) { 
              $(this).dialog("close");
            }
          }]
        });
      } else {
       // TODO: Proper error handling...
        alert(err);
      }
    });
  });
  
  $(document).on('click', '.forge-material-action-copy a', function (event) {
    var materialId = $(this).data('material-id');                
    var actionForm = $('#forge-action-form-container form');
    var prefix = actionForm.attr('name');
    var allowedTargets = ($(this).data('allowed-targets')||'').split(',');       
    var folderTypes = allowedTargets.slice();
    
    // If we are allowing event folders as targets we have to allow browsing of the Illusion folder also 
    if (folderTypes.indexOf('ILLUSION_GROUP_FOLDER') != -1) {
      folderTypes.push('ILLUSION_FOLDER');
    }
    
    // If we are allowing folders empty folders (root) are included also
    if (allowedTargets.indexOf('FOLDER') != -1) {
      allowedTargets.push('');
    }
 
    $.ajax({
      url : CONTEXTPATH + "/forge/folderbrowser/",
      traditional: true,
      data: {
        'types': folderTypes
      },
      success : function(result) {
        dust.render("forge-copy-material", {
          materialId : materialId,
          parents : result.parents,
          folders : result.folders
        }, function(err, html) {
          if (!err) {
            var dialog = $(html);
            $(dialog).data('folder-id', null);
            $(dialog).data('folder-type', $(dialog).find('.forge-copy-material-parent').last().data('parent-type'));
            var copyDisabled = allowedTargets.length != 0 && allowedTargets.indexOf($(dialog).data('folder-type')) == -1;
            
            dialog.dialog({
              modal : true,
              width : 600,
              buttons : [ {
                'text' : dialog.data('copy-button'),
                'class': 'copy-button',
                'disabled': copyDisabled,
                'click' : function(event) {
                  $('input[name="' + prefix + ':target-folder-id' + '"]').val($(dialog).data('folder-id'));
                  $('input[name="' + prefix + ':material-id' + '"]').val(materialId);
                  $('input[name="' + prefix + ':copy' + '"]').click();
                }
              }, {
                'text' : dialog.data('cancel-button'),
                'class': 'cancel-button',
                'click' : function(event) {
                  $(this).dialog("close");
                }
              } ]
            });

            var loadFolder = function(folderId) {
              $.ajax({
                url : CONTEXTPATH + "/forge/folderbrowser/",
                traditional: true,
                data : $.extend({
                  types: folderTypes
                }, folderId ? {
                  parent : folderId,
                } : {}),
                success : function(result) {
                  dust.render("forge-copy-material-list", {
                    materialId : materialId,
                    parents : result.parents,
                    folders : result.folders
                  }, function(err, html) {
                    $(dialog).find('.forge-copy-material-list').html(html);
                    $(dialog).data('folder-id', folderId);
                    $(dialog).data('folder-type', $(dialog).find('.forge-copy-material-parent').last().data('parent-type'));
                    $(dialog).dialog("option", "buttons", 
                      $(dialog).dialog("option", "buttons").map($.proxy(function (button, index) {
                        if (button['class'] == 'copy-button') {
                          button['disabled'] = allowedTargets.length != 0 && allowedTargets.indexOf($(this).data('folder-type')) == -1;  
                        }

                        return button;
                      }), dialog)
                    ); 
                  });
                }
              });
            };

            $(dialog).on('click', '.forge-copy-material-list-item', function(event) {
              if ($(this).data('folder-id') != 'DISABLED') {
                loadFolder($(this).data('folder-id'));
              }
            });

            $(dialog).on('click', '.forge-copy-material-parent', function(event) {
              loadFolder($(this).data('parent-id'));
            });

          } else {
            $('.notifications').notifications('notification', 'error', err);
          }
        });
      }
    });
  });

}).call(this);