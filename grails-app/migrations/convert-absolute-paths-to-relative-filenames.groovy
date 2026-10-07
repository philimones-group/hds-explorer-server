databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "convert-absolute-paths-to-relative-filenames-20250223") {

        // MySQL & MariaDB
        sql(dbms: "mysql, mariadb", """
            UPDATE ext_dataset 
            SET filename = SUBSTRING_INDEX(REPLACE(filename, '\\\\', '/'), '/', -1) 
            WHERE filename LIKE '%/%' OR filename LIKE '%\\\\%';

            UPDATE core_form_extension 
            SET ext_form_path = SUBSTRING_INDEX(REPLACE(ext_form_path, '\\\\', '/'), '/', -1) 
            WHERE ext_form_path LIKE '%/%' OR ext_form_path LIKE '%\\\\%';

            UPDATE _log_report_file 
            SET filename = SUBSTRING_INDEX(REPLACE(filename, '\\\\', '/'), '/', -1) 
            WHERE filename LIKE '%/%' OR filename LIKE '%\\\\%';
        """)

        // PostgreSQL
        sql(dbms: "postgresql", """
            UPDATE ext_dataset 
            SET filename = REGEXP_REPLACE(filename, '.*[\\\\/]', '') 
            WHERE filename LIKE '%/%' OR filename LIKE '%\\\\%';

            UPDATE core_form_extension 
            SET ext_form_path = REGEXP_REPLACE(ext_form_path, '.*[\\\\/]', '') 
            WHERE ext_form_path LIKE '%/%' OR ext_form_path LIKE '%\\\\%';

            UPDATE _log_report_file 
            SET filename = REGEXP_REPLACE(filename, '.*[\\\\/]', '') 
            WHERE filename LIKE '%/%' OR filename LIKE '%\\\\%';
        """)

        // Generic SQL / H2 Database
        sql(dbms: "h2", """
            UPDATE ext_dataset 
            SET filename = REGEXP_REPLACE(filename, '.*[\\\\/]', '') 
            WHERE filename LIKE '%/%' OR filename LIKE '%\\\\%';

            UPDATE core_form_extension 
            SET ext_form_path = REGEXP_REPLACE(ext_form_path, '.*[\\\\/]', '') 
            WHERE ext_form_path LIKE '%/%' OR ext_form_path LIKE '%\\\\%';

            UPDATE _log_report_file 
            SET filename = REGEXP_REPLACE(filename, '.*[\\\\/]', '') 
            WHERE filename LIKE '%/%' OR filename LIKE '%\\\\%';
        """)
    }
}
