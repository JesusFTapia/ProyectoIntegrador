package com.example.moxxdesignsfront.controllers;

import com.mycompany.moxxdesignsdbconnection.entitys.*;
import com.mycompany.moxxdesignsdbconnection.services.JobService;
import com.mycompany.moxxdesignsdbconnection.repository.JobRepositoryImpl;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class vistaGestionTrabajos {

    private JobService jobService;

    @FXML private TableView<Job> tablaTrabajos;
    @FXML private TableColumn<Job, Integer> columnaId;
    @FXML private TableColumn<Job, String> columnaCliente;
    @FXML private TableColumn<Job, String> columnaTipo;
    @FXML private TableColumn<Job, String> columnaEstado;
    @FXML private TableColumn<Job, String> columnaEntrega;
    @FXML private TableColumn<Job, Void> columnaAcciones;
    @FXML private TextField buscarTextField;

    private ObservableList<Job> listaTrabajos = FXCollections.observableArrayList();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @FXML
    public void initialize() {
        jobService = new JobService(new JobRepositoryImpl());

        configurarTabla();
        cargarTrabajos();
        configurarBusqueda();
    }

    @FXML
    public void refrescarTabla() {
        cargarTrabajos();
        buscarTextField.clear();
        System.out.println("Tabla de trabajos actualizada.");
    }

    private void configurarTabla() {
        columnaId.setCellValueFactory(c -> new SimpleIntegerProperty((int)c.getValue().getId()).asObject());

        columnaCliente.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getClient() != null ? c.getValue().getClient().getName() : "Sin Cliente"
        ));

        // Columna Tipo: Muestra "Vehículo" o el Tipo de Trabajo
        columnaTipo.setCellValueFactory(c -> {
            Job j = c.getValue();
            String texto;
            if (j instanceof VehicularJob) {
                VehicularJob vj = (VehicularJob) j;
                texto = (j.getJobType() != null ? j.getJobType().getName() : "Vehicular") +
                        ": " + vj.getModel() + " (" + vj.getYear() + ")";
            } else {
                texto = j.getJobType() != null ? j.getJobType().getName() : "General";
            }
            return new SimpleStringProperty(texto);
        });

        columnaEntrega.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDeliveryDate() != null ? dateFormat.format(c.getValue().getDeliveryDate()) : "N/A"
        ));

        // Columna Estado con Colores
        columnaEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getState()));
        columnaEstado.setCellFactory(col -> new TableCell<Job, String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); setStyle("");
                } else {
                    setText(item);
                    switch (item.toUpperCase()) {
                        case "PENDIENTE": setStyle("-fx-background-color: #fff3cd; -fx-text-fill: #856404; -fx-alignment: CENTER; -fx-font-weight: bold; -fx-background-radius: 5;"); break;
                        case "EN PROCESO": setStyle("-fx-background-color: #cce5ff; -fx-text-fill: #004085; -fx-alignment: CENTER; -fx-font-weight: bold; -fx-background-radius: 5;"); break;
                        case "COMPLETADO": setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-alignment: CENTER; -fx-font-weight: bold; -fx-background-radius: 5;"); break;
                        case "CANCELADO": setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #721c24; -fx-alignment: CENTER; -fx-font-weight: bold; -fx-background-radius: 5;"); break;
                        default: setStyle("-fx-alignment: CENTER;");
                    }
                }
            }
        });

        columnaAcciones.setCellFactory(param -> new TableCell<Job, Void>() {
            private final Button btnEditar = new Button("✏️ Editar");
            private final Button btnDetalles = new Button("👁 Detalles");

            {
                btnEditar.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 11px;");
                btnDetalles.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 11px;");

                btnEditar.setOnAction(e -> abrirModalEdicion(getTableView().getItems().get(getIndex())));
                btnDetalles.setOnAction(e -> mostrarDetalles(getTableView().getItems().get(getIndex())));
            }

            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else {
                    HBox box = new HBox(5, btnDetalles, btnEditar);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });

        tablaTrabajos.setItems(listaTrabajos);
    }

    private void cargarTrabajos() {
        listaTrabajos.clear();
        listaTrabajos.addAll(jobService.getAllJobs());
        // Ordenar: Más recientes primero
        listaTrabajos.sort((j1, j2) -> Long.compare(j2.getId(), j1.getId()));
    }

    // =================================================================================
    //  LÓGICA DE BÚSQUEDA MEJORADA
    // =================================================================================
    private void configurarBusqueda() {
        buscarTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                tablaTrabajos.setItems(listaTrabajos);
            } else {
                String lower = newVal.toLowerCase();

                tablaTrabajos.setItems(listaTrabajos.filtered(j -> {
                    // 1. Buscar por ID
                    if (String.valueOf(j.getId()).contains(lower)) return true;

                    // 2. Buscar por Cliente
                    if (j.getClient() != null && j.getClient().getName().toLowerCase().contains(lower)) return true;

                    // 3. Buscar por Estado
                    if (j.getState() != null && j.getState().toLowerCase().contains(lower)) return true;

                    // 4. Buscar por Tipo de Trabajo (Nombre)
                    if (j.getJobType() != null && j.getJobType().getName().toLowerCase().contains(lower)) return true;

                    // 5. Buscar por Datos del Vehículo (Modelo, Color, Año)
                    if (j instanceof VehicularJob) {
                        VehicularJob vj = (VehicularJob) j;
                        if (vj.getModel() != null && vj.getModel().toLowerCase().contains(lower)) return true;
                        if (vj.getColor() != null && vj.getColor().toLowerCase().contains(lower)) return true;
                        if (String.valueOf(vj.getYear()).contains(lower)) return true;
                    }

                    return false;
                }));
            }
        });
    }

    // =================================================================================

    private void abrirModalEdicion(Job job) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Editar Trabajo #" + job.getId());

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20));

        ComboBox<String> cmbEstado = new ComboBox<>();
        cmbEstado.getItems().addAll("PENDIENTE", "EN PROCESO", "COMPLETADO", "CANCELADO");
        cmbEstado.setValue(job.getState());

        DatePicker dpEntrega = new DatePicker();
        if (job.getDeliveryDate() != null) {
            dpEntrega.setValue(job.getDeliveryDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }

        TextArea txtDesc = new TextArea(job.getDescription());
        txtDesc.setPrefRowCount(3);

        grid.add(new Label("Estado:"), 0, 0); grid.add(cmbEstado, 1, 0);
        grid.add(new Label("Entrega:"), 0, 1); grid.add(dpEntrega, 1, 1);
        grid.add(new Label("Descripción:"), 0, 2); grid.add(txtDesc, 1, 2);

        // Variables para campos vehiculares
        TextField txtModelo = new TextField();
        TextField txtColor = new TextField();
        TextField txtAnio = new TextField();

        boolean esVehicular = job instanceof VehicularJob;
        if (esVehicular) {
            VehicularJob vj = (VehicularJob) job;
            txtModelo.setText(vj.getModel());
            txtColor.setText(vj.getColor());
            txtAnio.setText(String.valueOf(vj.getYear()));

            grid.add(new Separator(), 0, 3, 2, 1);
            Label lblV = new Label("Datos Vehículo");
            lblV.setStyle("-fx-font-weight: bold;");
            grid.add(lblV, 0, 3);

            grid.add(new Label("Modelo:"), 0, 4); grid.add(txtModelo, 1, 4);
            grid.add(new Label("Color:"), 0, 5); grid.add(txtColor, 1, 5);
            grid.add(new Label("Año:"), 0, 6); grid.add(txtAnio, 1, 6);
        }

        Button btnGuardar = new Button("Guardar Cambios");
        btnGuardar.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        btnGuardar.setMaxWidth(Double.MAX_VALUE);

        btnGuardar.setOnAction(e -> {
            try {
                job.setState(cmbEstado.getValue());
                job.setDescription(txtDesc.getText());
                if (dpEntrega.getValue() != null) {
                    job.setDeliveryDate(Date.from(dpEntrega.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                }

                if (esVehicular) {
                    VehicularJob vj = (VehicularJob) job;
                    vj.setModel(txtModelo.getText());
                    vj.setColor(txtColor.getText());
                    try {
                        vj.setYear(Integer.parseInt(txtAnio.getText()));
                    } catch (NumberFormatException nfe) {
                        mostrarAlerta("Error", "El año debe ser numérico");
                        return;
                    }
                }

                jobService.editJob(job);
                mostrarAlerta("Éxito", "Trabajo actualizado correctamente");
                refrescarTabla(); // Usar el método refrescar para limpiar filtro si es necesario
                stage.close();
            } catch (Exception ex) {
                mostrarAlerta("Error", "Datos inválidos: " + ex.getMessage());
            }
        });

        VBox layout = new VBox(15, grid, btnGuardar);
        layout.setAlignment(Pos.CENTER);
        stage.setScene(new Scene(layout));
        stage.showAndWait();
    }

    private void mostrarDetalles(Job job) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalles - Trabajo #" + job.getId());
        alert.setHeaderText(null);

        StringBuilder content = new StringBuilder();
        content.append("Cliente: ").append(job.getClient().getName()).append("\n");
        content.append("Teléfono: ").append(job.getClient().getPhoneNumber()).append("\n");
        content.append("Tipo: ").append(job.getJobType() != null ? job.getJobType().getName() : "N/A").append("\n");
        content.append("Descripción: ").append(job.getDescription()).append("\n");

        if (job instanceof VehicularJob) {
            VehicularJob vj = (VehicularJob) job;
            content.append("\n=== Vehículo ===\n");
            content.append("Modelo: ").append(vj.getModel()).append("\n");
            content.append("Color: ").append(vj.getColor()).append("\n");
            content.append("Año: ").append(vj.getYear()).append("\n");
        }

        TextArea area = new TextArea(content.toString());
        area.setEditable(false);
        area.setWrapText(true);
        area.setMaxWidth(Double.MAX_VALUE);
        area.setMaxHeight(Double.MAX_VALUE);

        alert.getDialogPane().setContent(area);
        alert.showAndWait();
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo); a.setContentText(msg); a.show();
    }
}