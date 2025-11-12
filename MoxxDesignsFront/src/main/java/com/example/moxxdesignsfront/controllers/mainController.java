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
    @FXML private Button citaButton;
    @FXML private Button consultarButton;
    private List<Button> menuButtons;

    @FXML
    private ImageView ImageViewTrabajo;

    @FXML
    private ImageView ImageViewCita;

    @FXML
    private ImageView ImageViewConsultar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        menuButtons = Arrays.asList(trabajosButton, citaButton, consultarButton);
        Image svgImage1 = SVGLoader.loadSVG("/icons/user.svg", 21, 21);
        ImageViewTrabajo.setImage(svgImage1);
        Image svgImage2 = SVGLoader.loadSVG("/icons/user.svg", 21, 21);
        ImageViewCita.setImage(svgImage2);
        Image svgImage3 = SVGLoader.loadSVG("/icons/user.svg", 21, 21);
        ImageViewConsultar.setImage(svgImage3);
        handleButtonActivation(trabajosButton);

        // Cargar directamente el menú de administrar trabajos
        setView("/fxml/vistaTrabajos.fxml");
    }

    private void setView(String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
            Parent newView = loader.load();

            Object controller = loader.getController();

            centerVBox.getChildren().clear();
            centerVBox.getChildren().add(newView);
            VBox.setVgrow(newView, javafx.scene.layout.Priority.ALWAYS);

            // Inyectar referencia al mainController en los controladores que lo necesiten
            if (controller instanceof vistaTrabajosController) {
                ((vistaTrabajosController) controller).setMainController(this);
            }

            if (controller instanceof vistaCrearTrabajoController) {
                ((vistaCrearTrabajoController) controller).cargarClientes();
            }

        } catch (IOException e) {
            System.err.println("Error al cargar la vista: " + fxmlFileName);
            e.printStackTrace();
        }
    }

    @FXML
    public void handleTrabajosButton() {
        handleButtonActivation(trabajosButton);
        setView("/fxml/vistaTrabajos.fxml");
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

    // Método público para que otros controladores puedan cambiar la vista
    public void cargarVistaEnCenter(String fxmlPath) {
        setView(fxmlPath);
    }

    private void handleButtonActivation(Button activeButton) {
        for (Button btn : menuButtons) {
            btn.getStyleClass().remove("menu-item-active");
            btn.setDisable(false);
        }
        activeButton.getStyleClass().add("menu-item-active");
        activeButton.setDisable(true);
    }
}