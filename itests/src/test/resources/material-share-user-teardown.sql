delete from MaterialShareUser where id in (select id from MaterialShare where material_id = {materialId});
delete from MaterialShare where material_id = {materialId};