insert into 
  Material (id, type, urlName, title, publicity, creator_id, modifier_id, parentFolder_id, created, modified)
values 
  (20060, 'CHARACTER_SHEET','charactersheet', 'Character Sheet', 'PUBLIC', 2, 2, 20000, PARSEDATETIME('1 1 2010', 'd M yyyy'), PARSEDATETIME('1 1 2010', 'd M yyyy'));
insert into CharacterSheet (id, contents, scripts, styles) select id, '<div/>', '', '' from Material where parentFolder_id = 20000 and urlName = 'charactersheet';