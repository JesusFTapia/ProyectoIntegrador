package com.example.moxxdesignsfront.controllers;

import com.mycompany.moxxdesignsdbconnection.entitys.*;
import com.mycompany.moxxdesignsdbconnection.services.JobService;
import com.mycompany.moxxdesignsdbconnection.repository.IJobRepository;
import com.mycompany.moxxdesignsdbconnection.repository.JobRepositoryImpl;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.scene.Scene;
import javafx.print.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class vistaGestionTrabajos {

    private JobService jobService;

    @FXML private TableView<Job> tablaTrabajos;
    @FXML private TableColumn<Job, Integer> columnaId;
    @FXML private TableColumn<Job, String> columnaFechaCreacion;
    @FXML private TableColumn<Job, String> columnaFechaEntrega;
    @FXML private TableColumn<Job, String> columnaCliente;
    @FXML private TableColumn<Job, String> columnaTipoTrabajo;
    @FXML private TableColumn<Job, String> columnaEstado;
    @FXML private TableColumn<Job, Integer> columnaCotizaciones;
    @FXML private TableColumn<Job, Double> columnaUltimoTotal;
    @FXML private TableColumn<Job, Void> columnaAcciones;
    @FXML private TextField buscarTextField;
    @FXML private Label totalRegistrosLabel;
    @FXML private Label filtradosLabel;

    private ObservableList<Job> todosLosTrabajos = FXCollections.observableArrayList();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        IJobRepository jobRepository = new JobRepositoryImpl();
        jobService = new JobService(jobRepository);

        configurarTabla();
        cargarTrabajos();
        configurarBusqueda();
    }

    private void configurarTabla() {
        // Columna ID
        columnaId.setCellValueFactory(cellData ->
                new SimpleIntegerProperty((int)cellData.getValue().getId()).asObject());

        // Columna Fecha de Creación (primera cotización)
        columnaFechaCreacion.setCellValueFactory(cellData -> {
            Job job = cellData.getValue();
            String fechaStr = "N/A";
            if (job.getQuotations() != null && !job.getQuotations().isEmpty()) {
                // Ordenar por fecha y tomar la primera
                List<Quotation> sortedQuotations = job.getQuotations().stream()
                        .sorted(Comparator.comparing(Quotation::getEmisionDate))
                        .collect(Collectors.toList());
                Date fecha = sortedQuotations.get(0).getEmisionDate();
                fechaStr = fecha != null ? dateFormat.format(fecha) : "N/A";
            }
            return new SimpleStringProperty(fechaStr);
        });

        // Columna Fecha de Entrega
        columnaFechaEntrega.setCellValueFactory(cellData -> {
            Job job = cellData.getValue();
            String fechaStr = job.getDeliveryDate() != null ?
                    dateFormat.format(job.getDeliveryDate()) : "N/A";
            return new SimpleStringProperty(fechaStr);
        });

        // Columna Cliente
        columnaCliente.setCellValueFactory(cellData -> {
            Job job = cellData.getValue();
            String clienteNombre = job.getClient() != null ?
                    job.getClient().getName() : "Sin cliente";
            return new SimpleStringProperty(clienteNombre);
        });

        // Columna Tipo de Trabajo
        columnaTipoTrabajo.setCellValueFactory(cellData -> {
            Job job = cellData.getValue();
            String tipoTrabajo = "General";

            if (job instanceof VehicularJob) {
                VehicularJob vj = (VehicularJob) job;
                tipoTrabajo = "Vehicular: " + vj.getModel() + " (" + vj.getYear() + ")";
            } else if (job.getJobType() != null) {
                tipoTrabajo = job.getJobType().getName();
            }

            return new SimpleStringProperty(tipoTrabajo);
        });

        // Columna Estado con colores
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
                    switch (estado.toUpperCase()) {
                        case "PENDIENTE":
                            setStyle("-fx-background-color: #fff3cd; -fx-text-fill: #856404; -fx-font-weight: bold; -fx-alignment: CENTER; -fx-background-radius: 5;");
                            break;
                        case "EN PROCESO":
                            setStyle("-fx-background-color: #cce5ff; -fx-text-fill: #004085; -fx-font-weight: bold; -fx-alignment: CENTER; -fx-background-radius: 5;");
                            break;
                        case "COMPLETADO":
                            setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-font-weight: bold; -fx-alignment: CENTER; -fx-background-radius: 5;");
                            break;
                        case "CANCELADO":
                            setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #721c24; -fx-font-weight: bold; -fx-alignment: CENTER; -fx-background-radius: 5;");
                            break;
                        default:
                            setStyle("-fx-alignment: CENTER;");
                    }
                }
            }
        });

        // Columna Número de Cotizaciones
        columnaCotizaciones.setCellValueFactory(cellData -> {
            Job job = cellData.getValue();
            int numCotizaciones = job.getQuotations() != null ?
                    job.getQuotations().size() : 0;
            return new SimpleIntegerProperty(numCotizaciones).asObject();
        });

        columnaCotizaciones.setCellFactory(col -> new TableCell<Job, Integer>() {
            @Override
            protected void updateItem(Integer num, boolean empty) {
                super.updateItem(num, empty);
                if (empty || num == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.valueOf(num));
                    if (num > 1) {
                        setStyle("-fx-background-color: #e3f2fd; -fx-text-fill: #1976d2; -fx-font-weight: bold; -fx-alignment: CENTER;");
                    } else {
                        setStyle("-fx-alignment: CENTER;");
                    }
                }
            }
        });

        // Columna Último Total
        columnaUltimoTotal.setCellValueFactory(cellData -> {
            Job job = cellData.getValue();
            double total = 0.0;
            if (job.getQuotations() != null && !job.getQuotations().isEmpty()) {
                // Obtener la cotización más reciente
                Quotation ultimaCotizacion = job.getQuotations().stream()
                        .max(Comparator.comparing(Quotation::getEmisionDate))
                        .orElse(job.getQuotations().get(0));
                total = ultimaCotizacion.getTotal();
            }
            return new SimpleDoubleProperty(total).asObject();
        });

        columnaUltimoTotal.setCellFactory(col -> new TableCell<Job, Double>() {
            @Override
            protected void updateItem(Double total, boolean empty) {
                super.updateItem(total, empty);
                if (empty || total == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", total));
                    setStyle("-fx-font-weight: bold; -fx-alignment: CENTER_RIGHT;");
                }
            }
        });

        // Columna Acciones con 4 botones
        columnaAcciones.setCellFactory(param -> new TableCell<Job, Void>() {
            private final Button btnDetalles = crearBoton("📋", "#3498db");
            private final Button btnCotizaciones = crearBoton("📊", "#9b59b6");
            private final Button btnEditar = crearBoton("✏️", "#f39c12");
            private final Button btnCancelar = crearBoton("❌", "#e74c3c");

            {
                btnDetalles.setOnAction(event -> {
                    Job job = getTableView().getItems().get(getIndex());
                    mostrarDetallesCompletos(job);
                });

                btnCotizaciones.setOnAction(event -> {
                    Job job = getTableView().getItems().get(getIndex());
                    mostrarHistorialCotizaciones(job);
                });

                btnEditar.setOnAction(event -> {
                    Job job = getTableView().getItems().get(getIndex());
                    editarTrabajo(job);
                });

                btnCancelar.setOnAction(event -> {
                    Job job = getTableView().getItems().get(getIndex());
                    cancelarTrabajo(job);
                });
            }

            private Button crearBoton(String texto, String color) {
                Button btn = new Button(texto);
                btn.setStyle(String.format(
                        "-fx-background-color: %s; -fx-text-fill: white; " +
                                "-fx-padding: 5 10; -fx-font-size: 11px; -fx-cursor: hand; " +
                                "-fx-background-radius: 5;", color));
                btn.setMinWidth(50);
                return btn;
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(5);
                    hbox.setAlignment(Pos.CENTER);
                    hbox.getChildren().addAll(btnDetalles, btnCotizaciones,
                            btnEditar, btnCancelar);
                    setGraphic(hbox);
                }
            }
        });

        tablaTrabajos.setItems(todosLosTrabajos);
    }

    private void cargarTrabajos() {
        try {
            List<Job> trabajos = jobService.getAllJobs();

            // Ordenar del más reciente al más antiguo por fecha de creación
            trabajos.sort((j1, j2) -> {
                Date fecha1 = obtenerFechaCreacion(j1);
                Date fecha2 = obtenerFechaCreacion(j2);
                return fecha2.compareTo(fecha1); // Orden descendente
            });

            todosLosTrabajos.clear();
            todosLosTrabajos.addAll(trabajos);
            actualizarContadores();
            System.out.println("Trabajos cargados: " + trabajos.size());
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudieron cargar los trabajos: " +
                    e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private Date obtenerFechaCreacion(Job job) {
        if (job.getQuotations() != null && !job.getQuotations().isEmpty()) {
            return job.getQuotations().stream()
                    .min(Comparator.comparing(Quotation::getEmisionDate))
                    .map(Quotation::getEmisionDate)
                    .orElse(new Date(0));
        }
        return new Date(0);
    }

    private void configurarBusqueda() {
        buscarTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                tablaTrabajos.setItems(todosLosTrabajos);
            } else {
                String textoFiltro = newValue.toLowerCase();
                ObservableList<Job> trabajosFiltrados = todosLosTrabajos.stream()
                        .filter(job -> cumpleFiltro(job, textoFiltro))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));

                tablaTrabajos.setItems(trabajosFiltrados);
            }
            actualizarContadores();
        });
    }

    private boolean cumpleFiltro(Job job, String filtro) {
        // Filtrar por ID
        if (String.valueOf(job.getId()).contains(filtro)) return true;

        // Filtrar por cliente
        if (job.getClient() != null &&
                job.getClient().getName().toLowerCase().contains(filtro)) return true;

        // Filtrar por estado
        if (job.getState() != null &&
                job.getState().toLowerCase().contains(filtro)) return true;

        // Filtrar por tipo de trabajo
        if (job instanceof VehicularJob) {
            VehicularJob vj = (VehicularJob) job;
            if (vj.getModel().toLowerCase().contains(filtro) ||
                    vj.getColor().toLowerCase().contains(filtro) ||
                    String.valueOf(vj.getYear()).contains(filtro)) return true;
        } else if (job.getJobType() != null &&
                job.getJobType().getName().toLowerCase().contains(filtro)) {
            return true;
        }

        // Filtrar por descripción
        if (job.getDescription() != null &&
                job.getDescription().toLowerCase().contains(filtro)) return true;

        return false;
    }

    private void mostrarDetallesCompletos(Job job) {
        Stage ventana = new Stage();
        ventana.initModality(Modality.APPLICATION_MODAL);
        ventana.setTitle("Detalles del Trabajo #" + job.getId());

        VBox contenido = new VBox(15);
        contenido.setStyle("-fx-padding: 20; -fx-background-color: white;");

        // Crear contenido de detalles
        Label titulo = new Label("TRABAJO #" + job.getId());
        titulo.setStyle("-fx-font-size: 18pt; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        TextArea detallesArea = new TextArea(construirTextoDetalles(job));
        detallesArea.setEditable(false);
        detallesArea.setWrapText(true);
        detallesArea.setPrefHeight(400);
        detallesArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11pt;");

        Button btnCerrar = new Button("Cerrar");
        btnCerrar.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; " +
                "-fx-padding: 10 30; -fx-font-size: 12pt; -fx-cursor: hand;");
        btnCerrar.setOnAction(e -> ventana.close());

        HBox botones = new HBox(btnCerrar);
        botones.setAlignment(Pos.CENTER);

        contenido.getChildren().addAll(titulo, detallesArea, botones);

        Scene escena = new Scene(contenido, 600, 550);
        ventana.setScene(escena);
        ventana.showAndWait();
    }

    private String construirTextoDetalles(Job job) {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════\n");
        sb.append("  INFORMACIÓN DEL TRABAJO\n");
        sb.append("═══════════════════════════════════════════════\n\n");

        // Cliente
        if (job.getClient() != null) {
            sb.append("▸ CLIENTE\n");
            sb.append("  Nombre: ").append(job.getClient().getName()).append("\n");
            sb.append("  Teléfono: ").append(job.getClient().getPhoneNumber()).append("\n\n");
        }

        // Información general
        sb.append("▸ DETALLES GENERALES\n");
        sb.append("  Estado: ").append(job.getState()).append("\n");
        sb.append("  Fecha de Entrega: ").append(dateFormat.format(job.getDeliveryDate())).append("\n");
        sb.append("  Descripción: ").append(job.getDescription()).append("\n\n");

        // Información específica
        if (job instanceof VehicularJob) {
            VehicularJob vj = (VehicularJob) job;
            sb.append("▸ INFORMACIÓN VEHICULAR\n");
            sb.append("  Modelo: ").append(vj.getModel()).append("\n");
            sb.append("  Color: ").append(vj.getColor()).append("\n");
            sb.append("  Año: ").append(vj.getYear()).append("\n\n");
        } else if (job.getJobType() != null) {
            sb.append("▸ TIPO DE TRABAJO\n");
            sb.append("  ").append(job.getJobType().getName()).append("\n\n");
        }

        // Cotizaciones
        if (job.getQuotations() != null && !job.getQuotations().isEmpty()) {
            sb.append("▸ COTIZACIONES (").append(job.getQuotations().size()).append(" registradas)\n");
            Quotation ultima = job.getQuotations().stream()
                    .max(Comparator.comparing(Quotation::getEmisionDate))
                    .orElse(job.getQuotations().get(0));
            sb.append("  Última cotización: ").append(dateFormat.format(ultima.getEmisionDate())).append("\n");
            sb.append("  Total: $").append(String.format("%.2f", ultima.getTotal())).append("\n");
        }

        return sb.toString();
    }

    private void mostrarHistorialCotizaciones(Job job) {
        if (job.getQuotations() == null || job.getQuotations().isEmpty()) {
            mostrarAlerta("Sin Cotizaciones",
                    "Este trabajo no tiene cotizaciones registradas.",
                    Alert.AlertType.INFORMATION);
            return;
        }

        Stage ventana = new Stage();
        ventana.initModality(Modality.APPLICATION_MODAL);
        ventana.setTitle("Historial de Cotizaciones - Trabajo #" + job.getId());

        VBox contenido = new VBox(15);
        contenido.setStyle("-fx-padding: 20; -fx-background-color: #f8f9fa;");

        Label titulo = new Label("Historial de Cotizaciones");
        titulo.setStyle("-fx-font-size: 18pt; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Ordenar cotizaciones de más reciente a más antigua
        List<Quotation> cotizacionesOrdenadas = job.getQuotations().stream()
                .sorted(Comparator.comparing(Quotation::getEmisionDate).reversed())
                .collect(Collectors.toList());

        ListView<Quotation> listaCotizaciones = new ListView<>();
        listaCotizaciones.setPrefHeight(300);
        listaCotizaciones.getItems().addAll(cotizacionesOrdenadas);

        listaCotizaciones.setCellFactory(param -> new ListCell<Quotation>() {
            @Override
            protected void updateItem(Quotation cotizacion, boolean empty) {
                super.updateItem(cotizacion, empty);
                if (empty || cotizacion == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    boolean esMasReciente = cotizacion == cotizacionesOrdenadas.get(0);

                    VBox box = new VBox(5);
                    box.setStyle("-fx-padding: 10; -fx-background-color: white; " +
                            "-fx-border-color: " + (esMasReciente ? "#27ae60" : "#bdc3c7") + "; " +
                            "-fx-border-width: 2; -fx-border-radius: 5; " +
                            "-fx-background-radius: 5;");

                    if (esMasReciente) {
                        Label etiqueta = new Label("✓ MÁS RECIENTE");
                        etiqueta.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 10pt;");
                        box.getChildren().add(etiqueta);
                    }

                    Label fecha = new Label("📅 Fecha: " + dateFormat.format(cotizacion.getEmisionDate()));
                    fecha.setStyle("-fx-font-weight: bold; -fx-font-size: 11pt;");

                    Label manoObra = new Label("Mano de Obra: $" +
                            String.format("%.2f", cotizacion.getLaborCost()));

                    Label total = new Label("TOTAL: $" +
                            String.format("%.2f", cotizacion.getTotal()));
                    total.setStyle("-fx-font-weight: bold; -fx-font-size: 13pt; -fx-text-fill: #2c3e50;");

                    int numMateriales = cotizacion.getQuotationMaterialDetails() != null ?
                            cotizacion.getQuotationMaterialDetails().size() : 0;
                    Label materiales = new Label("Materiales: " + numMateriales + " items");
                    materiales.setStyle("-fx-text-fill: #7f8c8d;");

                    box.getChildren().addAll(fecha, materiales, manoObra, total);
                    setGraphic(box);
                }
            }
        });

        // Botones de acción
        HBox botones = new HBox(15);
        botones.setAlignment(Pos.CENTER);

        Button btnVerDetalle = new Button("📄 Ver Detalle");
        btnVerDetalle.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                "-fx-padding: 10 20; -fx-font-size: 11pt; -fx-cursor: hand;");
        btnVerDetalle.setOnAction(e -> {
            Quotation seleccionada = listaCotizaciones.getSelectionModel().getSelectedItem();
            if (seleccionada != null) {
                mostrarDetalleCotizacion(job, seleccionada);
            } else {
                mostrarAlerta("Selección requerida",
                        "Por favor seleccione una cotización",
                        Alert.AlertType.WARNING);
            }
        });

        Button btnImprimir = new Button("🖨️ Imprimir");
        btnImprimir.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; " +
                "-fx-padding: 10 20; -fx-font-size: 11pt; -fx-cursor: hand;");
        btnImprimir.setOnAction(e -> {
            Quotation seleccionada = listaCotizaciones.getSelectionModel().getSelectedItem();
            if (seleccionada != null) {
                imprimirCotizacion(job, seleccionada);
            } else {
                mostrarAlerta("Selección requerida",
                        "Por favor seleccione una cotización para imprimir",
                        Alert.AlertType.WARNING);
            }
        });

        Button btnCerrar = new Button("Cerrar");
        btnCerrar.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; " +
                "-fx-padding: 10 20; -fx-font-size: 11pt; -fx-cursor: hand;");
        btnCerrar.setOnAction(e -> ventana.close());

        botones.getChildren().addAll(btnVerDetalle, btnImprimir, btnCerrar);

        Label info = new Label("Seleccione una cotización y haga clic en 'Ver Detalle' o 'Imprimir'");
        info.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");

        contenido.getChildren().addAll(titulo, listaCotizaciones, info, botones);

        Scene escena = new Scene(contenido, 700, 500);
        ventana.setScene(escena);
        ventana.showAndWait();
    }

    private void mostrarDetalleCotizacion(Job job, Quotation cotizacion) {
        Stage ventana = new Stage();
        ventana.initModality(Modality.APPLICATION_MODAL);
        ventana.setTitle("Detalle de Cotización");

        VBox contenido = new VBox(15);
        contenido.setStyle("-fx-padding: 20; -fx-background-color: white;");

        Label titulo = new Label("COTIZACIÓN - " + dateFormat.format(cotizacion.getEmisionDate()));
        titulo.setStyle("-fx-font-size: 16pt; -fx-font-weight: bold;");

        TextArea detalle = new TextArea(construirTextoCotizacion(job, cotizacion));
        detalle.setEditable(false);
        detalle.setWrapText(true);
        detalle.setPrefHeight(400);
        detalle.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10pt;");

        Button btnCerrar = new Button("Cerrar");
        btnCerrar.setOnAction(e -> ventana.close());

        HBox botones = new HBox(btnCerrar);
        botones.setAlignment(Pos.CENTER);

        contenido.getChildren().addAll(titulo, detalle, botones);

        Scene escena = new Scene(contenido, 600, 550);
        ventana.setScene(escena);
        ventana.showAndWait();
    }

    private String construirTextoCotizacion(Job job, Quotation cotizacion) {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════\n");
        sb.append("              COTIZACIÓN DETALLADA\n");
        sb.append("═══════════════════════════════════════════════\n\n");

        sb.append("Trabajo #").append(job.getId()).append("\n");
        sb.append("Fecha de Emisión: ").append(dateFormat.format(cotizacion.getEmisionDate())).append("\n");
        if (job.getClient() != null) {
            sb.append("Cliente: ").append(job.getClient().getName()).append("\n");
        }
        sb.append("\n───────────────────────────────────────────────\n");
        sb.append("  MATERIALES\n");
        sb.append("───────────────────────────────────────────────\n\n");

        if (cotizacion.getQuotationMaterialDetails() != null &&
                !cotizacion.getQuotationMaterialDetails().isEmpty()) {

            double subtotalMateriales = 0;
            for (QuotationMaterialDetail detalle : cotizacion.getQuotationMaterialDetails()) {
                double subtotal = detalle.getQuantity() * detalle.getUnitPrice();
                subtotalMateriales += subtotal;

                sb.append(String.format("%-30s", detalle.getMaterial().getName()));
                sb.append(String.format(" %3d x $%8.2f = $%10.2f\n",
                        detalle.getQuantity(),
                        detalle.getUnitPrice(),
                        subtotal));
            }

            sb.append("\n");
            sb.append(String.format("%-44s $%10.2f\n", "Subtotal Materiales:", subtotalMateriales));
        } else {
            sb.append("  Sin materiales registrados\n\n");
        }

        sb.append("\n───────────────────────────────────────────────\n");
        sb.append(String.format("%-44s $%10.2f\n", "Mano de Obra:", cotizacion.getLaborCost()));
        sb.append("───────────────────────────────────────────────\n");
        sb.append(String.format("%-44s $%10.2f\n", "TOTAL:", cotizacion.getTotal()));
        sb.append("═══════════════════════════════════════════════\n");

        return sb.toString();
    }

    private void imprimirCotizacion(Job job, Quotation cotizacion) {
        // Crear un TextArea con el contenido a imprimir
        TextArea areaImpresion = new TextArea(construirTextoCotizacion(job, cotizacion));
        areaImpresion.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10pt;");
        areaImpresion.setPrefSize(600, 700);

        // Configurar la impresión
        PrinterJob trabajoImpresion = PrinterJob.createPrinterJob();

        if (trabajoImpresion != null) {
            boolean continuar = trabajoImpresion.showPrintDialog(tablaTrabajos.getScene().getWindow());

            if (continuar) {
                boolean exito = trabajoImpresion.printPage(areaImpresion);

                if (exito) {
                    trabajoImpresion.endJob();
                    mostrarAlerta("Éxito",
                            "La cotización se envió a imprimir correctamente.",
                            Alert.AlertType.INFORMATION);
                } else {
                    mostrarAlerta("Error",
                            "No se pudo imprimir la cotización.",
                            Alert.AlertType.ERROR);
                }
            }
        } else {
            mostrarAlerta("Error",
                    "No se pudo inicializar el servicio de impresión.",
                    Alert.AlertType.ERROR);
        }
    }

    private void editarTrabajo(Job job) {
        Stage ventana = new Stage();
        ventana.initModality(Modality.APPLICATION_MODAL);
        ventana.setTitle("Editar Trabajo #" + job.getId());

        VBox contenido = new VBox(15);
        contenido.setStyle("-fx-padding: 20; -fx-background-color: #f8f9fa;");

        Label titulo = new Label("Editar Trabajo #" + job.getId());
        titulo.setStyle("-fx-font-size: 18pt; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Formulario de edición
        GridPane formulario = new GridPane();
        formulario.setHgap(15);
        formulario.setVgap(12);
        formulario.setStyle("-fx-padding: 15; -fx-background-color: white; " +
                "-fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-border-radius: 5;");

        // Campo Estado
        Label lblEstado = new Label("Estado:");
        lblEstado.setStyle("-fx-font-weight: bold;");
        ComboBox<String> cmbEstado = new ComboBox<>();
        cmbEstado.getItems().addAll("PENDIENTE", "EN PROCESO", "COMPLETADO", "CANCELADO");
        cmbEstado.setValue(job.getState());
        cmbEstado.setPrefWidth(250);

        // Campo Descripción
        Label lblDescripcion = new Label("Descripción:");
        lblDescripcion.setStyle("-fx-font-weight: bold;");
        TextArea txtDescripcion = new TextArea(job.getDescription());
        txtDescripcion.setPrefRowCount(3);
        txtDescripcion.setWrapText(true);
        txtDescripcion.setPrefWidth(250);

        // Campo Fecha de Entrega
        Label lblFechaEntrega = new Label("Fecha de Entrega:");
        lblFechaEntrega.setStyle("-fx-font-weight: bold;");
        DatePicker dpFechaEntrega = new DatePicker();
        if (job.getDeliveryDate() != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(job.getDeliveryDate());
            dpFechaEntrega.setValue(java.time.LocalDate.of(
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.DAY_OF_MONTH)
            ));
        }
        dpFechaEntrega.setPrefWidth(250);

        // Campos específicos para trabajos vehiculares
        Label lblModelo = null, lblColor = null, lblAnio = null;
        TextField txtModelo = null, txtColor = null, txtAnio = null;

        if (job instanceof VehicularJob) {
            VehicularJob vj = (VehicularJob) job;

            lblModelo = new Label("Modelo:");
            lblModelo.setStyle("-fx-font-weight: bold;");
            txtModelo = new TextField(vj.getModel());
            txtModelo.setPrefWidth(250);

            lblColor = new Label("Color:");
            lblColor.setStyle("-fx-font-weight: bold;");
            txtColor = new TextField(vj.getColor());
            txtColor.setPrefWidth(250);

            lblAnio = new Label("Año:");
            lblAnio.setStyle("-fx-font-weight: bold;");
            txtAnio = new TextField(String.valueOf(vj.getYear()));
            txtAnio.setPrefWidth(250);
        }

        // Agregar campos al formulario
        int row = 0;
        formulario.add(lblEstado, 0, row);
        formulario.add(cmbEstado, 1, row++);

        formulario.add(lblDescripcion, 0, row);
        formulario.add(txtDescripcion, 1, row++);

        formulario.add(lblFechaEntrega, 0, row);
        formulario.add(dpFechaEntrega, 1, row++);

        // Referencias finales para usar en lambda
        final TextField finalTxtModelo = txtModelo;
        final TextField finalTxtColor = txtColor;
        final TextField finalTxtAnio = txtAnio;

        if (job instanceof VehicularJob) {
            formulario.add(lblModelo, 0, row);
            formulario.add(txtModelo, 1, row++);

            formulario.add(lblColor, 0, row);
            formulario.add(txtColor, 1, row++);

            formulario.add(lblAnio, 0, row);
            formulario.add(txtAnio, 1, row++);
        }

        // Botones de acción
        HBox botones = new HBox(15);
        botones.setAlignment(Pos.CENTER);

        Button btnGuardar = new Button("💾 Guardar Cambios");
        btnGuardar.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; " +
                "-fx-padding: 10 20; -fx-font-size: 12pt; -fx-cursor: hand;");
        btnGuardar.setOnAction(e -> {
            try {
                // Validar datos
                if (cmbEstado.getValue() == null || cmbEstado.getValue().isEmpty()) {
                    mostrarAlerta("Error", "El estado es requerido", Alert.AlertType.ERROR);
                    return;
                }

                if (txtDescripcion.getText() == null || txtDescripcion.getText().trim().isEmpty()) {
                    mostrarAlerta("Error", "La descripción es requerida", Alert.AlertType.ERROR);
                    return;
                }

                if (dpFechaEntrega.getValue() == null) {
                    mostrarAlerta("Error", "La fecha de entrega es requerida", Alert.AlertType.ERROR);
                    return;
                }

                // Actualizar datos del trabajo
                job.setState(cmbEstado.getValue());
                job.setDescription(txtDescripcion.getText().trim());

                // Convertir LocalDate a Date
                Calendar cal = Calendar.getInstance();
                cal.set(dpFechaEntrega.getValue().getYear(),
                        dpFechaEntrega.getValue().getMonthValue() - 1,
                        dpFechaEntrega.getValue().getDayOfMonth());
                job.setDeliveryDate(cal.getTime());

                // Actualizar campos específicos si es vehicular
                if (job instanceof VehicularJob) {
                    VehicularJob vj = (VehicularJob) job;

                    if (finalTxtModelo.getText() == null || finalTxtModelo.getText().trim().isEmpty()) {
                        mostrarAlerta("Error", "El modelo es requerido", Alert.AlertType.ERROR);
                        return;
                    }
                    if (finalTxtColor.getText() == null || finalTxtColor.getText().trim().isEmpty()) {
                        mostrarAlerta("Error", "El color es requerido", Alert.AlertType.ERROR);
                        return;
                    }

                    try {
                        int anio = Integer.parseInt(finalTxtAnio.getText().trim());
                        if (anio < 1900 || anio > Calendar.getInstance().get(Calendar.YEAR) + 1) {
                            mostrarAlerta("Error", "El año debe estar entre 1900 y " +
                                    (Calendar.getInstance().get(Calendar.YEAR) + 1), Alert.AlertType.ERROR);
                            return;
                        }

                        vj.setModel(finalTxtModelo.getText().trim());
                        vj.setColor(finalTxtColor.getText().trim());
                        vj.setYear(anio);
                    } catch (NumberFormatException ex) {
                        mostrarAlerta("Error", "El año debe ser un número válido", Alert.AlertType.ERROR);
                        return;
                    }
                }

                // Guardar cambios usando el servicio
                jobService.editJob(job);

                // Actualizar la tabla
                tablaTrabajos.refresh();

                mostrarAlerta("Éxito",
                        "El trabajo se ha actualizado correctamente.",
                        Alert.AlertType.INFORMATION);

                ventana.close();

            } catch (Exception ex) {
                mostrarAlerta("Error",
                        "No se pudo actualizar el trabajo: " + ex.getMessage(),
                        Alert.AlertType.ERROR);
                ex.printStackTrace();
            }
        });

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; " +
                "-fx-padding: 10 20; -fx-font-size: 12pt; -fx-cursor: hand;");
        btnCancelar.setOnAction(e -> ventana.close());

        botones.getChildren().addAll(btnGuardar, btnCancelar);

        contenido.getChildren().addAll(titulo, formulario, botones);

        Scene escena = new Scene(contenido, 500, job instanceof VehicularJob ? 550 : 400);
        ventana.setScene(escena);
        ventana.showAndWait();
    }

    private void cancelarTrabajo(Job job) {
        // Verificar si el trabajo ya está cancelado
        if (job.getState() != null && job.getState().equalsIgnoreCase("CANCELADO")) {
            mostrarAlerta("Información",
                    "Este trabajo ya está cancelado.",
                    Alert.AlertType.INFORMATION);
            return;
        }

        // Crear alerta de confirmación
        Alert confirmacion = new Alert(Alert.AlertType.WARNING);
        confirmacion.setTitle("Confirmar Cancelación");
        confirmacion.setHeaderText("¿Está seguro de cancelar este trabajo?");

        StringBuilder contenido = new StringBuilder();
        contenido.append("ID: ").append(job.getId()).append("\n");
        contenido.append("Cliente: ").append(job.getClient() != null ?
                job.getClient().getName() : "N/A").append("\n");

        if (job.getQuotations() != null && !job.getQuotations().isEmpty()) {
            Quotation ultima = job.getQuotations().stream()
                    .max(Comparator.comparing(Quotation::getEmisionDate))
                    .orElse(job.getQuotations().get(0));
            contenido.append("Último Total: $").append(String.format("%.2f", ultima.getTotal())).append("\n");
        }

        contenido.append("Estado Actual: ").append(job.getState()).append("\n\n");
        contenido.append("⚠️ El estado cambiará a CANCELADO.\n");
        contenido.append("Esta acción NO se puede deshacer.");

        confirmacion.setContentText(contenido.toString());

        ButtonType btnSi = new ButtonType("Sí, cancelar trabajo", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnNo = new ButtonType("No cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmacion.getButtonTypes().setAll(btnSi, btnNo);

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == btnSi) {
            try {
                // Cambiar el estado a CANCELADO
                job.setState("CANCELADO");
                jobService.editJob(job);

                // Refrescar la tabla
                tablaTrabajos.refresh();

                mostrarAlerta("Éxito",
                        "El trabajo #" + job.getId() + " ha sido cancelado correctamente.",
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

    @FXML
    private void refrescarTabla() {
        cargarTrabajos();
        buscarTextField.clear();
        mostrarAlerta("Éxito", "Tabla actualizada correctamente.", Alert.AlertType.INFORMATION);
    }

    private void actualizarContadores() {
        int total = todosLosTrabajos.size();
        int mostrados = tablaTrabajos.getItems().size();

        totalRegistrosLabel.setText("Total: " + total);
        filtradosLabel.setText("Mostrando: " + mostrados);
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}