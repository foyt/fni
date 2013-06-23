AbstractViewController = Class.create({
  initialize : function() {
    this._searching = false;
    this._searchAfter = null;
    
    this._searcKeyupListener = this._onSearchKeyUp.bindAsEventListener(this);
    this._searchInputChangeListener = this._onSearchInputChange.bindAsEventListener(this);
  },
  setup: function () {
    Event.observe($('forumSearchInput'), 'keyup', this._searcKeyupListener);
    Event.observe($('forumSearchInput'), 'change', this._searchInputChangeListener);
  },
  destroy: function () {
    Event.stopObserving($('forumSearchInput'), 'keyup', this._searcKeyupListener);
    Event.stopObserving($('forumSearchInput'), 'change', this._searchInputChangeListener);
  },
  _searchForum: function (text) {
    var _this = this;
    
    if (text.blank()) {
      $('forumSearchResults').hide().update('');
    } else {
      this._searching = true;
      this._searchAfter = null;
      $('forumSearchInput').addClassName('forumSearchInputLoading');
      API.get(CONTEXTPATH + '/v1/forum/search', {
        parameters: {
          text: text
        },
        onSuccess: function (jsonResponse) {
          _this._searching = false;
          $('forumSearchInput').removeClassName('forumSearchInputLoading');
          $('forumSearchResults').hide().update('');
  
          var posts = jsonResponse.response.posts;
          for (var i = 0, l = posts.length; i < l; i++) {
            var post = posts[i];
            _this._addSearchResult(post.title, post.link, post.text);
          }
          
          _this._checkScheduled();
        }
      });
    }
  },
  _addSearchResult: function (title, href, text) {
    var li = new Element('li', {
      className: 'forumSearchResult'
    });
    
    var link = new Element("a", {
      href: href
    }).update(title);
    
    var content = new Element("div", {
      className: "forumSearchResultText"
    }).update(text);
    
    li.appendChild(link);
    li.appendChild(content);
    
    $('forumSearchResults').show().appendChild(li);
    
    return li;
  },
  _scheduleSearch: function () {
    this._searchAfter = new Date().getTime() + 200;
  },
  _checkScheduled: function () {
    if (!this._searching && this._searchAfter) {
      var now = new Date().getTime();
      
      if (now > this._searchAfter)
        this._searchForum($('forumSearchInput').value);
    }
  },
  _onSearchKeyUp: function (event) {
    Event.stop(event);
    this._scheduleSearch();

    var _this = this;
    setTimeout(function () {
      _this._checkScheduled();
    }, 300);
  },
  _onSearchInputChange: function (event) {
    Event.stop(event);
    this._scheduleSearch();

    var _this = this;
    setTimeout(function () {
      _this._checkScheduled();
    }, 300);
  }
});