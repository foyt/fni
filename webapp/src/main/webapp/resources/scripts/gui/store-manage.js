(function() {
  'use strict';
/**
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
        $('.notifications').notifications('notification', 'error', err);
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
        $('.notifications').notifications('notification', 'error', err);
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
        $('.notifications').notifications('notification', 'error', err);
      }
    }); 
  });
**/  
  $(document).on('click', '.store-manage-publish-link', function (event) {
    var form = $(this).closest('form');
    
    dust.render("store-publish", {
      name: $.trim($(this).closest('.store-manage-list-cell').find('.store-manage-list-name').text())
    }, function(err, html) {
      if (!err) {
        var dialog = $(html);
        dialog.dialog({
          modal: true,
          width: 400,
          buttons: [{
            'text': dialog.data('publish-button'),
            'click': function(event) { 
              form.find('.store-manage-publish-button').click();
            }
          }, {
            'text': dialog.data('cancel-button'),
            'click': function(event) { 
              $(this).dialog("close");
            }
          }]
        });
      } else {
        $('.notifications').notifications('notification', 'error', err);
      }
    });
  });

  $(document).on('click', '.store-manage-unpublish-link', function (event) {
    var form = $(this).closest('form');
    
    dust.render("store-unpublish", {
      name: $.trim($(this).closest('.store-manage-list-cell').find('.store-manage-list-name').text())
    }, function(err, html) {
      if (!err) {
        var dialog = $(html);
        dialog.dialog({
          modal: true,
          width: 400,
          buttons: [{
            'text': dialog.data('unpublish-button'),
            'click': function(event) { 
              form.find('.store-manage-unpublish-button').click();
            }
          }, {
            'text': dialog.data('cancel-button'),
            'click': function(event) { 
              $(this).dialog("close");
            }
          }]
        });
      } else {
        $('.notifications').notifications('notification', 'error', err);
      }
    });
  });
/**
  $(document).on('click', '.gamelibrary-manage-remove-link', function (event) {
    var form = $(this).closest('form');
    
    dust.render("gamelibrary-remove", {
      name: $.trim($(this).closest('.store-manage-list-cell').find('.store-manage-list-name').text())
    }, function(err, html) {
      if (!err) {
        var dialog = $(html);
        dialog.dialog({
          modal: true,
          width: 400,
          buttons: [{
            'text': dialog.data('remove-button'),
            'click': function(event) { 
              form.find('.gamelibrary-manage-delete-button').click();
            }
          }, {
            'text': dialog.data('cancel-button'),
            'click': function(event) { 
              $(this).dialog("close");
            }
          }]
        });
      } else {
        $('.notifications').notifications('notification', 'error', err);
      }
    });
  });
  **/
}).call(this);