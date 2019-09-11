[![Build Status](https://travis-ci.com/tpiskorski/machinator.svg?branch=master)](https://travis-ci.com/tpiskorski/machinator)
[![codecov](https://codecov.io/gh/tpiskorski/machinator/branch/master/graph/badge.svg)](https://codecov.io/gh/tpiskorski/machinator)
# Machinator
Machinator is graphical user interface of the VirtualBox distributed virtualization system.
This tool helps managing virtual machines across multiple servers.

## Features
* Graphical User Interface (GUI)
* Displaying state of virtual machines
* Managing state of virtual machines
* Migration of virtual machines
* Creating backup and cron backups (periodic backups) of virtual machines
* High Availability mechanism (Watchdog) for virtual machines. You can define failover server in case of failure.

## Requirements
* JDK11
* Any Linux distribution or MacOS
* To build you need JDK11, Maven, Git

## Building
By building the project, you get package in tar containing binaries and the structure required to run Machinator.

### Command
Use `mvn clean install`, ensure u have *JAVA_PATH* defined as env variable

## Running
After building the project, you need to unzip the archive in the target directory (i.e. with unzip command).
Then run the jar with command `java -jar machinator.jar`

