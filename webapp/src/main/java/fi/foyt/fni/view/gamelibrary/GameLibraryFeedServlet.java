package fi.foyt.fni.view.gamelibrary;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;

import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndCategoryImpl;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.SyndFeedOutput;

import fi.foyt.fni.gamelibrary.GameLibraryTagController;
import fi.foyt.fni.gamelibrary.PublicationController;
import fi.foyt.fni.i18n.ExternalLocales;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.gamelibrary.BookPublication;
import fi.foyt.fni.persistence.model.gamelibrary.GameLibraryTag;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationImage;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.utils.servlet.RequestUtils;

@WebServlet(urlPatterns = "/gamelibrary/feed/", name = "gamelibrary-feed")
@Transactional
public class GameLibraryFeedServlet extends HttpServlet {

  private static final long serialVersionUID = 5587885239252935231L;

  private static final int MAX_PUBLICATIONS = 50;
  
  @Inject
  private Logger logger;

  @Inject
  private PublicationController publicationController;

  @Inject
  private GameLibraryTagController gameLibraryTagController;

  @Inject
  private SystemSettingsController systemSettingsController;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    try {
      FeedType feedType = FeedType.byType(request.getParameter("type"));
      if (feedType == null) {
        feedType = FeedType.RSS_2_0;
      }
      
      Locale locale = LocaleUtils.toLocale(request.getParameter("locale"));
      if (locale == null) {
        locale = systemSettingsController.getDefaultLocale();
      }
      
      if (!systemSettingsController.isSupportedLocale(locale)) {
        sendError(response, HttpServletResponse.SC_BAD_REQUEST, String.format("Locale %s is not supported", locale.toString()));
        return;
      }
      
      String baseUrl = RequestUtils.getRequestHostUrl(request);
      
      SyndFeed feed = new SyndFeedImpl();
      feed.setFeedType(feedType.getType());
      feed.setTitle(ExternalLocales.getText(locale, "gamelibrary.feed.title"));
      feed.setLink(String.format("%s/gamelibrary/", baseUrl));
      feed.setDescription(ExternalLocales.getText(locale, "gamelibrary.feed.description"));
      feed.setEncoding("UTF-8");

      List<SyndEntry> entries = new ArrayList<>();
      
      List<BookPublication> publications = publicationController.listRecentBookPublications(MAX_PUBLICATIONS);
      for (BookPublication publication : publications) {
        SyndEntry entry = new SyndEntryImpl();
        
        List<SyndEnclosure> enclosuers = new ArrayList<>();
        List<SyndCategory> categories = new ArrayList<>();
        SyndContent description = new SyndContentImpl();
        
        PublicationImage defaultImage = publication.getDefaultImage();
        if (defaultImage != null) {
          String imageUrl = String.format("%s/gamelibrary/publicationImages/%d", baseUrl, defaultImage.getId());
          description.setType("text/html");
          description.setValue(String.format("<img src=\"%s?height=200\" style=\"float:right;margin-left: 10px; margin-bottom:10px;\"/><p>%s</p>", imageUrl, publication.getDescription()));
        } else {
          description.setType("text/html");
          description.setValue(publication.getDescription());
        }

        ForumTopic forumTopic = publication.getForumTopic();
        if (forumTopic != null) {
          entry.setComments(String.format("%s/forum/%s/%s", baseUrl, forumTopic.getForum().getUrlName(), forumTopic.getUrlName()));
        }
        
        List<GameLibraryTag> tags = gameLibraryTagController.listPublicationGameLibraryTags(publication);
        for (GameLibraryTag tag : tags) {
          SyndCategory category = new SyndCategoryImpl();
          category.setName(tag.getText());
          category.setTaxonomyUri(String.format("%s/gamelibrary/tags/%s", baseUrl, tag.getText()));
          categories.add(category);
        }
        
        entry.setTitle(publication.getName());
        entry.setLink(String.format("%s/gamelibrary/%s", baseUrl, publication.getUrlName()));
        entry.setPublishedDate(publication.getModified());
        entry.setDescription(description);
        entry.setEnclosures(enclosuers);
        entry.setCategories(categories);
        
        entries.add(entry);
      }

      feed.setEntries(entries);
      
      response.setContentType("application/rss+xml");
      response.setStatus(HttpServletResponse.SC_OK);
      
      PrintWriter outputWriter = response.getWriter();
      SyndFeedOutput output = new SyndFeedOutput();
      output.output(feed, outputWriter);
      outputWriter.flush();
      outputWriter.close();
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Failed to create RSS feed", e);
      sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }
  }
  
  private void sendError(HttpServletResponse response, int code, String message) {
    try {
      response.setStatus(code);
      PrintWriter writer = response.getWriter();
      writer.append(message);
      writer.flush();
      writer.close();
    } catch (IOException e) {
      logger.log(Level.WARNING, "Failed to send servlet error", e);
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); 
    }
  }

  private enum FeedType {
    
    RSS_0_90 ("rss_0.90"),
    RSS_0_91 ("rss_0.91"),
    RSS_0_92 ("rss_0.92"),
    RSS_0_93 ("rss_0.93"),
    RSS_0_94 ("rss_0.94"),
    RSS_1_0 ("rss_1.0"), 
    RSS_2_0 ("rss_2.0"),
    ATOM_0_3 ("atom_0.3");
    
    private FeedType(String type) {
      this.type = type;
    }
    
    public String getType() {
      return type;
    }
    
    public static FeedType byType(String type) {
      for (FeedType feedType : values()) {
        if (StringUtils.equals(feedType.getType(), type)) {
          return feedType;
        }
      }
      
      return null;
    }
    
    private String type;
  }
  
}
