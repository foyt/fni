update Order_ set type = 'DELETE' where id in (select order_id from OrderItem where publication_id in (4, 5));
delete from OrderItem where publication_id in (4, 5);
delete from Order_ where type = 'DELETE';
delete from StoreProductTag where id in (1, 2, 3);
delete from StoreTag where id in (1, 2, 3);
delete from StoreProduct where id in (4, 5);
update Publication set defaultImage_id = null where id in (4, 5);
delete from PublicationImage where id in (4, 5);
delete from Publication where id in (4, 5)