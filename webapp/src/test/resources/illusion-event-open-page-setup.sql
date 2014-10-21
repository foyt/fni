insert into   
  Material (id, created, modified, publicity, title, type, urlName, creator_id, modifier_id, parentFolder_id)
values 
  (20150, PARSEDATETIME('1 1 2010', 'd M yyyy'), PARSEDATETIME('1 1 2010', 'd M yyyy'), 'PUBLIC', 'Test Page', 'ILLUSION_GROUP_DOCUMENT', 'testpage', 2, 2, 20000);

insert into 
  Document (id, data)
values 
  (20150, '<p>Page contents</p>');

insert into
  IllusionEventDocument (id, documentType)
values 
  (20150, 'PAGE');  