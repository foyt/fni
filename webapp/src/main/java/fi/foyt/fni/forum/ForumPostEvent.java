package fi.foyt.fni.forum;

import java.util.Locale;

public class ForumPostEvent {
	
	public ForumPostEvent(String localAddress, String contextPath, Locale locale, Long forumPostId) {
		this.locale = locale;
		this.forumPostId = forumPostId;
		this.localAddress = localAddress;
		this.contextPath = contextPath;
	}
	
	public Long getForumPostId() {
    return forumPostId;
  }

	public Locale getLocale() {
		return locale;
	}
	
	public String getLocalAddress() {
    return localAddress;
  }
	
	public String getContextPath() {
    return contextPath;
  }
	
	private Locale locale;
	private Long forumPostId;
	private String contextPath;
	private String localAddress;
}
