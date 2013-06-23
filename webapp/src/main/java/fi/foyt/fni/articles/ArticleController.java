package fi.foyt.fni.articles;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.math.NumberUtils;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.materials.PublishedArticleDAO;
import fi.foyt.fni.persistence.model.materials.Document;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.PublishedArticle;
import fi.foyt.fni.persistence.model.materials.PublishedArticleType;
import fi.foyt.fni.system.SystemSettingsController;

@RequestScoped
@Stateful
public class ArticleController {

	private static final String DEFAULT_ARTICLE_KEY = "contents.articles.default_";

	@Inject
	private SystemSettingsController systemSettingsController;

	@Inject
	@DAO
	private PublishedArticleDAO publishedArticleDAO;
	
	public ArticleDataBean getArticleData(PublishedArticle article) throws UnsupportedEncodingException {
		String title = "";
    String content = "";
    
    Material material = article.getMaterial();
    switch (material.getType()) {
      case DOCUMENT:
      	Document document = (Document) material;
      	title = document.getTitle();
      	content = new String(document.getData(), "UTF-8");
      break;
      default:
      	return null;
    }
    
    return new ArticleDataBean(article.getId(), title, content);
	}

	public PublishedArticle getLocaleDefaultArticle(Locale locale) {
		String setting = systemSettingsController.getSetting(getLocaleDefaultArticleKey(locale));
		if (NumberUtils.isNumber(setting)) {
			return publishedArticleDAO.findById(NumberUtils.createLong(setting));
		}
		
		return null;
	}
	
	public void setLocaleDefaultArticle(Locale locale, PublishedArticle article) {
		systemSettingsController.updateSetting(getLocaleDefaultArticleKey(locale), article.getId().toString());
	}
	
	private String getLocaleDefaultArticleKey(Locale locale) {
		return DEFAULT_ARTICLE_KEY + locale.getLanguage();
	}

	public List<PublishedArticle> listLatestPublishedArticles(int count) {
		return publishedArticleDAO.listByTypeSortByCreated(PublishedArticleType.ARTICLE, 0, count);
	}

	public List<PublishedArticle> listLatestTechnicalAnnouncements(int count) {
		return publishedArticleDAO.listByTypeSortByCreated(PublishedArticleType.TECHNICAL, 0, count);
	}
}
