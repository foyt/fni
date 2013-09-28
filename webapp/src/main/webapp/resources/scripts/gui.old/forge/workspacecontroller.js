ForgeWorkspaceController = Class.create({
  initialize: function (options) {
    this._needsReloading = new Array();
    this._searching = false;
    this._searchAfter = null;
    
    this._dockingBar = new ForgeWorkspaceWindowDockingBar({});
    this._dockingBar.addListener("windowUndocked", this, this._onWindowUndocked);
    this._dockingBarContainer = options.dockingBarContainer;
    this._windowContainer = options.windowContainer;
    this._dockingBarContainer.appendChild(this._dockingBar.domNode);
    this._dockingBar.componentOnDOM();
    
    this._materialStarClickListener = this._onMaterialStarClick.bindAsEventListener(this);
    this._starredMaterialListShowMoreClickListener = this._onStarredMaterialListShowMoreClick.bindAsEventListener(this);
    this._materialTitleClickListener = this._onMaterialTitleClick.bindAsEventListener(this);
    this._materialListTitleClickListener = this._onMaterialListTitleClick.bindAsEventListener(this);
    this._materialEditLinkClickListener = this._onMaterialEditLinkClick.bindAsEventListener(this);
    this._materialMoveLinkClickListener = this._onMaterialMoveLinkClick.bindAsEventListener(this);
    this._materialDeleteLinkClickListener = this._onMaterialDeleteLinkClick.bindAsEventListener(this);
    this._materialPrintToPdfLinkClickListener = this._onMaterialPrintToPdfLinkClick.bindAsEventListener(this);
    this._materialPublishLinkClickListener = this._onMaterialPublishLinkClick.bindAsEventListener(this);
    this._materialUnpublishLinkClickListener = this._onMaterialUnpublishLinkClick.bindAsEventListener(this);
    this._materialShareLinkClickListener = this._onMaterialShareLinkClick.bindAsEventListener(this);
    this._materialParentLinkClickListener = this._onMaterialParentinkClick.bindAsEventListener(this);
    this._searchChangeListener = this._onSearchChange.bindAsEventListener(this);
    this._searchKeyUpListener = this._onSearchKeyUp.bindAsEventListener(this);

    this._materialLists = $('forgeMaterialLists');
    this._materialList = $('forgeMaterialList');
    this._starredMaterialList = $('forgeStarredMaterialList');
    this._recentlyModifiedMaterialList = $('forgeRecentlyModifiedMaterialList');
    this._recentlyViewedMaterialList = $('forgeRecentlyViewedMaterialList');
    this._foundMaterialList = $('forgeFoundMaterials');
      
    this._addMaterialListListeners(this._materialList);
    this._addMaterialListListeners(this._starredMaterialList);
    this._addMaterialListListeners(this._recentlyModifiedMaterialList);
    this._addMaterialListListeners(this._recentlyViewedMaterialList);
    
    var _this = this;
    this._materialLists.select('.forgeWorkspaceMaterialList h2').each(function (element) {
      Event.observe(element, "click", _this._materialListTitleClickListener);
    });
    
    this._addStarredMaterialShowMoreListener();

    Event.observe($('forgeMaterialSearch'), "change", this._searchChangeListener);
    Event.observe($('forgeMaterialSearch'), "keyup", this._searchKeyUpListener);
  },    
  destroy: function () {
    // TODO: call this method
    
    this._removeMaterialListListeners(this._materialList);
    this._removeMaterialListListeners(this._starredMaterialList);
    this._removeMaterialListListeners(this._recentlyModifiedMaterialList);
    this._removeMaterialListListeners(this._recentlyViewedMaterialList);
    
    this._materialLists.select('.forgeWorkspaceMaterialList h2').each(function (element) {
      Event.stopObserving(element, "click", _this._materialListTitleClickListener);
    });

    this._removeStarredMaterialShowMoreListener();
    
    Event.stopObserving($('forgeMaterialSearch'), "change", this._searchChangeListener);
    Event.stopObserving($('forgeMaterialSearch'), "keyup", this._searchKeyUpListener);
  },
  getOpenWindow: function () {
    return this._openWindow;
  },
  createNewWindow: function(title, iconURL){
    var windowComponent = new ForgeWorkspaceWindow({
      title: title,
      iconURL: iconURL
    });
    
    return windowComponent;
  },
  openNewWindow: function(title, iconURL){
    var windowComponent = this.createNewWindow(title, iconURL);
    
    this.addWindow(windowComponent);
    
    return windowComponent;
  },
  addWindow: function (windowComponent) {
    windowComponent.addListener("minimize", this, this._onWindowMinimize);
    windowComponent.addListener("maximize", this, this._onWindowMaximize);
    windowComponent.addListener("restoreMaximized", this, this._onWindowRestoreMaximized);
    windowComponent.addListener("close", this, this._onWindowClose);
    
    this._windowContainer.appendChild(windowComponent.domNode);
    windowComponent.componentOnDOM();
    
    this.openWindow(windowComponent);
  },
  openWindow: function (windowComponent) {
    if (this._openWindow != windowComponent) {
      if (this._openWindow) 
        this._dockingBar.dockWindow(this._openWindow);
            
      this._openWindow = windowComponent;
      
      if (this._dockingBar.getDockedWindow(windowComponent)) {
        this._dockingBar.undockWindow(this._openWindow);
      } else {
        this._openWindow.show();
      }
    } 
  },
  minimizeWindow: function (windowComponent) {
    this._dockingBar.dockWindow(windowComponent);
    this._openWindow = undefined;
    this._reloadRequestedLists();
  },
  reloadMaterialLists: function (lists) {
    this._loadMaterialLists(this._getMaterialListStates(lists));
  },
  _getWindowComponentById: function (windowComponentId) {
    return getComponentById(windowComponentId);
  },
  editMaterial: function (materialId, materialType, materialTitle) {
    var windowComponentId = 'material-editor-window-' + materialId;
    var windowComponent = this._getWindowComponentById(windowComponentId);
    if (windowComponent) {
      this.openWindow(windowComponent);
    } else {
      this._markReloadNeed('modified');
      
      switch (materialType) {
        case 'DOCUMENT':
          this.addWindow(new DocumentEditor({
            documentTitle: materialTitle,
            documentId: materialId,
            id: windowComponentId
          }));
        break;
        case 'VECTOR_IMAGE':
          this.addWindow(new VectorImageEditor({
            vectorImageTitle: materialTitle,
            vectorImageId: materialId,
            id: windowComponentId
          }));
        break;
        case 'FOLDER':
          var dialog = new EditFolderDialogController({
            folderId: materialId
          });
          dialog.open();
        break;
        default:
          alert(materialType);
        break;
      }
    }
  },
  _clearMaterialList: function (materialList) {
    this._removeMaterialListListeners(materialList);
    materialList.down('.forgeWorkspaceMaterialListItems').select('li').invoke('remove');
  },
  _addMaterialListItem: function (materialList, id, type, archetype, title, created, creatorName, editable, printableToPdf, starred, mayDelete, mayEdit) {
    var materialElement = new Element("li", {
      className: "forgeWorkspaceMaterial"
    });
    materialElement.appendChild(new Element("input", {
      type: "hidden",
      value: id,
      name: "materialId"
    }));
    materialElement.appendChild(new Element("input", {
      type: "hidden",
      value: type,
      name: "materialType"
    }));
    materialElement.appendChild(new Element("input", {
      type: "hidden",
      value: archetype,
      name: "materialSubtype"
    }));
    
    var starElement = new Element("span", {
      className: "forgeWorkspaceMaterialStar"
    }).update('★');
    Event.observe(starElement, "click", this._materialStarClickListener);
    if (starred) {
      starElement.addClassName('forgeWorkspaceMaterialStarred');
    }
    materialElement.appendChild(starElement);
    materialElement.appendChild(document.createTextNode('\n'));
    materialElement.appendChild(new Element("span", {
      className: "forgeWorkspaceMaterialIcon " + archetype + ' ' + archetype + '-' + type
    }));
    
    materialElement.appendChild(new Element("span", {
      className: "forgeWorkspaceMaterialDate"
    }).update(getLocale().getDate(created)));
    materialElement.appendChild(document.createTextNode('\n'));
    var titleElement = new Element("span", {
      className: "forgeWorkspaceMaterialTitle"
    }).update(title);
    Event.observe(titleElement, "click", this._materialTitleClickListener);
    materialElement.appendChild(titleElement);
    materialElement.appendChild(document.createTextNode('\n'));
    materialElement.appendChild(new Element("span", {
      className: "forgeWorkspaceMaterialEditor"
    }).update(creatorName));

    var actionsContainer = new Element("div", {
      className: "forgeWorkspaceMaterialActions"
    });
    
    if (mayEdit && editable) {
      var editLink = new Element("a", {
        title: getLocale().getText('forge.indexPage.materialsListEditTooltip'),
        className: "forgeWorkspaceMaterialEditLink",
        href: "javascript:void(null);"
      });
      Event.observe(editLink, "click", this._materialEditLinkClickListener);
      actionsContainer.appendChild(editLink);
      actionsContainer.appendChild(document.createTextNode('\n'));
    }
    
    if (mayDelete) {
      var deleteLink = new Element("a", {
        title: getLocale().getText('forge.indexPage.materialsListDeleteTooltip'),
        className: "forgeWorkspaceMaterialDeleteLink",
        href: "javascript:void(null);"
      });

      Event.observe(deleteLink, "click", this._materialDeleteLinkClickListener);
      actionsContainer.appendChild(deleteLink);
      actionsContainer.appendChild(document.createTextNode('\n'));
    }
    
    if (printableToPdf) {
      var printToPdfLink = new Element("a", {
        title: getLocale().getText('forge.indexPage.materialsListPrintToPdfTooltip'),
        className: "forgeWorkspaceMaterialPrintToPdfLink",
        href: "javascript:void(null);"
      });
      Event.observe(printToPdfLink, "click", this._materialPrintToPdfLinkClickListener);
      actionsContainer.appendChild(printToPdfLink);
      actionsContainer.appendChild(document.createTextNode('\n'));
    }
    
    if (mayEdit) {
      var moveLink = new Element("a", {
        title: getLocale().getText('forge.indexPage.materialsListMoveTooltip'),
        className: "forgeWorkspaceMaterialMoveLink",
        href: "javascript:void(null);"
      });
      var shareLink = new Element("a", {
        title: getLocale().getText('forge.indexPage.materialsListShareTooltip'),
        className: "forgeWorkspaceMaterialShareLink",
        href: "javascript:void(null);"
      });
      
      Event.observe(moveLink, "click", this._materialMoveLinkClickListener);
      Event.observe(shareLink, "click", this._materialShareLinkClickListener);
      actionsContainer.appendChild(moveLink);
      actionsContainer.appendChild(document.createTextNode('\n'));
      actionsContainer.appendChild(shareLink);
      actionsContainer.appendChild(document.createTextNode('\n'));
    }
    
    materialElement.appendChild(actionsContainer);
    materialList.down('.forgeWorkspaceMaterialListItems').appendChild(materialElement);
  },
  _addMaterialListListeners: function (materialList) {
    materialList.select('.forgeWorkspaceMaterialTitle').invoke("observe", "click", this._materialTitleClickListener);
    materialList.select('.forgeWorkspaceMaterialStar').invoke("observe", "click", this._materialStarClickListener);
    materialList.select('.forgeWorkspaceMaterialEditLink').invoke("observe", "click", this._materialEditLinkClickListener);
    materialList.select('.forgeWorkspaceMaterialMoveLink').invoke("observe", "click", this._materialMoveLinkClickListener);
    materialList.select('.forgeWorkspaceMaterialDeleteLink').invoke("observe", "click", this._materialDeleteLinkClickListener);
    materialList.select('.forgeWorkspaceMaterialPrintToPdfLink').invoke("observe", "click", this._materialPrintToPdfLinkClickListener);
    materialList.select('.forgeWorkspaceMaterialPublishLink').invoke("observe", "click", this._onMaterialPublishLinkClick);
    materialList.select('.forgeWorkspaceMaterialUnpublishLink').invoke("observe", "click", this._materialUnpublishLinkClickListener);
    materialList.select('.forgeWorkspaceMaterialShareLink').invoke("observe", "click", this._materialShareLinkClickListener);
    materialList.select('.forgeWorkspaceMaterialParentLink').invoke("observe", "click", this._materialParentLinkClickListener);
  },
  _removeMaterialListListeners: function (materialList) {
    materialList.select('.forgeWorkspaceMaterialTitle').invoke("stopObserving", "click", this._materialTitleClickListener);
    materialList.select('.forgeWorkspaceMaterialStar').invoke("stopObserving", "click", this._materialStarClickListener);
    materialList.select('.forgeWorkspaceMaterialEditLink').invoke("stopObserving", "click", this._materialEditLinkClickListener);
    materialList.select('.forgeWorkspaceMaterialMoveLink').invoke("stopObserving", "click", this._materialMoveLinkClickListener);
    materialList.select('.forgeWorkspaceMaterialDeleteLink').invoke("stopObserving", "click", this._materialDeleteLinkClickListener);
    materialList.select('.forgeWorkspaceMaterialPrintToPdfLink').invoke("stopObserving", "click", this._materialPrintToPdfLinkClickListener);
    materialList.select('.forgeWorkspaceMaterialPublishLink').invoke("stopObserving", "click", this._onMaterialPublishLinkClick);
    materialList.select('.forgeWorkspaceMaterialUnpublishLink').invoke("stopObserving", "click", this._materialUnpublishLinkClickListener);
    materialList.select('.forgeWorkspaceMaterialShareLink').invoke("stopObserving", "click", this._materialShareLinkClickListener);
    materialList.select('.forgeWorkspaceMaterialParentLink').invoke("stopObserving", "click", this._materialParentLinkClickListener);
  },
  _addStarredMaterialShowMoreListener: function () {
    var showMoreLink = this._starredMaterialList.down('.forgeWorkspaceMaterialListShowMoreLink');
    if (showMoreLink)
      Event.observe(showMoreLink, "click", this._starredMaterialListShowMoreClickListener);
  },
  _removeStarredMaterialShowMoreListener: function () {
    var showMoreLink = this._starredMaterialList.down('.forgeWorkspaceMaterialListShowMoreLink');
    if (showMoreLink)
      Event.observe(showMoreLink, "click", this._starredMaterialListShowMoreClickListener);
  },
  _loadFolder: function (folderId) {
    this._loadMaterialLists([{
      id: 'materials',
      folderId: folderId,
      unfoldOnFinish: true
    }]);
    
    this._currentFolderId = folderId;
  },
  _startMaterialListsLoading: function (lists) {
    for (var i = 0, l = lists.length; i < l; i++) {
      this._startMaterialListLoading(this._getMaterialListById(lists[i]));
    }
  },
  _startMaterialListLoading: function (materialList) {
    if (materialList.visible()) {
      this._foldMaterialList(materialList);
      materialList.addClassName("forgeWorkspaceMaterialListLoading");
    }
  },
  _foldMaterialList: function (materialList) {
    if (this._unfoldEffect)
      this._unfoldEffect.finish();

    var materialListContent = materialList.down('.forgeWorkspaceMaterialListContent');
    
    this._foldEffect = new S2.FX.SlideUp(materialListContent, {
      after: function () {
        materialList.addClassName("forgeWorkspaceMaterialListFolded");
      }
    }).play();
  },
  _unfoldMaterialList: function (materialList) {
    if (this._foldEffect)
      this._foldEffect.finish();
    
    var materialListContent = materialList.down('.forgeWorkspaceMaterialListContent');
    
    this._unfoldEffect = new S2.FX.SlideDown(materialListContent, {
      after: function () {
        materialList.removeClassName("forgeWorkspaceMaterialListFolded");
      }
    }).play();
  },
  _materialListFolded: function (materialList) {
    return materialList.hasClassName("forgeWorkspaceMaterialListFolded");
  },
  viewMaterial: function (materialId, materialType, materialArchetype, materialTitle, materialPath) {
    if (materialId) {
      var windowComponentId = 'material-view-window-' + materialId;
      var windowComponent = this._getWindowComponentById(windowComponentId);
      if (windowComponent) {
        this.openWindow(windowComponent);
      } else {
        var _this = this;
        API.get(CONTEXTPATH + '/v1/materials/-/' + materialId + '/markView', {
          onSuccess: function (jsonResponse) {
            _this._markReloadNeed('viewed');
            
            switch (materialType) {
              case 'GOOGLE_DOCUMENT':
                _this.addWindow(new GoogleDocumentViewer({
                  materialTitle: materialTitle,
                  materialId: materialId,
                  materialArchetype: materialArchetype,
                  id: windowComponentId
                }));
              break;
              default:
                switch (materialArchetype) {
                  case 'IMAGE':
                    _this.addWindow(new ImageViewer({
                      materialTitle: materialTitle,
                      materialId: materialId,
                      id: windowComponentId
                    }));
                  break;
                  case 'DOCUMENT':
                    _this.addWindow(new DocumentViewer({
                      materialTitle: materialTitle,
                      materialId: materialId,
                      id: windowComponentId
                    }));
                  break;
                  case 'VECTOR_IMAGE':
                    _this.addWindow(new VectorImageViewer({
                      materialTitle: materialTitle,
                      materialId: materialId,
                      id: windowComponentId
                    }));
                  break;
                  case 'PDF':
                  case 'FILE':
                    window.location.href = CONTEXTPATH + '/' + materialPath;
                  break;
                  case 'FOLDER':
                    _this._loadFolder(materialId);
                  break;
                  default:
                    alert('Unimplemented material viewer:' + materialType);
                  break;
                }
              break;
            }
          }
        });    
      }
    } else {
      // Root folder
      this._loadFolder(null);
    }
  },
  createMaterial: function (materialType) {
    this._markReloadNeed('modified');
    this._markReloadNeed('materials');
    
    switch (materialType) {
      case 'DOCUMENT':
        this.addWindow(new DocumentEditor({
          documentId: 'NEW'
        }));
      break;
      case 'VECTOR_IMAGE':
        this.addWindow(new VectorImageEditor({
          vectorImageId: 'NEW'
        }));
      break;
      default:
        alert(materialType);
      break;
    }
  },
  _markReloadNeed: function (listId) {
    if (this._needsReloading.indexOf(listId) == -1)
      this._needsReloading.push(listId);
  },
  _reloadRequestedLists: function () {
    if (this._needsReloading.length > 0) {
      var listStates = this._getMaterialListStates(this._needsReloading);
      this._needsReloading.clear();
      this._loadMaterialLists(listStates);
    }
  },
  _getMaterialListStates: function (lists) {
    if (!lists) {
      lists = ['materials', 'starred', 'modified', 'viewed', 'found'];
    }
    
    var result = new Array();
    
    if  (lists.indexOf('materials') > -1) {
      result.push({
        id : 'materials',
        folderId: this._currentFolderId,
        unfoldOnFinish : !this._materialListFolded(this._getMaterialListById('materials'))
      });
    }
    
    if  (lists.indexOf('starred') > -1) {
      result.push({
        id : 'starred',
        // TODO: showAll: true, 
        unfoldOnFinish : !this._materialListFolded(this._getMaterialListById('starred'))
      });
    }
    
    if  (lists.indexOf('modified') > -1) {
      result.push({
        id : 'modified',
        unfoldOnFinish : !this._materialListFolded(this._getMaterialListById('modified'))
      });
    }
    
    if  (lists.indexOf('viewed') > -1) {
      result.push({
        id : 'viewed',
        unfoldOnFinish : !this._materialListFolded(this._getMaterialListById('viewed'))
      });
    }

    if  (lists.indexOf('found') > -1) {
      result.push({
        id : 'found',
        unfoldOnFinish : !this._materialListFolded(this._getMaterialListById('found'))
      });
    }
    return result;
  },
  _getMaterialListById: function (id) {
    switch (id) {
      case 'materials':
        return this._materialList;
      case 'starred':
        return this._starredMaterialList;
      case 'modified':
        return this._recentlyModifiedMaterialList;
      case 'viewed':
        return this._recentlyModifiedMaterialList;
      case 'found':
        return this._foundMaterialList;
    }
  },
  _loadMaterialLists: function (lists) {
    for (var i = 0, l = lists.length; i < l; i++) {
      var list = lists[i];
      
      switch (list.id) {
        case 'materials':
          this._loadMaterialList(list.folderId, list.unfoldOnFinish);
        break;
        case 'starred':
          this._loadStarredMaterialList(list.showAll, list.unfoldOnFinish);
        break;
        case 'modified':
          this._loadModifiedMaterialList(list.unfoldOnFinish);
        break;
        case 'viewed':
          this._loadViewedMaterialList(list.unfoldOnFinish);
        break;
        case 'found':
          var _this = this;
          this._searchMaterials($('forgeMaterialSearch').value, function () {
            _this._foundMaterialList.removeClassName("forgeWorkspaceMaterialListLoading");
            if (list.unfoldOnFinish)
              _this._unfoldMaterialList(_this._foundMaterialList);
          });
        break;
      }
    }
  },
  _loadMaterialList: function (folderId, unfoldOnFinish) {
    var _this = this;
    this._removeMaterialListListeners(this._materialList);
    this._materialList.addClassName("forgeWorkspaceMaterialListLoading");
    this._foldMaterialList(this._materialList);
    new Ajax.Request(CONTEXTPATH + '/forge/materiallist.page' + (folderId ? '?parentFolderId=' + folderId : ''), {
      onComplete: function (transport) {
        _this._materialList.removeClassName("forgeWorkspaceMaterialListLoading");
      },
      onSuccess: function (transport) {
        _this._materialList.down('.forgeWorkspaceMaterialListContent').update(transport.responseText);
        _this._addMaterialListListeners(_this._materialList);
        
        if (unfoldOnFinish)
          _this._unfoldMaterialList(_this._materialList);
      },
      onFailure: function (transport) {
        getNotificationQueue().addNotification(new NotificationMessage({
          text: transport.responseText,
          className: 'errorMessage'
        }));
      }
    });
  },
  _loadStarredMaterialList: function (showAll, unfoldOnFinish) {
    this._foldMaterialList(this._starredMaterialList);
    this._starredMaterialList.addClassName("forgeWorkspaceMaterialListLoading");
    var _this = this;
    
    new Ajax.Request(CONTEXTPATH + '/forge/starredmateriallist.page' + (showAll ? '?showAll=1' : ''), {
      onComplete: function (transport) {
        _this._starredMaterialList.removeClassName("forgeWorkspaceMaterialListLoading");
      },
      onSuccess: function (transport) {
        _this._removeMaterialListListeners(_this._starredMaterialList);
        _this._removeStarredMaterialShowMoreListener();
        _this._starredMaterialList.down('.forgeWorkspaceMaterialListContent').update(transport.responseText);
        _this._addMaterialListListeners(_this._starredMaterialList);
        _this._addStarredMaterialShowMoreListener();
        
        if (unfoldOnFinish)
          _this._unfoldMaterialList(_this._starredMaterialList);
      },
      onFailure: function (transport) {
        getNotificationQueue().addNotification(new NotificationMessage({
          text: transport.responseText,
          className: 'errorMessage'
        }));
      }
    });
  },
  _loadModifiedMaterialList: function (unfoldOnFinish) {
    this._foldMaterialList(this._recentlyModifiedMaterialList);
    this._recentlyModifiedMaterialList.addClassName("forgeWorkspaceMaterialListLoading");
    var _this = this;
    
    new Ajax.Request(CONTEXTPATH + '/forge/modifiedmateriallist.page', {
      onComplete: function (transport) {
        _this._recentlyModifiedMaterialList.removeClassName("forgeWorkspaceMaterialListLoading");
      },
      onSuccess: function (transport) {
        _this._removeMaterialListListeners(_this._recentlyModifiedMaterialList);
        _this._recentlyModifiedMaterialList.down('.forgeWorkspaceMaterialListContent').update(transport.responseText);
        _this._addMaterialListListeners(_this._recentlyModifiedMaterialList);
        
        if (unfoldOnFinish)
          _this._unfoldMaterialList(_this._recentlyModifiedMaterialList);
      },
      onFailure: function (transport) {
        getNotificationQueue().addNotification(new NotificationMessage({
          text: transport.responseText,
          className: 'errorMessage'
        }));
      }
    });
  },
  _loadViewedMaterialList: function (unfoldOnFinish) {
    this._foldMaterialList(this._recentlyViewedMaterialList);
    this._recentlyViewedMaterialList.addClassName("forgeWorkspaceMaterialListLoading");
    var _this = this;
    
    new Ajax.Request(CONTEXTPATH + '/forge/viewedmateriallist.page', {
      onComplete: function (transport) {
        _this._recentlyViewedMaterialList.removeClassName("forgeWorkspaceMaterialListLoading");
      },
      onSuccess: function (transport) {
        _this._removeMaterialListListeners(_this._recentlyViewedMaterialList);
        _this._recentlyViewedMaterialList.down('.forgeWorkspaceMaterialListContent').update(transport.responseText);
        _this._addMaterialListListeners(_this._recentlyViewedMaterialList);
        
        if (unfoldOnFinish)
          _this._unfoldMaterialList(_this._recentlyViewedMaterialList);
      },
      onFailure: function (transport) {
        getNotificationQueue().addNotification(new NotificationMessage({
          text: transport.responseText,
          className: 'errorMessage'
        }));
      }
    });
  },
  _scheduleSearch: function () {
    this._searchAfter = new Date().getTime() + 200;
  },
  _checkScheduled: function () {
    if (!this._searching && this._searchAfter) {
      var now = new Date().getTime();
      
      if (now > this._searchAfter) {
        this._searchMaterials($('forgeMaterialSearch').value, function () {
          
        });
      }
    }
  },
  _searchMaterials: function (text, callback) {
    var _this = this;
    
    if (text.blank()) {
      this._foundMaterialList.hide();
    } else {
      this._foundMaterialList.show();
      this._searching = true;
      this._searchAfter = null;
      this._clearMaterialList(this._foundMaterialList);
      
      $('forgeMaterialSearch').addClassName('forgeMaterialSearchLoading');
      API.get(CONTEXTPATH + '/v1/materials/-/search', {
        parameters: {
          text: text
        },
        onSuccess: function (jsonResponse) {
          _this._searching = false;
          $('forgeFoundMaterials').show();
          $('forgeMaterialSearch').removeClassName('forgeMaterialSearchLoading');
          var messageContainer = $('forgeFoundMaterials').down('.forgeWorkspaceMaterialListMessage');
          
          var materials = jsonResponse.response.materials;
          if (materials.length > 0) {
            messageContainer.hide();
            for (var i = 0, l = materials.length; i < l; i++) {
              _this._addMaterialListItem(_this._foundMaterialList, 
                  materials[i].entity.id, materials[i].entity.type, materials[i].entity.archetype, 
                  materials[i].entity.title, materials[i].entity.created, materials[i].entity.creator.fullName, 
                  materials[i].entity.typeProperties.editable, materials[i].entity.typeProperties.printableToPdf,
                  materials[i].entity.starred, materials[i].entity.mayDelete, materials[i].entity.mayEdit);
            }
          } else {
            messageContainer.show();
          }

          _this._checkScheduled();
          
          if (Object.isFunction(callback)) {
            callback();
          }
        }
      });
    }
  },
  _onWindowUndocked: function(event) {
    if (this._openWindow != event.window) {
      if (this._openWindow) 
        this._dockingBar.dockWindow(this._openWindow);
    
      this._openWindow = event.window;
    }
  },
  _onWindowMinimize: function (event) {
    this.minimizeWindow(event.component);  
  },
  _onWindowMaximize: function (event) {
    var window = event.component;
    document.body.appendChild(window.domNode);
    window.domNode.addClassName("forgeWorkspaceWindowMaximized");
  },
  _onWindowRestoreMaximized: function (event) {
    var window = event.component;
    this._windowContainer.appendChild(window.domNode);
    window.domNode.removeClassName("forgeWorkspaceWindowMaximized");
  },
  _onWindowClose: function (event) {
    if (this._openWindow == event.component) {
      this._reloadRequestedLists();
      this._openWindow = undefined;
    }
  },
  _onMaterialStarClick: function (event) {
    var element = Event.element(event);
    var materialId = element.up('li').down('input[name="materialId"]').value;
    var isStarred = element.hasClassName('forgeWorkspaceMaterialStarred');
    var url = isStarred ? '/v1/materials/-/' + materialId + '/unstar' : '/v1/materials/-/' + materialId + '/star';
    
    this._startMaterialListLoading(this._getMaterialListById("starred"));
    var listStates = this._getMaterialListStates(["starred"]);

    var _this = this;
    API.get(CONTEXTPATH + url, {
      onComplete: function (transport) {
        if (isStarred) {
          _this._materialLists.select('.forgeWorkspaceMaterial input[value="' + materialId + '"]').each(function (inputElement) {
            var starElement = inputElement.up('.forgeWorkspaceMaterial').down('.forgeWorkspaceMaterialStar');
            starElement.removeClassName('forgeWorkspaceMaterialStarred');
          });
        } else {
          _this._materialLists.select('.forgeWorkspaceMaterial input[value="' + materialId + '"]').each(function (inputElement) {
            var starElement = inputElement.up('.forgeWorkspaceMaterial').down('.forgeWorkspaceMaterialStar');
            starElement.addClassName('forgeWorkspaceMaterialStarred');
          });
        }
        
        _this._loadMaterialLists(listStates);
      }
    });
  },
  _onMaterialTitleClick: function (event) {
    var element = Event.element(event);
    
    var li = element.up('li');
    var materialId = li.down('input[name="materialId"]').value;
    var materialType = li.down('input[name="materialType"]').value;
    var materialArchetype = li.down('input[name="materialArchetype"]').value;
    var materialTitle = li.down('.forgeWorkspaceMaterialTitle').innerHTML;
    var materialPath = li.down('input[name="materialPath"]').value;
    
    this.viewMaterial(materialId, materialType, materialArchetype, materialTitle, materialPath);
  },
  _onMaterialListTitleClick: function (event) {
    var element = Event.element(event);
    var materialList = element.up('.forgeWorkspaceMaterialList');
    var materialListContent = materialList.down('.forgeWorkspaceMaterialListContent');
    
    if (materialListContent.visible()) {
      this._foldMaterialList(materialList);
    } else {
      this._unfoldMaterialList(materialList);
    }
  },
  _onMaterialEditLinkClick: function (event) {
    var element = Event.element(event);
    
    var li = element.up('li');
    var materialId = li.down('input[name="materialId"]').value;
    var materialType = li.down('input[name="materialType"]').value;
    var materialTitle = li.down('.forgeWorkspaceMaterialTitle').innerHTML;
    
    this.editMaterial(materialId, materialType, materialTitle);
  },
  _onMaterialMoveLinkClick: function (event) {
    var element = Event.element(event);
    
    var li = element.up('li');
    var materialId = li.down('input[name="materialId"]').value;
    var dialog = new MoveToFolderDialogController({
      materialId: materialId
    });
    dialog.open();
  },
  _onMaterialDeleteLinkClick: function (event) {
    var element = Event.element(event);
    
    var li = element.up('li');
    var materialId = li.down('input[name="materialId"]').value;
    var materialType = li.down('input[name="materialType"]').value;
    var materialTitle = li.down('.forgeWorkspaceMaterialTitle').innerHTML.strip();
    
    var text = '';
    
    switch (materialType) {
      case 'DOCUMENT':
        text = getLocale().getText('forge.removeMaterial.dialogTextDocument', materialTitle);
      break;
      case 'IMAGE':
        text = getLocale().getText('forge.removeMaterial.dialogTextImage', materialTitle);
      break;
      case 'PDF':
        text = getLocale().getText('forge.removeMaterial.dialogTextPdf', materialTitle);
      break;
      case 'FILE':
        text = getLocale().getText('forge.removeMaterial.dialogTextFile', materialTitle);
      break;
      case 'FOLDER':
        text = getLocale().getText('forge.removeMaterial.dialogTextFolder', materialTitle);
      break;
      case 'VECTOR_IMAGE':
        text = getLocale().getText('forge.removeMaterial.dialogTextVectorImage', materialTitle);
      break;
      case 'GOOGLE_DOCUMENT':
        text = getLocale().getText('forge.removeMaterial.dialogTextGoogleDocument', materialTitle);
      break;
      case 'DROPBOX_ROOT_FOLDER':
        text = getLocale().getText('forge.removeMaterial.dialogTextDropboxRootFolder');
      break;
      case 'UBUNTU_ONE_ROOT_FOLDER':
        text = getLocale().getText('forge.removeMaterial.dialogTextUbuntuOneRootFolder');
      break;
    }
    
    var _this = this;
    var dialog = new ConfirmDialogController({
      title: getLocale().getText('forge.removeMaterial.dialogTitle'),
      text: text,
      buttons: [{
        label: getLocale().getText('forge.removeMaterial.dialogCancelButton'),
        type: 'cancel'
      }, {
        label: getLocale().getText('forge.removeMaterial.dialogDeleteButton'),
        type: 'delete',
        formValid: true,
        onClick: function (event) {
          event.dialog.close();
          var listStates = _this._getMaterialListStates(['materials', 'starred', 'modified', 'viewed', 'found']);
          _this._startMaterialListsLoading(['materials', 'starred', 'modified', 'viewed', 'found']);
          API.post(CONTEXTPATH + '/v1/materials/-/' + materialId + '/delete', {
            onComplete: function (transport) {
              _this._loadMaterialLists(listStates);
            }
          });
        }
      }]
    });
    
    dialog.open();
  },
  _onMaterialPrintToPdfLinkClick: function (event) {
    var element = Event.element(event);
    
    var li = element.up('li');
    var materialId = li.down('input[name="materialId"]').value;

    var listStates = this._getMaterialListStates(["modified", "materials", "found"]);
    this._startMaterialListsLoading(["modified", "materials", "found"]);
    
    var _this = this;
    API.post(CONTEXTPATH + '/v1/materials/documents/' + materialId + '/printToPdf', {
      onComplete: function (transport) {
        _this._loadMaterialLists(listStates);
      }
    });
  },
  _onStarredMaterialListShowMoreClick: function (event) {
    var listStates = this._getMaterialListStates(["starred"]);
    listStates[0].showAll = true;
    this._loadMaterialLists(listStates);
  },
  _onMaterialShareLinkClick: function (event) {
    var element = Event.element(event);
    
    var li = element.up('li');
    var materialId = li.down('input[name="materialId"]').value;
    
    var dialog = new ShareMaterialDialogController({
      materialId: materialId
    });
    dialog.open();
  },
  _onMaterialParentinkClick: function (event) {
    var element = Event.element(event);
    
    var container = element.up('.forgeWorkspaceMaterialParentContainer');
    var parentId = container.down('input[name="parentId"]').value;
    
    this._loadFolder(parentId);
  },
  _onSearchChange: function (event) {
    Event.stop(event);
    this._scheduleSearch();

    var _this = this;
    setTimeout(function () {
      _this._checkScheduled();
    }, 300);
  },
  _onSearchKeyUp: function (event) {
    Event.stop(event);
    this._scheduleSearch();

    var _this = this;
    setTimeout(function () {
      _this._checkScheduled();
    }, 300);
  }
});

window._workspaceController = null;

function initializeWorkspaceController(options) {
  window._workspaceController = new ForgeWorkspaceController(options);
};

function getWorkspaceController() {
  return window._workspaceController;
};