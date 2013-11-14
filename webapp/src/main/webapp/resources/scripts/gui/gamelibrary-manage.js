(function() {
  'use strict';

  $(document).ready(function () {
    $('.gamelibrary-manage-publication-image-link').magnificPopup({ 
      type: 'image'
    });
  });
  
  $(document).on('click', '.gamelibrary-manage-upload-image-link', function (event) {
    dust.render("gamelibrary-image-upload", {
      publicationId: $(this).data('publication-id')
    }, function(err, html) {
      if (!err) {
        var dialog = $(html);
        dialog.dialog({
          modal: true,
          width: 600,
          buttons: [{
            'text': dialog.data('upload-button'),
            'click': function(event) { 
              $(this).find('iframe').load($.proxy(function () {
                window.location.reload();
              }, this));
              
              $(this).find('form').submit();
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
  
  $(document).on('click', '.gamelibrary-manage-upload-downloadable-link', function (event) {
    dust.render("gamelibrary-downloadable-upload", {
      publicationId: $(this).data('publication-id')
    }, function(err, html) {
      if (!err) {
        var dialog = $(html);
        dialog.dialog({
          modal: true,
          width: 600,
          buttons: [{
            'text': dialog.data('upload-button'),
            'click': function(event) { 
              $(this).find('iframe').load($.proxy(function () {
                window.location.reload();
              }, this));
              
              $(this).find('form').submit();
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
  
  $(document).on('click', '.gamelibrary-manage-upload-printable-link', function (event) {
    dust.render("gamelibrary-printable-upload", {
      publicationId: $(this).data('publication-id')
    }, function(err, html) {
      if (!err) {
        var dialog = $(html);
        dialog.dialog({
          modal: true,
          width: 600,
          buttons: [{
            'text': dialog.data('upload-button'),
            'click': function(event) { 
              $(this).find('iframe').load($.proxy(function () {
                window.location.reload();
              }, this));
              
              $(this).find('form').submit();
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
  
  $(document).on('click', '.gamelibrary-manage-publish-link', function (event) {
    var form = $(this).closest('form');
    
    dust.render("gamelibrary-publish", {
      publicationName: $.trim($(this).closest('.gamelibrary-manage-publicationlist-name-actions').find('.gamelibrary-manage-publicationlist-name').html())
    }, function(err, html) {
      if (!err) {
        var dialog = $(html);
        dialog.dialog({
          modal: true,
          width: 400,
          buttons: [{
            'text': dialog.data('publish-button'),
            'click': function(event) { 
              form.find('input[type="submit"]').click();
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

  $(document).on('click', '.gamelibrary-manage-unpublish-link', function (event) {
    var form = $(this).closest('form');
    
    dust.render("gamelibrary-unpublish", {
      publicationName: $.trim($(this).closest('.gamelibrary-manage-publicationlist-name-actions').find('.gamelibrary-manage-publicationlist-name').html())
    }, function(err, html) {
      if (!err) {
        var dialog = $(html);
        dialog.dialog({
          modal: true,
          width: 400,
          buttons: [{
            'text': dialog.data('unpublish-button'),
            'click': function(event) { 
              form.find('input[type="submit"]').click();
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

  $(document).on('click', '.gamelibrary-manage-remove-link', function (event) {
    var form = $(this).closest('form');
    
    dust.render("gamelibrary-remove", {
      publicationName: $.trim($(this).closest('.gamelibrary-manage-publicationlist-name-actions').find('.gamelibrary-manage-publicationlist-name').html())
    }, function(err, html) {
      if (!err) {
        var dialog = $(html);
        dialog.dialog({
          modal: true,
          width: 400,
          buttons: [{
            'text': dialog.data('remove-button'),
            'click': function(event) { 
              form.find('input[type="submit"]').click();
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
  
}).call(this);