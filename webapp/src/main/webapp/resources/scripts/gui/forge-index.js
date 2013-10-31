(function() {
  'use strict';

  $(document).ready(function () {
    
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
      materialName: $.trim($(this).closest('.forge-material').find('.forge-material-title').html())
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
            	  $('input[name="' + prefix + ':move-target-folder-id' + '"]').val($(dialog).data('folder-id'));
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
  
}).call(this);