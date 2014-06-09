(function() {
  /* global hex_md5 */
  
  $.widget("custom.collaborators", {
    
    _create: function () {
    },
    
    addCollaborator: function (sessionId, name, email) {
      $(this.element).append(
        $('<div>').collaborator({
          sessionId: sessionId,
          name: name,
          email: email
        })
      );
    },
    
    removeCollaborator: function (sessionId) {
      $(this.element).find('#collaborator-' + sessionId).hide("blind", function () {
        $(this).remove();
      });
    },
    
    _destroy : function() {
      
    }
  });
  
  $.widget("custom.collaborator", {
    options: {
      gravatarDefault: 'retro',
      gravatarRating: 'g',
      gravatarSize: 32
    },
    _create: function () {
      $(this.element)
        .addClass('collaborator')
        .attr({
          id: 'collaborator-' + this.options.sessionId,
        })
        .append($('<img>')
          .attr({
            title: this.options.name,
            src: '//www.gravatar.com/avatar/' + hex_md5(this.options.email) +
              '?d=' + this.options.gravatarDefault +
              '&r=' + this.options.gravatarRating +
              "&s=" + this.options.gravatarSize
          })
        );
    },
    
    _destroy : function() {
      
    }
  });
    
}).call(this);