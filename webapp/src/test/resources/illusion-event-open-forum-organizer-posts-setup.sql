insert into   
  ForumMessage (id, created, modified, views, author_id)
values 
  (20102, PARSEDATETIME('1 1 2010', 'd M yyyy'), PARSEDATETIME('1 1 2010', 'd M yyyy'), 0, 4),
  (20103, PARSEDATETIME('1 1 2010', 'd M yyyy'), PARSEDATETIME('1 1 2010', 'd M yyyy'), 0, 4);

insert into 
  ForumPost (id, content, topic_id)
values 
  (20102, 'message #3', 20000),
  (20103, 'message #4', 20000);