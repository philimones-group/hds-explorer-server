# HDS-Explorer Server
![Platform](https://img.shields.io/badge/platform-Grails-blue.svg)
![Language](https://img.shields.io/badge/platform-Groovy-blue.svg)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

HDS-Explorer Server application provides the data repository for a Health and Demographics Surveillance system and data management tool for data collection for program or research implementation

This application server can be deployed on a Tomcat server (or any servlet 2.5+ compatible web container) and backed by any Hibernate supported database (eg. MySQL or PostgreSQL database server, etc).

* HDS-Explorer website: [https://www.hds-explorer.org](https://www.hds-explorer.org)
* ODK website: [https://opendatakit.org](https://opendatakit.org)


### Build/Development Instructions
HDS-Explorer is being developed using Grails Framework 5.3.2, a Groovy Web Application Framework https://grails.org.

To get started with HDS-Explorer, simply clone the repository and then from within your local copy:

**Install the Development Framework - Grails using SDKMAN**  
   For instructions on how to install SDKMAN visit https://sdkman.io/install
   * Execute the command below to install grails 5.3.2  
   `sdk install grails 5.3.2`   
   * Clone HDS-Explorer repository  
   `git clone https://github.com/philimones-group/hds-explorer-server.git`
   * Configure the MySQL Database Access (under the cloned directory hds-explorer, import the file **user.sql** to mysql database)  
   `mysql -u $MYSQL_USER -p < user.sql`
   * Create HDS-Explorer resources directory and grant access to your Linux User  
   `sudo mkdir /var/lib/hds-explorer`   
   `sudo chown -R $USER:$USER_GROUP /var/lib/hds-explorer/`  
   * Goto hds-explorer cloned directory and Generate WAR  
   `cd hds-explorer-server`   
   `grails clean && grails war`
  
### Binary Files for HDS-Explorer Server and Client/Mobile

**hds-explorer-server.war** - A War binary file to be deployed in a Java web container server  
**hds-explorer-tablet.apk** - A Android APK file to be installed in a Android Device (Minimum: API-21 or Android 5.0+)<br>
**hds-explorer-installer-vXXX.run** - A executable file for Linux enviroments that allows you to use a graphical interface to customize HDS-Explorer Server database access and app parameters and then export a War file to be deployed on Tomcat  
**hds-explorer-installer-vXXX.exe** - A executable file for Windows enviroments that allows you to use a graphical interface to customize HDS-Explorer Server database access and app parameters and then export a War file to be deployed on Tomcat

The binary file **hds-explorer-server.war** can be found in link https://github.com/philimones-group/hds-explorer-server/releases    
The binary file **hds-explorer-tablet.apk** can be found in link https://github.com/philimones-group/hds-explorer-tablet/releases<br />
The binary file **hds-explorer-installer-vXXX.run/exe** can be found in link https://github.com/philimones-group/hds-explorer-server/releases

### Server Deployment instructions (in Linux System):
Lets consider that we are in a Linux distribution and Tomcat Server that was manually installed in the path: /opt/tomcat.

**1. Create the hds-explorer resources directory**  
   `sudo mkdir /var/lib/hds-explorer`  
   
**2. Grant access to tomcat user to the folder /var/lib/hds-explorer**  
   `sudo chown -R tomcat8:tomcat8 /var/lib/hds-explorer/`  
   
**3. Copy the war file to $TOMCAT/webapps**  
   `cp hds-explorer-server.war /opt/tomcat/webapps`  
   
**4. Start the webserver or we can copy the war file while the server is running and it will be deployed onfly.**  
   `/opt/tomcat/bin/startup.sh`  