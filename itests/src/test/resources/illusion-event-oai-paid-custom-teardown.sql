update IllusionEvent set domain = null, oAuthClient_id = null where id = 2; 
delete from OAuthAccessToken where authorizationCode_id = (select id from OAuthAuthorizationCode where client_id = 10000); 
delete from OAuthAuthorizationCode where client_id = 10000; 
delete from OAuthClient where id = 10000;
 