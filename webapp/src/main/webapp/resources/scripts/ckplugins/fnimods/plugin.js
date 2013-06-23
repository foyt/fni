(function() {
  function getDialogElement(elementId, elements) {
    for (var i in elements) {
      element = elements[i];

      if (element.type == 'hbox' || element.type == 'vbox') {
        var result = getDialogElement(elementId, element.children);
        if (result)
          return result;
      }

      if (element.id != elementId)
        continue;
      
      return element;
    }
    
    return null;
  }
  
  CKEDITOR.plugins.add('fnimods', {
    init : function(editor, pluginPath) {
      var lang = editor.lang.fnimods; 
      var conf = editor.config.fniMods;
      if (conf) {
        CKEDITOR.on('dialogDefinition', function(evt) {
          for ( var i in evt.data.definition.contents) {
            var dialogName = evt.data.name;
            var dialogConf = conf.dialogs[dialogName]; 
            if (dialogConf) {
              var definition = evt.data.definition;
              var tab = definition.contents[i];
              var tabConf = dialogConf.tabs[tab.id];
              if (tabConf) {
                if (tabConf.hide == true)
                  tab.hidden = true;
                else {
                  if (tabConf.elements) {
                   
                    for (var elementLocator in tabConf.elements) {
                      var element = null;
                      if (elementLocator.startsWith('index:')) {
                        element = tab.elements[parseInt(elementLocator.substring(6))];
                      } else {
                        element = getDialogElement(elementLocator, tab.elements);
                      }

                      if (element) {
                        for (var setting in tabConf.elements[elementLocator]) {
                          var value = tabConf.elements[elementLocator][setting];
                          if ((typeof value == 'string') && value.startsWith('loc:')) {
                            element[setting] = lang[value.substring(4)];
                          } else
                            element[setting] = value;
                        }
                      }

                    }
                  }
                }
              }               
            }
          }
        });
      }
    }
  });
})();
