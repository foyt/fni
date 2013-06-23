IndexViewController = Class.create({
  initialize : function() {
    this._articleLinkClickListener = this._onArticleLinkClick.bindAsEventListener(this);
    this._defaultArticleLinkClickListener = this._onDefaultArticleLinkClick.bindAsEventListener(this);
  },
  destroy: function () {
    $('indexSidebarContent').select('.articleLink').invoke("stopObserving", "click", this._articleLinkClickListener);
    $('indexSidebarContent').select('.defaultArticleLink').invoke("stopObserving", "click", this._defaultArticleLinkClickListener);
  },
  setup: function () {
    // TODO: Test cookie support
    
    $('indexSidebarContent').select('.articleLink').invoke("observe", "click", this._articleLinkClickListener);
    $('indexSidebarContent').select('.defaultArticleLink').invoke("observe", "click", this._defaultArticleLinkClickListener);
    
    var action = getJsVariable("action");
    if (action) {
      var actionParameters = getJsVariable("actionParameters");
      this._handleAction(action, actionParameters);
    }
  },
  openArticle: function (articleId) {
    $('frontPageArticle').down('.frontPageArticleLoading').show();
    API.get(CONTEXTPATH + '/v1/articles/' + articleId, {
      onComplete: function () {
        $('frontPageArticle').down('.frontPageArticleLoading').hide();
      },
      onSuccess: function (jsonResponse) {
        var article = jsonResponse.response;
        
        var frontPageArticle = $('frontPageArticle');
        frontPageArticle.down('.frontPageArticleTitle').update(article.title);
        frontPageArticle.down('.frontPageArticleContent').update(article.content);
      }
    });
  },
  _handleAction: function (action, actionParameters) {
    var parameters = new Hash();
    
    if (actionParameters) {
      var parametersArray = actionParameters.split(';');
      for (var i = 0, l = parametersArray.length; i < l; i++) {
        var parameterArray = parametersArray[i].split(':');
        parameters.set(parameterArray[0], parameterArray[1]);
      }
    }
    
    switch (action) {
      case 'viewmessage':
        this._handleViewMessageAction(parameters);
      break;
    }
  },
  _handleViewMessageAction: function (parameters) {
    var messageId = parameters.get('messageId');
    // If no message id is found, the request is invalid and thus should be ignored
    if (messageId) {
      // Viewing messages requires authentication. If user is not logged in we need to login first.
      if (!isLoggedIn()) {
        window.location.href = CONTEXTPATH + '/login?redirectUrl=' + encodeURIComponent(window.location.href); 
      } else {
        var messagesWidget = siteMenuBarController.getWidget('messages');
        var onFolderLoad = null;
        onFolderLoad = function (event) {
          messagesWidget.removeListener("folderLoad", window, onFolderLoad);
          messagesWidget.viewMessage(messageId);
        };
        
        messagesWidget.addListener("folderLoad", window, onFolderLoad, 9999);
        siteMenuBarController.openMenuWidget(messagesWidget);
      }
    }
  },
  _onArticleLinkClick: function (event) {
    var list = Event.element(event).up('li');
    var articleId = list.down('input[name="articleId"]').value;
    
    this.openArticle(articleId);
  },
  _onDefaultArticleLinkClick: function (event) {
    var list = Event.element(event).up('li');
    var articleId = list.down('input[name="articleId"]').value;
    
    var _this = this;
    API.put(CONTEXTPATH + '/v1/articles/setLocaleDefault/' + articleId, {
      onSuccess: function (jsonResponse) {
        var article = jsonResponse.response;
        _this.openArticle(article.id);
      }
    });
  }
});
