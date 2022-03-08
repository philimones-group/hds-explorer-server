CREATE USER 'hds_test'@'localhost' IDENTIFIED BY 'hds_test';

GRANT ALL ON `hds_explorer_db_test`.* TO 'hds_test'@'localhost';
flush privileges;