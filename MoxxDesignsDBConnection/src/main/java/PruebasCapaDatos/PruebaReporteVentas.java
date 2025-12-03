/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PruebasCapaDatos;

import com.mycompany.moxxdesignsdbconnection.entitys.Job;
import com.mycompany.moxxdesignsdbconnection.entitys.Quotation;
import com.mycompany.moxxdesignsdbconnection.entitys.QuotationMaterialDetail;
import com.mycompany.moxxdesignsdbconnection.repository.IJobRepository;
import com.mycompany.moxxdesignsdbconnection.repository.JobRepositoryImpl;
import java.util.List;

public class PruebaReporteVentas {

    public static void main(String[] args) {
        IJobRepository jobRepo = new JobRepositoryImpl();

        System.out.println("=== REPORTE DE VENTAS (Cálculo Dinámico de Costos) ===");

        List<Job> todosLosTrabajos = jobRepo.findAll();

        double sumaTotalVentas = 0;
        int conteo = 0;

        System.out.printf("%-5s %-40s %-15s%n", "ID", "Concepto", "Costo Total");
        System.out.println("---------------------------------------------------------------");

        for (Job job : todosLosTrabajos) {
            // Calculamos el costo dinámicamente usando el método auxiliar
            double costoDelTrabajo = calcularCostoTotal(job);

            System.out.printf("%-5d %-40s $%-14.2f%n", 
                job.getId(), 
                job.getDescription(), // [cite: 13]
                costoDelTrabajo
            );

            sumaTotalVentas += costoDelTrabajo;
            conteo++;
        }

        System.out.println("---------------------------------------------------------------");
        System.out.println("TOTAL DE TRABAJOS: " + conteo);
        System.out.printf("VENTA TOTAL ACUMULADA: $%.2f%n", sumaTotalVentas);
    }

    /**
     * Método auxiliar para calcular el costo total de un trabajo
     * basándose en sus cotizaciones y materiales.
     */
    private static double calcularCostoTotal(Job job) {
        double totalJob = 0.0;
        
        if (job.getQuotations() != null) {
            for (Quotation q : job.getQuotations()) {
                
                totalJob += q.getLaborCost(); 

                if (q.getQuotationMaterialDetails() != null) {
                    for (QuotationMaterialDetail detalle : q.getQuotationMaterialDetails()) {
                        int cantidad = detalle.getQuantity();
                        
                        double precioMaterial = (detalle.getMaterial() != null) ? 
                                                detalle.getMaterial().getPrice() : 0.0;

                        totalJob += (cantidad * precioMaterial);
                    }
                }
            }
        }
        return totalJob;
    }
}