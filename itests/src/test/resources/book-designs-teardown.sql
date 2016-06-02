delete from CoOpsSession where material_id in (123,124,125);
update Material set parentFolder_id = null where parentFolder_id in (123,124,125);
delete from MaterialShareUser where id in (select id from MaterialShare where material_id between 123 and 125);
delete from MaterialShare where material_id between 123 and 125;
delete from MaterialView where material_id in (123,124,125);
delete from BookDesign where id in (123,124,125);
delete from Material where id in (123,124,125);