package com.example.moxxdesignsfront.controllers;

import com.mycompany.moxxdesignsdbconnection.entitys.*;
import com.mycompany.moxxdesignsdbconnection.services.*;
import com.mycompany.moxxdesignsdbconnection.repository.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class vistaCotizacionRead {

    private JobService jobService;

    @FXML
    private TableView<Job> tablaQuotations;

    @FXML
    private TableColumn<Job, Integer> columnaId;

    @FXML
    private TableColumn<Job, String> columnaFechaEmision;

    @FXML
    private TableColumn<Job, String> columnaCliente;

    @FXML
    private TableColumn<Job, String> columnaTipoTrabajo;

    @FXML
    private TableColumn<Job, String> columnaEstado;

    @FXML
    private TableColumn<Job, Double> columnaTotal;

    @FXML
    private TableColumn<Job, Double> columnaManoObra;

    @FXML
    private TableColumn<Job, Void> columnaAcciones;

    @FXML
    private TextField buscarTextField;

    @FXML
    private Label totalRegistrosLabel;

    private ObservableList<Job> todosLosTrabajos = FXCollections.observableArrayList();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @FXML
    public void initialize() {
        // Inicializar el servicio con el repositorio correspondiente
        IJobRepository jobRepository = new JobRepositoryImpl(); // Ajusta según tu implementación
        jobService = new JobService(jobRepository);
        
        configurarTabla();
        cargarTrabajos();
        configurarBusqueda();
    }

    private void configurarTabla() {
        // Configurar columna ID
        columnaId.setCellValueFactory(cellData -> 
            new SimpleIntegerProperty((int)cellData.getValue().getId()).asObject());

        // Configurar columna Fecha
        columnaFechaEmision.setCellValueFactory(cellData -> {
            Job job = cellData.getValue();
            String fechaStr = "N/A";
            if (job.getQuotations() != null && !job.getQuotations().isEmpty()) {
                Date fecha = job.getQuotations().get(0).getEmisionDate();
                fechaStr = fecha != null ? dateFormat.format(fecha) : "N/A";
            }
            return new SimpleStringProperty(fechaStr);
        });

        // Configurar columna Cliente
        columnaCliente.setCellValueFactory(cellData -> {
            Job job = cellData.getValue();
            String clienteNombre = job.getClient() != null ? job.getClient().getName() : "N/A";
            return new SimpleStringProperty(clienteNombre);
        });

        // Configurar columna Tipo de Trabajo
        columnaTipoTrabajo.setCellValueFactory(cellData -> {
            Job job = cellData.getValue();
            String tipoTrabajo = "N/A";
            
            if (job instanceof VehicularJob) {
                VehicularJob vj = (VehicularJob) job;
                tipoTrabajo = "Vehicular - " + vj.getModel();
            } else if (job.getJobType() != null) {
                tipoTrabajo = job.getJobType().getName();
            }
            
            return new SimpleStringProperty(tipoTrabajo);
        });

        // Configurar columna Estado
        columnaEstado.setCellValueFactory(cellData -> {
            String estado = cellData.getValue().getState();
            return new SimpleStringProperty(estado != null ? estado : "N/A");
        });

        columnaEstado.setCellFactory(col -> new TableCell<Job, String>() {
            @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);
                if (empty || estado == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(estado);
                    // Colorear según el estado
                    switch (estado.toUpperCase()) {
                        case "PENDIENTE":
                            setStyle("-fx-background-color: #fff3cd; -fx-text-fill: #856404; -fx-font-weight: bold; -fx-alignment: CENTER;");
                            break;
                        case "EN PROCESO":
                            setStyle("-fx-background-color: #cce5ff; -fx-text-fill: #004085; -fx-font-weight: bold; -fx-alignment: CENTER;");
                            break;
                        case "COMPLETADO":
                            setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-font-weight: bold; -fx-alignment: CENTER;");
                            break;
                        default:
                            setStyle("-fx-alignment: CENTER;");
                    }
                }
            }
        });

        // Configurar columna Total
        columnaTotal.setCellValueFactory(cellData -> {
            Job job = cellData.getValue();
            double total = 0.0;
            if (job.getQuotations() != null && !job.getQuotations().isEmpty()) {
                total = job.getQuotations().get(0).getTotal();
            }
            return new SimpleDoubleProperty(total).asObject();
        });
        
        columnaTotal.setCellFactory(col -> new TableCell<Job, Double>() {
            @Override
            protected void updateItem(Double total, boolean empty) {
                super.updateItem(total, empty);
                if (empty || total == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", total));
                }
            }
        });

        // Configurar columna Mano de Obra
        columnaManoObra.setCellValueFactory(cellData -> {
            Job job = cellData.getValue();
            double laborCost = 0.0;
            if (job.getQuotations() != null && !job.getQuotations().isEmpty()) {
                laborCost = job.getQuotations().get(0).getLaborCost();
            }
            return new SimpleDoubleProperty(laborCost).asObject();
        });
        
        columnaManoObra.setCellFactory(col -> new TableCell<Job, Double>() {
            @Override
            protected void updateItem(Double costo, boolean empty) {
                super.updateItem(costo, empty);
                if (empty || costo == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", costo));
                }
            }
        });

        // Configurar columna Acciones
        columnaAcciones.setCellFactory(param -> new TableCell<Job, Void>() {
            private final Button btnEliminar = new Button("Eliminar");
            private final Button btnDetalles = new Button("Detalles");

            {
                btnEliminar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 5 10; -fx-font-size: 11px;");
                btnEliminar.setOnAction(event -> {
                    Job job = getTableView().getItems().get(getIndex());
                    eliminarTrabajo(job);
                });

                btnDetalles.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 5 10; -fx-font-size: 11px;");
                btnDetalles.setOnAction(event -> {
                    Job job = getTableView().getItems().get(getIndex());
                    mostrarDetalles(job);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    javafx.scene.layout.HBox hbox = new javafx.scene.layout.HBox(5);
                    hbox.setAlignment(javafx.geometry.Pos.CENTER);
                    hbox.getChildren().addAll(btnDetalles, btnEliminar);
                    setGraphic(hbox);
                }
            }
        });

        tablaQuotations.setItems(todosLosTrabajos);
    }

    private void cargarTrabajos() {
        try {
            List<Job> trabajos = jobService.getAllJobs();
            todosLosTrabajos.clear();
            todosLosTrabajos.addAll(trabajos);
            actualizarContador();
            System.out.println("Trabajos cargados: " + trabajos.size());
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudieron cargar los trabajos: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void configurarBusqueda() {
        buscarTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                tablaQuotations.setItems(todosLosTrabajos);
            } else {
                String textoFiltro = newValue.toLowerCase();
                ObservableList<Job> trabajosFiltrados = FXCollections.observableArrayList();

                for (Job job : todosLosTrabajos) {
                    // Filtrar por ID
                    if (String.valueOf(job.getId()).contains(textoFiltro)) {
                        trabajosFiltrados.add(job);
                        continue;
                    }

                    // Filtrar por nombre de cliente
                    if (job.getClient() != null &&
                        job.getClient().getName().toLowerCase().contains(textoFiltro)) {
                        trabajosFiltrados.add(job);
                        continue;
                    }

                    // Filtrar por estado
                    if (job.getState() != null && 
                        job.getState().toLowerCase().contains(textoFiltro)) {
                        trabajosFiltrados.add(job);
                        continue;
                    }

                    // Filtrar por tipo de trabajo
                    if (job instanceof VehicularJob) {
                        VehicularJob vj = (VehicularJob) job;
                        if (vj.getModel().toLowerCase().contains(textoFiltro)) {
                            trabajosFiltrados.add(job);
                            continue;
                        }
                    } else if (job.getJobType() != null &&
                              job.getJobType().getName().toLowerCase().contains(textoFiltro)) {
                        trabajosFiltrados.add(job);
                        continue;
                    }

                    // Filtrar por total
                    if (job.getQuotations() != null && !job.getQuotations().isEmpty()) {
                        String total = String.format("%.2f", job.getQuotations().get(0).getTotal());
                        if (total.contains(textoFiltro)) {
                            trabajosFiltrados.add(job);
                        }
                    }
                }

                tablaQuotations.setItems(trabajosFiltrados);
            }
            actualizarContador();
        });
    }

    private void eliminarTrabajo(Job job) {
        // Verificar si el trabajo ya está cancelado
        if (job.getState() != null && job.getState().equalsIgnoreCase("CANCELADO")) {
            mostrarAlerta("Información", 
                "Este trabajo ya está cancelado.", 
                Alert.AlertType.INFORMATION);
            return;
        }

        // Obtener información de la cotización
        String totalStr = "N/A";
        String fechaStr = "N/A";
        if (job.getQuotations() != null && !job.getQuotations().isEmpty()) {
            Quotation quotation = job.getQuotations().get(0);
            totalStr = String.format("$%.2f", quotation.getTotal());
            fechaStr = dateFormat.format(quotation.getEmisionDate());
        }

        // Crear alerta de confirmación
        Alert confirmacion = new Alert(Alert.AlertType.WARNING);
        confirmacion.setTitle("Confirmar Cancelación");
        confirmacion.setHeaderText("¿Está seguro de cancelar este trabajo?");
        confirmacion.setContentText(
            "ID: " + job.getId() + "\n" +
            "Cliente: " + (job.getClient() != null ? job.getClient().getName() : "N/A") + "\n" +
            "Total: " + totalStr + "\n" +
            "Fecha: " + fechaStr + "\n" +
            "Estado Actual: " + job.getState() + "\n\n" +
            "El estado cambiará a CANCELADO.\n" +
            "Esta acción no se puede deshacer."
        );

        ButtonType btnSi = new ButtonType("Sí, cancelar trabajo");
        ButtonType btnNo = new ButtonType("No cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmacion.getButtonTypes().setAll(btnSi, btnNo);

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == btnSi) {
            try {
                // Cambiar el estado a CANCELADO usando el servicio
                job.setState("CANCELADO");
                job= jobService.editJob(job);
                
                // Actualizar el estado en el objeto local
                job.setState("CANCELADO");
                
                // Refrescar la tabla para mostrar el cambio
                tablaQuotations.refresh();
                
                mostrarAlerta("Éxito", 
                    "El trabajo ha sido cancelado correctamente.", 
                    Alert.AlertType.INFORMATION);
                System.out.println("Trabajo cancelado: ID " + job.getId());
            } catch (Exception e) {
                mostrarAlerta("Error", 
                    "No se pudo cancelar el trabajo: " + e.getMessage(), 
                    Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    private void mostrarDetalles(Job job) {
        StringBuilder detalles = new StringBuilder();
        detalles.append("=== TRABAJO #").append(job.getId()).append(" ===\n\n");
        
        // Información del Cliente
        if (job.getClient() != null) {
            Client cliente = job.getClient();
            detalles.append("--- CLIENTE ---\n");
            detalles.append("Nombre: ").append(cliente.getName()).append("\n");
            detalles.append("Teléfono: ").append(cliente.getPhoneNumber()).append("\n\n");
        }
        
        // Información del Trabajo
        detalles.append("--- INFORMACIÓN DEL TRABAJO ---\n");
        detalles.append("Estado: ").append(job.getState()).append("\n");
        detalles.append("Fecha de Entrega: ").append(dateFormat.format(job.getDeliveryDate())).append("\n");
        detalles.append("Descripción: ").append(job.getDescription()).append("\n");
        
        // Si es trabajo vehicular
        if (job instanceof VehicularJob) {
            VehicularJob vj = (VehicularJob) job;
            detalles.append("\n--- DATOS DEL VEHÍCULO ---\n");
            detalles.append("Modelo: ").append(vj.getModel()).append("\n");
            detalles.append("Color: ").append(vj.getColor()).append("\n");
            detalles.append("Año: ").append(vj.getYear()).append("\n");
        } else if (job.getJobType() != null) {
            detalles.append("Tipo de Trabajo: ").append(job.getJobType().getName()).append("\n");
        }
        
        // Información de la Cotización
        if (job.getQuotations() != null && !job.getQuotations().isEmpty()) {
            Quotation quotation = job.getQuotations().get(0);
            detalles.append("\n--- COTIZACIÓN ---\n");
            detalles.append("Fecha de Emisión: ").append(dateFormat.format(quotation.getEmisionDate())).append("\n");
            
            detalles.append("\n--- MATERIALES ---\n");
            if (quotation.getQuotationMaterialDetails() != null && 
                !quotation.getQuotationMaterialDetails().isEmpty()) {
                for (QuotationMaterialDetail detalle : quotation.getQuotationMaterialDetails()) {
                    detalles.append("• ").append(detalle.getMaterial().getName())
                        .append(" - Cant: ").append(detalle.getQuantity())
                        .append(" x $").append(String.format("%.2f", detalle.getUnitPrice()))
                        .append(" = $").append(String.format("%.2f", detalle.getQuantity() * detalle.getUnitPrice()))
                        .append("\n");
                }
            } else {
                detalles.append("Sin materiales registrados\n");
            }
            
            detalles.append("\n--- TOTALES ---\n");
            detalles.append("Mano de Obra: $").append(String.format("%.2f", quotation.getLaborCost())).append("\n");
            detalles.append("TOTAL: $").append(String.format("%.2f", quotation.getTotal())).append("\n");
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalles del Trabajo");
        alert.setHeaderText(null);
        alert.setContentText(detalles.toString());
        alert.getDialogPane().setPrefWidth(500);
        alert.getDialogPane().setPrefHeight(600);
        alert.showAndWait();
    }

    @FXML
    private void refrescarTabla() {
        cargarTrabajos();
        buscarTextField.clear();
        mostrarAlerta("Éxito", "Tabla actualizada correctamente.", Alert.AlertType.INFORMATION);
    }

    private void actualizarContador() {
        int total = tablaQuotations.getItems().size();
        totalRegistrosLabel.setText("Total de registros: " + total);
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}