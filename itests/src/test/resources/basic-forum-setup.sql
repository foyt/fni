insert into 
  Forum (id, allowTopicCreation, name, description, urlName, category_id)
values 
  (2, true, 'Empty Forum', 'Topicless forum', 'empty_forum', 1),
  (3, true, 'Single topic Forum', 'Single topic forum', '1_topic_forum', 1),
  (4, true, 'Five topic Forum', 'Five topic forum', '5_topic_forum', 1),
  (5, false, 'Immutable Forum', 'No topic creation allowed Forum', 'immutable', 1);
  
insert into 
  ForumMessage (id, created, modified, views, author_id)
values 
  (1, PARSEDATETIME('1 1 2010 12:30', 'd M yyyy HH:mm'), PARSEDATETIME('1 1 2010 12:30', 'd M yyyy HH:mm'), 0, 1),
  (2, PARSEDATETIME('1 1 2010 13:30', 'd M yyyy HH:mm'), PARSEDATETIME('1 1 2010 13:30', 'd M yyyy HH:mm'), 0, 1),
  (3, PARSEDATETIME('1 1 2010 14:30', 'd M yyyy HH:mm'), PARSEDATETIME('1 1 2010 14:30', 'd M yyyy HH:mm'), 0, 1),
  (4, PARSEDATETIME('1 1 2010 15:30', 'd M yyyy HH:mm'), PARSEDATETIME('1 1 2010 15:30', 'd M yyyy HH:mm'), 0, 1),
  (5, PARSEDATETIME('1 1 2010 16:30', 'd M yyyy HH:mm'), PARSEDATETIME('1 1 2010 16:30', 'd M yyyy HH:mm'), 0, 1),
  (6, PARSEDATETIME('1 1 2010 17:30', 'd M yyyy HH:mm'), PARSEDATETIME('1 1 2010 17:30', 'd M yyyy HH:mm'), 0, 1),
  (7, PARSEDATETIME('1 1 2010 18:30', 'd M yyyy HH:mm'), PARSEDATETIME('1 1 2010 17:30', 'd M yyyy HH:mm'), 0, 1),
  (8, PARSEDATETIME('1 1 2011 16:30', 'd M yyyy HH:mm'), PARSEDATETIME('1 1 2011 16:30', 'd M yyyy HH:mm'), 0, 1),
  (9, PARSEDATETIME('1 1 2012 17:30', 'd M yyyy HH:mm'), PARSEDATETIME('1 1 2012 17:30', 'd M yyyy HH:mm'), 0, 1),
  (10, PARSEDATETIME('1 1 2013 18:30', 'd M yyyy HH:mm'), PARSEDATETIME('1 1 2013 18:30', 'd M yyyy HH:mm'), 0, 1);

insert into 
  ForumTopic (id, forum_id, urlName, subject) 
values 
  (1, 3, 'single_topic', 'Topic of single topic forum'),
  (2, 4, 'topic1of5', 'Topic 1 of 5 topic forum'),
  (3, 4, 'topic2of5', 'Topic 2 of 5 topic forum'),
  (4, 4, 'topic3of5', 'Topic 3 of 5 topic forum'),
  (5, 4, 'topic4of5', 'Topic 4 of 5 topic forum'),
  (6, 4, 'topic5of5', 'Topic 5 of 5 topic forum'),
  (7, 5, 'immutable_topic', 'Topic of immutable Forum topic'),
  (8, 5, 'testbook_1', 'Fat hag dwarves quickly zap jinx mob'),
  (9, 5, 'testbook_2', 'Эх, чужак, общий съём цен шляп (юфть) – вдрызг'),
  (10, 5, 'pangram_fi', 'Beowulf pohti zuluja ja ångström-yksikköä katsellessaan Q-stone- ja CMX-yhtyeitä videolta.');
 
insert into 
  ForumMessage (id, created, modified, views, author_id)
