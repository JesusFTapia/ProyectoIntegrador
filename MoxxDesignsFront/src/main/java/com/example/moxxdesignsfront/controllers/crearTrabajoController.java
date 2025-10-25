package com.example.moxxdesignsfront.controllers;

import com.mycompany.moxxdesignsdbconnection.entitys.Client;
import com.mycompany.moxxdesignsdbconnection.services.*;
import com.mycompany.moxxdesignsdbconnection.repository.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
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

        clienteComboBox.setConverter(new StringConverter<Client>() {
            @Override
            public String toString(Client cliente) {
                if (cliente == null) {
                    return "";
                }
                return cliente.getName() + " - " + cliente.getPhoneNumber();
            }

            @Override
            public Client fromString(String string) {
                if (string == null || string.isEmpty()) {
                    return null;
                }
                for (Client cliente : todosLosClientes) {
                    String clienteString = cliente.getName() + " - " + cliente.getPhoneNumber();
                    if (clienteString.equals(string)) {
                        return cliente;
                    }
                }
                return null;
            }
        });

        TextField editor = clienteComboBox.getEditor();

        editor.textProperty().addListener((observable, oldValue, newValue) -> {
            final Client selected = clienteComboBox.getSelectionModel().getSelectedItem();

            if (selected != null && newValue.equals(clienteComboBox.getConverter().toString(selected))) {
                return;
            }

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

    @FXML
    private void abrirRegistroCliente() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/registrarCliente.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Registrar Nuevo Cliente");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.showAndWait();

            cargarClientes();

        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo abrir el formulario de registro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
