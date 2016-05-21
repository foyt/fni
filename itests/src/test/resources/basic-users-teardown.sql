delete from ShoppingCart where customer_id in (1,2,3,4,5,6);
delete from InternalAuth where id in (1,2,3,4,5,6);
delete from UserEmail where id in (1,2,3,4,5,6);
delete from UserToken where userIdentifier_id in (select id from UserIdentifier where user_id in (1,2,3,4,5,6));
delete from UserIdentifier where user_id in (1,2,3,4,5,6);
delete from PasswordResetKey where user_id in (1,2,3,4,5,6);
delete from Address where user_id in (1,2,3,4,5,6);
delete from UserSetting where user_id between 1 and 6;
delete from User where id in (1,2,3,4,5,6);