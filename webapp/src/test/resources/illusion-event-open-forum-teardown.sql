update IllusionEvent set forumTopic_id = null where id = 1;
update ForumMessage set views = -100 where id in (select id from ForumPost where topic_id = 20000);
delete from ForumPost where topic_id = 20000;
delete from ForumMessage where views = -100;
delete from ForumTopicWatcher where topic_id = 20000;
delete from ForumTopic where id in (20000);
delete from ForumMessage where id in (20000);