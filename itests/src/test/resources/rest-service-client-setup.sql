insert into 
  User (id, archived, firstName, lastName, locale, profileImageSource, registrationDate, role) 
values 
  (100, false, 'Service', 'Test', 'en_US', 'GRAVATAR', PARSEDATETIME('1 1 2010', 'd M yyyy'), 'GUEST');

  insert into 
  UserEmail (id, email, primaryEmail, user_id)
values 
  (100, 'servicetest@foyt.fi', true, 100);
  
insert into 
  OAuthClient (id, clientId, clientSecret, name, type, redirectUrl, serviceUser_id)
values 
  (100, 'client-id', 'client-secret', 'test-service-client', 'SERVICE', 'http://dev.forgeandillusion.net:8080/fake-redirect', 100);
  
  