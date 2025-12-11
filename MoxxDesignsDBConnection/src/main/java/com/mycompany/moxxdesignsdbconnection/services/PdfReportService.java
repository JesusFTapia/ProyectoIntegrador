/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.moxxdesignsdbconnection.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.mycompany.moxxdesignsdbconnection.entitys.*;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class PdfReportService {

    private static final Font TITULO_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
    private static final Font SUBTITULO_FONT = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.DARK_GRAY);
    private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
    private static final Font CELL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK);
    private static final Font TOTAL_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);

    public File generarReporteVentas(List<Job> trabajos, LocalDate inicio, LocalDate fin) throws Exception {
        
        File directorio = new File("reportes");
        if (!directorio.exists()) directorio.mkdir();

        String nombreArchivo = "reportes/Reporte_Ventas_" + System.currentTimeMillis() + ".pdf";
        File archivo = new File(nombreArchivo);

        Document document = new Document(PageSize.LETTER.rotate()); // Hoja horizontal para más espacio
        PdfWriter.getInstance(document, new FileOutputStream(archivo));
        document.open();

        Paragraph titulo = new Paragraph("Reporte de Ventas - Trabajos Completados", TITULO_FONT);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String rango = "Periodo: " + (inicio != null ? inicio.format(dtf) : "Inicio") + 
                       " - " + (fin != null ? fin.format(dtf) : "Hoy");
        Paragraph periodo = new Paragraph(rango, SUBTITULO_FONT);
        periodo.setAlignment(Element.ALIGN_CENTER);
        periodo.setSpacingAfter(20);
        document.add(periodo);

        PdfPTable table = new PdfPTable(5); // 5 Columnas
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 3, 2, 2, 1.5f}); // Anchos relativos

        agregarCeldaEncabezado(table, "ID");
        agregarCeldaEncabezado(table, "Cliente");
        agregarCeldaEncabezado(table, "Tipo Trabajo");
        agregarCeldaEncabezado(table, "Fecha Entrega");
        agregarCeldaEncabezado(table, "Monto Final");

        double granTotal = 0.0;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (Job job : trabajos) {
            double totalJob = obtenerTotalJob(job);
            granTotal += totalJob;

            agregarCeldaDatos(table, String.valueOf(job.getId()), Element.ALIGN_CENTER);
            agregarCeldaDatos(table, job.getClient().getName(), Element.ALIGN_LEFT);
            
            String tipo = (job.getJobType() != null) ? job.getJobType().getName() : "Vehicular";
            agregarCeldaDatos(table, tipo, Element.ALIGN_LEFT);
            
            String fecha = (job.getDeliveryDate() != null) ? sdf.format(job.getDeliveryDate()) : "N/A";
            agregarCeldaDatos(table, fecha, Element.ALIGN_CENTER);
            
            agregarCeldaDatos(table, String.format("$%.2f", totalJob), Element.ALIGN_RIGHT);
        }

        document.add(table);

        document.add(Chunk.NEWLINE);
        Paragraph totalP = new Paragraph(String.format("TOTAL VENTAS: $%.2f", granTotal), TOTAL_FONT);
        totalP.setAlignment(Element.ALIGN_RIGHT);
        document.add(totalP);

        Paragraph info = new Paragraph("Reporte generado el " + sdf.format(new Date()), CELL_FONT);
        info.setAlignment(Element.ALIGN_LEFT);
        document.add(info);

        document.close();
        return archivo;
    }

    private double obtenerTotalJob(Job job) {
        if (job.getQuotations() == null || job.getQuotations().isEmpty()) return 0.0;
        return job.getQuotations().stream()
                .max(Comparator.comparing(Quotation::getEmisionDate))
                .map(q -> (double) q.getTotal())
                .orElse(0.0);
    }

    private void agregarCeldaEncabezado(PdfPTable table, String texto) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, HEADER_FONT));
        cell.setBackgroundColor(new BaseColor(52, 58, 64)); // Gris oscuro
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(6);
        table.addCell(cell);
    }

    private void agregarCeldaDatos(PdfPTable table, String texto, int alineacion) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, CELL_FONT));
        cell.setHorizontalAlignment(alineacion);
        cell.setPadding(5);
        table.addCell(cell);
    }
}
