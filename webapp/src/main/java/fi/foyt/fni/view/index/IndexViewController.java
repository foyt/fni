 package fi.foyt.fni.view.index;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.articles.ArticleController;
import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.materials.PublishedArticleDAO;
import fi.foyt.fni.persistence.model.materials.PublishedArticle;
import fi.foyt.fni.persistence.model.materials.PublishedArticleType;
import fi.foyt.fni.utils.view.ViewUtils;
import fi.foyt.fni.view.Locales;
import fi.foyt.fni.view.PageViewController;
import fi.foyt.fni.view.ViewControllerContext;
import fi.foyt.fni.view.ViewControllerException;

@RequestScoped
@Stateful
public class IndexViewController extends PageViewController {
	
	@Inject
	private ArticleController articleController;

	@Inject
	@DAO
  private PublishedArticleDAO publishedArticleDAO;

  @Override
  public boolean checkPermissions(ViewControllerContext context) {
    return true;
  }

  @Override
  public void execute(ViewControllerContext context) {
  	super.execute(context);
  	
  	PublishedArticle defaultArticle = articleController.getLocaleDefaultArticle(context.getRequest().getLocale());
  	if (defaultArticle != null) {
  		try {
	      context.getRequest().setAttribute("defaultArticle", articleController.getArticleData(defaultArticle));
      } catch (UnsupportedEncodingException e) {
        throw new ViewControllerException(Locales.getText(context.getRequest().getLocale(), "generic.error.configurationError"), e);
      }
  	}
  	
  	context.getRequest().setAttribute("articles", publishedArticleDAO.listByTypeSortByCreated(PublishedArticleType.ARTICLE, 0, 5));
  	context.getRequest().setAttribute("technicalAnnouncements", publishedArticleDAO.listByTypeSortByCreated(PublishedArticleType.TECHNICAL, 0, 5));
  	
  	handleActions(context);

    context.setIncludeJSP("/jsp/index/index.jsp");
  }
  
  private void handleActions(ViewControllerContext context) {
	  String actionParam = context.getStringParameter("a");
  	if (StringUtils.isNotBlank(actionParam)) {
  		Action action = Action.getByName(actionParam);
  		Map<String, String> parameters = ViewUtils.explodeActionParameters(context.getStringParameter("ap"));
  		
  		switch (action) {
  			case VIEW_MESSAGE:
  				handleViewMessageAction(parameters);
  			break;
  			default:
  				return;
  		}
  		
			try {
				context.setJsVariable("actionParameters", ViewUtils.implodeActionParameters(parameters));
      } catch (UnsupportedEncodingException e) {
      	throw new ViewControllerException(Locales.getText(context.getRequest().getLocale(), "error.generic.configurationError"));
      }
  		
  		context.setJsVariable("action", actionParam);
  	}
  }
  
  private void handleViewMessageAction(Map<String, String> parameters) {
  	// Everything we need is in the request 
  }

	private enum Action {
		VIEW_MESSAGE ("viewmessage");
		
		private Action(String name) {
	    this.name = name;
    }
		
		public String getName() {
	    return name;
    }
		
		public static Action getByName(String name) {
			for (Action action : values()) {
				if (action.getName().equals(name))
					return action;
			}
			
			return null;
		}
	
		private String name;
	}
}