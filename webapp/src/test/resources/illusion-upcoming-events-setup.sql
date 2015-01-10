insert into 
  Material (id, created, modified, publicity, title, type, urlName, creator_id, language_id, modifier_id, parentFolder_id) 
values 
  (20010, PARSEDATETIME('1 1 2010', 'd M yyyy'), PARSEDATETIME('1 1 2010', 'd M yyyy'), 'PRIVATE', 'Upcoming Event #1', 'ILLUSION_GROUP_FOLDER', 'upcoming_1', 1, null, 1, null);
insert into Folder (id) values (20010);
insert into IllusionEventFolder (id) values (20010);
insert into 
  IllusionEvent (id, urlName, xmppRoom, name, description, created, folder_id, joinMode, signUpFee, signUpFeeCurrency, start, end) 
values 
  (2, 'upcoming_1', 'upcoming_1@bogustalk.net', 'Upcoming #1', 'Upcoming event #1', PARSEDATETIME('1 1 2010', 'd M yyyy'), 20010, 'OPEN', null, null, DATEADD('DAY', 1, CURRENT_DATE()), DATEADD('DAY', 2, CURRENT_DATE()));

insert into 
  Material (id, created, modified, publicity, title, type, urlName, creator_id, language_id, modifier_id, parentFolder_id) 
values 
  (20011, PARSEDATETIME('1 1 2010', 'd M yyyy'), PARSEDATETIME('1 1 2010', 'd M yyyy'), 'PRIVATE', 'Upcoming Event #2', 'ILLUSION_GROUP_FOLDER', 'upcoming_2', 1, null, 1, null);
insert into Folder (id) values (20011);
insert into IllusionEventFolder (id) values (20011);
insert into 
  IllusionEvent (id, urlName, xmppRoom, name, description, created, folder_id, joinMode, signUpFee, signUpFeeCurrency, start, end) 
values 
  (3, 'upcoming_2', 'upcoming_2@bogustalk.net', 'Upcoming #2', 'Upcoming event #2', PARSEDATETIME('1 1 2010', 'd M yyyy'), 20011, 'OPEN', null, null, DATEADD('DAY', -1, CURRENT_DATE()), DATEADD('DAY', 2, CURRENT_DATE()));
