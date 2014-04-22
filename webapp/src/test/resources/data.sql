insert into 
  User (id, archived, firstName, lastName, locale, profileImageSource, registrationDate, role) 
values 
  (1, false, 'Test', 'Guest', 'en_US', 'GRAVATAR', PARSEDATETIME('1 1 2010', 'd M yyyy'), 'GUEST'), 
  (2, false, 'Test', 'User', 'en_US', 'GRAVATAR', PARSEDATETIME('2 2 2011', 'd M yyyy'), 'USER'), 
  (3, false, 'Test', 'Librarian', 'en_US', 'GRAVATAR', PARSEDATETIME('3 3 2012', 'd M yyyy'), 'LIBRARIAN'), 
  (4, false, 'Test', 'Admin', 'en_US', 'GRAVATAR', PARSEDATETIME('4 4 2013', 'd M yyyy'), 'ADMINISTRATOR');

insert into 
  UserEmail (id, email, primaryEmail, user_id)
values 
  (1, 'guest@foyt.fi', true, 1),
  (2, 'user@foyt.fi', true, 2),
  (3, 'librarian@foyt.fi', true, 3),
  (4, 'admin@foyt.fi', true, 4);
  
insert into 
  InternalAuth (id, password, verified, user_id)
values 
  (1, '1a1dc91c907325c69271ddf0c944bc72', true, 1),
  (2, '1a1dc91c907325c69271ddf0c944bc72', true, 2),
  (3, '1a1dc91c907325c69271ddf0c944bc72', true, 3),
  (4, '1a1dc91c907325c69271ddf0c944bc72', true, 4);
  
