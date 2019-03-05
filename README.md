# VBOXCM
VBOXCM aims at virtualbox cluster orchestration and management.


## Requirements
* JDK11
## Building
By building the project, you get package in tar containing binaries and the structure required to run vboxcm.
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
You can run vboxcm with following profiles
* **dev** - vboxcm restricts itself to use just in memory config and console logging
* **stub** - autogenerates content for demo and development purposes
* **stub_dynamic** - simulates dynamic behaviour i.e. monitoring of servers, changing state and such

