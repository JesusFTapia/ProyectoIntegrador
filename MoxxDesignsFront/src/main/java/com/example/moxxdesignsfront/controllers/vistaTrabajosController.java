package com.example.moxxdesignsfront.controllers;

import javafx.fxml.FXML;


public class vistaTrabajosController {

    private mainController mainController;

    // Método para inyectar la referencia al controlador principal
    public void setMainController(mainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void cargarVistaUno() {
        if (mainController != null) {
            mainController.cargarVistaEnCenter("/fxml/vistaCrearTrabajo.fxml");
        }
    }

    @FXML
    public void cargarVistaDos() {
        if (mainController != null) {
            mainController.cargarVistaEnCenter("/fxml/vistaGestionTrabajos.fxml");
        }
    }
}
