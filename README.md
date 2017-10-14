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
    
#### Configure http

Remove default site

    rm /etc/nginx/sites-enabled/default
    
Create http fni -site into /etc/nginx/sites-available (replace jed with your favourite editor)

    jed /etc/nginx/sites-available/fni-http.conf

Add add following contents:

    server {
      listen 80;
      server_name www.forgeandillusion.net;
      root /usr/share/nginx/html;
      location ~ /.well-known {
        allow all;
      }
    
      location / {
        return 301 https://$server_name$request_uri;
      }  
    }
    
Enable http site

    sudo ln -s /etc/nginx/sites-enabled/www.forgeandillusion.net-http.conf /etc/nginx/sites-available/www.forgeandillusion.net-http.conf
    sudo service nginx restart
    
#### Configure https

Install Let's encrypt

    sudo apt install python-certbot-nginx
    
Create dhparam cert

    sudo openssl dhparam -dsaparam -out /etc/ssl/certs/dhparam.pem 2048
    
Obtain SSL Certificate

    sudo certbot --nginx -d www.forgeandillusion.net
    
Remove Let's encrypt generated contents from /etc/nginx/sites-available/fni-http.conf and create https fni -site into /etc/nginx/sites-available (replace jed with your favourite editor)

    jed /etc/nginx/sites-available/fni-https.conf

Add add following contents:
    
    server {
        listen 443 ssl default_server;
        server_name www.forgeandillusion.net;
        root /usr/share/nginx/html;
        ssl_certificate /etc/letsencrypt/live/www.forgeandillusion.net/fullchain.pem;
        ssl_certificate_key /etc/letsencrypt/live/www.forgeandillusion.net/privkey.pem;
        ssl_protocols TLSv1 TLSv1.1 TLSv1.2;
        ssl_prefer_server_ciphers on;
        ssl_dhparam /etc/ssl/certs/dhparam.pem;
        ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES256-GCM-SHA384:ECDHE-ECDSA-AES256-GCM-SHA384:DHE-RSA-AES128-GCM-SHA256:DHE-DSS-AES128-GCM-SHA256:kEDH+AESGCM:ECDHE-RSA-AES128-SHA256:ECDHE-ECDSA-AES128-SHA256:ECDHE-RSA-AES128-SHA:ECDHE-ECDSA-AES128-SHA:ECDHE-RSA-AES256-SHA384:ECDHE-ECDSA-AES256-SHA384:ECDHE-RSA-AES256-SHA:ECDHE-ECDSA-AES256-SHA:DHE-RSA-AES128-SHA256:DHE-RSA-AES128-SHA:DHE-DSS-AES128-SHA256:DHE-RSA-AES256-SHA256:DHE-DSS-AES256-SHA:DHE-RSA-AES256-SHA:AES128-GCM-SHA256:AES256-GCM-SHA384:AES128-SHA256:AES256-SHA256:AES128-SHA:AES256-SHA:AES:CAMELLIA:DES-CBC3-SHA:!aNULL:!eNULL:!EXPORT:!DES:!RC4:!MD5:!PSK:!aECDH:!EDH-DSS-DES-CBC3-SHA:!EDH-RSA-DES-CBC3-SHA:!KRB5-DES-CBC3-SHA;
        ssl_session_timeout 1d;
        ssl_session_cache shared:SSL:50m;
        ssl_stapling on;
        ssl_stapling_verify on;
        client_max_body_size 200M;
    
        location / {
          proxy_pass http://localhost:8080;
          proxy_http_version 1.1;
          proxy_buffering off;
          client_max_body_size 150M;
          proxy_set_header X-Real-IP $remote_addr;
          proxy_set_header Host $host;
          proxy_set_header X-Forwarded-Proto https;
          proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
          add_header Strict-Transport-Security max-age=15768000;
          proxy_read_timeout 1m;
        }
    }

Enable https site

    sudo ln -s /etc/nginx/sites-enabled/www.forgeandillusion.net-https.conf /etc/nginx/sites-available/www.forgeandillusion.net-https.conf
    sudo service nginx restart
