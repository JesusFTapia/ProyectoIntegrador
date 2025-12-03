package PruebasCapaDatos;

import com.mycompany.moxxdesignsdbconnection.entitys.*;
import com.mycompany.moxxdesignsdbconnection.repository.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PruebaAdministrarTrabajo {

    public static void main(String[] args) {
        
        System.out.println("=== INICIANDO SISTEMA ===");
        
        IUserRepository userRepo = new UserRepositoryImpl();
        IClientRepository clientRepo = new ClientRepositoryImpl();
        IJobTypeRepository jobTypeRepo = new JobTypeRepositoryImpl();
        IMaterialRepository materialRepo = new MaterialRepositoryImpl();
        IJobRepository jobRepo = new JobRepositoryImpl();

        User admin = new User("Admin", "Carlos", "pass123");
        userRepo.save(admin);

        Client clienteJuan = new Client("Juan", "Perez", "644-000-1111");
        clientRepo.save(clienteJuan);
        
        Client clienteMaria = new Client("Maria", "Lopez", "644-999-8888");
        clientRepo.save(clienteMaria);

        JobType tipoGeneral = new JobType("General", "Trabajos varios");
        jobTypeRepo.save(tipoGeneral);

        Material matPintura = new Material("Pintura", "Lata 1L", 100, "Lata", 200.0f);
        materialRepo.save(matPintura);
        
        Material matMadera = new Material("Madera", "Tablón Pino", 50, "Pieza", 50.0f);
        materialRepo.save(matMadera);

        Material matLija = new Material("Lija", "Lija de agua", 200, "Hoja", 10.0f);
        materialRepo.save(matLija);

        System.out.println("\n=== 1. LISTADO INICIAL DE TRABAJOS ===");

        VehicularJob jobAuto = new VehicularJob();
        jobAuto.setDescription("Pintura Completa Nissan");
        jobAuto.setModel("Nissan Versa");
        jobAuto.setColor("Rojo");
        jobAuto.setYear(2018);
        jobAuto.setClient(clienteJuan);
        jobAuto.setUser(admin);
        jobAuto.setDeliveryDate(new Date());
        jobAuto.setState("PENDIENTE");
        jobAuto.setJobType(tipoGeneral);

        Quotation quoteAuto = new Quotation();
        quoteAuto.setLaborCost(2500.0f);
        quoteAuto.setEmisionDate(new Date());
        
        List<QuotationMaterialDetail> detsAuto = new ArrayList<>();
        detsAuto.add(new QuotationMaterialDetail(quoteAuto, matPintura, 3, 200.0));
        
        quoteAuto.setQuotationMaterialDetails(detsAuto);
        quoteAuto.setTotal(3100.0f);
        quoteAuto.setJob(jobAuto);

        jobAuto.setQuotations(new ArrayList<>(List.of(quoteAuto)));
        jobRepo.save(jobAuto);

        GeneralJob jobMesa = new GeneralJob();
        jobMesa.setDescription("Fabricación Mesa de Centro");
        jobMesa.setClient(clienteMaria);
        jobMesa.setUser(admin);
        jobMesa.setDeliveryDate(new Date());
        jobMesa.setState("PENDIENTE");
        jobMesa.setJobType(tipoGeneral);

        Quotation quoteMesa1 = new Quotation();
        quoteMesa1.setLaborCost(400.0f);
        quoteMesa1.setEmisionDate(new Date());

        List<QuotationMaterialDetail> detsMesa1 = new ArrayList<>();
        detsMesa1.add(new QuotationMaterialDetail(quoteMesa1, matMadera, 4, 50.0));
        
        quoteMesa1.setQuotationMaterialDetails(detsMesa1);
        quoteMesa1.setTotal(600.0f);
        quoteMesa1.setJob(jobMesa);

        jobMesa.setQuotations(new ArrayList<>(List.of(quoteMesa1)));
        jobRepo.save(jobMesa);

        imprimirDetallesTrabajo(jobAuto);
        imprimirDetallesTrabajo(jobMesa);

        System.out.println("\n\n=== 2. AGREGANDO NUEVA COTIZACIÓN AL TRABAJO ===");

        GeneralJob trabajoAEditar = jobMesa; 

        Quotation nuevaCotizacion = new Quotation();
        nuevaCotizacion.setEmisionDate(new Date());
        nuevaCotizacion.setLaborCost(600.0f); 
        nuevaCotizacion.setJob(trabajoAEditar);

        List<QuotationMaterialDetail> nuevosDetalles = new ArrayList<>();
        nuevosDetalles.add(new QuotationMaterialDetail(nuevaCotizacion, matMadera, 4, 50.0));
        nuevosDetalles.add(new QuotationMaterialDetail(nuevaCotizacion, matLija, 10, 10.0));

        nuevaCotizacion.setQuotationMaterialDetails(nuevosDetalles);

        float totalVersion2 = 600.0f + (4 * 50.0f) + (10 * 10.0f);
        nuevaCotizacion.setTotal(totalVersion2);

        trabajoAEditar.getQuotations().add(nuevaCotizacion);

        jobRepo.save(trabajoAEditar); 
        
        System.out.println("\n>> TRABAJO ACTUALIZADO (HISTORIAL DE COTIZACIONES):");
        imprimirDetallesTrabajo(trabajoAEditar);

        System.out.println("=== PRUEBA FINALIZADA ===");
    }

    public static void imprimirDetallesTrabajo(Job job) {
        System.out.println("--------------------------------");
        System.out.print("JOB INFO: " + job.getDescription());
        
        if (job instanceof VehicularJob) {
            System.out.print(" | VEHICULO: " + ((VehicularJob) job).getModel());
        }
        System.out.println("\nCLIENTE : " + (job.getClient() != null ? job.getClient().getName() : "N/A"));
        System.out.println("--------------------------------");

        if (job.getQuotations() == null || job.getQuotations().isEmpty()) {
            System.out.println(" (Sin Cotizaciones)");
            return;
        }

        int index = 1;
        for (Quotation q : job.getQuotations()) {
            System.out.println("COTIZACIÓN #" + index);
            System.out.println("- ID Cotización: " + q.getId());
            System.out.println("- Mano de Obra: $" + q.getLaborCost());
            System.out.println("- Materiales:");

            float sumaMateriales = 0;
            for (QuotationMaterialDetail d : q.getQuotationMaterialDetails()) {
                float subtotal = (float)(d.getQuantity() * d.getUnitPrice());
                sumaMateriales += subtotal;

                System.out.println("   * Material: " + d.getMaterial().getName());
                System.out.println("   * Cantidad: " + d.getQuantity());
                System.out.println("   * Precio U.: $" + d.getUnitPrice());
                System.out.println("   * Subtotal Mat.: $" + subtotal);
            }

            System.out.println("--------------------------------");
            System.out.println("-> CALCULO: " + q.getLaborCost() + " (M.O.) + " + sumaMateriales + " (Mat.)");
            System.out.println("-> TOTAL REGISTRADO: $" + q.getTotal());

           
            System.out.println("--------------------------------\n");
            index++;
        }
    }
}