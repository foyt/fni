insert into   
  ForumMessage (id, created, modified, views, author_id)
values 
  (20100, PARSEDATETIME('1 1 2010', 'd M yyyy'), PARSEDATETIME('1 1 2010', 'd M yyyy'), 0, 2),
  (20101, PARSEDATETIME('1 1 2010', 'd M yyyy'), PARSEDATETIME('1 1 2010', 'd M yyyy'), 0, 2);

insert into 
  ForumPost (id, content, topic_id)
values 
  (20100, 'message #1', 20000),
  (20101, 'message #2', 20000);