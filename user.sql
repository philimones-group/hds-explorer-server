CREATE USER 'hds_explorer'@'localhost' IDENTIFIED BY 'hds_test';
GRANT ALL ON `hds_explorer_db`.* TO 'hds_explorer'@'localhost';
flush privileges;

-- creating data export user
CREATE USER 'hds_export'@'localhost' IDENTIFIED BY 'hds_test2';
GRANT SELECT ON hds_explorer_db.* TO 'hds_export'@'localhost';
FLUSH PRIVILEGES;
