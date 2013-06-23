ViewForumViewController = Class.create(AbstractViewController, {
  initialize : function($super) {
    $super();
    
    this._newTopicButtonClickListener = this._onNewTopicButtonClick.bindAsEventListener(this);
    this._createNewTopicButtonClickListener = this._onCreateNewTopicButtonClick.bindAsEventListener(this);
    this._editorVisible = false;
  },
  destroy: function ($super) {
    $super();

    if (this._ckInstance) {
      this._ckInstance.destroy();
    }
    
    $$('.forumNewTopicButton').invoke('purge');
  },
  setup: function ($super) {
    $super();

    var _this = this;
    $$('.forumNewTopicButton').each(function (element) {
      Event.observe(element, "click", _this._newTopicButtonClickListener);
    });
  },
  _onNewTopicButtonClick: function (event) {
    var newTopicButton = Event.element(event);
    newTopicButton.hide();
    var newTopicContainer = $('forumNewTopicContainer');
    
    if (this._editorVisible === false) {
      newTopicContainer.setStyle({
        display: ''
      });
      
      CKEDITOR.on('instanceReady', function(evt) {
        initializeValidation(newTopicContainer);
      });
      
      this._ckInstance = CKEDITOR.replace('newTopicContent', {
        toolbar: 'ForumPost'
      });
      
      this._subjectInput = $$('input[name="newTopicSubject"]')[0];

      var createNewTopicButton = newTopicContainer.down('input[name="createNewTopicButton"]');

      Event.observe(createNewTopicButton, "click", this._createNewTopicButtonClickListener);
      
      this._editorVisible = true;
    }
    
    var elementTop = newTopicContainer.getLayout().get('top');
    var windowTop = document.viewport.getScrollOffsets().top;
    
    var scrollEffect = new S2.FX.Attribute(window, windowTop, elementTop, {
      duration : 1
    }, function(t) {
      window.scrollTo(0, t);
    });
    
    scrollEffect.play().start();
  },
  _onCreateNewTopicButtonClick: function (event) {
    Event.stop(event);
    
    var subject = this._subjectInput.value;
    var content = this._ckInstance.getData();  
    var forumId = $(document.body).down('input[name="forumId"]').value;
    
    API.post(CONTEXTPATH + '/v1/forum/' + forumId + '/createTopic', {
      parameters: {
        subject: subject,
        content: content
      },
      onSuccess: function (jsonResponse) {
        window.location.href = CONTEXTPATH + '/forum/' + jsonResponse.response.fullPath;
      }
    });
  }
});
