drop table if exists conversations;
create table conversations (id int primary key, request nvarchar(256), reply nvarchar(256));