CREATE USER 'hds_explorer'@'localhost' IDENTIFIED BY 'hds_test';
GRANT ALL ON `hds_explorer_db`.* TO 'hds_explorer'@'localhost';
flush privileges;