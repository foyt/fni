package fi.foyt.fni.forum;


public class ForumPostEvent {
	
	public ForumPostEvent(Long forumTopicId, Long forumPostId) {
		this.forumTopicId = forumTopicId;
		this.forumPostId = forumPostId;
	}
	
	public Long getForumTopicId() {
    return forumTopicId;
  }
	
	public Long getForumPostId() {
    return forumPostId;
  }

	private Long forumTopicId;
	private Long forumPostId;
}
