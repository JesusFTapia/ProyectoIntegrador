module com.example.moxxdesignsfront {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires javafx.graphics;
    requires java.desktop;
    requires java.xml;

    requires java.sql;
    requires java.naming;
    requires java.persistence;
    requires java.management;

    requires org.eclipse.persistence.core;
    requires org.eclipse.persistence.jpa;

    requires batik.transcoder;

    requires MoxxDesignsDBConnection;
    requires itextpdf;

    opens com.example.moxxdesignsfront to javafx.fxml;
    exports com.example.moxxdesignsfront;
    exports com.example.moxxdesignsfront.controllers;
    opens com.example.moxxdesignsfront.controllers to javafx.fxml;
}