package com.example.moxxdesignsfront;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MoxxDesignsStart extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        System.out.println("=== INICIANDO APLICACIÓN ===");
            System.out.println("Cargando FXML...");
            FXMLLoader fxmlLoader = new FXMLLoader(MoxxDesignsStart.class.getResource("/fxml/main.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            stage.setTitle("Moxx Designs App");
            stage.setScene(scene);
            stage.show();

            System.out.println("=== APLICACIÓN INICIADA ===");

    }
}
