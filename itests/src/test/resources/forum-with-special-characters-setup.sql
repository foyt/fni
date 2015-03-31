insert into 
  Forum (allowTopicCreation, name, description, urlName, category_id)
values 
  (true, 'With Special Characters', 'Forum with special characters in urlname', 'with-special.characters', 1);
  
insert into 
  ForumMessage (id, created, modified, views, author_id)
values
  (27, PARSEDATETIME('1 1 2013 20:30', 'd M yyyy HH:mm'), PARSEDATETIME('1 1 2013 20:30', 'd M yyyy HH:mm'), 0, 1);
  
insert into 
  ForumTopic (id, forum_id, urlName, subject) 
values 
  (27, (select id from Forum where urlName = 'with-special.characters'), 'with-special.characters', 'Topic for testing url names with special characters');
  
insert into 
  ForumMessage (id, created, modified, views, author_id)
values 
  (28, PARSEDATETIME('1 1 2014 03:00', 'd M yyyy HH:mm'), PARSEDATETIME('1 1 2014 03:00', 'd M yyyy HH:mm'), 0, 1);
  
insert into 
  ForumPost (id, topic_id, content)
values 
  (28, 27, '<p>With special characters</p>');