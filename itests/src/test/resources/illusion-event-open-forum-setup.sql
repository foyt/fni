insert into ForumMessage (id, created, modified, views, author_id) values (20000, PARSEDATETIME('1 1 2010', 'd M yyyy'), PARSEDATETIME('1 1 2010', 'd M yyyy'), 0, 2);
insert into ForumTopic (id, subject, urlName, forum_id) values (20000, 'openevent', 'openevent', (select id from Forum where urlName = 'illusion'));
update IllusionEvent set forumTopic_id = 20000 where id = 1;  