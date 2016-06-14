insert into 
  Material (id, created, modified, publicity, title, type, urlName, creator_id, language_id, modifier_id, parentFolder_id) 
values 
  ({folderId|20000}, PARSEDATETIME('1 1 2010', 'd M yyyy'), PARSEDATETIME('1 1 2010', 'd M yyyy'), 'PRIVATE', '{title|Open Event}', 'ILLUSION_GROUP_FOLDER', '{urlName|openevent}', 2, null, 2, 10000);
insert into Folder (id) values ({folderId|20000});
insert into IllusionEventFolder (id) values ({folderId|20000});
insert into 
  IllusionEvent (id, urlName, xmppRoom, name, description, created, folder_id, joinMode, signUpFee, signUpFeeCurrency, start, end, published, paymentMode) 
values 
  ({eventId|1}, '{urlName|openevent}', '{urlName|openevent}@bogustalk.net', '{title|Open Event}', 'Event for automatic testing (Open)', PARSEDATETIME('1 1 2010', 'd M yyyy'), {folderId|20000}, '{joinMode|OPEN}', null, null, PARSEDATETIME('1 1 2010', 'd M yyyy'), PARSEDATETIME('2 1 2010', 'd M yyyy'), true, 'NONE');

insert into 
  Material (id, created, modified, publicity, title, type, urlName, creator_id, language_id, modifier_id, parentFolder_id) 
values 
  ({indexId|20001}, PARSEDATETIME('1 1 2010', 'd M yyyy'), PARSEDATETIME('1 1 2010', 'd M yyyy'), 'PRIVATE', 'Front page', 'ILLUSION_GROUP_DOCUMENT', 'index', 2, null, 2, {folderId|20000});
  
insert into 
  Document (id, data)
values 
  ({indexId|20001}, '<p>Index</p>');

insert into
  IllusionEventDocument (id, documentType, indexNumber)
values 
  ({indexId|20001}, 'INDEX', 0);  
  
