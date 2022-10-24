create database "hds_explorer_db";
create user "hds_explorer" with password 'hds_test';

grant all privileges on database "hds_explorer_db" to "hds_explorer";
alter database "hds_explorer_db" owner to "hds_explorer";

-- \c "hds_explorer_db";
-- create schema "hds_explorer_db";
-- grant all privileges on schema "hds_explorer_db" to "hds_explorer";
-- alter schema "hds_explorer_db" owner to "hds_explorer";