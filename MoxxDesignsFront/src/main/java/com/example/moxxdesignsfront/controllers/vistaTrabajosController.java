package com.example.moxxdesignsfront.controllers;

import com.example.moxxdesignsfront.SVGLoader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class vistaTrabajosController{

    @FXML
    private AnchorPane areaDeContenido;

    public void cargarVistaPorDefecto() {
        cargarVista("/fxml/vistaAdministrarTrabajos.fxml");
    }
    @FXML
    public void cargarVistaUno() {
        cargarVista("/fxml/vistaCrearTrabajo.fxml");
    }

    @FXML
    public void cargarVistaDos() {
        cargarVista("/fxml/vistaConsultarTrabajos.fxml");
    }

    protected void cargarVista(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent vistaCargada = loader.load();

            Object controller = loader.getController();

            if (controller instanceof administrarTrabajosController) {
                ((administrarTrabajosController) controller).setPadreController(this);
            }
            if (controller instanceof crearTrabajoController) {
                crearTrabajoController crearController = (crearTrabajoController) controller;
                crearController.cargarClientes();
            }
            areaDeContenido.getChildren().clear();
            areaDeContenido.getChildren().add(vistaCargada);

            AnchorPane.setTopAnchor(vistaCargada, 0.0);
            AnchorPane.setBottomAnchor(vistaCargada, 0.0);
            AnchorPane.setLeftAnchor(vistaCargada, 0.0);
            AnchorPane.setRightAnchor(vistaCargada, 0.0);

        } catch (IOException e) {
            System.err.println("Error al cargar la vista FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }
}
