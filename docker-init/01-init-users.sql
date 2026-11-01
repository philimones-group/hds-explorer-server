CREATE DATABASE IF NOT EXISTS `hds_explorer_db` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Primary read-write user
CREATE USER IF NOT EXISTS 'hds_explorer'@'%' IDENTIFIED BY 'hds_explorer_pass';
GRANT ALL PRIVILEGES ON `hds_explorer_db`.* TO 'hds_explorer'@'%';

-- Strictly read-only user for Data Export Tool & AI Prompt Engine
CREATE USER IF NOT EXISTS 'hds_export'@'%' IDENTIFIED BY 'hds_export_pass';
GRANT SELECT ON `hds_explorer_db`.* TO 'hds_export'@'%';

FLUSH PRIVILEGES;
