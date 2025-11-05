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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class crearTrabajoController {

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
                new SimpleDoubleProperty(cellData.getValue().getUnitPrice()).asObject());

        columnaSubtotal.setCellValueFactory(cellData -> {
            QuotationMaterialDetail detalle = cellData.getValue();
            double subtotal = detalle.getQuantity() * detalle.getUnitPrice();
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
        String precioStr = precioUnitarioTextField.getText();

        if (materialSeleccionado == null) {
            mostrarAlerta("Error", "Debe seleccionar un material");
            return;
        }

        if (cantidadStr.isEmpty() || precioStr.isEmpty()) {
            mostrarAlerta("Error", "Debe ingresar cantidad y precio unitario");
            return;
        }

        try {
            int cantidad = Integer.parseInt(cantidadStr);
            double precioUnitario = Double.parseDouble(precioStr);

            if (cantidad <= 0 || precioUnitario <= 0) {
                mostrarAlerta("Error", "La cantidad y el precio deben ser mayores a cero");
                return;
            }

            QuotationMaterialDetail detalle = new QuotationMaterialDetail();
            detalle.setMaterial(materialSeleccionado);
            detalle.setQuantity(cantidad);
            detalle.setUnitPrice(precioUnitario);

            materialesAgregados.add(detalle);

            materialComboBox.setValue(null);
            cantidadTextField.clear();
            precioUnitarioTextField.clear();

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
        precioUnitarioTextField.clear();
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
}