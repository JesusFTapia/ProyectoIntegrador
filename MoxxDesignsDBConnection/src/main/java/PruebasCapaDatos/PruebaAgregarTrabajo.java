/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package PruebasCapaDatos;

import com.mycompany.moxxdesignsdbconnection.entitys.Client;
import com.mycompany.moxxdesignsdbconnection.entitys.GeneralJob;
import com.mycompany.moxxdesignsdbconnection.entitys.Job;
import com.mycompany.moxxdesignsdbconnection.entitys.JobType;
import com.mycompany.moxxdesignsdbconnection.entitys.Material;
import com.mycompany.moxxdesignsdbconnection.entitys.Quotation;
import com.mycompany.moxxdesignsdbconnection.entitys.QuotationMaterialDetail;
import com.mycompany.moxxdesignsdbconnection.entitys.User;
import com.mycompany.moxxdesignsdbconnection.entitys.VehicularJob;
import com.mycompany.moxxdesignsdbconnection.exceptions.DuplicateException;
import com.mycompany.moxxdesignsdbconnection.exceptions.IncompleteDataException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.mycompany.moxxdesignsdbconnection.repository.ClientRepositoryImpl;
import com.mycompany.moxxdesignsdbconnection.repository.JobRepositoryImpl;
import com.mycompany.moxxdesignsdbconnection.repository.JobTypeRepositoryImpl;
import com.mycompany.moxxdesignsdbconnection.repository.MaterialRepositoryImpl;
import com.mycompany.moxxdesignsdbconnection.repository.UserRepositoryImpl;
import com.mycompany.moxxdesignsdbconnection.repository.IUserRepository;
import com.mycompany.moxxdesignsdbconnection.repository.IClientRepository;
import com.mycompany.moxxdesignsdbconnection.repository.IJobRepository;
import com.mycompany.moxxdesignsdbconnection.repository.IJobTypeRepository;
import com.mycompany.moxxdesignsdbconnection.repository.IMaterialRepository;
import com.mycompany.moxxdesignsdbconnection.services.ClientService;
import com.mycompany.moxxdesignsdbconnection.services.JobService;
import com.mycompany.moxxdesignsdbconnection.services.JobTypeService;
import com.mycompany.moxxdesignsdbconnection.services.MaterialService;
import com.mycompany.moxxdesignsdbconnection.services.UserService;

/**
 *
 * @author USER
 */
public class PruebaAgregarTrabajo {

    public static void main(String[] args) {
        
        
        IUserRepository userRepository=new UserRepositoryImpl();
        IClientRepository clientRepository=new ClientRepositoryImpl();
        IJobTypeRepository jobTypeRepository=new JobTypeRepositoryImpl();
        IMaterialRepository materialRepository= new MaterialRepositoryImpl();
        IJobRepository jobRepository=new JobRepositoryImpl();
        
        UserService userService=new UserService(userRepository);
        ClientService clientService=new ClientService(clientRepository);
        JobTypeService jobTypeService=new JobTypeService(jobTypeRepository);
        MaterialService materialService=new MaterialService(materialRepository);
        JobService jobService=new JobService(jobRepository);
        
        
        try{
            User usuario=userService.registerNewUser(new User("Admin", "Jesús", "Pedro2"));
//            
            Client cliente=clientService.registerNewClient(new Client("Jesús", "Tapia", "6443334444"));
            JobType tipoAuto=jobTypeService.registerNewJobType(new JobType("Auto", "Tiene 4 llantas"));
            //JobType tipoAuto=jobTypeService.registerNewJobType(new JobType("Auto", "Tiene 4 llantas"));
            JobType tipoCartel=jobTypeService.registerNewJobType(new JobType("Cartel", "Es un cartel"));
            //JobType tipoCartel=jobTypeService.registerNewJobType(new JobType("", "Es un cartel"));
            Material material = materialService.registerNewMaterial(new Material("Madera", "Es madera", 0));
            
            Quotation quotation=new Quotation(25000, new Date(), 250);
            QuotationMaterialDetail quotationMaterialDetail=new QuotationMaterialDetail(quotation,material, 1, 1);
            List<QuotationMaterialDetail> quotationMaterialDetails=new ArrayList<>();
            
            quotationMaterialDetails.add(quotationMaterialDetail);
            
            
            quotation.setQuotationMaterialDetails(quotationMaterialDetails);
            List<Quotation> quotations=new ArrayList<>();
            quotations.add(quotation);


            GeneralJob trabajoCartel=(GeneralJob) jobService.registerNewJob(new GeneralJob(new Date(), "activo", "descripcion",quotations, tipoCartel,usuario, cliente));
            //GeneralJob trabajoCartel=(GeneralJob) jobService.registerNewJob(new GeneralJob(new Date(), "activo", "descripcion",quotations, tipoCartel,usuario, cliente));

            VehicularJob trabajoCarro=(VehicularJob) jobService.registerNewJob(new VehicularJob("CRV", "Rojo", 2004, new Date(), "Activo", "descripcion",quotations, tipoAuto, usuario, cliente));
            //VehicularJob trabajoCarro=(VehicularJob) jobService.registerNewJob(new VehicularJob("CRV", "Rojo", 2004, new Date(), "Activo", "descripcion",quotations, tipoAuto, usuario, cliente));
            
            trabajoCartel.setState("Cancelado");
            Job job= jobService.editJob(trabajoCartel);
        }catch(DuplicateException e){
            System.out.println(e.getMessage());
        }catch(IncompleteDataException e){
            System.out.println(e.getMessage());
        }
        
        
        
    }
    
    
    
}
