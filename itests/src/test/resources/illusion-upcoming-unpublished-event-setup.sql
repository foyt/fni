insert into 
  Material (id, created, modified, publicity, title, type, urlName, creator_id, language_id, modifier_id, parentFolder_id) 
values 
  (20014, PARSEDATETIME('1 1 2010', 'd M yyyy'), PARSEDATETIME('1 1 2010', 'd M yyyy'), 'PRIVATE', 'Upcoming unpublished', 'ILLUSION_GROUP_FOLDER', 'upcoming_unpublished', 1, null, 1, null);
insert into Folder (id) values (20014);
insert into IllusionEventFolder (id) values (20014);
insert into 
  IllusionEvent (id, urlName, xmppRoom, name, description, created, folder_id, joinMode, signUpFee, signUpFeeCurrency, start, end, published) 
values 
  (4, 'upcoming_unpublished', 'upcoming_unpublished@bogustalk.net', 'Upcoming unpublished', 'Upcoming unpublished', PARSEDATETIME('1 1 2010', 'd M yyyy'), 20014, 'OPEN', null, null, DATEADD('DAY', 1, CURRENT_DATE()), DATEADD('DAY', 2, CURRENT_DATE()), false);
insert into
  IllusionEventParticipant (role, event_id, user_id, created)
values 
  ('ORGANIZER', 4, 2, PARSEDATETIME('1 1 2010', 'd M yyyy'));