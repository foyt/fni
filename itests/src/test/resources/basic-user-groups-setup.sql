insert into
  UserGroup (id, name, creator_id) 
values  
  (2001, 'Lowborn', 1),
  (2002, 'Highborn', 1);
  
insert into
  UserGroupMember (group_id, user_id) 
values  
  (2001, 2),
  (2001, 5),
  (2001, 6),
  (2002, 4),
  (2002, 5);