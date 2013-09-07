(function() {
  'use strict';

  /* Live Listeners */

  /**
   * Publication Admin Actions / unpublish Publication
   */
 
  $(document).on('click', '.publication-unpublish', function (event) {
    var publicationId = $(this).data('publication-id');
    var publicationName = $(this).data('publication-name');
    
    $.ajax(CONTEXTPATH + '/gamelibrary/dialogs/publicationunpublish.jsf?publicationId=' + publicationId + '&publicationName=' + publicationName, {
      async: false,
      success : function(data, textStatus, jqXHR) {
        var dialog = $(data).dialog({
          modal: true,
          width: 400,
          maxHeight: 600,
          buttons: [{
            'class': 'cancel-button',
            'text': 'Cancel',
            'click': function(event) { 
              $(this).dialog("close");
            }
          }, {
            'class': 'unpublish-button',
            'text': 'Unpublish',
            'click': function(event) { 
              executePublicationAdminAction(publicationId, 'unpublish');
            }
          }]
        });
      }
    });
  });
  
}).call(this);