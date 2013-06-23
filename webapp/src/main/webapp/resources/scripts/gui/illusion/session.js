SessionViewController = Class.create({
  initialize : function() {
    this._createSessionButtonClickListener = this._onCreateSessionButtonClick.bindAsEventListener(this);
  },
  destroy : function() {
    var createSessionButton = $('createSession').down('input[name="createSessionButton"]');
    Event.stopObserving(createSessionButton, "click", this._createSessionButtonClickListener);
  },
  setup : function() {
    var createSessionButton = $('createSession').down('input[name="createSessionButton"]');
    Event.observe(createSessionButton, "click", this._createSessionButtonClickListener);
  },
  _onCreateSessionButtonClick : function(event) {
    Event.stop(event);

    var overlay = new S2.UI.Overlay();
    overlay.element.addClassName('loadingPane');
    $(document.body).insert(overlay);
    
    var form = Event.element(event).form;
    API.post(CONTEXTPATH + '/v1/illusion/createSession', {
      parameters: {
        name: form['createSession:name'].value
      },
      onComplete: function (transport) {
        overlay.destroy();
      },
      onSuccess: function (jsonResponse) {
        $('hiddenEnterForm').down('input[name="hiddenEnterForm:sessionId"]').value = jsonResponse.response.id;
        $('hiddenEnterForm').down('input[type="submit"]').click();
      }
    });
  }
});

var viewController = new SessionViewController();

Event.observe(document, "dom:loaded", function (event) {
  viewController.setup();
});

Event.observe(window, "beforeunload", function (event) {
  viewController.destroy();
});