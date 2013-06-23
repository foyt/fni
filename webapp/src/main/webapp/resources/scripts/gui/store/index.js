IndexViewController = Class.create({
  initialize : function() {
  },
  destroy : function() {
  },
  setup : function() {
    var viewCartButton = $('viewCartButton');

    S2.UI.addClassNames(viewCartButton, 'ui-state-default ui-corner-all');
    viewCartButton.addClassName('ui-priority-primary');

    new S2.UI.Button(viewCartButton);
  }
});
