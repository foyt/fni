insert into   
  Material (id, created, modified, publicity, title, type, urlName, creator_id, modifier_id, parentFolder_id)
values 
  ({id}, PARSEDATETIME('1 1 2010', 'd M yyyy'), PARSEDATETIME('1 1 2010', 'd M yyyy'), 'PRIVATE', 'Book Design', 'BOOK_DESIGN', '{urlName}', 2, 2, {parentFolderId|null});
  
INSERT INTO BookDesign (id, data, fonts, styles, pageTypes) VALUES 
  ({id}, '{data}', '{fonts}', '{styles}', '{pageTypes}');