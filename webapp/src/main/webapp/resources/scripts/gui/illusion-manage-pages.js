(function() {
  'use strict';
  
  $.widget("custom.illusionPageVisibilityDialog", {
    options: {
      eventId: null,
      pageId: null,
      pageTitle: null,
      pageVisibility: null,
      pageRequiresUser: null
    },
    
    _create: function () {
      this._illusionClient = new $.RestClient(CONTEXTPATH + '/rest/illusion/');
      this._illusionClient.add('events');
      this._illusionClient.events.add('groups');
      
      this._load($.proxy(function (html) {
        this._dialog = $(html);
        
        this._dialog.on("click", "input[name='group']", $.proxy(this._onGroupClick, this));

        this._dialog.dialog({
          modal: true,
          width: 400,
          buttons: [{
            'text': this._dialog.attr('data-change-button'),
            'click': $.proxy(function(event) { 
              var groupIds = $.map($(this._dialog).find('input[name="group"]:checked'), function (input) {
                return $(input).val();
              });
              
              $('.jsfActionPageId').val(this.options.pageId);
              $('.jsfActionPageVisibility').val($(this._dialog).find('input[name="visibility"]:checked').val());
              $('.jsfActionGroupIds').val(groupIds.join('&'));
              $('.jsfActionChangeVisibility')[0].click();
            }, this)
          }, {
            'text': this._dialog.attr('data-cancel-button'),
            'click': function(event) { 
              $(this).dialog("close");
            }
          }]
        });
      }, this));
    },
    
    _load: function (callback) {
      var tasks = [this._createListEventGroupsCall()];
      
      async.parallel(tasks, $.proxy(function (err, results) {
        if (err) {
          $('.notifications').notifications('notification', 'error', err);
        } else {
          var groups = $.map(results[0], $.proxy(function (group) {
            return $.extend(group, {
              selected: this.options.groupIds && this.options.groupIds.indexOf(group.id) != -1
            });
          }, this));
          
          dust.render("illusion-page-visibility", {
            pageTitle: this.options.pageTitle,
            pageVisibility: this.options.pageVisibility,
            pageRequiresUser: this.options.pageRequiresUser == 'true',
            groups: groups
          }, $.proxy(function(renderErr, html) {
            if (renderErr) {
              $('.notifications').notifications('notification', 'error', renderErr);
            } else {
              callback(html);
            }
          }, this));
        }
      }, this));
    },
    
    _restCall: function (request, callback) {
      request
        .done(function (result) {
          callback(null, result);
        })
        .fail(function (jqXHR, textStatus, errorThrown) {
          if ((textStatus === "abort") || (jqXHR.status === 0)) {
            return;
          }
          
          callback(textStatus ? jqXHR.responseText || jqXHR.statusText || textStatus : null, jqXHR);
        })
    },
    
    _createListEventGroupsCall: function () {
      return $.proxy(function (callback) {
        this._restCall(this._illusionClient.events.groups.read(this.options.eventId), callback);
      }, this)
    },
    
    _onGroupClick: function (event) {
      this._dialog.find("input[name='visibility'][value='GROUPS']")
        .prop("checked", true);  
    }
  });

  $(document).on('click', '.illusion-remove-page', function (event) {
    var eventId =  $(this).attr('data-event-id');
    var pageId = $(this).attr('data-page-id');
    var pageTitle = $(this).attr('data-page-title');
    
    dust.render("illusion-page-remove", {
      pageTitle: pageTitle
    }, function(err, html) {
      if (!err) {
        var dialog = $(html);
        dialog.dialog({
          modal: true,
          width: 400,
          buttons: [{
            'text': dialog.attr('data-remove-button'),
            'click': function(event) { 
              $.ajax(CONTEXTPATH + '/rest/illusion/events/' + eventId + '/pages/' + pageId, {
                type: 'DELETE',
                success: function (jqXHR, textStatus) {
                  window.location.reload(true);
                },
                error: function (jqXHR, textStatus, errorThrown) {
                  $('.notifications').notifications('notification', 'error', textStatus);
                }
              });
            }
          }, {
            'text': dialog.attr('data-cancel-button'),
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

  $(document).on('click', '.illusion-change-visibility', function (event) {
    var eventId = $('.event-id').val();
    var pageId = $(this).attr('data-page-id');
    var pageTitle = $(this).attr('data-page-title');
    var pageVisibility = $(this).attr('data-page-visibility');
    var pageRequiresUser = $(this).attr('data-page-requires-user');
    var groupIdsAttr = $(this).attr('data-group-ids');
    var groupIds = groupIdsAttr ? $.map(groupIdsAttr.split(','), function (id) {
      return parseInt(id);
    }) : null;
    
    $('<div>').illusionPageVisibilityDialog({
      eventId: eventId,
      pageId: pageId,
      pageTitle: pageTitle,
      pageVisibility: pageVisibility,
      pageRequiresUser: pageRequiresUser,
      groupIds: groupIds
    });
  });
  
}).call(this);