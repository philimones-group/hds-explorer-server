
echo Copying configuration files for Local Development for MAC - HDS-Explorer

cp ../hds-explorer-resources/deployments/icemr-mac/dev/app-config.yml src/main/resources/

echo Running Grails

grails clean && grails run-app


