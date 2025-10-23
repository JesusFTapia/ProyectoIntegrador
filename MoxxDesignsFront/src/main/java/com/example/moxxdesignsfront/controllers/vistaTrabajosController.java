package com.example.moxxdesignsfront.controllers;

import com.example.moxxdesignsfront.SVGLoader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class vistaTrabajosController implements Initializable {
    @FXML
    private Label welcomeText;

    @FXML
    private ImageView ImageViewTrabajos;

    @FXML
    private ImageView ImageViewCitas;

    @FXML
    private ImageView ImageViewConsultar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}
