package com.example.moxxdesignsfront.controllers;

import com.example.moxxdesignsfront.SVGLoader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class mainController implements Initializable {
    @FXML
    private Label welcomeText;

    @FXML
    private VBox centerVBox;

    @FXML private Button trabajosButton;
    @FXML private Button cotizacionButton;
    @FXML private Button citaButton;
    @FXML private Button consultarButton;
    private List<Button> menuButtons;

    @FXML
    private ImageView ImageViewCotizacion;
    
    @FXML
    private ImageView ImageViewTrabajo;

    @FXML
    private ImageView ImageViewCita;

    @FXML
    private ImageView ImageViewConsultar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        menuButtons = Arrays.asList(cotizacionButton, trabajosButton, citaButton, consultarButton);
        handleButtonActivation(cotizacionButton);
        setView("/fxml/vistaCotizacion.fxml");
        Image svgImage0 = SVGLoader.loadSVG("/icons/user.svg", 21, 21);
        ImageViewCotizacion.setImage(svgImage0);
        Image svgImage1 = SVGLoader.loadSVG("/icons/user.svg", 21, 21);
        ImageViewTrabajo.setImage(svgImage1);
        Image svgImage2 = SVGLoader.loadSVG("/icons/user.svg", 21, 21);
        ImageViewCita.setImage(svgImage2);
        Image svgImage3 = SVGLoader.loadSVG("/icons/user.svg", 21, 21);
        ImageViewConsultar.setImage(svgImage3);
    }
    private void setView(String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
            Parent newView = loader.load(); // La vista (Parent) se crea aquí.

            // **Paso 1: Obtener el controlador antes de añadir la vista**
            Object controller = loader.getController();

            centerVBox.getChildren().clear();
            centerVBox.getChildren().add(newView);
            VBox.setVgrow(newView, javafx.scene.layout.Priority.ALWAYS);

            // **Paso 2: Inicialización condicional**
            // Si la vista que acabamos de cargar es vistaTrabajos...
            if (controller instanceof com.example.moxxdesignsfront.controllers.vistaTrabajosController) {

                // ... llamamos al método para que cargue su contenido interno
                ((com.example.moxxdesignsfront.controllers.vistaTrabajosController) controller).cargarVistaPorDefecto();

                // System.out.println("Vista de Trabajos inicializada con contenido por defecto."); // Debug
            }

        } catch (IOException e) {
            System.err.println("Error al cargar la vista: " + fxmlFileName);
            e.printStackTrace();
        }
    }

    @FXML
    public void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    public void handleTrabajosButton() {
        handleButtonActivation(trabajosButton);
        setView("/fxml/vistaTrabajos.fxml");
    }
    
    @FXML
    public void handleCotizacionButton() {
        handleButtonActivation(cotizacionButton);
        setView("/fxml/vistaCotizacion.fxml");
    }

    @FXML
    public void handleCitasButton() {
        handleButtonActivation(citaButton);
        setView("/fxml/vistaCitas.fxml");
    }

    @FXML
    public void handleConsultarButton() {
        setView("/fxml/consultar_view.fxml");
    }

    private void handleButtonActivation(Button activeButton) {
        // 1. Recorrer y deshabilitar/habilitar
        for (Button btn : menuButtons) {
            // Quitar la clase 'active' de todos (estilo)
            btn.getStyleClass().remove("menu-item-active");

            // ¡CLAVE! Habilita todos los botones para que se puedan presionar de nuevo
            btn.setDisable(false);
        }

        // 2. Activar el estilo del botón actual
        activeButton.getStyleClass().add("menu-item-active");

        // 3. ¡CLAVE! Deshabilita el botón que ya está activo
        activeButton.setDisable(true);
    }
}
