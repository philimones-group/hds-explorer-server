
echo Copying configuration files for Malaria Alert Centre - HDS-Explorer

cp ../hds-explorer-resources/deployments/icemr-mac/prod/app-config.yml src/main/resources/

echo Running Grails WAR

grails clean && grails war

echo Restore the local configuration files

cp ../hds-explorer-resources/deployments/icemr-mac/dev/app-config.yml src/main/resources/