CREATE USER 'hds_explorer'@'localhost' IDENTIFIED BY 'd33_2019xx';

GRANT ALL ON `hds_explorer_db`.* TO 'hds_explorer'@'localhost';
GRANT ALL ON `openhds`.* TO 'hds_explorer'@'localhost';
flush privileges;