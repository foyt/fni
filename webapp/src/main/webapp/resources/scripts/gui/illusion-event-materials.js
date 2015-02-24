(function() {
  'use strict';
  
  $(document).ready(function () {
    $('.export-character-sheet-xls').click(function (event) {
      event.preventDefault();
      var id = $(event.target).closest('.illusion-event-material').data('id');
      var eventId = $('#event-id').val();
      window.location.href = CONTEXTPATH + '/rest/illusion/events/' + eventId + '/characterSheets/' + id + '/data';
    });
  });

}).call(this);