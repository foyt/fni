delete from OAuthAccessToken where accessToken = 'access-token';
delete from OAuthAuthorizationCode where client_id = (select id from OAuthClient where name = 'test-user-client');
delete from OAuthClient where name = 'test-user-client';