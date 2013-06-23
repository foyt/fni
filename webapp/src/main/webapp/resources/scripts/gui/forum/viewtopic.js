ViewTopicViewController = Class.create(AbstractViewController, {
  initialize : function($super) {
    $super();
    
    this._replyButtonClickListener = this._onReplyButtonClick.bindAsEventListener(this);
    this._postReplyButtonClickListener = this._onPostReplyButtonClick.bindAsEventListener(this);
    this._editorVisible = false;
  },
  destroy: function ($super) {
    $super();
    // TODO: CKEditor
    // TODO: Reply buttons
  },
  setup: function ($super) {
    $super();
    
    var _this = this;
    $$('.forumPostReplyButton').each(function (element) {
      Event.observe(element, "click", _this._replyButtonClickListener);
    });
  },
  _onReplyButtonClick: function (event) {
    var replyButton = Event.element(event);
    replyButton.hide();
    
    var replyContainer = $('forumReplyTopicContainer');
    
    if (this._editorVisible === false) {
      replyContainer.setStyle({
        display: ''
      });
      
      this._ckInstance = CKEDITOR.replace('forumReplyTopic', {
        toolbar: 'ForumPost'
      });
      
      var replyButton = replyContainer.down('input[name="postReplyButton"]');
      Event.observe(replyButton, "click", this._postReplyButtonClickListener);
      
      this._editorVisible = true;
    }
    
    var elementTop = replyContainer.getLayout().get('top');
    var windowTop = document.viewport.getScrollOffsets().top;
    
    var scrollEffect = new S2.FX.Attribute(window, windowTop, elementTop, {
      duration : 1
    }, function(t) {
      window.scrollTo(0, t);
    });
    
    scrollEffect.play().start();
  },
  _onPostReplyButtonClick: function (event) {
    Event.stop(event);
    var data = this._ckInstance.getData();  
    var topicId = $(document.body).down('input[name="topicId"]').value;
    
    API.post(CONTEXTPATH + '/v1/forum/' + topicId + '/postReply', {
      parameters: {
        content: data
      },
      onSuccess: function (jsonResponse) {
        var postId = jsonResponse.response.id;
        var topicId = jsonResponse.response.topic.id;
        window.location.href = '?topicId=' + topicId + '#p' + postId;
        window.location.reload();
      }
    });
  }
});
