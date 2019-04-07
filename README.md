[![Build Status](https://travis-ci.com/tpiskorski/machinator.svg?branch=master)](https://travis-ci.com/tpiskorski/machinator)
[![codecov](https://codecov.io/gh/tpiskorski/machinator/branch/master/graph/badge.svg)](https://codecov.io/gh/tpiskorski/machinator)
# Machinator
Machinator aims at virtualbox cluster orchestration and management.


## Requirements
* JDK11
## Building
By building the project, you get package in tar containing binaries and the structure required to run Machinator.
### Binaries
##### Use mvn wrapper 
Linux:`./mvnw clean install` <br/>
Windows: `./mvnw.cmd clean install`
##### With maven installed 
Use `mvn clean install`, ensure u have *JAVA_PATH* defined as env variable
### Development
##### Use mvn wrapper 
Linux:`./mvnw clean spring-boot:run` <br/>
Windows: `./mvnw.cmd clean spring-boot:run`
##### With maven installed 
Use `mvn clean spring-boot:run`, ensure u have *JAVA_PATH* defined as env variable
#### Spring profiles
You can run Machinator with following profiles
* **dev** - Machinator restricts itself to use just in memory config and console logging
* **demo** - auto-generates content for demo and development purposes, simulates dynamic behaviour

