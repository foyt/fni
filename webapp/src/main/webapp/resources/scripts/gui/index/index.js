(function() {
  
  function viewArticle(id) {
    $('#index-front-page-article > index-front-page-article-loading').show();
    $.ajax({
      url: CONTEXTPATH + '/v1/articles/' + id
    }).done(function(data, textStatus, jqXHR) {
      var article = data.response;
      $('#index-front-page-article > .index-front-page-article-title').html(article.title);
      $('#index-front-page-article > .index-front-page-article-content').html(article.content);
      $('#index-front-page-article > index-front-page-article-loading').hide();
    });
  };

  $(document).ready(function(){
    /* Articles */
    
    $(document).on( 'click', '.index-sidebar-article-link', function (event) {
      viewArticle($(this).data('article-id'));
    });
    
    $(document).on('click', '.index-sidebar-default-article-link', function (event) {
      $('#index-front-page-article > index-front-page-article-loading').show();
      var id = $(this).data('article-id');
      $.ajax({
        url: CONTEXTPATH + '/v1/articles/setLocaleDefault/' + id,
        method: 'PUT',
        data: {
          locale: LOCALE
        }
      }).done(function(data, textStatus, jqXHR) {
        viewArticle(id);
      });
    });
  });
  
}).call(this);