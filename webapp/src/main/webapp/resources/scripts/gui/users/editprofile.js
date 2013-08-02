(function() {
  
  function openProfileImageUploadDialog() {
    $.ajax(CONTEXTPATH + '/users/dialogs/profileimageupload.jsf', {
      async: false,
      success : function(data, textStatus, jqXHR) {
        var dialog = $(data).dialog({
          modal: true,
          width: 800,
          maxHeight: 600,
          buttons: [{
            'class': 'cancel-button',
            'text': 'Cancel',
            'click': function(event) { 
              $(this).dialog("close");
            }
          }, {
            'class': 'update-button',
            'text': 'Update',
            'click': function(event) { 
              var form = $(this).find('form');
              var frame = $(this).find('iframe[name="' + form.attr('target') + '"]');
              var _this = this;
              
              $(frame).one('load', function () {
                var img = $('.edit-profile-basic-profile-image-container img');
                img.attr("src", CONTEXTPATH + "/users/profileImages/1?width=128&height=128&t=" + new Date().getTime());
                $(_this).dialog("close");
                $(_this).remove();
              });
              
              form.submit();
            }
          }]
        });
        
        
      }
    });
  };

  $(document).ready(function(){
    $("#edit-profile-tabs").tabs();
    
    $('#edit-profile-basic-profile-image-change').click(function (event) {
      event.preventDefault();
      openProfileImageUploadDialog();
    });
  });
  
}).call(this);
