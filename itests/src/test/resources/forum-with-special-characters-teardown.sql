delete from ForumPost where id = 28;
delete from ForumTopicRead where topic_id in (27);
delete from ForumTopic where id = 27;
delete from ForumMessage where id in (27, 28);
delete from Forum where urlName = 'with-special.characters';