update ForumMessage set views = 666 where id in (select id from ForumPost where topic_id in (1,2,3,4,5,6,7,8,9,10,29,30));
delete from ForumPost where topic_id in (1,2,3,4,5,6,7,8,9,10,29,30);
delete from ForumMessage where views = 666;
delete from ForumTopicRead where topic_id in (1,2,3,4,5,6,7,8,9,10,29,30);
delete from ForumTopicWatcher where topic_id in (1,2,3,4,5,6,7,8,9,10,29,30);
delete from ForumTopic where id in (1,2,3,4,5,6,7,8,9,10,29,30);
delete from ForumMessage where id in (1,2,3,4,5,6,7,8,9,10,29,30);
delete from Forum where id in (2,3,4,5);