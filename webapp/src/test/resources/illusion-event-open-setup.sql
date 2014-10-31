insert into 
  Material (id, created, modified, publicity, title, type, urlName, creator_id, language_id, modifier_id, parentFolder_id) 
values 
  (20000, PARSEDATETIME('1 1 2010', 'd M yyyy'), PARSEDATETIME('1 1 2010', 'd M yyyy'), 'PRIVATE', 'Open Event', 'ILLUSION_GROUP_FOLDER', 'openevent', 2, null, 2, 10000);
insert into Folder (id) values (20000);
insert into IllusionEventFolder (id) values (20000);
insert into 
  IllusionEvent (id, urlName, xmppRoom, name, description, created, folder_id, joinMode, signUpFee, signUpFeeCurrency) 
values 
  (1, 'openevent', 'openevent@bogustalk.net', 'Open Event', 'Event for automatic testing (Open)', PARSEDATETIME('1 1 2010', 'd M yyyy'), 20000, 'OPEN', null, null);

insert into 
  Material (id, created, modified, publicity, title, type, urlName, creator_id, language_id, modifier_id, parentFolder_id) 
values 
  (20001, PARSEDATETIME('1 1 2010', 'd M yyyy'), PARSEDATETIME('1 1 2010', 'd M yyyy'), 'PRIVATE', 'Front page', 'ILLUSION_GROUP_DOCUMENT', 'index', 2, null, 2, 20000);
  
insert into 
  Document (id, data)
values 
  (20001, '<p>Index</p>');

insert into
  IllusionEventDocument (id, documentType)
values 
  (20001, 'INDEX');  
  
