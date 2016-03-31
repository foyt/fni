(function() {
  'use strict';
  
  $.widget("custom.materialShareDialog", {
    options : {
      materialId: null,
      illusionEventId: null,
      publishableTypes: [ 'DOCUMENT', 'IMAGE', 'PDF', 'FILE', 'BINARY', 'VECTOR_IMAGE', 'GOOGLE_DOCUMENT', 'DROPBOX_FILE' ]
    },
    
    _createRemoveMaterialUser: function (id) {
      return $.proxy(function (callback) {
        this._restCall(this._getMaterialClient().materials.users.del(this.options.materialId, id), $.proxy(function (err) {
          callback(err);
        }, this));
      }, this);
    },
    
    _createUpdateMaterialUser: function (id, role) {
      return $.proxy(function (callback) {
        this._restCall(this._getMaterialClient(false).materials.users.read(this.options.materialId, id), $.proxy(function (getErr, materialUser) {
          if (getErr) {
            callback(getErr);
          } else {
            this._restCall(this._getMaterialClient(true).materials.users.update(this.options.materialId, id, $.extend(materialUser, {
              role: role
            })), $.proxy(function (updErr, updatedUser) {
              callback(updErr, updatedUser);
            }, this));
          }
        }, this));
      }, this);
    },
    
    _createCreateMaterialUser: function (userId, role) {
      return $.proxy(function (callback) {
        this._restCall(this._getMaterialClient(true).materials.users.create(this.options.materialId, {
          userId: userId,
          role: role
        }), $.proxy(function (err, materialUser) {
          callback(err, materialUser);
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
                      operations.push(this._createRemoveMaterialUser(originalId));
                    } else {
                      operations.push(this._createUpdateMaterialUser(originalId, role));
                    }
                  }
                } else {
                  if (role != 'NONE') {
                    operations.push(this._createCreateMaterialUser(userId, role));
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
                response($.map(invitables, function(invitable) {
                  return {
                    value: invitable.id,
                    label: invitable.displayName
                  }
                }));
              }
            });
          }, this),
          select: function( event, ui ) {
            var collaborators = $(dialog).find('.forge-share-material-collaborators');
            var userId = ui.item.value;
            var displayName = ui.item.label;
            
            if (collaborators.find('.forge-share-material-collaborator[data-user-id="' + userId + '"]').length == 0) {
              $('<div>')
                .addClass('forge-share-material-collaborator')
                .attr({
                  'data-user-id': userId
                })
                .append($('<label>').text(displayName))
                .append(createRoleSelect())
                .appendTo(collaborators);
            }
          },
          close: function( event, ui ) {
            $(this).val('');
          }
        });

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
    
    _load: function (callback) {
      async.parallel([this._createMaterialLoad(), this._createTagsLoad(), this._createUsersLoad(), this._createPublishGuideUrlLoad()], $.proxy(function (err, results) {
        if (err) {
          $('.notifications').notifications('notification', 'error', err);
        } else {
          var material = results[0];
          var allTags = results[1];
          var materialUsers = results[2];
          var publishGuideLink = results[3].value;
          
          var href = window.location.href;
          var baseUrl = href.substring(0, href.length - (window.location.pathname.length));
          
          var data = $.extend(material, {
            materialUsers: materialUsers,
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
    
    _createUsersLoad: function () {
      return $.proxy(function (callback) {
        this._restCall(this._getMaterialClient(false).materials.users.read(this.options.materialId), $.proxy(function (err, materialUsers) {
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
    
    _searchInvitables: function (term, callback) {
      if (this.options.illusionEventId) {
        this._restCall(this._getIllusionClient(false).events.participants.read(this.options.illusionEventId, { search: term }), function (participants, err) {
          if (err) {
            callback(err);
          } else {
            callback(err, $.map(participants, function(participant) {
              var displayName = user.firstName && user.lastName ? user.firstName + ' ' + user.lastName : '<' + user.emails[0] + '>';
              return {
                id: participant.userId,
                displayName: participant.displayName
              }
            }));
          }
        });
      } else {
        this._restCall(this._getUserClient(false).users.read({ search: term }), function (err, users) {
          if (err) {
            callback(err);
          } else {
            callback(err, $.map(users, function(user) {
              var displayName = user.firstName && user.lastName ? user.firstName + ' ' + user.lastName : '<' + user.emails[0] + '>';
              return {
                id: user.id,
                displayName: displayName
              }
            }));
          }
        });
      }
    },
    
    _getSystemClient: function (stringify) {
      var client = new $.RestClient(CONTEXTPATH + '/rest/system/', {stringifyData: stringify === false ? false : true});
      client.add("settings");
      return client;
    },
    
    _getMaterialClient: function (stringify) {
      var materialClient = new $.RestClient(CONTEXTPATH + '/rest/material/', {stringifyData: stringify === false ? false : true});
      materialClient.add("materials");
      materialClient.materials.add("users");
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
      return illusionClient;
    }
  });
  
}).call(this);