package fi.foyt.fni.forum;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.util.Version;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;

import fi.foyt.fni.persistence.dao.forum.ForumCategoryDAO;
import fi.foyt.fni.persistence.dao.forum.ForumDAO;
import fi.foyt.fni.persistence.dao.forum.ForumPostDAO;
import fi.foyt.fni.persistence.dao.forum.ForumTopicDAO;
import fi.foyt.fni.persistence.model.forum.Forum;
import fi.foyt.fni.persistence.model.forum.ForumCategory;
import fi.foyt.fni.persistence.model.forum.ForumPost;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.utils.search.SearchResult;
import fi.foyt.fni.utils.servlet.RequestUtils;

@Dependent
@Stateful
public class ForumController implements Serializable {

	private static final long serialVersionUID = -5991883993762343104L;

	@Inject
	private FullTextEntityManager fullTextEntityManager;
	
	@Inject
	private Logger logger;
	
	@Inject
	private ForumCategoryDAO forumCategoryDAO;

	@Inject
	private ForumDAO forumDAO;
	
	@Inject
	private ForumTopicDAO forumTopicDAO;

	@Inject
	private ForumPostDAO forumPostDAO;
	
	// Categories
	
	public List<ForumCategory> listForumCategories() {
		return forumCategoryDAO.listAll();
	}
	
	// Forums
	
	public Forum findForumById(Long id) {
		return forumDAO.findById(id);
	}

	public Forum findForumByUrlName(String urlName) {
		return forumDAO.findByUrlName(urlName);
	}
	
	public List<Forum> listForumsByCategory(ForumCategory category) {
		return forumDAO.listByCategory(category);
	}
	
	// Topics

	public ForumTopic createTopic(Forum forum, String subject, User author) {
		Date now = new Date();
		return forumTopicDAO.create(forum, author, now, now, createUrlName(forum, subject), subject, 0l);
	}

	public ForumTopic findForumTopicByForumAndUrlName(Forum forum, String urlName) {
		return forumTopicDAO.findByForumAndUrlName(forum, urlName);
	}
	
	public ForumTopic findForumTopicByUrlNames(String forumUrlName, String topicUrlName) {
		return findForumTopicByForumAndUrlName(findForumByUrlName(forumUrlName), topicUrlName);
	}

	public ForumTopic findMostActiveTopicByAuthor(User author) {
		List<ForumTopic> forums = forumTopicDAO.listAllSortByAuthorMessageCount(author, 0, 1);
		if (forums.size() == 1) {
			return forums.get(0);
		}
		
		return null;
	}
	
	public List<ForumTopic> listTopicsByForum(Forum forum) {
		return forumTopicDAO.listByForum(forum);
	}	
	
	public List<ForumTopic> listLatestForumTopicsByForum(Forum forum, int maxForumTopics) {
		return forumPostDAO.listTopicsByForumSortByCreated(forum, 0, maxForumTopics);
	}
	
	public List<ForumTopic> listLatestForumTopics(int maxForumTopics) {
		return forumPostDAO.listTopicsSortByCreated(0, maxForumTopics);
	}

	public Long countPostsByTopic(ForumTopic topic) {
		return forumPostDAO.countByTopic(topic);
	}

	public Long countPostsByTopicAndAuthor(ForumTopic topic, User author) {
		return forumPostDAO.countByTopicAndAuthor(topic, author);
	}

	public Long countTopicsByForum(Forum forum) {
		return forumTopicDAO.countByForum(forum);
	}

	public ForumTopic updateTopicViews(ForumTopic topic, long views) {
    return forumTopicDAO.updateViews(topic, views);
	}	
	
	// Posts

	public ForumPost createForumPost(ForumTopic topic, User author, String content) {
		Date now = new Date();
		return forumPostDAO.create(topic, author, now, now, content, 0l);
	}
	
	public ForumPost findLastTopicPost(ForumTopic forumTopic) {
		ForumPost lastTopicPost = null;
    List<ForumPost> forumPosts = forumPostDAO.listByTopicSortByCreated(forumTopic, 0, 1);
    if (forumPosts.size() == 1)
      lastTopicPost = forumPosts.get(0);
    
    return lastTopicPost;
  }
	
	public List<ForumPost> listPostsByTopic(ForumTopic topic) {
		return forumPostDAO.listByTopic(topic);
	}
	
	public List<ForumPost> listPostsByTopic(ForumTopic topic, Integer firstResult, Integer maxResults) {
		return forumPostDAO.listByTopic(topic, firstResult, maxResults);
	}

	public ForumPost findLastPostByAuthor(User author) {
		List<ForumPost> posts = forumPostDAO.listByAuthorSortByCreated(author, 0, 1);
		if (posts.size() == 1) {
			return posts.get(0);
		}
		
		return null;
	}

	public Long countPostsByAuthor(User author) {
		return forumPostDAO.countByAuthor(author);
  }

	public ForumPost updatePostViews(ForumPost post, long views) {
    return forumPostDAO.updateViews(post, views);
	}

	public Long countPostsByForum(Forum forum) {
		return forumPostDAO.countByForum(forum);
	}

	public List<SearchResult<ForumTopic>> searchTopics(String text) {
		return searchTopics(text, null);
	}

