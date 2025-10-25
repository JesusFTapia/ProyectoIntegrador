package com.example.moxxdesignsfront.controllers;

import javafx.fxml.FXML;

public class administrarTrabajosController {

    private vistaTrabajosController padreController;

    public void setPadreController(vistaTrabajosController controller) {
        this.padreController = controller;
    }

    @FXML
    public void handleIrAVistaUno() {
        if (padreController != null) {

            padreController.cargarVista("/fxml/vistaCrearTrabajo.fxml");
        }
    }

    @FXML
    public void cargarVistaUno() {
        if (padreController != null) {
            // Llama al método del padre para cargar la nueva vista en su AnchorPane
            padreController.cargarVista("/fxml/vistaCrearTrabajo.fxml");
        } else {
            System.err.println("Error: No se ha establecido la referencia al controlador padre (vistaTrabajosController).");
        }
    }

    // 4. Método para manejar el botón "Ir a Vista Dos"
    @FXML
    public void cargarVistaDos() {
        if (padreController != null) {
            // Llama al método del padre para cargar la nueva vista
            padreController.cargarVista("/fxml/vistaConsultarTrabajos.fxml");
        }
    }
}
