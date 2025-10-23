module com.example.moxxdesignsfront {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires java.desktop;
    requires batik.transcoder;
    requires java.xml;
    requires javafx.graphics;


    opens com.example.moxxdesignsfront to javafx.fxml;
    exports com.example.moxxdesignsfront;
    exports com.example.moxxdesignsfront.controllers;
    opens com.example.moxxdesignsfront.controllers to javafx.fxml;
}