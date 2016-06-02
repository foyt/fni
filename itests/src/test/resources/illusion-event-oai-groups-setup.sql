insert into   
  UserGroup (id, name)
values 
  (1, 'Test Group in event #2'),
  (2, 'Test Group #1 in event #3'),
  (3, 'Test Group #2 in event #3');
  
insert into   
  IllusionEventGroup (id, event_id)
values 
  (1, 2),
  (2, 3),
  (3, 3);