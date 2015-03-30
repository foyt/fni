delete from CharacterSheet where id = (select id from Material where parentFolder_id = 20000 and urlName = 'charactersheet');
delete from MaterialView where material_id in (select id from Material where parentFolder_id = 20000 and urlName = 'charactersheet');
delete from Material where parentFolder_id = 20000 and urlName = 'charactersheet';