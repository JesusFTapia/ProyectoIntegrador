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
    @FXML private Button citasButton;
    @FXML private Button consultarButton;
    private List<Button> menuButtons;

    @FXML
    private ImageView ImageViewTrabajos;

    @FXML
    private ImageView ImageViewCitas;

    @FXML
    private ImageView ImageViewConsultar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        menuButtons = Arrays.asList(trabajosButton, citasButton, consultarButton);
        handleButtonActivation(trabajosButton);
        setView("/fxml/vistaTrabajos.fxml");
        Image svgImage1 = SVGLoader.loadSVG("/icons/user.svg", 21, 21);
        ImageViewTrabajos.setImage(svgImage1);
        Image svgImage2 = SVGLoader.loadSVG("/icons/user.svg", 21, 21);
        ImageViewCitas.setImage(svgImage2);
        Image svgImage3 = SVGLoader.loadSVG("/icons/user.svg", 21, 21);
        ImageViewConsultar.setImage(svgImage3);
    }
    private void setView(String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
            Parent newView = loader.load();

            centerVBox.getChildren().clear();

            centerVBox.getChildren().add(newView);

            VBox.setVgrow(newView, javafx.scene.layout.Priority.ALWAYS);

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
    protected void handleTrabajosButton() {
        handleButtonActivation(trabajosButton);
        setView("/fxml/vistaTrabajos.fxml");
    }

    @FXML
    public void handleCitasButton() {
        handleButtonActivation(citasButton);
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
