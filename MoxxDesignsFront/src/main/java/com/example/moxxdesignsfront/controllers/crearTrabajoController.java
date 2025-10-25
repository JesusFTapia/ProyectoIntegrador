package com.example.moxxdesignsfront.controllers;

import com.mycompany.moxxdesignsdbconnection.entitys.Client;
import com.mycompany.moxxdesignsdbconnection.services.*;
import com.mycompany.moxxdesignsdbconnection.repository.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;

public class crearTrabajoController {

    private IClientRepository clientRepository = new ClientRepositoryImpl();
    private ClientService clientService = new ClientService(clientRepository);

    @FXML
    private ComboBox<Client> clienteComboBox;

    @FXML
    private TextField tituloTextField;

    @FXML
    private TextArea descripcionTextArea;

    @FXML
    private TextField campo10TextField;

    private ObservableList<Client> todosLosClientes;

    public void cargarClientes() {
        try {
            List<Client> clientes = clientRepository.findAll();

            todosLosClientes = FXCollections.observableArrayList(clientes);

            clienteComboBox.setItems(todosLosClientes);

            clienteComboBox.setEditable(true);

            clienteComboBox.setCellFactory(param -> new ListCell<Client>() {
                @Override
                protected void updateItem(Client cliente, boolean empty) {
                    super.updateItem(cliente, empty);
                    if (empty || cliente == null) {
                        setText(null);
                    } else {
                        setText(cliente.getName() + " - " + cliente.getPhoneNumber());
                    }
                }
            });

            clienteComboBox.setButtonCell(new ListCell<Client>() {
                @Override
                protected void updateItem(Client cliente, boolean empty) {
                    super.updateItem(cliente, empty);
                    if (empty || cliente == null) {
                        setText(null);
                    } else {
                        setText(cliente.getName() + " - " + cliente.getPhoneNumber());
                    }
                }
            });

            configurarBusquedaEnComboBox();

            System.out.println("Clientes cargados: " + clientes.size());

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudieron cargar los clientes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurarBusquedaEnComboBox() {
        TextField editor = clienteComboBox.getEditor();

        editor.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!clienteComboBox.isShowing() && newValue != null && !newValue.isEmpty()) {
                clienteComboBox.show();
            }

            if (newValue == null || newValue.isEmpty()) {
                clienteComboBox.setItems(todosLosClientes);
            } else {
                String textoFiltro = newValue.toLowerCase();
                ObservableList<Client> clientesFiltrados = FXCollections.observableArrayList();

                for (Client cliente : todosLosClientes) {
                    String nombre = cliente.getName() != null ? cliente.getName().toLowerCase() : "";
                    String telefono = cliente.getPhoneNumber() != null ? cliente.getPhoneNumber() : "";

                    if (nombre.contains(textoFiltro) || telefono.contains(textoFiltro)) {
                        clientesFiltrados.add(cliente);
                    }
                }

                clienteComboBox.setItems(clientesFiltrados);
            }
        });

        clienteComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                editor.setText(newVal.getName() + " - " + newVal.getPhoneNumber());
            }
        });

        editor.setOnMouseClicked(event -> {
            if (!clienteComboBox.isShowing()) {
                clienteComboBox.show();
            }
        });
    }

    @FXML
    private void guardarTrabajo() {
        Client clienteSeleccionado = clienteComboBox.getValue();

        if (clienteSeleccionado == null) {
            mostrarAlerta("Error", "Debe seleccionar un cliente de la lista");
            return;
        }

        String titulo = tituloTextField.getText();
        String descripcion = descripcionTextArea.getText();
        String campo10 = campo10TextField.getText();

        System.out.println("Cliente: " + clienteSeleccionado.getName());
        System.out.println("Título: " + titulo);

        mostrarAlerta("Éxito", "Trabajo guardado correctamente");

        limpiarFormulario();
    }

    private void limpiarFormulario() {
        clienteComboBox.setValue(null);
        clienteComboBox.getEditor().clear();
        tituloTextField.clear();
        descripcionTextArea.clear();
        campo10TextField.clear();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
