/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.moxxdesignsdbconnection.services;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.mycompany.moxxdesignsdbconnection.entitys.*;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import com.mycompany.moxxdesignsdbconnection.entitys.Client;
import com.mycompany.moxxdesignsdbconnection.entitys.JobType;
import com.mycompany.moxxdesignsdbconnection.entitys.QuotationMaterialDetail;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class PdfService {

    // Definimos las fuentes como constantes para reutilizarlas
    private static final Font EMPRESA_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, new BaseColor(33, 37, 41));
    private static final Font TITULO_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new BaseColor(52, 58, 64));
    private static final Font SECCION_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new BaseColor(52, 58, 64));
    private static final Font NORMAL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
    private static final Font BOLD_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK);
    private static final Font SMALL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 8, new BaseColor(108, 117, 125));

    /**
     * Genera el PDF y retorna el objeto File creado.
     */
    public File generarCotizacion(Client client, 
                                  JobType jobType, 
                                  LocalDate deliveryDate, 
                                  String description, 
                                  String vehModel, String vehColor, String vehYear, 
                                  List<QuotationMaterialDetail> materials, 
                                  double laborCost) throws Exception {

        // Crear directorio si no existe
        File directorio = new File("cotizaciones");
        if (!directorio.exists()) {
            directorio.mkdir();
        }

        String numeroCotizacion = "COT-" + System.currentTimeMillis();
        String nombreArchivo = "cotizaciones/cotizacion_" +
                client.getName().replaceAll("\\s+", "_") +
                "_" + System.currentTimeMillis() + ".pdf";
        
        File archivoFinal = new File(nombreArchivo);

        Document document = new Document(PageSize.LETTER);
        document.setMargins(40, 40, 50, 50);
        PdfWriter.getInstance(document, new FileOutputStream(archivoFinal));
        document.open();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date fechaActual = new Date();

        // 1. ENCABEZADO
        agregarEncabezado(document, numeroCotizacion, dateFormat.format(fechaActual));

        // 2. DATOS DEL CLIENTE
        agregarSeccionCliente(document, client);

        // 3. DETALLES DEL SERVICIO
        agregarSeccionServicio(document, jobType, deliveryDate, description, dateFormat);

        // 4. DATOS DEL VEHÍCULO (Solo si los datos no están vacíos)
        if (vehModel != null && !vehModel.isEmpty()) {
            agregarSeccionVehiculo(document, vehModel, vehColor, vehYear);
        }

        document.add(Chunk.NEWLINE);

        // 5. MATERIALES Y TOTALES
        agregarTablaMaterialesYTotales(document, materials, laborCost);

        // 6. PIE DE PÁGINA
        agregarPieDePagina(document);

        document.close();
        
        return archivoFinal;
    }

    // --- MÉTODOS PRIVADOS AUXILIARES PARA ORGANIZAR EL CÓDIGO ---

    private void agregarEncabezado(Document document, String numCotizacion, String fechaStr) throws DocumentException {
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{1, 2});
        headerTable.setSpacingAfter(20);

        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(Rectangle.NO_BORDER);
        Paragraph empresa = new Paragraph("MOXX\nDESIGNS", EMPRESA_FONT);
        logoCell.addElement(empresa);
        headerTable.addCell(logoCell);

        PdfPCell infoCell = new PdfPCell();
        infoCell.setBorder(Rectangle.NO_BORDER);
        infoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        Paragraph contacto = new Paragraph();
        contacto.add(new Chunk("COTIZACIÓN\n", TITULO_FONT));
        contacto.add(new Chunk("\nNúmero: " + numCotizacion + "\n", BOLD_FONT));
        contacto.add(new Chunk("Fecha: " + fechaStr + "\n", NORMAL_FONT));
        contacto.add(new Chunk("\nTeléfono: (644) 123-4567\nEmail: contacto@moxxdesigns.com\n", SMALL_FONT));
        contacto.setAlignment(Element.ALIGN_RIGHT);
        infoCell.addElement(contacto);
        headerTable.addCell(infoCell);

        document.add(headerTable);
        
        LineSeparator line = new LineSeparator();
        line.setLineColor(new BaseColor(52, 58, 64));
        document.add(new Chunk(line));
        document.add(Chunk.NEWLINE);
    }

    private void agregarSeccionCliente(Document document, Client client) throws DocumentException {
        PdfPTable clienteTable = crearTablaSeccion("DATOS DEL CLIENTE");
        addTableRow(clienteTable, "Nombre:", client.getName(), BOLD_FONT, NORMAL_FONT);
        addTableRow(clienteTable, "Teléfono:", client.getPhoneNumber(), BOLD_FONT, NORMAL_FONT);
        document.add(clienteTable);
        document.add(Chunk.NEWLINE);
    }

    private void agregarSeccionServicio(Document document, JobType jobType, LocalDate deliveryDate, String description, SimpleDateFormat dateFormat) throws DocumentException {
        PdfPTable servicioTable = crearTablaSeccion("DETALLES DEL SERVICIO");
        Date fechaDate = Date.from(deliveryDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        
        addTableRow(servicioTable, "Tipo de trabajo:", jobType.getName(), BOLD_FONT, NORMAL_FONT);
        addTableRow(servicioTable, "Fecha de entrega:", dateFormat.format(fechaDate), BOLD_FONT, NORMAL_FONT);
        document.add(servicioTable);

        Paragraph descTitulo = new Paragraph("Descripción del trabajo:", BOLD_FONT);
        descTitulo.setSpacingBefore(5);
        document.add(descTitulo);

        Paragraph descContenido = new Paragraph(description, NORMAL_FONT);
        descContenido.setIndentationLeft(15);
        descContenido.setSpacingAfter(15);
        descContenido.setAlignment(Element.ALIGN_JUSTIFIED);
        document.add(descContenido);
    }

    private void agregarSeccionVehiculo(Document document, String modelo, String color, String anio) throws DocumentException {
        PdfPTable vehiculoTable = crearTablaSeccion("DATOS DEL VEHÍCULO");
        addTableRow(vehiculoTable, "Modelo:", modelo, BOLD_FONT, NORMAL_FONT);
        addTableRow(vehiculoTable, "Color:", color, BOLD_FONT, NORMAL_FONT);
        addTableRow(vehiculoTable, "Año:", anio, BOLD_FONT, NORMAL_FONT);
        document.add(vehiculoTable);
    }

    private void agregarTablaMaterialesYTotales(Document document, List<QuotationMaterialDetail> materials, double laborCost) throws DocumentException {
        if (materials != null && !materials.isEmpty()) {
            Paragraph matTitulo = new Paragraph("DETALLE DE MATERIALES", SECCION_FONT);
            matTitulo.setSpacingBefore(10);
            matTitulo.setSpacingAfter(10);
            document.add(matTitulo);

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3, 1, 1.5f, 1.5f});
            
            BaseColor headerColor = new BaseColor(52, 58, 64);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);

            addHeaderCell(table, "Material", headerFont, headerColor);
            addHeaderCell(table, "Cant.", headerFont, headerColor);
            addHeaderCell(table, "Precio Unit.", headerFont, headerColor);
            addHeaderCell(table, "Subtotal", headerFont, headerColor);

            double totalMateriales = 0.0;
            for (QuotationMaterialDetail detalle : materials) {
                double subtotal = detalle.getQuantity() * detalle.getUnitPrice();
                totalMateriales += subtotal;

                addBodyCell(table, detalle.getMaterial().getName(), NORMAL_FONT, Element.ALIGN_LEFT);
                addBodyCell(table, String.valueOf(detalle.getQuantity()), NORMAL_FONT, Element.ALIGN_CENTER);
                addBodyCell(table, String.format("$%.2f", detalle.getUnitPrice()), NORMAL_FONT, Element.ALIGN_RIGHT);
                addBodyCell(table, String.format("$%.2f", subtotal), NORMAL_FONT, Element.ALIGN_RIGHT);
            }
            document.add(table);

            Paragraph subtotalMat = new Paragraph(String.format("Subtotal Materiales: $%.2f", totalMateriales), BOLD_FONT);
            subtotalMat.setAlignment(Element.ALIGN_RIGHT);
            subtotalMat.setSpacingBefore(10);
            document.add(subtotalMat);
            
            // Si hay mano de obra se suma aquí
            double totalGeneral = totalMateriales + laborCost;
            
            if (laborCost > 0) {
                Paragraph manoTxt = new Paragraph(String.format("Mano de Obra: $%.2f", laborCost), BOLD_FONT);
                manoTxt.setAlignment(Element.ALIGN_RIGHT);
                manoTxt.setSpacingBefore(5);
                document.add(manoTxt);
            }

            document.add(Chunk.NEWLINE);
            LineSeparator totalLine = new LineSeparator();
            totalLine.setLineColor(new BaseColor(52, 58, 64));
            totalLine.setLineWidth(2f);
            document.add(new Chunk(totalLine));

            Paragraph totalParagraph = new Paragraph(String.format("TOTAL: $%.2f MXN", totalGeneral),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, new BaseColor(52, 58, 64)));
            totalParagraph.setAlignment(Element.ALIGN_RIGHT);
            totalParagraph.setSpacingBefore(10);
            totalParagraph.setSpacingAfter(10);
            document.add(totalParagraph);
            
            document.add(new Chunk(totalLine));
        }
    }

    private void agregarPieDePagina(Document document) throws DocumentException {
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);

        Paragraph validez = new Paragraph("Validez de la cotización: 15 días a partir de la fecha de emisión", SMALL_FONT);
        validez.setSpacingBefore(20);
        document.add(validez);

        Paragraph condiciones = new Paragraph("• Esta cotización no incluye IVA\n• Se requiere anticipo del 50%", SMALL_FONT);
        condiciones.setSpacingBefore(10);
        document.add(condiciones);

        Paragraph gracias = new Paragraph("\n\n¡Gracias por su confianza!",
                FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 12, new BaseColor(52, 58, 64)));
        gracias.setAlignment(Element.ALIGN_CENTER);
        document.add(gracias);
    }
    
    // Helpers de tablas
    private PdfPTable crearTablaSeccion(String titulo) {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        try { table.setWidths(new float[]{1, 1}); } catch (DocumentException e) {}
        table.setSpacingAfter(15);
        
        PdfPCell header = new PdfPCell(new Phrase(titulo, SECCION_FONT));
        header.setBackgroundColor(new BaseColor(248, 249, 250));
        header.setPadding(8);
        header.setColspan(2);
        header.setBorder(Rectangle.NO_BORDER);
        table.addCell(header);
        return table;
    }

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
