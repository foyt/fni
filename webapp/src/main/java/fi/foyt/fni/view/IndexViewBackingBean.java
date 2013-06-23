package fi.foyt.fni.view;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import fi.foyt.fni.articles.ArticleController;
import fi.foyt.fni.articles.ArticleDataBean;
import fi.foyt.fni.persistence.model.materials.PublishedArticle;
import fi.foyt.fni.session.SessionController;

@RequestScoped
@Named
@Stateful
public class IndexViewBackingBean {

	@Inject
	private Logger logger;
	
	@Inject
	private SessionController sessionController;

	@Inject
	private ArticleController articleController;

	public ArticleDataBean getDefaultArticleData() {
		Locale locale = sessionController.getLocale();
		PublishedArticle defaultArticle = articleController.getLocaleDefaultArticle(locale);
		if (defaultArticle != null) {
			try {
				return articleController.getArticleData(defaultArticle);
			} catch (UnsupportedEncodingException e) {
				logger.log(Level.SEVERE, "UTF-8 not supported", e);
			}
		}
		
		return null;
	}
	
	public List<PublishedArticle> getArticles() {
		return articleController.listLatestPublishedArticles(5);
	}
	
	public List<PublishedArticle> getTechnicalAnnouncements() {
		return articleController.listLatestTechnicalAnnouncements(5);
	}

}
