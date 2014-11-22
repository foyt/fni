insert into 
  Material (id, created, modified, publicity, title, type, urlName, creator_id, language_id, modifier_id, parentFolder_id) 
values 
  (10000, PARSEDATETIME('1 1 2010', 'd M yyyy'), PARSEDATETIME('1 1 2010', 'd M yyyy'), 'PRIVATE', 'Illusion', 'ILLUSION_FOLDER', 'illusion', 2, null, 2, null);
insert into Folder (id) values (10000);
insert into IllusionFolder (id) values (10000);

insert into Genre (id, name) values (1, 'Genre #1'), (2, 'Genre #2'), (3, 'Genre #3'), (4, 'Genre #4');
insert into IllusionEventType (id, name) values (1, 'Type #1'), (2, 'Type #2');