	@SuppressWarnings("unchecked")
	public List<SearchResult<ForumTopic>> searchTopics(String text, Integer maxHits) {
		String[] criterias = text.replace(",", " ").replaceAll("\\s+", " ").split(" ");

		List<SearchResult<ForumTopic>> topics = new ArrayList<>();
		
		StringBuilder queryStringBuilder = new StringBuilder();
		queryStringBuilder.append("+(");
		for (int i = 0, l = criterias.length; i < l; i++) {
			String criteria = QueryParser.escape(criterias[i]);
			
			queryStringBuilder.append("contentPlain:");
			queryStringBuilder.append(criteria);
			queryStringBuilder.append("* ");
			
			queryStringBuilder.append("topicSubject:");
			queryStringBuilder.append(criteria);
			queryStringBuilder.append("*");
			
			if (i < l - 1)
			  queryStringBuilder.append(' ');
		}
		
		queryStringBuilder.append(")");
		
		Set<Long> topicIds = new HashSet<>();
		
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
		QueryParser parser = new QueryParser(Version.LUCENE_35, "", analyzer);
		try {
			Query luceneQuery = parser.parse(queryStringBuilder.toString());
	    FullTextQuery query = (FullTextQuery) fullTextEntityManager.createFullTextQuery(luceneQuery, ForumPost.class);
  		
  		for (ForumPost forumPost : (List<ForumPost>) query.getResultList()) {
  			ForumTopic topic = forumPost.getTopic();
  			if (!topicIds.contains(topic.getId())) {
          String link = "/forum/" + topic.getFullPath(); 
          topics.add(new SearchResult<ForumTopic>(topic, topic.getSubject(), link, null));
          topicIds.add(topic.getId());
          
          if (maxHits != null && topics.size() >= maxHits) {
          	return topics;
          }
  			}
  		}
		} catch (ParseException e) {
			logger.log(Level.SEVERE, "Lucene query parsing failed", e);
    }
		
		return topics;
	}
	
	@SuppressWarnings("unchecked")
	public List<SearchResult<ForumPost>> searchPosts(String text) {
		String[] criterias = text.replace(",", " ").replaceAll("\\s+", " ").split(" ");
		int maxFragments = 1;

		List<SearchResult<ForumPost>> posts = new ArrayList<SearchResult<ForumPost>>();
		
		StringBuilder queryStringBuilder = new StringBuilder();
		queryStringBuilder.append("+(");
		for (int i = 0, l = criterias.length; i < l; i++) {
			String criteria = QueryParser.escape(criterias[i]);
			
			queryStringBuilder.append("contentPlain:");
			queryStringBuilder.append(criteria);
			queryStringBuilder.append("* ");
			
			queryStringBuilder.append("topicSubject:");
			queryStringBuilder.append(criteria);
			queryStringBuilder.append("*");
			
			if (i < l - 1)
			  queryStringBuilder.append(' ');
		}
		
		queryStringBuilder.append(")");
		
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
		QueryParser parser = new QueryParser(Version.LUCENE_35, "", analyzer);
		try {
			Query luceneQuery = parser.parse(queryStringBuilder.toString());
	    FullTextQuery query = (FullTextQuery) fullTextEntityManager.createFullTextQuery(luceneQuery, ForumPost.class);
  		Highlighter highlighter = new Highlighter(new QueryScorer(luceneQuery));
  		highlighter.setTextFragmenter(new SimpleFragmenter());
  		
  		for (ForumPost forumPost : (List<ForumPost>) query.getResultList()) {
  			String content = forumPost.getContentPlain();
    		TokenStream tokenStream = analyzer.tokenStream( "contentPlain", new StringReader(content) );
    		try {
    			String matchText = highlighter.getBestFragments(tokenStream, content, maxFragments, "...");
    			if (StringUtils.isBlank(matchText)) {
    				matchText = StringUtils.abbreviate(forumPost.getContentPlain(), 100);
    			}
    			
          ForumTopic topic = forumPost.getTopic();
          String link = "/forum/" + topic.getFullPath() + "#p" + forumPost.getId(); 
          posts.add(new SearchResult<ForumPost>(forumPost, forumPost.getTopic().getSubject(), link, matchText));
        } catch (IOException e) {
        	logger.log(Level.WARNING, "Lucene query analyzing failed", e);
        } catch (InvalidTokenOffsetsException e) {
        	logger.log(Level.WARNING, "Lucene query analyzing failed", e);
        }
  		}
		} catch (ParseException e) {
			logger.log(Level.SEVERE, "Lucene query parsing failed", e);
    }
		
		return posts;
	}

	public ForumPost getLastPostByTopic(ForumTopic topic) {
		List<ForumPost> posts = forumPostDAO.listByTopicSortByCreated(topic, 0, 1);
		if (posts.size() == 1) {
			return posts.get(0);
		}
		
		return null;
	}

	public ForumPost getLastPostByForum(Forum forum) {
		List<ForumPost> posts = forumPostDAO.listByForumSortByCreated(forum, 0, 1);
		if (posts.size() == 1) {
			return posts.get(0);
		}
		
		return null;
	}

	private String createUrlName(Forum forum, String subject) {
		int maxLength = 20;
		int padding = 0;
		do {
			String urlName = RequestUtils.createUrlName(subject, maxLength);
			if (padding > 0) {
				urlName = urlName.concat(StringUtils.repeat('_', padding));
			}
			
			ForumTopic topic = forumTopicDAO.findByForumAndUrlName(forum, urlName);
			if (topic == null) {
				return urlName;
			}
			
			if (maxLength < subject.length()) {
				maxLength++;
			} else {
				padding++;
			}
		} while (true);
	}

}