values 
  (11, PARSEDATETIME('1 1 2010 13:00', 'd M yyyy HH:mm'), PARSEDATETIME('1 1 2010 13:00', 'd M yyyy HH:mm'), 0, 1),
  (12, PARSEDATETIME('1 1 2010 14:00', 'd M yyyy HH:mm'), PARSEDATETIME('1 1 2010 14:00', 'd M yyyy HH:mm'), 0, 1),
  (13, PARSEDATETIME('1 1 2010 15:00', 'd M yyyy HH:mm'), PARSEDATETIME('1 1 2010 15:00', 'd M yyyy HH:mm'), 0, 1),
  (14, PARSEDATETIME('1 1 2010 16:00', 'd M yyyy HH:mm'), PARSEDATETIME('1 1 2010 16:00', 'd M yyyy HH:mm'), 0, 1),
  (15, PARSEDATETIME('1 1 2010 17:00', 'd M yyyy HH:mm'), PARSEDATETIME('1 1 2010 17:00', 'd M yyyy HH:mm'), 0, 1),
  (16, PARSEDATETIME('1 1 2010 18:00', 'd M yyyy HH:mm'), PARSEDATETIME('1 1 2010 18:00', 'd M yyyy HH:mm'), 0, 1),
  (17, PARSEDATETIME('1 1 2010 19:00', 'd M yyyy HH:mm'), PARSEDATETIME('1 1 2010 19:00', 'd M yyyy HH:mm'), 0, 1),
  (18, PARSEDATETIME('1 1 2010 20:00', 'd M yyyy HH:mm'), PARSEDATETIME('1 1 2010 20:00', 'd M yyyy HH:mm'), 0, 1),
  (19, PARSEDATETIME('1 1 2010 21:00', 'd M yyyy HH:mm'), PARSEDATETIME('1 1 2010 21:00', 'd M yyyy HH:mm'), 0, 1),
  (20, PARSEDATETIME('1 1 2010 22:00', 'd M yyyy HH:mm'), PARSEDATETIME('1 1 2010 22:00', 'd M yyyy HH:mm'), 0, 1),
  (21, PARSEDATETIME('1 1 2010 23:00', 'd M yyyy HH:mm'), PARSEDATETIME('1 1 2010 23:00', 'd M yyyy HH:mm'), 0, 1),
  (22, PARSEDATETIME('1 1 2011 01:00', 'd M yyyy HH:mm'), PARSEDATETIME('1 1 2011 01:00', 'd M yyyy HH:mm'), 0, 1),
  (23, PARSEDATETIME('1 1 2011 02:00', 'd M yyyy HH:mm'), PARSEDATETIME('1 1 2011 02:00', 'd M yyyy HH:mm'), 0, 1),
  (24, PARSEDATETIME('1 1 2012 02:00', 'd M yyyy HH:mm'), PARSEDATETIME('1 1 2012 02:00', 'd M yyyy HH:mm'), 0, 1),
  (25, PARSEDATETIME('1 1 2013 02:00', 'd M yyyy HH:mm'), PARSEDATETIME('1 1 2013 02:00', 'd M yyyy HH:mm'), 0, 1),
  (26, PARSEDATETIME('1 1 2014 02:00', 'd M yyyy HH:mm'), PARSEDATETIME('1 1 2014 02:00', 'd M yyyy HH:mm'), 0, 1);
  
insert into 
  ForumPost (id, topic_id, content)
values 
  (11, 1, '<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi eu iaculis mi, et fringilla nulla. Integer ut imperdiet lacus. In in massa eget lacus tincidunt aliquet auctor ut enim. Praesent rutrum leo eu sem feugiat, non dignissim lorem vulputate. Nam fermentum euismod sapien, et cursus mi pretium quis.</p>'),
  (12, 3, '<p>Replyless</p>'),
  (13, 4, '<p>With one reply</p>'),
  (14, 4, '<p>Re: With one reply</p>'),
  (15, 5, '<p>With two replies</p>'),
  (16, 5, '<p>Re: With two replies</p>'),
  (17, 5, '<p>Re: With two replies</p>'),
  (18, 6, '<p>With three replies</p>'),
  (19, 6, '<p>Re: With three replies</p>'),
  (20, 6, '<p>Re: With three replies</p>'),
  (21, 6, '<p>Re: With three replies</p>'),
  (22, 7, '<p>Immutable</p>'),
  (23, 7, '<p>Re: Immutable</p>'),
  (24, 8, '<p>Re: Fat hag dwarves</p>'),
  (25, 8, '<p>Re: Fat hag dwarves quickly zap jinx mob</p>'),
  (26, 9, '<p>Re: Эх, чужак</p>');