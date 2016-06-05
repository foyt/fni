insert into   
  UserGroup (id, name, creator_id)
values 
  (1, 'Test Group in event #2', 1),
  (2, 'Test Group #1 in event #3', 1),
  (3, 'Test Group #2 in event #3', 1);
  
insert into   
  IllusionEventGroup (id, event_id)
values 
  (1, 2),
  (2, 3),
  (3, 3);