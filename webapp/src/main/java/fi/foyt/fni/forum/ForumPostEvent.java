package fi.foyt.fni.forum;

import java.util.Locale;

public class ForumPostEvent {
	
	public ForumPostEvent(Locale locale, Long forumPostId) {
		this.locale = locale;
		this.forumPostId = forumPostId;
	}
	
	public Long getForumPostId() {
    return forumPostId;
  }
	
	public void setForumPostId(Long forumPostId) {
    this.forumPostId = forumPostId;
  }
	
	public Locale getLocale() {
		return locale;
	}
	
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
	private Locale locale;
	private Long forumPostId;
}
