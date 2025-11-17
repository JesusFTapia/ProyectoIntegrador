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
    @FXML
    private void generarTicketPDF() {
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

        String descripcion = descripcionTextArea.getText();
        if (descripcion == null || descripcion.trim().isEmpty()) {
            mostrarAlerta("Error", "Debe ingresar una descripción del trabajo");
            return;
        }

        try {
            // Crear directorio si no existe
            File directorio = new File("cotizaciones");
            if (!directorio.exists()) {
                directorio.mkdir();
            }

            // Generar número de cotización único
            String numeroCotizacion = "COT-" + System.currentTimeMillis();

            // Nombre del archivo
            String nombreArchivo = "cotizaciones/cotizacion_" +
                    clienteSeleccionado.getName().replaceAll("\\s+", "_") +
                    "_" + System.currentTimeMillis() + ".pdf";

            // Crear documento tamaño carta
            Document document = new Document(PageSize.LETTER);
            document.setMargins(40, 40, 50, 50);
            PdfWriter.getInstance(document, new FileOutputStream(nombreArchivo));
            document.open();

            // Definir fuentes
            Font empresaFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, new BaseColor(33, 37, 41));
            Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new BaseColor(52, 58, 64));
            Font seccionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new BaseColor(52, 58, 64));
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK);
            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 8, new BaseColor(108, 117, 125));

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date fechaActual = new Date();

            // ==================== ENCABEZADO ====================
            // Tabla para encabezado con logo (espacio) y datos de empresa
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1, 2});
            headerTable.setSpacingAfter(20);

            // Celda izquierda - Logo/Empresa
            PdfPCell logoCell = new PdfPCell();
            logoCell.setBorder(Rectangle.NO_BORDER);
            Paragraph empresa = new Paragraph("MOXX\nDESIGNS", empresaFont);
            empresa.setAlignment(Element.ALIGN_LEFT);
            logoCell.addElement(empresa);
            headerTable.addCell(logoCell);

            // Celda derecha - Información de contacto
            PdfPCell infoCell = new PdfPCell();
            infoCell.setBorder(Rectangle.NO_BORDER);
            infoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

            Paragraph contacto = new Paragraph();
            contacto.add(new Chunk("COTIZACIÓN\n", tituloFont));
            contacto.add(new Chunk("\nNúmero: " + numeroCotizacion + "\n", boldFont));
            contacto.add(new Chunk("Fecha: " + dateFormat.format(fechaActual) + "\n", normalFont));
            contacto.add(new Chunk("\nTeléfono: (644) 123-4567\n", smallFont));
            contacto.add(new Chunk("Email: contacto@moxxdesigns.com\n", smallFont));
            contacto.setAlignment(Element.ALIGN_RIGHT);
            infoCell.addElement(contacto);
            headerTable.addCell(infoCell);

            document.add(headerTable);

            // Línea divisoria
            LineSeparator line = new LineSeparator();
            line.setLineColor(new BaseColor(52, 58, 64));
            line.setLineWidth(1f);
            document.add(new Chunk(line));
            document.add(Chunk.NEWLINE);

            // ==================== INFORMACIÓN DEL CLIENTE ====================
            PdfPTable clienteTable = new PdfPTable(2);
            clienteTable.setWidthPercentage(100);
            clienteTable.setWidths(new float[]{1, 1});
            clienteTable.setSpacingAfter(15);

            // Datos del cliente
            PdfPCell clienteHeader = new PdfPCell(new Phrase("DATOS DEL CLIENTE", seccionFont));
            clienteHeader.setBackgroundColor(new BaseColor(248, 249, 250));
            clienteHeader.setPadding(8);
            clienteHeader.setColspan(2);
            clienteHeader.setBorder(Rectangle.NO_BORDER);
            clienteTable.addCell(clienteHeader);

            addTableRow(clienteTable, "Nombre:", clienteSeleccionado.getName(), boldFont, normalFont);
            addTableRow(clienteTable, "Teléfono:", clienteSeleccionado.getPhoneNumber(), boldFont, normalFont);

            document.add(clienteTable);
            document.add(Chunk.NEWLINE);

            // ==================== DETALLES DEL SERVICIO ====================
            PdfPTable servicioTable = new PdfPTable(2);
            servicioTable.setWidthPercentage(100);
            servicioTable.setWidths(new float[]{1, 1});
            servicioTable.setSpacingAfter(15);

            PdfPCell servicioHeader = new PdfPCell(new Phrase("DETALLES DEL SERVICIO", seccionFont));
            servicioHeader.setBackgroundColor(new BaseColor(248, 249, 250));
            servicioHeader.setPadding(8);
            servicioHeader.setColspan(2);
            servicioHeader.setBorder(Rectangle.NO_BORDER);
            servicioTable.addCell(servicioHeader);

            addTableRow(servicioTable, "Tipo de trabajo:", tipoTrabajo.getName(), boldFont, normalFont);
            addTableRow(servicioTable, "Fecha de entrega:",
                    dateFormat.format(Date.from(fechaEntrega.atStartOfDay(ZoneId.systemDefault()).toInstant())),
                    boldFont, normalFont);

            document.add(servicioTable);

            // Descripción del trabajo
            Paragraph descTitulo = new Paragraph("Descripción del trabajo:", boldFont);
            descTitulo.setSpacingBefore(5);
            document.add(descTitulo);

            Paragraph descContenido = new Paragraph(descripcion, normalFont);
            descContenido.setIndentationLeft(15);
            descContenido.setSpacingAfter(15);
            descContenido.setAlignment(Element.ALIGN_JUSTIFIED);
            document.add(descContenido);

            // ==================== DATOS DEL VEHÍCULO ====================
            String nombreTipo = tipoTrabajo.getName().toLowerCase();
            if (nombreTipo.contains("auto") || nombreTipo.contains("moto") || nombreTipo.contains("vehículo")) {
                String modelo = modeloTextField.getText();
                String color = colorTextField.getText();
                String anio = anioTextField.getText();

                if (!modelo.isEmpty() || !color.isEmpty() || !anio.isEmpty()) {
                    PdfPTable vehiculoTable = new PdfPTable(2);
                    vehiculoTable.setWidthPercentage(100);
                    vehiculoTable.setWidths(new float[]{1, 1});
                    vehiculoTable.setSpacingAfter(15);

                    PdfPCell vehiculoHeader = new PdfPCell(new Phrase("DATOS DEL VEHÍCULO", seccionFont));
                    vehiculoHeader.setBackgroundColor(new BaseColor(248, 249, 250));
                    vehiculoHeader.setPadding(8);
                    vehiculoHeader.setColspan(2);
                    vehiculoHeader.setBorder(Rectangle.NO_BORDER);
                    vehiculoTable.addCell(vehiculoHeader);

                    if (!modelo.isEmpty()) addTableRow(vehiculoTable, "Modelo:", modelo, boldFont, normalFont);
                    if (!color.isEmpty()) addTableRow(vehiculoTable, "Color:", color, boldFont, normalFont);
                    if (!anio.isEmpty()) addTableRow(vehiculoTable, "Año:", anio, boldFont, normalFont);

                    document.add(vehiculoTable);
                }
            }

            document.add(Chunk.NEWLINE);

            // ==================== TABLA DE MATERIALES ====================
            if (!materialesAgregados.isEmpty()) {
                Paragraph matTitulo = new Paragraph("DETALLE DE MATERIALES", seccionFont);
                matTitulo.setSpacingBefore(10);
                matTitulo.setSpacingAfter(10);
                document.add(matTitulo);

                PdfPTable materialesTable = new PdfPTable(4);
                materialesTable.setWidthPercentage(100);
                materialesTable.setWidths(new float[]{3, 1, 1.5f, 1.5f});

                // Encabezados de tabla
                BaseColor headerColor = new BaseColor(52, 58, 64);
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);

                addHeaderCell(materialesTable, "Material", headerFont, headerColor);
                addHeaderCell(materialesTable, "Cant.", headerFont, headerColor);
                addHeaderCell(materialesTable, "Precio Unit.", headerFont, headerColor);
                addHeaderCell(materialesTable, "Subtotal", headerFont, headerColor);

                double totalMateriales = 0.0;
                for (QuotationMaterialDetail detalle : materialesAgregados) {
                    double subtotal = detalle.getQuantity() * detalle.getUnitPrice();
                    totalMateriales += subtotal;

                    addBodyCell(materialesTable, detalle.getMaterial().getName(), normalFont, Element.ALIGN_LEFT);
                    addBodyCell(materialesTable, String.valueOf(detalle.getQuantity()), normalFont, Element.ALIGN_CENTER);
                    addBodyCell(materialesTable, String.format("$%.2f", detalle.getUnitPrice()), normalFont, Element.ALIGN_RIGHT);
                    addBodyCell(materialesTable, String.format("$%.2f", subtotal), normalFont, Element.ALIGN_RIGHT);
                }

                document.add(materialesTable);

                // Subtotal materiales
                Paragraph subtotalMat = new Paragraph(String.format("Subtotal Materiales: $%.2f", totalMateriales), boldFont);
                subtotalMat.setAlignment(Element.ALIGN_RIGHT);
                subtotalMat.setSpacingBefore(10);
                document.add(subtotalMat);
            }

            // ==================== MANO DE OBRA ====================
            double manoDeObra = 0.0;
            try {
                String manoDeObraStr = manoDeObraTextField.getText();
                if (manoDeObraStr != null && !manoDeObraStr.isEmpty()) {
                    manoDeObra = Double.parseDouble(manoDeObraStr);
                }
            } catch (NumberFormatException e) {
                manoDeObra = 0.0;
            }

            if (manoDeObra > 0) {
                Paragraph manoTxt = new Paragraph(String.format("Mano de Obra: $%.2f", manoDeObra), boldFont);
                manoTxt.setAlignment(Element.ALIGN_RIGHT);
                manoTxt.setSpacingBefore(5);
                document.add(manoTxt);
            }

            // ==================== TOTAL ====================
            document.add(Chunk.NEWLINE);
            LineSeparator totalLine = new LineSeparator();
            totalLine.setLineColor(new BaseColor(52, 58, 64));
            totalLine.setLineWidth(2f);
            document.add(new Chunk(totalLine));

            double totalGeneral = calcularTotalGeneral();
            Paragraph totalParagraph = new Paragraph(String.format("TOTAL: $%.2f MXN", totalGeneral),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, new BaseColor(52, 58, 64)));
            totalParagraph.setAlignment(Element.ALIGN_RIGHT);
            totalParagraph.setSpacingBefore(10);
            totalParagraph.setSpacingAfter(10);
            document.add(totalParagraph);

            document.add(new Chunk(totalLine));

            // ==================== PIE DE PÁGINA ====================
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);

            Paragraph validez = new Paragraph("Validez de la cotización: 15 días a partir de la fecha de emisión", smallFont);
            validez.setAlignment(Element.ALIGN_LEFT);
            validez.setSpacingBefore(20);
            document.add(validez);

            Paragraph condiciones = new Paragraph(
                    "• Esta cotización no incluye IVA\n" +
                            "• Los precios pueden variar según disponibilidad de materiales\n" +
                            "• Se requiere anticipo del 50% para iniciar el trabajo\n" +
                            "• Tiempos de entrega sujetos a confirmación",
                    smallFont
            );
            condiciones.setSpacingBefore(10);
            document.add(condiciones);

            // Agradecimiento
            Paragraph gracias = new Paragraph("\n\n¡Gracias por su confianza!",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 12, new BaseColor(52, 58, 64)));
            gracias.setAlignment(Element.ALIGN_CENTER);
            document.add(gracias);

            document.close();

            // Mostrar mensaje de éxito y preguntar si desea abrir el PDF
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Cotización Generada");
            alert.setHeaderText("Cotización PDF creada exitosamente");
            alert.setContentText("Número: " + numeroCotizacion + "\n" +
                    "Archivo: " + nombreArchivo + "\n\n¿Desea abrir el archivo?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                Desktop.getDesktop().open(new File(nombreArchivo));
            }

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo generar la cotización: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Métodos auxiliares para crear celdas de tabla
    private void addTableRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5);
        table.addCell(valueCell);
    }

    private void addHeaderCell(PdfPTable table, String text, Font font, BaseColor bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bgColor);
        cell.setPadding(8);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    private void addBodyCell(PdfPTable table, String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(6);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }
}