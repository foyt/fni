delete from OAuthAccessToken where accessToken in ('access-token', 'admin-access-token', 'guest-access-token');
delete from OAuthAuthorizationCode where client_id in (select id from OAuthClient where name in ('test-user-client', 'test-admin-client', 'test-guest-client'));
delete from OAuthClient where name in ('test-user-client', 'test-admin-client', 'test-guest-client');