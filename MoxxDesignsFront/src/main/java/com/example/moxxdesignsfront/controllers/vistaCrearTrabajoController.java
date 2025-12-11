package com.example.moxxdesignsfront.controllers;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.mycompany.moxxdesignsdbconnection.entitys.*;
import com.mycompany.moxxdesignsdbconnection.services.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.application.Platform;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class vistaCrearTrabajoController {

    private mainController mainController;

    private QuotationService quotationService = new QuotationService();

    @FXML
    private ComboBox<Client> clienteComboBox;

    @FXML
    private ComboBox<JobType> tipoTrabajoComboBox;

    @FXML
    private ComboBox<Material> materialComboBox;

    @FXML
    private TextArea descripcionTextArea;

    @FXML
    private DatePicker fechaEntregaDatePicker;

    @FXML
    private TextField cantidadTextField;

    @FXML
    private TextField precioUnitarioTextField;

    @FXML
    private TextField manoDeObraTextField;

    // Campo para la ruta del archivo de imagen
    @FXML
    private TextField archivoImagenTextField;

    @FXML
    private Label nombreArchivoLabel;

    // Campos de vehículo
    @FXML
    private VBox vehiculoContainer;

    @FXML
    private TextField modeloTextField;

    @FXML
    private TextField colorTextField;

    @FXML
    private TextField anioTextField;

    // Tabla de materiales
    @FXML
    private TableView<QuotationMaterialDetail> tablaMateriales;

    @FXML
    private TableColumn<QuotationMaterialDetail, String> columnaMaterial;

    @FXML
    private TableColumn<QuotationMaterialDetail, Integer> columnaCantidad;

    @FXML
    private TableColumn<QuotationMaterialDetail, Double> columnaPrecioUnitario;

    @FXML
    private TableColumn<QuotationMaterialDetail, Double> columnaSubtotal;

    @FXML
    private TableColumn<QuotationMaterialDetail, Void> columnaAcciones;

    @FXML
    private Label totalMaterialesLabel;

    @FXML
    private Label totalGeneralLabel;

    private PdfService pdfService = new PdfService();

    private ObservableList<Client> todosLosClientes;
    private ObservableList<QuotationMaterialDetail> materialesAgregados = FXCollections.observableArrayList();

    // Variable para almacenar la ruta del archivo seleccionado
    private String rutaArchivoImagen = "";

    public void setMainController(mainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void regresar() {
        if (mainController != null) {
            mainController.cargarVistaEnCenter("/fxml/vistaTrabajos.fxml");
        }
    }

    @FXML
    public void initialize() {
        cargarClientes();
        cargarTiposTrabajo();
        cargarMateriales();
        configurarTipoTrabajoListener();
        configurarTablaMateriales();
        configurarListenerManoDeObra();

        fechaEntregaDatePicker.setDayCellFactory(datePicker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ccc;");
                }
            }
        });

        fechaEntregaDatePicker.setValue(LocalDate.now());
    }

    private void configurarListenerManoDeObra() {
        manoDeObraTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            actualizarTotales();
        });
    }

    private void cargarTiposTrabajo() {
        try {
            List<JobType> tiposTrabajo = quotationService.getAllJobTypes();
            tipoTrabajoComboBox.setItems(FXCollections.observableArrayList(tiposTrabajo));

            tipoTrabajoComboBox.setCellFactory(param -> new ListCell<JobType>() {
                @Override
                protected void updateItem(JobType tipo, boolean empty) {
                    super.updateItem(tipo, empty);
                    if (empty || tipo == null) {
                        setText(null);
                    } else {
                        setText(tipo.getName());
                    }
                }
            });

            tipoTrabajoComboBox.setButtonCell(new ListCell<JobType>() {
                @Override
                protected void updateItem(JobType tipo, boolean empty) {
                    super.updateItem(tipo, empty);
                    if (empty || tipo == null) {
                        setText(null);
                    } else {
                        setText(tipo.getName());
                    }
                }
            });

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudieron cargar los tipos de trabajo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarMateriales() {
        try {
            List<Material> materiales = quotationService.getAllMaterials();
            materialComboBox.setItems(FXCollections.observableArrayList(materiales));

            materialComboBox.setCellFactory(param -> new ListCell<Material>() {
                @Override
                protected void updateItem(Material material, boolean empty) {
                    super.updateItem(material, empty);
                    if (empty || material == null) {
                        setText(null);
                    } else {
                        setText(material.getName() + " - " + material.getDescription());
                    }
                }
            });

            materialComboBox.setButtonCell(new ListCell<Material>() {
                @Override
                protected void updateItem(Material material, boolean empty) {
                    super.updateItem(material, empty);
                    if (empty || material == null) {
                        setText(null);
                    } else {
                        setText(material.getName());
                    }
                }
            });

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudieron cargar los materiales: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurarTipoTrabajoListener() {
        tipoTrabajoComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String nombreTipo = newValue.getName().toLowerCase();
                boolean esVehiculo = nombreTipo.contains("auto") || nombreTipo.contains("moto");
                vehiculoContainer.setVisible(esVehiculo);
                vehiculoContainer.setManaged(esVehiculo);

                if (!esVehiculo) {
                    modeloTextField.clear();
                    colorTextField.clear();
                    anioTextField.clear();
                }
            }
        });
    }

    private void configurarTablaMateriales() {
        columnaMaterial.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getMaterial().getName()));

        columnaCantidad.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());

        columnaPrecioUnitario.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getMaterial().getPrice()).asObject());

        columnaSubtotal.setCellValueFactory(cellData -> {
            QuotationMaterialDetail detalle = cellData.getValue();

            double subtotal = detalle.getQuantity() * detalle.getMaterial().getPrice();
            return new SimpleDoubleProperty(subtotal).asObject();
        });

        columnaPrecioUnitario.setCellFactory(col -> new TableCell<QuotationMaterialDetail, Double>() {
            @Override
            protected void updateItem(Double precio, boolean empty) {
                super.updateItem(precio, empty);
                if (empty || precio == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", precio));
                }
            }
        });

        columnaSubtotal.setCellFactory(col -> new TableCell<QuotationMaterialDetail, Double>() {
            @Override
            protected void updateItem(Double subtotal, boolean empty) {
                super.updateItem(subtotal, empty);
                if (empty || subtotal == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", subtotal));
                }
            }
        });

        columnaAcciones.setCellFactory(param -> new TableCell<QuotationMaterialDetail, Void>() {
            private final Button btnEliminar = new Button("Eliminar");

            {
                btnEliminar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                btnEliminar.setOnAction(event -> {
                    QuotationMaterialDetail detalle = getTableView().getItems().get(getIndex());
                    eliminarMaterial(detalle);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnEliminar);
                }
            }
        });

        tablaMateriales.setItems(materialesAgregados);
    }

    @FXML
    private void agregarMaterial() {
        Material materialSeleccionado = materialComboBox.getValue();
        String cantidadStr = cantidadTextField.getText();

        if (materialSeleccionado == null) {
            mostrarAlerta("Error", "Debe seleccionar un material");
            return;
        }



        try {
            int cantidad = Integer.parseInt(cantidadStr);

            if (cantidad <= 0 ) {
                mostrarAlerta("Error", "La cantidad y el precio deben ser mayores a cero");
                return;
            }

            QuotationMaterialDetail detalle = new QuotationMaterialDetail();
            detalle.setMaterial(materialSeleccionado);
            detalle.setQuantity(cantidad);
            detalle.setUnitPrice(materialSeleccionado.getPrice());

            materialesAgregados.add(detalle);

            materialComboBox.setValue(null);
            cantidadTextField.clear();

            actualizarTotales();

            System.out.println("Material agregado: " + materialSeleccionado.getName());

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Cantidad y precio deben ser valores numéricos válidos");
        }
    }

    private void eliminarMaterial(QuotationMaterialDetail detalle) {
        materialesAgregados.remove(detalle);
        actualizarTotales();
        System.out.println("Material eliminado: " + detalle.getMaterial().getName());
    }

    private void actualizarTotales() {
        double totalMateriales = 0.0;
        for (QuotationMaterialDetail detalle : materialesAgregados) {
            totalMateriales += detalle.getQuantity() * detalle.getUnitPrice();
        }
        totalMaterialesLabel.setText(String.format("Total Materiales: $%.2f", totalMateriales));

        double manoDeObra = 0.0;
        try {
            String manoDeObraStr = manoDeObraTextField.getText();
            if (manoDeObraStr != null && !manoDeObraStr.isEmpty()) {
                manoDeObra = Double.parseDouble(manoDeObraStr);
            }
        } catch (NumberFormatException e) {
            // Si no es un número válido, se queda en 0
        }

        double totalGeneral = totalMateriales + manoDeObra;
        totalGeneralLabel.setText(String.format("$%.2f", totalGeneral));
    }

    // NUEVO: Método para seleccionar archivo de imagen
    @FXML
    private void seleccionarArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen del Trabajo");

        // Filtros para solo permitir imágenes PNG y JPG
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg", "*.jpeg")
        );

        // Obtener el stage actual
        Stage stage = (Stage) archivoImagenTextField.getScene().getWindow();

        // Abrir el diálogo de selección de archivo
        File archivoSeleccionado = fileChooser.showOpenDialog(stage);

        if (archivoSeleccionado != null) {
            rutaArchivoImagen = archivoSeleccionado.getAbsolutePath();
            archivoImagenTextField.setText(rutaArchivoImagen);
            nombreArchivoLabel.setText("📁 " + archivoSeleccionado.getName());
            nombreArchivoLabel.setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
            System.out.println("Archivo seleccionado: " + rutaArchivoImagen);
        }
    }

    // NUEVO: Método para limpiar la selección de archivo
    @FXML
    private void limpiarArchivo() {
        rutaArchivoImagen = "";
        archivoImagenTextField.clear();
        nombreArchivoLabel.setText("Ningún archivo seleccionado");
        nombreArchivoLabel.setStyle("-fx-text-fill: #6c757d; -fx-font-style: italic;");
    }

    public void cargarClientes() {
        try {
            List<Client> clientes = quotationService.getAllClients();

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
        // Validaciones básicas
        Client clienteSeleccionado = clienteComboBox.getValue();
        if (clienteSeleccionado == null) {
            mostrarAlerta("Error", "Debe seleccionar un cliente de la lista");
            return;
        }

        JobType tipoTrabajo = tipoTrabajoComboBox.getValue();
        if (tipoTrabajo == null) {
            mostrarAlerta("Error", "Debe seleccionar un tipo de trabajo");
            return;
        }

        LocalDate fechaEntrega = fechaEntregaDatePicker.getValue();

        if (fechaEntrega == null) {
            mostrarAlerta("Error", "Debe seleccionar una fecha de entrega");
            return;
        }
        if (fechaEntrega.isBefore(LocalDate.now())) {
        mostrarAlerta("Error", "La fecha de entrega no puede ser un día anterior a hoy.");
        return;
    }

        String descripcion = descripcionTextArea.getText();
        if (descripcion == null || descripcion.trim().isEmpty()) {
            mostrarAlerta("Error", "Debe ingresar una descripción del trabajo");
            return;
        }

        // Validar datos de vehículo si aplica
        String nombreTipo = tipoTrabajo.getName().toLowerCase();
        VehicularJob trabajoVehicular = null;

        if (nombreTipo.contains("auto") || nombreTipo.contains("moto")) {
            String modelo = modeloTextField.getText();
            String color = colorTextField.getText();
            String anio = anioTextField.getText();

            if (modelo.isEmpty() || color.isEmpty() || anio.isEmpty()) {
                mostrarAlerta("Error", "Debe completar todos los datos del vehículo");
                return;
            }

            trabajoVehicular = new VehicularJob();
            trabajoVehicular.setModel(modelo);
            trabajoVehicular.setColor(color);
            trabajoVehicular.setYear(Integer.parseInt(anio));
        }

        // Obtener mano de obra
        double manoDeObra = 0.0;
        try {
            String manoDeObraStr = manoDeObraTextField.getText();
            if (manoDeObraStr != null && !manoDeObraStr.isEmpty()) {
                manoDeObra = Double.parseDouble(manoDeObraStr);
                if (manoDeObra < 0) {
                    mostrarAlerta("Error", "La mano de obra no puede ser negativa");
                    return;
                }
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "La mano de obra debe ser un número válido");
            return;
        }

        try {
            Quotation quotation = new Quotation();
            quotation.setEmisionDate(new Date());
            quotation.setTotal((float)calcularTotalGeneral());
            quotation.setLaborCost((float)manoDeObra);

            List<QuotationMaterialDetail> quotationMaterialDetails = new ArrayList<>();

            for (QuotationMaterialDetail detalle : materialesAgregados) {
                QuotationMaterialDetail qmd = new QuotationMaterialDetail();
                qmd.setMaterial(detalle.getMaterial());
                qmd.setQuantity(detalle.getQuantity());
                qmd.setUnitPrice(detalle.getUnitPrice());
                qmd.setQuotation(quotation);

                quotationMaterialDetails.add(qmd);
            }

            quotation.setQuotationMaterialDetails(quotationMaterialDetails);

            List<Quotation> quotations = new ArrayList<>();
            quotations.add(quotation);

            Date fechaEntregaDate = Date.from(fechaEntrega.atStartOfDay(ZoneId.systemDefault()).toInstant());

            User usuario = obtenerUsuarioActual();

            // Usar la ruta del archivo de imagen (o cadena vacía si no se seleccionó)
            String rutaArchivo = rutaArchivoImagen.isEmpty() ? "" : rutaArchivoImagen;

            Job trabajo;

            if (trabajoVehicular != null) {

                trabajoVehicular.setDeliveryDate(fechaEntregaDate);
                trabajoVehicular.setState("PENDIENTE");
                trabajoVehicular.setDescription(descripcion);
                trabajoVehicular.setFileDirection(rutaArchivo); // USAR LA RUTA DEL ARCHIVO
                trabajoVehicular.setQuotations(quotations);
                trabajoVehicular.setJobType(tipoTrabajo);
                trabajoVehicular.setUser(usuario);
                trabajoVehicular.setClient(clienteSeleccionado);

                quotation.setJob(trabajoVehicular);

                trabajo = quotationService.registerNewJob(trabajoVehicular);
            } else {
                GeneralJob trabajoGenerico = new GeneralJob(fechaEntregaDate,
                        "PENDIENTE",
                        descripcion,
                        rutaArchivo, // USAR LA RUTA DEL ARCHIVO
                        quotations,
                        tipoTrabajo,
                        usuario,
                        clienteSeleccionado);
                
                quotation.setJob(trabajoGenerico);

                trabajo = quotationService.registerNewJob(trabajoGenerico);
            }

            mostrarAlerta("Éxito", "Trabajo guardado correctamente con ID: " + trabajo.getId());

            limpiarFormulario();

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo guardar el trabajo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private double calcularTotalGeneral() {
        double totalMateriales = 0.0;
        for (QuotationMaterialDetail detalle : materialesAgregados) {
            totalMateriales += detalle.getQuantity() * detalle.getUnitPrice();
        }

        double manoDeObra = 0.0;
        try {
            String manoDeObraStr = manoDeObraTextField.getText();
            if (manoDeObraStr != null && !manoDeObraStr.isEmpty()) {
                manoDeObra = Double.parseDouble(manoDeObraStr);
            }
        } catch (NumberFormatException e) {
            // Si no es válido, queda en 0
        }

        return totalMateriales + manoDeObra;
    }

    private User obtenerUsuarioActual() {
        User usuarioPrueba = quotationService.getUser1();
        return usuarioPrueba;
    }

    private void limpiarFormulario() {
        clienteComboBox.setValue(null);
        clienteComboBox.getEditor().clear();
        tipoTrabajoComboBox.setValue(null);
        descripcionTextArea.clear();
        fechaEntregaDatePicker.setValue(LocalDate.now());
        modeloTextField.clear();
        colorTextField.clear();
        anioTextField.clear();
        materialComboBox.setValue(null);
        cantidadTextField.clear();
        manoDeObraTextField.clear();
        materialesAgregados.clear();
        limpiarArchivo(); // Limpiar también el archivo
        actualizarTotales();
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
            int cantidadAntes = (todosLosClientes != null) ? todosLosClientes.size() : 0;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/registrarCliente.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Registrar Nuevo Cliente");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.showAndWait();

            cargarClientes();
            
            if (todosLosClientes != null && todosLosClientes.size() > cantidadAntes) {
                
                Client nuevoCliente = todosLosClientes.get(todosLosClientes.size() - 1);
                clienteComboBox.setValue(nuevoCliente);
                
                Platform.runLater(() -> {
                    clienteComboBox.getSelectionModel().select(nuevoCliente);
                });
                
                System.out.println("Cliente nuevo seleccionado automáticamente: " + nuevoCliente.getName());
            }

        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo abrir el formulario de registro: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @FXML
    private void generarTicketPDF() {
        // 1. Recolección y Validación de Datos (UI)
        Client clienteSeleccionado = clienteComboBox.getValue();
        JobType tipoTrabajo = tipoTrabajoComboBox.getValue();
        LocalDate fechaEntrega = fechaEntregaDatePicker.getValue();
        String descripcion = descripcionTextArea.getText();

        if (clienteSeleccionado == null || tipoTrabajo == null || fechaEntrega == null || descripcion.trim().isEmpty()) {
            mostrarAlerta("Error", "Faltan datos obligatorios (Cliente, Tipo, Fecha o Descripción)");
            return;
        }

        // 2. Preparar datos del vehículo (si aplica)
        String modelo = "", color = "", anio = "";
        String nombreTipo = tipoTrabajo.getName().toLowerCase();

        // OJO: Aquí podrías usar el booleano si mejoras la entidad JobType como sugerí antes
        if (nombreTipo.contains("auto") || nombreTipo.contains("moto") || nombreTipo.contains("vehículo")) {
            modelo = modeloTextField.getText();
            color = colorTextField.getText();
            anio = anioTextField.getText();
        }

        // 3. Obtener mano de obra
        double manoDeObra = 0.0;
        try {
            String moStr = manoDeObraTextField.getText();
            if (moStr != null && !moStr.isEmpty()) manoDeObra = Double.parseDouble(moStr);
        } catch (NumberFormatException e) { /* Ignorar */ }

        // 4. LLAMAR AL SERVICIO (Try-Catch solo para el proceso de archivo)
        try {
            File pdfFile = pdfService.generarCotizacion(
                    clienteSeleccionado,
                    tipoTrabajo,
                    fechaEntrega,
                    descripcion,
                    modelo, color, anio,
                    materialesAgregados,
                    manoDeObra
            );

            // 5. Lógica de UI (Abrir archivo)
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Cotización Generada");
            alert.setHeaderText("PDF Creado Exitosamente");
            alert.setContentText("Archivo: " + pdfFile.getName() + "\n¿Desea abrirlo ahora?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(pdfFile);
                }
            }

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo generar el PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }
}