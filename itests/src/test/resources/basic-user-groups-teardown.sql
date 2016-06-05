delete from MaterialShareGroup where userGroup_id in (2001, 2002);
delete from UserGroupMember where group_id in (2001, 2002);
delete from UserGroup where id in (2001, 2002);