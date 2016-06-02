(function() {
  'use strict';
  
  $.widget("custom.materialShareDialog", {
    options : {
      materialId: null,
      illusionEventId: null,
      publishableTypes: [ 'DOCUMENT', 'IMAGE', 'PDF', 'FILE', 'BINARY', 'VECTOR_IMAGE', 'GOOGLE_DOCUMENT', 'DROPBOX_FILE' ]
    },
    
    _createRemoveMaterialShareUser: function (id) {
      return $.proxy(function (callback) {
        this._restCall(this._getMaterialClient().materials.shareUsers.del(this.options.materialId, id), $.proxy(function (err) {
          callback(err);
        }, this));
      }, this);
    },
    
    _createUpdateMaterialShareUser: function (id, role) {
      return $.proxy(function (callback) {
        this._restCall(this._getMaterialClient(false).materials.shareUsers.read(this.options.materialId, id), $.proxy(function (getErr, materialUser) {
          if (getErr) {
            callback(getErr);
          } else {
            this._restCall(this._getMaterialClient(true).materials.shareUsers.update(this.options.materialId, id, $.extend(materialUser, {
              role: role
            })), $.proxy(function (updErr, updatedUser) {
              callback(updErr, updatedUser);
            }, this));
          }
        }, this));
      }, this);
    },
    
    _createCreateMaterialShareUser: function (userId, role) {
      return $.proxy(function (callback) {
        this._restCall(this._getMaterialClient(true).materials.shareUsers.create(this.options.materialId, {
          userId: userId,
          role: role
        }), $.proxy(function (err, shareUser) {
          callback(err, shareUser);
        }, this));
      }, this);
    },
    
    _createUpdateMaterial: function (data) {
      return $.proxy(function (callback) {
        this._restCall(this._getMaterialClient(true).materials.read(this.options.materialId), $.proxy(function (err, material) {
          if (err) {
            callback(err);
          } else {
            this._restCall(this._getMaterialClient(true).materials.update(this.options.materialId, $.extend(material, data)), $.proxy(function (updateErr, updatedMaterial) {
              callback(updateErr, updatedMaterial);
            }, this));
          }
        }, this));
      }, this);
    },
    
    _create: function () {
      this._load($.proxy(function (data, html) {
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
          open: function( event, ui ) {
            $(dialog).dialog('widget').css('overflow', 'visible');
          },
          buttons: [{
            'text': dialog.data('save-button'),
            'click': $.proxy(function(event) { 
              var operations = [];
              
              var publicity = $(dialog).find('input[name="publicity"]:checked').val();
              var tagsVal = $(dialog).find('.forge-share-material-tags input').val();
              var tags = tagsVal ? tagsVal.split(',') : [];
              var description = $(dialog).find('.forge-share-material-description textarea').val();
              var license = $(dialog).find('input[name="license"]').val();
              
              $(dialog).find('.forge-share-material-collaborator').each($.proxy(function(index, element) {
                var originalId = $(element).attr('data-original-id');
                var originalRole = $(element).attr('data-original-role');
                var userId = $(element).attr('data-user-id');
                var role = $(element).find('select[name="role"]').val();
                
                if (originalId) {
                  if (originalRole != role) {
                    if (role == 'NONE') {
                      operations.push(this._createRemoveMaterialShareUser(originalId));
                    } else {
                      operations.push(this._createUpdateMaterialShareUser(originalId, role));
                    }
                  }
                } else {
                  if (role != 'NONE') {
                    operations.push(this._createCreateMaterialShareUser(userId, role));
                  }
                }
              }, this));
              
              operations.push(this._createUpdateMaterial({
                publicity: publicity,
                description: description,
                license: license,
                tags: tags
              }));
              
              async.series(operations, $.proxy(function (err, results) {
                if (err) {
                  $('.notifications').notifications('notification', 'error', err);
                } else {
                  $(dialog).dialog("close");
                  window.location.reload(true);
                }
              }, this));
            }, this)
          }, {
            'text': dialog.data('cancel-button'),
            'click': function(event) { 
              $(this).dialog("close");
            }
          }]
        });
        
        dialog.find('input[type="radio"]').change(function (event) {
          if ($(this).val() == 'PRIVATE') {
            $(dialog).find('.forge-share-material-url input[type="text"]').attr('disabled', 'disabled');
          } else {
            $(dialog).find('.forge-share-material-url input[type="text"]').attr('disabled', null);
          }
        });
        
        dialog.find('.forge-share-material-invite input').autocomplete({
          source: $.proxy(function (request, response) {
            this._searchInvitables(request.term, function (err, invitables) {
              if (err) {
                $('.notifications').notifications('notification', 'error', err);
              } else {
                response(invitables);
              }
            });
          }, this),
          select: function( event, ui ) {
            var collaborators = $(dialog).find('.forge-share-material-collaborators');
            var item = ui.item;
            var userId = item.value;
            var displayName = item.label;
            
            console.log(item);
            
            if (collaborators.find('.forge-share-material-collaborator[data-user-id="' + userId + '"]').length == 0) {
              var collaborator = $('<div>')
                .addClass('forge-share-material-collaborator')
                .attr({
                  'data-user-id': userId,
                  'data-type': item.type
                })
                .append($('<label>').text(displayName))
                .append(createRoleSelect())
                .appendTo(collaborators);
              
              switch (item.type) {
                case 'ILLUSION-GROUP':
                  collaborator.prepend($('<span>').addClass('fa fa-users'));
                break;
                default:
                  collaborator.prepend($('<span>').addClass('fa fa-user'));
                break;
              }
            }
          },
          close: function( event, ui ) {
            $(this).val('');
          }
        });
        
        dialog.find('.forge-share-material-invite input')
          .data("ui-autocomplete")
          ._renderItem = $.proxy(this._buildInviteAutocompleteItem);
        
        $(dialog).find('.forge-share-material-tags input').tagsInput({
          'autocomplete_url': 'about:blank',
          'autocomplete': {
            source: data.allTags
          },
          width: '100%',
          height: '80px'
        });
      }, this));
    },
    
    _buildInviteAutocompleteItem: function (ul, item) {
      var li = $("<li>")
        .append($('<span>').text(item.label))
        .appendTo(ul);
      
      switch (item.type) {
        case 'ILLUSION-GROUP':
          li.prepend($('<span>').addClass('fa fa-users'));
        break;
        default:
          li.prepend($('<span>').addClass('fa fa-user'));
        break;
      }
      
      return li;
    },
    
    _load: function (callback) {
      async.parallel([this._createMaterialLoad(), this._createTagsLoad(), this._createMaterialShareUsersLoad(), this._createPublishGuideUrlLoad()], $.proxy(function (err, results) {
        if (err) {
          $('.notifications').notifications('notification', 'error', err);
        } else {
          var material = results[0];
          var allTags = results[1];
          var materialShareUsers = results[2];
          var publishGuideLink = results[3].value;
          
          var href = window.location.href;
          var baseUrl = href.substring(0, href.length - (window.location.pathname.length));
          
          var data = $.extend(material, {
            materialShareUsers: materialShareUsers,
            publicUrl: baseUrl + '/materials/' + material.path,
            allTags: $.map(allTags, function (tag) {
              return tag.text;
            }),
            publishable: this.options.publishableTypes.indexOf(material.type) != -1,
            publishGuideLink: publishGuideLink
          });
          
          dust.render("forge-share-material", data, function(err, html) {
            if (err) {
              $('.notifications').notifications('notification', 'error', err);
            } else {
              callback(data, html);
            }
          });
        }
      }, this));
    },
    
    _createMaterialLoad: function () {
      return $.proxy(function (callback) {
        this._getMaterialClient(false).materials.read(this.options.materialId).done(function (material, message, xhr) {
          if (xhr.status !== 200) {
            callback(message);
          } else {
            callback(null, material);
          }
        });
      }, this);
    },
    
    _createPublishGuideUrlLoad: function () {
      return $.proxy(function (callback) {
        this._restCall(this._getSystemClient(false).settings.read('PUBLISH_GUIDE_' + LOCALE.toUpperCase()), function (err, setting) {
          callback(err, setting);
        });
      }, this);
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
    
    _createUserLoad: function (materialUser) {
      return $.proxy(function (callback) {
        this._restCall(this._getUserClient(false).users.read(materialUser.userId), function (err, user) {
          if (err) {
            callback(err);
          } else {
            callback(err, $.extend(materialUser, {
              firstName: user.firstName,
              lastName: user.lastName,
              emails: user.emails
            }));
          }
        });
      }, this);
    },
    
    _createMaterialShareUsersLoad: function () {
      return $.proxy(function (callback) {
        this._restCall(this._getMaterialClient(false).materials.shareUsers.read(this.options.materialId), $.proxy(function (err, materialUsers) {
          if (err) {
            callback(err);
          } else {
            var userLoads = $.map(materialUsers, $.proxy(function (materialUser) {
              return this._createUserLoad(materialUser);
            }, this));
            
            async.parallel(userLoads, function (err, users) {
              if (err) {
                callback(err);
              } else {
                callback(null, $.map(users, function (user) {
                  return $.extend(user, {
                    displayName: user.firstName && user.lastName ? user.firstName + ' ' + user.lastName : '<' + user.emails[0] + '>'
                  });
                }));
              }
            });
          }
        }, this));
      }, this);
    },
    
    _createTagsLoad: function () {
      return $.proxy(function (callback) {
        this._restCall(this._getMaterialClient(false).tags.read(), function (err, tags) {
          callback(err, tags);
        });
      }, this);
    },
    
    _getUserDisplayName: function (user) {
      var displayName = user.firstName && user.lastName ? user.firstName + ' ' + user.lastName : '';
      
      if (user.emails.length) {
        displayName = (displayName ? displayName + ' ' : '') + '<' + user.emails[0] + '>';
      }
      
      return displayName;
    },
    
    _createUserSearch: function (term) {
      return $.proxy(function (callback) {
        this._restCall(this._getUserClient(false).users.read({ search: term }), $.proxy(function (err, users) {
          if (err) {
            callback(err);
          } else {
            callback(err, $.map(users, $.proxy(function(user) {
              var displayName = this._getUserDisplayName(user);
              return {
                value: user.id,
                label: displayName,
                type: 'USER'
              }
            }, this)));
          }
        }, this));
      }, this)
    },
    
    _createListEventGroupsCall: function (event) {
      return $.proxy(function (callback) {
        this._restCall(this._getIllusionClient(false).events.groups.read(event.id), function (err, groups) {
          if (err) {
            callback(err);
          } else {
            if (groups != null && groups.length) {
              callback(null, {
                groups: groups,
                event: event
              });
            } else {
              callback(null, null);
            }
          }
        });
      }, this)
    },
    
    _stringContains: function (str, value) {
      if (!str || !value) {
        return false;
      }
      
      return _.toLower(str).indexOf(_.toLower(value)) != -1;
    },
    
    _createIllusionGroupSearch: function (term) {
      return $.proxy(function (mainCallback) {
        this._restCall(this._getUserClient(false).users.read('me'), $.proxy(function (meErr, me) {
          if (meErr) {
            mainCallback(meErr);
          } else {
            this._restCall(this._getIllusionClient(false).events.read({ organizer: me.id }), $.proxy(function (err, events) {
              
              var calls = _.map(events, $.proxy(function (event) {
                return this._createListEventGroupsCall(event);
              }, this));
              
              async.parallel(calls, $.proxy(function (groupsErr, groups) {
                if (groupsErr) {
                  mainCallback(groupsErr);
                } else {
                  var results = _.map(_.compact(_.flatten(groups)), $.proxy(function (result) {
                    return _.map(result.groups, $.proxy(function (group) {
                      var eventName = result.event.name;
                      var groupName = group.name;
                      if (!this._stringContains(groupName, term) && !this._stringContains(eventName, term)) {
                        return null;
                      }
                      
                      return {
                        value: group.id,
                        label: groupName + ' (' + eventName + ')',
                        type: 'ILLUSION-GROUP'
                      };
                    }, this));
                  }, this));
                  
                  mainCallback(null, _.compact(_.flatten(results)));
                }
              }, this));
            }, this));
          }
        }, this));
      }, this)
    },
    
    _searchInvitables: function (term, callback) {
      var tasks = [ this._createUserSearch(term), this._createIllusionGroupSearch(term) ];
      async.parallel(tasks, function (err, results) {
        if (err) {
          callback(err);
        } else {
          callback(null, _.flatten(results));
        }
      });
    },
    
    _getSystemClient: function (stringify) {
      var client = new $.RestClient(CONTEXTPATH + '/rest/system/', {stringifyData: stringify === false ? false : true});
      client.add("settings");
      return client;
    },
    
    _getMaterialClient: function (stringify) {
      var materialClient = new $.RestClient(CONTEXTPATH + '/rest/material/', {stringifyData: stringify === false ? false : true});
      materialClient.add("materials");
      materialClient.materials.add("shareUsers");
      materialClient.add("tags");
      return materialClient;
    },
    
    _getUserClient: function (stringify) {
      var userClient = new $.RestClient(CONTEXTPATH + '/rest/users/', {stringifyData: stringify === false ? false : true});
      userClient.add("users");
      return userClient;
    },
    
    _getIllusionClient: function (stringify) {
      var illusionClient = new $.RestClient(CONTEXTPATH + '/rest/illusion/', {stringifyData: stringify === false ? false : true});
      illusionClient.add('events');
      illusionClient.events.add('participants');
      illusionClient.events.add('groups');
      return illusionClient;
    }
  });
  
}).call(this);