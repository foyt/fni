insert into 
  Material (id, created, modified, publicity, title, type, urlName, creator_id, language_id, modifier_id, parentFolder_id) 
values 
  (20002, PARSEDATETIME('1 1 2010', 'd M yyyy'), PARSEDATETIME('1 1 2010', 'd M yyyy'), 'PRIVATE', 'Open', 'ILLUSION_GROUP_FOLDER', 'open', 1, null, 1, null),
  (20003, PARSEDATETIME('1 1 2010', 'd M yyyy'), PARSEDATETIME('1 1 2010', 'd M yyyy'), 'PRIVATE', 'Approve', 'ILLUSION_GROUP_FOLDER', 'approve', 1, null, 1, null),
  (20004, PARSEDATETIME('1 1 2010', 'd M yyyy'), PARSEDATETIME('1 1 2010', 'd M yyyy'), 'PRIVATE', 'Invite', 'ILLUSION_GROUP_FOLDER', 'invite', 1, null, 1, null);
  
insert into Folder (id) values (20002), (20003), (20004);
insert into IllusionEventFolder (id) values (20002), (20003), (20004);
insert into 
  IllusionEvent (id, urlName, xmppRoom, name, description, created, folder_id, joinMode, signUpFee, signUpFeeCurrency, start, end, published)
values 
  (2, 'open', 'open@bogustalk.net', 'Open', 'Event for automatic testing (Open)', PARSEDATETIME('1 1 2010', 'd M yyyy'), 20002, 'OPEN', null, null, PARSEDATETIME('1 1 2010', 'd M yyyy'), PARSEDATETIME('2 1 2010', 'd M yyyy'), true),
  (3, 'approve', 'approve@bogustalk.net', 'Approve', 'Event for automatic testing (Approve)', PARSEDATETIME('1 1 2010', 'd M yyyy'), 20003, 'APPROVE', null, null, PARSEDATETIME('1 1 2010', 'd M yyyy'), PARSEDATETIME('2 1 2010', 'd M yyyy'), true),
  (4, 'invite', 'invite@bogustalk.net', 'Invite Only', 'Event for automatic testing (Invite Only)', PARSEDATETIME('1 1 2010', 'd M yyyy'), 20004, 'INVITE_ONLY', null, null, PARSEDATETIME('1 1 2010', 'd M yyyy'), PARSEDATETIME('2 1 2010', 'd M yyyy'), true);

insert into 
  Material (id, created, modified, publicity, title, type, urlName, creator_id, language_id, modifier_id, parentFolder_id) 
values 
  (20012, PARSEDATETIME('1 1 2010', 'd M yyyy'), PARSEDATETIME('1 1 2010', 'd M yyyy'), 'PRIVATE', 'Front page', 'ILLUSION_GROUP_DOCUMENT', 'index', 1, null, 1, 20002),
  (20013, PARSEDATETIME('1 1 2010', 'd M yyyy'), PARSEDATETIME('1 1 2010', 'd M yyyy'), 'PRIVATE', 'Front page', 'ILLUSION_GROUP_DOCUMENT', 'index', 1, null, 1, 20003),
  (20014, PARSEDATETIME('1 1 2010', 'd M yyyy'), PARSEDATETIME('1 1 2010', 'd M yyyy'), 'PRIVATE', 'Front page', 'ILLUSION_GROUP_DOCUMENT', 'index', 1, null, 1, 20004);
  
insert into 
  Document (id, data)
values 
  (20012, '<p>Index</p>'),
  (20013, '<p>Index</p>'),
  (20014, '<p>Index</p>');

insert into
  IllusionEventDocument (id, documentType, indexNumber)
values 
  (20012, 'INDEX', 0),
  (20013, 'INDEX', 0),
  (20014, 'INDEX', 0);  
  
insert into
  IllusionEventParticipant (id, role, event_id, user_id, created)
values 
  (2, 'ORGANIZER', 2, 4, PARSEDATETIME('1 1 2010', 'd M yyyy')),
  (3, 'ORGANIZER', 3, 4, PARSEDATETIME('1 1 2010', 'd M yyyy')),
  (4, 'ORGANIZER', 4, 4, PARSEDATETIME('1 1 2010', 'd M yyyy'));
