module vboxcm {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires spring.context;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires org.apache.logging.log4j;
    requires slf4j.api;
    requires cfg4j.core;


    exports com.github.tpiskorski.vboxcm;
    opens com.github.tpiskorski.vboxcm to spring.core, spring.beans;
    opens com.github.tpiskorski.vboxcm.controller to spring.core, spring.beans, javafx.fxml;

}