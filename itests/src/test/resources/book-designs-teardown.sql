delete from CoOpsSession where material_id in (23,24,25);
update Material set parentFolder_id = null where parentFolder_id in (23,24,25);
delete from UserMaterialRole where material_id in (23,24,25);
delete from MaterialView where material_id in (23,24,25);
delete from BookDesign where id in (23,24,25);
delete from Material where id in (23,24,25);