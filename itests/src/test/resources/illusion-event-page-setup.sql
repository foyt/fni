insert into   
  Material (id, created, modified, publicity, title, type, urlName, creator_id, modifier_id, parentFolder_id)
values 
  ({id|20150}, PARSEDATETIME('1 1 2010', 'd M yyyy'), PARSEDATETIME('1 1 2010', 'd M yyyy'), '{publicity|PUBLIC}', 'Test Page', 'ILLUSION_GROUP_DOCUMENT', '{urlName|testpage}', {creatorId|2}, {modifierId|2}, {parentFolderId|20000});

insert into 
  Document (id, data)
values 
  ({id|20150}, '<p>Page contents</p>');

insert into
  IllusionEventDocument (id, documentType, indexNumber)
values 
  ({id|20150}, 'PAGE', 1);  