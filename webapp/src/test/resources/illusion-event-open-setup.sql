insert into 
  Material (id, created, modified, publicity, title, type, urlName, creator_id, language_id, modifier_id, parentFolder_id) 
values 
  (20000, PARSEDATETIME('1 1 2010', 'd M yyyy'), PARSEDATETIME('1 1 2010', 'd M yyyy'), 'PRIVATE', 'Open Event', 'ILLUSION_GROUP_FOLDER', 'illusion', 1, null, 1, null);
insert into Folder (id) values (20000);
insert into IllusionEventFolder (id) values (20000);
insert into 
  IllusionEvent (id, urlName, xmppRoom, name, description, created, folder_id, joinMode, signUpFee, signUpFeeCurrency) 
values 
  (1, 'openevent', 'openevent@bogustalk.net', 'Open Event', 'Event for automatic testing (Open)', PARSEDATETIME('1 1 2010', 'd M yyyy'), 20000, 'OPEN', null, null);
