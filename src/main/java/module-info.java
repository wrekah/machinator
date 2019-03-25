open module vboxcm {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires spring.context;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires transitive org.apache.logging.log4j;
    requires spring.core;
    requires spring.beans;
    requires slf4j.api;
    requires java.sql;
    requires java.desktop;

    exports tpiskorski.vboxcm;
}
