delete from OrderItem where publication_id in (1,2,3);
delete from Order_ where id in (1);
delete from Address where id in (1);
delete from PublicationAuthor where publication_id in (1,2,3);
delete from PublicationTag where publication_id in (1,2,3);
delete from GameLibraryTag where id in (1,2,3);
delete from BookPublication where id in (1,2,3);
update Publication set defaultImage_id = null where id in (1,2,3);
delete from PublicationImage where id in (1,2,3);
delete from PublicationFile where id in (1,2,3);
delete from Publication where id in (1,2,3);