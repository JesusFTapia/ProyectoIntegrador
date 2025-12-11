package com.example.moxxdesignsfront.controllers;

import com.mycompany.moxxdesignsdbconnection.entitys.*;
import com.mycompany.moxxdesignsdbconnection.services.PdfService;
import com.mycompany.moxxdesignsdbconnection.services.QuotationService;
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
import javafx.util.StringConverter;

import java.awt.Desktop;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class vistaGestionCotizaciones {

    private QuotationService quotationService = new QuotationService();
    private PdfService pdfService = new PdfService();

    @FXML private TableView<Quotation> tablaCotizaciones;
    @FXML private TableColumn<Quotation, Long> colId;
    @FXML private TableColumn<Quotation, String> colJobRef;
    @FXML private TableColumn<Quotation, String> colFecha;
    @FXML private TableColumn<Quotation, Double> colTotal;
    @FXML private TableColumn<Quotation, String> colEstadoVersion;
    @FXML private TableColumn<Quotation, Void> colAcciones;

    @FXML private CheckBox checkSoloActivas;
    @FXML private TextField buscarTextField; // <--- Nuevo campo de búsqueda

    private ObservableList<Quotation> todasLasCotizaciones = FXCollections.observableArrayList();
    private ObservableList<Quotation> cotizacionesFiltradas = FXCollections.observableArrayList();
    private Map<Long, Long> mapaUltimaCotizacionPorJob = new HashMap<>();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        configurarTabla();
        cargarDatos();

        // Listeners para filtros
        checkSoloActivas.selectedProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        buscarTextField.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
    }

    @FXML
    public void cargarDatos() {
        List<Quotation> listaBD = quotationService.getAllQuotations();

        // Lógica para determinar cuál es la activa (la más reciente por Job)
        Map<Long, Optional<Quotation>> maxPorJob = listaBD.stream()
                .filter(q -> q.getJob() != null)
                .collect(Collectors.groupingBy(
                        q -> q.getJob().getId(),
                        Collectors.maxBy(Comparator.comparing(Quotation::getEmisionDate))
                ));

        mapaUltimaCotizacionPorJob.clear();
        maxPorJob.forEach((jobId, optQuot) -> {
            optQuot.ifPresent(q -> mapaUltimaCotizacionPorJob.put(jobId, q.getId()));
        });

        todasLasCotizaciones.setAll(listaBD);
        todasLasCotizaciones.sort((q1, q2) -> Long.compare(q2.getId(), q1.getId()));

        aplicarFiltros();
    }

    private void aplicarFiltros() {
        String textoBusqueda = buscarTextField.getText() == null ? "" : buscarTextField.getText().toLowerCase();
        boolean soloActivas = checkSoloActivas.isSelected();

        List<Quotation> resultado = todasLasCotizaciones.stream()
                .filter(q -> {
                    // Filtro 1: Activa vs Historial
                    if (soloActivas && !esCotizacionActiva(q)) return false;

                    // Filtro 2: Búsqueda por Texto (ID, Cliente, Job ID)
                    if (!textoBusqueda.isEmpty()) {
                        String cliente = (q.getJob() != null && q.getJob().getClient() != null)
                                ? q.getJob().getClient().getName().toLowerCase() : "";
                        String idStr = String.valueOf(q.getId());
                        String jobIdStr = (q.getJob() != null) ? String.valueOf(q.getJob().getId()) : "";

                        return cliente.contains(textoBusqueda) || idStr.contains(textoBusqueda) || jobIdStr.contains(textoBusqueda);
                    }
                    return true;
                })
                .collect(Collectors.toList());

        cotizacionesFiltradas.setAll(resultado);
        tablaCotizaciones.setItems(cotizacionesFiltradas);
        tablaCotizaciones.refresh();
    }

    private boolean esCotizacionActiva(Quotation q) {
        if (q.getJob() == null) return false;
        Long idUltima = mapaUltimaCotizacionPorJob.get(q.getJob().getId());
        return idUltima != null && idUltima.equals(q.getId());
    }

    private void configurarTabla() {
        colId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));

        colJobRef.setCellValueFactory(c -> {
            Job j = c.getValue().getJob();
            String info = (j != null) ? "Job #" + j.getId() : "Sin Job";
            if (j != null && j.getClient() != null) {
                info += " - " + j.getClient().getName();
            }
            return new SimpleStringProperty(info);
        });

        colFecha.setCellValueFactory(c -> new SimpleStringProperty(sdf.format(c.getValue().getEmisionDate())));

        colTotal.setCellValueFactory(c -> new SimpleObjectProperty<>((double)c.getValue().getTotal()));
        colTotal.setCellFactory(c -> new TableCell<Quotation, Double>(){
            @Override protected void updateItem(Double item, boolean empty){
                super.updateItem(item, empty);
                if(!empty && item != null) {
                    setText(String.format("$%.2f", item));
                    setStyle("-fx-alignment: CENTER_RIGHT; -fx-font-weight: bold;");
                } else setText(null);
            }
        });

        colEstadoVersion.setCellValueFactory(c -> new SimpleStringProperty(
                esCotizacionActiva(c.getValue()) ? "ACTIVA" : "HISTORIAL"
        ));

        colEstadoVersion.setCellFactory(c -> new TableCell<Quotation, String>(){
            @Override protected void updateItem(String item, boolean empty){
                super.updateItem(item, empty);
                if(empty || item == null) {
                    setText(null); setStyle("");
                } else {
                    setText(item);
                    if(item.equals("ACTIVA")) {
                        setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-alignment: CENTER; -fx-background-radius: 5;");
                    } else {
                        setStyle("-fx-background-color: #ecf0f1; -fx-text-fill: #7f8c8d; -fx-alignment: CENTER; -fx-background-radius: 5;");
                    }
                }
            }
        });

        colAcciones.setCellFactory(param -> new TableCell<Quotation, Void>() {
            private final Button btnNuevaVersion = new Button("📝 Editar / Versión");
            private final Button btnPDF = new Button("🖨 PDF");

            {
                btnNuevaVersion.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 11px; -fx-cursor: hand;");
                btnPDF.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 11px; -fx-cursor: hand;");

                btnNuevaVersion.setOnAction(e -> dialogoNuevaVersionAvanzada(getTableView().getItems().get(getIndex())));
                btnPDF.setOnAction(e -> generarPDF(getTableView().getItems().get(getIndex())));
            }

            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else {
                    boolean esActiva = esCotizacionActiva(getTableView().getItems().get(getIndex()));
                    HBox box = new HBox(5, btnPDF);
                    if (esActiva) box.getChildren().add(btnNuevaVersion);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });

        tablaCotizaciones.setItems(cotizacionesFiltradas);
    }


    private void dialogoNuevaVersionAvanzada(Quotation cotizacionBase) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Editar Cotización - Job #" + cotizacionBase.getJob().getId());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        layout.setPrefWidth(700);

        Label lblTitulo = new Label("Generar Nueva Versión (Edición)");
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        HBox manoObraBox = new HBox(10);
        manoObraBox.setAlignment(Pos.CENTER_LEFT);
        TextField txtManoObra = new TextField(String.valueOf(cotizacionBase.getLaborCost()));
        manoObraBox.getChildren().addAll(new Label("Costo Mano de Obra: $"), txtManoObra);

        ObservableList<QuotationMaterialDetail> materialesTemp = FXCollections.observableArrayList();

        if (cotizacionBase.getQuotationMaterialDetails() != null) {
            for (QuotationMaterialDetail old : cotizacionBase.getQuotationMaterialDetails()) {
                QuotationMaterialDetail copy = new QuotationMaterialDetail();
                copy.setMaterial(old.getMaterial());
                copy.setQuantity(old.getQuantity());
                copy.setUnitPrice(old.getUnitPrice());
                materialesTemp.add(copy);
            }
        }

        // 4. Tabla de Materiales (Editable)
        TableView<QuotationMaterialDetail> tablaMat = new TableView<>();
        tablaMat.setPrefHeight(200);

        TableColumn<QuotationMaterialDetail, String> cMat = new TableColumn<>("Material");
        cMat.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMaterial().getName()));

        TableColumn<QuotationMaterialDetail, Integer> cCant = new TableColumn<>("Cantidad");
        cCant.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getQuantity()));

        TableColumn<QuotationMaterialDetail, Double> cPrecio = new TableColumn<>("Precio Unit.");
        cPrecio.setCellValueFactory(c -> new SimpleObjectProperty<>((double)c.getValue().getUnitPrice()));

        TableColumn<QuotationMaterialDetail, Double> cSub = new TableColumn<>("Subtotal");
        cSub.setCellValueFactory(c -> new SimpleObjectProperty<>((double)(c.getValue().getQuantity() * c.getValue().getUnitPrice())));

        // Columna Eliminar
        TableColumn<QuotationMaterialDetail, Void> cAcc = new TableColumn<>("Acción");
        cAcc.setCellFactory(p -> new TableCell<>() {
            private final Button btnDel = new Button("❌");
            {
                btnDel.setStyle("-fx-text-fill: red; -fx-background-color: transparent; -fx-cursor: hand;");
                btnDel.setOnAction(e -> {
                    materialesTemp.remove(getTableView().getItems().get(getIndex()));
                    tablaMat.refresh();
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnDel);
            }
        });

        tablaMat.getColumns().addAll(cMat, cCant, cPrecio, cSub, cAcc);
        tablaMat.setItems(materialesTemp);
        tablaMat.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // 5. Formulario para AGREGAR Materiales nuevos
        HBox agregarBox = new HBox(10);
        agregarBox.setAlignment(Pos.CENTER_LEFT);
        agregarBox.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10;");

        ComboBox<Material> cmbMateriales = new ComboBox<>();
        cmbMateriales.setPromptText("Seleccionar Material...");
        cmbMateriales.setPrefWidth(200);

        // Cargar materiales del servicio
        try {
            cmbMateriales.setItems(FXCollections.observableArrayList(quotationService.getAllMaterials()));
        } catch (Exception e) { System.out.println("Error cargando materiales: " + e.getMessage()); }

        // Convertidor para ver el nombre en el combo
        cmbMateriales.setConverter(new StringConverter<Material>() {
            @Override public String toString(Material m) { return m == null ? null : m.getName() + " ($" + m.getPrice() + ")"; }
            @Override public Material fromString(String s) { return null; }
        });

        TextField txtCantidadNueva = new TextField();
        txtCantidadNueva.setPromptText("Cant.");
        txtCantidadNueva.setPrefWidth(60);

        Button btnAgregarMat = new Button("Agregar");
        btnAgregarMat.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        btnAgregarMat.setOnAction(e -> {
            Material m = cmbMateriales.getValue();
            String qStr = txtCantidadNueva.getText();
            if (m != null && qStr != null && !qStr.isEmpty()) {
                try {
                    int cant = Integer.parseInt(qStr);
                    if (cant > 0) {
                        QuotationMaterialDetail nuevoDetalle = new QuotationMaterialDetail();
                        nuevoDetalle.setMaterial(m);
                        nuevoDetalle.setQuantity(cant);
                        nuevoDetalle.setUnitPrice(m.getPrice()); // Usamos el precio ACTUAL del material
                        materialesTemp.add(nuevoDetalle);
                        tablaMat.refresh();

                        cmbMateriales.setValue(null);
                        txtCantidadNueva.clear();
                    }
                } catch (NumberFormatException ex) { mostrarAlerta("Error", "Cantidad inválida"); }
            }
        });

        agregarBox.getChildren().addAll(cmbMateriales, txtCantidadNueva, btnAgregarMat);

        // 6. Botón Guardar
        Button btnGuardar = new Button("💾 Guardar Nueva Versión");
        btnGuardar.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        btnGuardar.setMaxWidth(Double.MAX_VALUE);

        btnGuardar.setOnAction(e -> {
            try {
                double mo = Double.parseDouble(txtManoObra.getText());
                if (mo < 0) throw new NumberFormatException();

                // CREAR LA NUEVA COTIZACIÓN
                Quotation nueva = new Quotation();
                nueva.setJob(cotizacionBase.getJob()); // Mismo trabajo
                nueva.setEmisionDate(new Date());      // Fecha actual
                nueva.setLaborCost((float)mo);

                List<QuotationMaterialDetail> detallesFinales = new ArrayList<>();
                double totalMateriales = 0;

                for (QuotationMaterialDetail tmp : materialesTemp) {
                    QuotationMaterialDetail detalleFinal = new QuotationMaterialDetail();
                    detalleFinal.setMaterial(tmp.getMaterial());
                    detalleFinal.setQuantity(tmp.getQuantity());
                    detalleFinal.setUnitPrice(tmp.getUnitPrice());
                    detalleFinal.setQuotation(nueva); // Vincular

                    detallesFinales.add(detalleFinal);
                    totalMateriales += (tmp.getQuantity() * tmp.getUnitPrice());
                }

                nueva.setQuotationMaterialDetails(detallesFinales);
                nueva.setTotal((float)(totalMateriales + mo));

                quotationService.save(nueva);
                mostrarAlerta("Éxito", "Cotización actualizada correctamente.");
                cargarDatos(); // Refrescar tabla principal
                stage.close();

            } catch (NumberFormatException ex) {
                mostrarAlerta("Error", "La mano de obra debe ser un número válido.");
            } catch (Exception ex) {
                mostrarAlerta("Error Crítico", ex.getMessage());
                ex.printStackTrace();
            }
        });

        layout.getChildren().addAll(lblTitulo, manoObraBox, new Separator(), tablaMat, agregarBox, new Separator(), btnGuardar);

        stage.setScene(new Scene(layout));
        stage.showAndWait();
    }

    // ============================================================================================

    private void generarPDF(Quotation q) {
        try {
            Job job = q.getJob();
            JobType tipo = job.getJobType();
            Client cliente = job.getClient();

            String modelo="", color="", anio="";
            if(job instanceof VehicularJob) {
                VehicularJob vj = (VehicularJob) job;
                modelo = vj.getModel(); color = vj.getColor(); anio = String.valueOf(vj.getYear());
            }

            LocalDate entrega = job.getDeliveryDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            File pdf = pdfService.generarCotizacion(
                    cliente, tipo, entrega, job.getDescription(),
                    modelo, color, anio,
                    q.getQuotationMaterialDetails(),
                    q.getLaborCost()
            );

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("PDF Generado");
            alert.setHeaderText("Archivo creado: " + pdf.getName());
            alert.setContentText("¿Desea abrirlo?");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdf);
            }

        } catch (Exception e) {
            mostrarAlerta("Error PDF", e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo); a.setContentText(msg); a.show();
    }
}