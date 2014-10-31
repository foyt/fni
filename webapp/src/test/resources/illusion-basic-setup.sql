insert into 
  Material (id, created, modified, publicity, title, type, urlName, creator_id, language_id, modifier_id, parentFolder_id) 
values 
  (10000, PARSEDATETIME('1 1 2010', 'd M yyyy'), PARSEDATETIME('1 1 2010', 'd M yyyy'), 'PRIVATE', 'Illusion', 'ILLUSION_FOLDER', 'illusion', 2, null, 2, null);
insert into Folder (id) values (10000);
insert into IllusionFolder (id) values (10000);