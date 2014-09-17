(function() {
  'use strict';
  
  $.widget("custom.illusionGroupMemberList", {
    options : {
    },
    _create : function() {
      var selectedMembers = ($(this.element).find('.illusion-event-group-members-list-ids').val()||'').split('&');
      for (var i = 0, l = selectedMembers.length; i < l; i++) {
        var selectedMember = selectedMembers[i];
        $(this.element)
          .find('.illusion-event-group-members-list-item[data-participant-id="' + selectedMember + '"]')
          .appendTo($(this.element).find('.illusion-event-group-members-list-column-members .illusion-event-group-members-list-column-contents'));
      }
      
      this._updateList();
            
      $(this.element).on('click', '.illusion-event-group-members-list-item', $.proxy(function (event) {
        $(this.element).find('.illusion-event-group-members-list-item-selected').removeClass('illusion-event-group-members-list-item-selected');
        $(event.target).addClass('illusion-event-group-members-list-item-selected');
        this._updateList();
      }, this));
      
      $(this.element).on('click', '.illusion-event-group-members-list-action-add', $.proxy(function (event) {
        event.preventDefault();
        $(this.element)
          .find('.illusion-event-group-members-list-item-selected')
          .appendTo($(this.element).find('.illusion-event-group-members-list-column-members .illusion-event-group-members-list-column-contents'));
        this._updateList();
      }, this));
      
      $(this.element).on('click', '.illusion-event-group-members-list-action-remove', $.proxy(function (event) {
        event.preventDefault();
        $(this.element).find('.illusion-event-group-members-list-column-participants .illusion-event-group-members-list-column-contents').append($(this.element).find('.illusion-event-group-members-list-item-selected'));
        this._updateList();
      }, this));
    },
    _updateList: function() {
      var participantSelected = $(this.element).find('.illusion-event-group-members-list-item-selected').closest('.illusion-event-group-members-list-column-participants').length > 0;
      var memberSelected = $(this.element).find('.illusion-event-group-members-list-item-selected').closest('.illusion-event-group-members-list-column-members').length > 0;
      
      if (participantSelected) {
        $(this.element).find('.illusion-event-group-members-list-action-add').removeAttr('disabled');
      } else {
        $(this.element).find('.illusion-event-group-members-list-action-add').attr('disabled', 'disabled');
      }
      
      if (memberSelected) {
        $(this.element).find('.illusion-event-group-members-list-action-remove').removeAttr('disabled');
      } else {
        $(this.element).find('.illusion-event-group-members-list-action-remove').attr('disabled', 'disabled');
      } 
      
      var ids = $.map($(this.element).find('.illusion-event-group-members-list-column-members .illusion-event-group-members-list-item'), function (item) {
        return $(item).data('participant-id');
      });
      
      $(this.element).find('.illusion-event-group-members-list-ids').val(ids.join('&'));
    }
  });
  
  $(document).ready(function() { 
    $('.illusion-event-group-members-list').illusionGroupMemberList();
    
    $('.illusion-new-group').click(function (event) {
      dust.render("illusion-event-new-group", { }, function(err, html) {
        if (!err) {
          var dialog = $(html);
          dialog.dialog({
            modal: true,
            width: 600,
            buttons: [{
              'text': dialog.data('create-button'),
              'click': function(event) {
                $.ajax(CONTEXTPATH + '/rest/illusion/events/' + $('input[name="eventUrlName"]').val() + '/groups/', {
                  type: 'POST',
                  contentType: "application/json",
                  dataType : "json",
                  data: JSON.stringify({
                    'name': $(this).find('input[name="name"]').val()
                  }),
                  accepts: {
                    'json' : 'application/json'
                  },
                  success : function(data) {
                    window.location.reload(true);
                  }
                });
                
              }
            }, {
              'text': dialog.data('cancel-button'),
              'click': function(event) {
                $(this).dialog("close");
              }
            }]
          });
        } else {
          $('.notifications').notifications('notification', 'error', err);
        }
      });
    });
  });

}).call(this);