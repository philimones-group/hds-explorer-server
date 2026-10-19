
create user "hds_explorer" with password 'hds_test';
\c "hds_explorer_db";
grant all privileges on schema "hds_explorer_db" to "hds_explorer";
alter schema "hds_explorer_db" owner to "hds_explorer";

-- create data export user
\c hds_explorer_db
-- 2. Permit hds_export to establish a connection
GRANT CONNECT ON DATABASE hds_explorer_db TO hds_export;
-- 3. Permit looking up objects inside the default public schema
GRANT USAGE ON SCHEMA public TO hds_export;
-- 4. Grant read-only SELECT permissions to all existing tables
GRANT SELECT ON ALL TABLES IN SCHEMA public TO hds_export;
-- 5. CRITICAL: Automatically grant read access to future tables created by Grails migrations
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO hds_export;
