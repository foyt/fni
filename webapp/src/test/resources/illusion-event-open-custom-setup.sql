insert into OAuthClient (id, clientId, clientSecret, name, redirectUrl, type) values (10000,'id-of-a-client','not-very-secret', 'OAuth Client for custom-test.forgeandillusion.net', 'http://custom-test.forgeandillusion.net:8080/fnici/login/?return=1&loginMethod=ILLUSION_INTERNAL', 'USER');
update IllusionEvent set domain = 'custom-test.forgeandillusion.net', oAuthClient_id = 10000 where id = 1;