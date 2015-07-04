(function() {
  'use strict';
    
  $.widget("custom.forgeMaterialBrowser", {
    options: {
      types: []
    },
    _create : function() {
      this._dialog = null;

      $.ajax({
        url : CONTEXTPATH + "/forge/materialbrowser/",
        traditional: true,
        data: {
          'types': this.options.types
        },
        success : $.proxy(function(result) {
          dust.render("forge-material-browser", {
            parents : result.parents,
            folders : result.folders
          }, $.proxy(function(err, html) {
            if (!err) {
              this._dialog = $(html);
              
              this._dialog.on('click', '.forge-material-browser-parent', $.proxy(this._onParentClick, this));
              
              this._dialog.dialog({
                modal : true,
                width : 600,
                buttons : [{
                  'text' : this._dialog.attr('data-cancel-button'),
                  'class': 'cancel-button',
                  'click' : $.proxy(function(event) {
                    this._close();
                  }, this)
                } ]
              });

              $(this._dialog).on('click', '.forge-material-browser-list-item', $.proxy(function(event) {
                var item = $(event.target);
                if (!item.hasClass('forge-material-browser-list-item')) {
                  item = item.closest('.forge-material-browser-list-item');
                }
                
                var id = $(item).attr('data-id');
                if (id != 'DISABLED') {
                  var type = $(item).attr('data-type');
                  switch (type) {
                    case 'FOLDER':
                    case 'DROPBOX_ROOT_FOLDER':
                    case 'DROPBOX_FOLDER':
                    case 'ILLUSION_FOLDER':
                    case 'ILLUSION_GROUP_FOLDER':
                      this._loadFolder(id);
                    break;
                    default:
                      this.element.trigger("materialSelect", {
                        id: id,
                        type: type
                      });

                      this._close();
                    break;
                  }
                }
              }, this));

            } else {
              $('.notifications').notifications('notification', 'error', err);
            }
          }, this));
        }, this)
      });
    },
    
    _loadFolder: function (folderId) {
      $.ajax({
        url : CONTEXTPATH + "/forge/materialbrowser/",
        traditional: true,
        data : $.extend({
          types: this.options.types
        }, folderId ? {
          parent : folderId,
        } : {}),
        success : $.proxy(function(result) {
          dust.render("forge-material-browser-list", {
            parents : result.parents,
            folders : result.folders
          }, $.proxy(function(err, html) {
            if (err) {
              $('.notifications').notifications('notification', 'error', err);
            } else {
              $(this._dialog).find('.forge-material-browser-list').html(html);
              $(this._dialog).data('folder-id', folderId);
              $(this._dialog).data('folder-type', $(this._dialog).find('.forge-material-browser-parent').last().data('parent-type'));
            }
          }, this));
        }, this)
      });
    },
    
    _close: function () {
      this._dialog.dialog('close');
      this.destroy();
    },
    
    _onParentClick: function (event) {
      var parentLink = $(event.target);
      var parentId = $(parentLink).attr('data-parent-id');
      this._loadFolder(parentId);
    }
  });

}).call(this);