delete from CoOpsSession where material_id = {id};
update Material set parentFolder_id = null where parentFolder_id = {id};
delete from MaterialShareUser where id in (select id from MaterialShare where material_id = {id});
delete from MaterialShare where material_id = {id};
delete from MaterialView where material_id = {id};
delete from BookDesign where id = {id};
delete from Material where id = {id};