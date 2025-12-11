package com.example.moxxdesignsfront.controllers;

import com.mycompany.moxxdesignsdbconnection.entitys.*;
import com.mycompany.moxxdesignsdbconnection.repository.JobRepositoryImpl;
import com.mycompany.moxxdesignsdbconnection.services.JobService;
import com.mycompany.moxxdesignsdbconnection.services.PdfReportService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.awt.Desktop;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class vistaReportes {

    private JobService jobService;
    private PdfReportService pdfReportService;

    @FXML private DatePicker dpInicio;
    @FXML private DatePicker dpFin;
    @FXML private Label lblGranTotal;

    @FXML private TableView<Job> tablaVentas;
    @FXML private TableColumn<Job, Integer> colId;
    @FXML private TableColumn<Job, String> colCliente;
    @FXML private TableColumn<Job, String> colTipo;
    @FXML private TableColumn<Job, String> colFecha;
    @FXML private TableColumn<Job, Double> colTotal;

    private ObservableList<Job> listaVentas = FXCollections.observableArrayList();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    @FXML
    public void initialize() {
        jobService = new JobService(new JobRepositoryImpl());
        pdfReportService = new PdfReportService();

        // Configurar fechas por defecto (Mes actual)
        dpInicio.setValue(LocalDate.now().withDayOfMonth(1));
        dpFin.setValue(LocalDate.now());

        configurarTabla();
        generarReporteEnPantalla();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(c -> new SimpleIntegerProperty((int)c.getValue().getId()).asObject());

        colCliente.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getClient() != null ? c.getValue().getClient().getName() : "N/A"
        ));

        colTipo.setCellValueFactory(c -> {
            Job j = c.getValue();
            String tipo = (j.getJobType() != null) ? j.getJobType().getName() : "Vehicular";
            if(j instanceof VehicularJob) {
                tipo += " (" + ((VehicularJob)j).getModel() + ")";
            }
            return new SimpleStringProperty(tipo);
        });

        colFecha.setCellValueFactory(c -> {
            Date d = c.getValue().getDeliveryDate();
            return new SimpleStringProperty(d != null ? sdf.format(d) : "-");
        });

        // CÁLCULO DEL TOTAL (Basado en última cotización)
        colTotal.setCellValueFactory(c -> new SimpleDoubleProperty(calcularTotalJob(c.getValue())).asObject());

        // Formato de moneda en la celda
        colTotal.setCellFactory(c -> new TableCell<Job, Double>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if(empty || item == null) setText(null);
                else {
                    setText(String.format("$%.2f", item));
                    setStyle("-fx-alignment: CENTER_RIGHT; -fx-font-weight: bold;");
                }
            }
        });

        tablaVentas.setItems(listaVentas);
    }

    @FXML
    public void generarReporteEnPantalla() {
        LocalDate inicio = dpInicio.getValue();
        LocalDate fin = dpFin.getValue();

        if (inicio == null || fin == null) {
            mostrarAlerta("Error", "Seleccione un rango de fechas válido.");
            return;
        }

        List<Job> todos = jobService.getAllJobs();

        // FILTRADO:
        // 1. Estado = COMPLETADO
        // 2. Fecha de entrega dentro del rango seleccionado
        List<Job> filtrados = todos.stream()
                .filter(j -> "COMPLETADO".equalsIgnoreCase(j.getState()))
                .filter(j -> {
                    if (j.getDeliveryDate() == null) return false;
                    LocalDate fechaEntrega = j.getDeliveryDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    return !fechaEntrega.isBefore(inicio) && !fechaEntrega.isAfter(fin);
                })
                .collect(Collectors.toList());

        listaVentas.setAll(filtrados);
        actualizarGranTotal();
    }

    private void actualizarGranTotal() {
        double total = listaVentas.stream()
                .mapToDouble(this::calcularTotalJob)
                .sum();
        lblGranTotal.setText(String.format("$%.2f", total));
    }

    private double calcularTotalJob(Job job) {
        if (job.getQuotations() == null || job.getQuotations().isEmpty()) return 0.0;

        // Obtener la cotización más reciente
        return job.getQuotations().stream()
                .max(Comparator.comparing(Quotation::getEmisionDate))
                .map(q -> (double) q.getTotal()) // Casting explicito float -> double
                .orElse(0.0);
    }

    @FXML
    public void imprimirPDF() {
        if (listaVentas.isEmpty()) {
            mostrarAlerta("Atención", "No hay datos para imprimir en el rango seleccionado.");
            return;
        }

        try {
            File pdf = pdfReportService.generarReporteVentas(listaVentas, dpInicio.getValue(), dpFin.getValue());

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Reporte Generado");
            alert.setHeaderText("Se ha creado el reporte PDF.");
            alert.setContentText("Ubicación: " + pdf.getAbsolutePath() + "\n\n¿Desea abrirlo?");

            alert.showAndWait().ifPresent(res -> {
                if (res == ButtonType.OK && Desktop.isDesktopSupported()) {
                    try { Desktop.getDesktop().open(pdf); } catch(Exception e) { e.printStackTrace(); }
                }
            });

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo generar el PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo); a.setContentText(msg); a.show();
    }
}