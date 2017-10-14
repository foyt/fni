Forge & Illusion
===
[![Stories in Ready](https://badge.waffle.io/foyt/fni.png?label=ready&title=Ready)](https://waffle.io/foyt/fni) [![Build Status](https://travis-ci.org/foyt/fni.png?branch=devel)](https://travis-ci.org/foyt/fni)

Forge & Illusion is an open platform built for roleplaying and roleplayers. The application can be found from http://www.forgeandillusion.net 

## Installation

These instructions assume that system is being installed on Ubuntu Zesty 17.04 Linux.

### Prerequisites

#### Database

Get access to a MySQL dump file from current installation.

#### OAuth API Keys

Register OAuth API keys to following services:

  - Google (https://console.developers.google.com/apis/credentials)
  - Facebook (https://developers.facebook.com/)
  - Dropbox (https://www.dropbox.com/developers) OAuth API keys.

You can set up development version without them but for production use all keys are required.


#### Google Drive Service Account

Register service account to Google Drive access in https://console.developers.google.com/apis/credentials

### Install and configure MariaDB server

Install APT packages

    sudo apt update
    sudo apt install mariadb-server mariadb-client

Secure your newly installed database (optional but recommended)

    sudo mysql_secure_installation
    
Create database and database user

    sudo mysql -u root -p
    create database fni default character set = utf8mb4 collate = utf8mb4_unicode_ci;
    create user 'fni'@'localhost' IDENTIFIED BY 'yourpassword';
    grant all privileges on fni.* to fni@localhost identified by 'yourpassword';

### Install Java

    apt install openjdk-9-jdk

### Wildfly

Download an extract wildfly 10.1.0.Final (http://download.jboss.org/wildfly/10.1.0.Final/wildfly-10.1.0.Final.tar.gz) to some folder (e.g. /opt/wildfly).

    curl -s http://download.jboss.org/wildfly/10.1.0.Final/wildfly-10.1.0.Final.tar.gz|tar -xvzC /opt/ && ln -s wildfly-10.1.0.Final /opt/wildfly
    chown wildfly.wildfly -R /opt/wildfly-10.1.0.Final/
    
Create user for wildfly
 
   useradd wildly
    
Change wildfly to automatically start on boot

    cp /opt/wildfly/docs/contrib/scripts/init.d/wildfly-init-debian.sh /etc/init.d/wildfly
    cp /opt/wildfly/docs/contrib/scripts/init.d/wildfly.conf /etc/default/wildfly
    update-rc.d wildfly defaults 5
    
### Configure Wildfly

Configuration is done by editing Wildfly's standalone configuration file (/opt/wildfly/standalone/configuration/standalone.xml)

#### System properites

System properties define some global options for the application.

    <system-properties>
        <property name="jsf.project.stage" value="Production"/>
        <property name="fni-google-drive.keyFile" value="[Google Drive Key File]"/>
        <property name="fni-google-drive.accountId" value="[Google Drive Account Id]"/>
        <property name="fni-google-drive.accountUser" value="[Google Drive Account User]"/>
        <property name="fni-host" value="www.forgeandillusion.net"/>
        <property name="fni-http-port" value="80"/>
        <property name="fni-https-port" value="443"/>
    </system-properties>
    
#### Database Driver

Download and extract Wildfly MySQL module:

    curl https://dl.dropboxusercontent.com/s/qxahn0zbze2jfco/mysql-module.tar|tar -xvC /opt/wildfly/
    
Add MySQL driver into standalone.xml

    <drivers>
      <driver name="mysql" module="com.mysql.jdbc">
      <xa-datasource-class>com.mysql.jdbc.jdbc2.optional.MysqlXADataSource</xa-datasource-class>
      </driver>
    </drivers>
    
#### Database

Add database settings.

     <datasource jta="true" jndi-name="java:jboss/datasources/fni" pool-name="fni" enabled="true" use-ccm="true" statistics-enabled="true">
       <connection-url>jdbc:mysql://localhost:3306/dbname?useUnicode=true&amp;characterEncoding=UTF-8&amp;useSSL=false</connection-url>
       <driver>mysql</driver>
       <security>
         <user-name>fni</user-name>
         <password>yourpassword</password>
       </security>
       <validation>
         <check-valid-connection-sql>SELECT 1</check-valid-connection-sql>
         <validate-on-match>false</validate-on-match>
       </validation>
       <statement>
         <share-prepared-statements>false</share-prepared-statements>
       </statement>
     </datasource>

Now you can safely remove default h2 driver and database but before that you need to locate 'urn:jboss:domain:ee:4.0' subsystem and remove datasource -attribute from default-bindings context-service. 
      
### Deploy Forge & Illusion

Download WAR file and deploy it by copying it into Wildfly deployments folder

    wget http://repo1.maven.org/maven2/fi/foyt/fni/webapp/3.3.13/webapp-3.3.13.war -O /opt/wildfly/standalone/deployments/fni.war

### NGINX

Nginx is used as reverse-proxy server for the Forge & Illusion.

#### Install NGINX

    apt install nginx    
    
#### Configure NGINX

