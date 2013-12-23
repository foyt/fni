(function() {
  'use strict';
  
  $.widget("custom.illusionParticipant", {
    options : {},
    _create : function() {
      this.element
        .addClass('illusion-participant')
        .append(
          $('<img>').attr('src', this.options.imageUrl)
        ).click(function () {
          $('.illusion-table').hide();
          var table = $(this).illusionParticipant("option", "table");
          $(table)
            .show()
            .illusionTable('changeImage', 'static', {
              imageUrl: $(this).illusionParticipant("option", "imageUrl")
            });
        });
    },
    _destroy : function() {
    }
  });
  
  $.widget("custom.illusionTable", {
    options : {},
    _create : function() {
      this.element
        .tosstable()
        .addClass('illusion-table');
    },
    changeImage: function (type, opts) {
      switch (type) {
        case 'static':
          $(this.element)
            .css({
              'background-image': "url('" + opts.imageUrl + "'",
              'background-size': '90% 90%',
              'background-position': 'center', 
              'background-repeat': 'no-repeat'
            });
        break;
      }
    },
      
    _destroy : function() {
    }
  });
  
  
  function createParticipant(imageUrl) {
    var table = $('<div>')
      .illusionTable()
      .hide();
    
    var icon = $('<div>')
      .illusionParticipant({
        table: table,
        imageUrl: imageUrl
      });
    
    $('.illusion-participants').append(icon);
    $('.illusion-tables').append(table);
  }
  
  $(document).ready(function() {
    $('.illusion-item').tossable();
    createParticipant('http://farm5.staticflickr.com/4118/4857026160_be2146d4fa.jpg');
    createParticipant('http://farm3.staticflickr.com/2625/3774896826_a9eb810112.jpg');
    createParticipant('http://farm9.staticflickr.com/8037/8056870326_24befc3c2f.jpg');
    createParticipant('http://farm2.staticflickr.com/1233/4726132269_aa71fe7a4c.jpg');
  });
  
  $(document).on("illusion-table-show", ".illusion-table", function (e) {
    
  });

}).call(this);