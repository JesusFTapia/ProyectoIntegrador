package com.example.moxxdesignsfront.controllers;

import com.mycompany.moxxdesignsdbconnection.entitys.Client;
import com.mycompany.moxxdesignsdbconnection.repository.ClientRepositoryImpl;
import com.mycompany.moxxdesignsdbconnection.services.ClientService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class vistaGestionClientes {

    private ClientService clientService;

    @FXML private TableView<Client> tablaClientes;
    @FXML private TableColumn<Client, Integer> colId;
    @FXML private TableColumn<Client, String> colNombre;
    @FXML private TableColumn<Client, String> colApellido;
    @FXML private TableColumn<Client, String> colTelefono;
    @FXML private TableColumn<Client, Void> colAcciones;
    @FXML private TextField buscarTextField;

    private ObservableList<Client> listaClientes = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Inicializamos el servicio
        clientService = new ClientService(new ClientRepositoryImpl());

        configurarTabla();
        cargarClientes();
        configurarBusqueda();
    }

    @FXML
    public void refrescarTabla() {
        cargarClientes();
        buscarTextField.clear();
    }

    @FXML
    public void nuevoCliente() {
        abrirFormulario(null); // null indica que es un cliente nuevo
    }

    private void configurarTabla() {
        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId().intValue()).asObject());
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        colApellido.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLastName()));
        colTelefono.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPhoneNumber()));

        // Botón Editar en la tabla
        colAcciones.setCellFactory(param -> new TableCell<Client, Void>() {
            private final Button btnEditar = new Button("✏️ Editar");

            {
                btnEditar.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 11px;");
                btnEditar.setOnAction(e -> abrirFormulario(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(btnEditar);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });

        tablaClientes.setItems(listaClientes);
    }

    private void cargarClientes() {
        listaClientes.clear();
        try {
            listaClientes.addAll(clientService.getAllClients());
            // Ordenar por ID descendente (opcional)
            listaClientes.sort((c1, c2) -> Long.compare(c2.getId(), c1.getId()));
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudieron cargar los clientes.");
            e.printStackTrace();
        }
    }

    private void configurarBusqueda() {
        buscarTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                tablaClientes.setItems(listaClientes);
            } else {
                String lower = newVal.toLowerCase();
                tablaClientes.setItems(listaClientes.filtered(c ->
                        (c.getName() != null && c.getName().toLowerCase().contains(lower)) ||
                                (c.getLastName() != null && c.getLastName().toLowerCase().contains(lower)) ||
                                (c.getPhoneNumber() != null && c.getPhoneNumber().contains(lower))
                ));
            }
        });
    }

    // --- FORMULARIO MODAL (CREAR / EDITAR) ---
    private void abrirFormulario(Client clienteExistente) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        boolean esEdicion = (clienteExistente != null);
        stage.setTitle(esEdicion ? "Editar Cliente" : "Registrar Nuevo Cliente");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(15);
        grid.setPadding(new Insets(20));
        grid.setAlignment(Pos.CENTER);

        // Campos
        TextField txtNombre = new TextField();
        TextField txtApellido = new TextField();
        TextField txtTelefono = new TextField();

        // Si es edición, llenamos los datos
        if (esEdicion) {
            txtNombre.setText(clienteExistente.getName());
            txtApellido.setText(clienteExistente.getLastName());
            txtTelefono.setText(clienteExistente.getPhoneNumber());
        }

        grid.add(new Label("Nombre:"), 0, 0); grid.add(txtNombre, 1, 0);
        grid.add(new Label("Apellido:"), 0, 1); grid.add(txtApellido, 1, 1);
        grid.add(new Label("Teléfono:"), 0, 2); grid.add(txtTelefono, 1, 2);

        Button btnGuardar = new Button(esEdicion ? "Actualizar" : "Registrar");
        btnGuardar.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 150;");

        btnGuardar.setOnAction(e -> {
            try {
                if (txtNombre.getText().isEmpty() || txtTelefono.getText().isEmpty()) {
                    mostrarAlerta("Advertencia", "Nombre y Teléfono son obligatorios.");
                    return;
                }

                Client cliente = esEdicion ? clienteExistente : new Client();

                cliente.setName(txtNombre.getText().trim());
                cliente.setLastName(txtApellido.getText().trim());
                cliente.setPhoneNumber(txtTelefono.getText().trim());

                if (esEdicion) {
                    clientService.updateClient(cliente); // Llamamos al método nuevo
                    mostrarAlerta("Éxito", "Datos del cliente actualizados correctamente.");
                } else {
                    clientService.registerNewClient(cliente);
                    mostrarAlerta("Éxito", "Cliente registrado correctamente.");
                }

                refrescarTabla();
                stage.close();

            } catch (Exception ex) {
                mostrarAlerta("Error", ex.getMessage());
                ex.printStackTrace();
            }
        });

        VBox layout = new VBox(20, grid, btnGuardar);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10));

        stage.setScene(new Scene(layout, 350, 250));
        stage.showAndWait();
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo); a.setHeaderText(null); a.setContentText(msg); a.show();
    }
}