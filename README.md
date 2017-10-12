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

  - Google (https://console.cloud.google.com)
  - Facebook (https://developers.facebook.com/)
  - Dropbox (https://www.dropbox.com/developers) OAuth API keys.

You can set up development version without them but for production use all keys are required.

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
