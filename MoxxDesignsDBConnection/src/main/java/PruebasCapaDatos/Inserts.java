/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PruebasCapaDatos;

import com.mycompany.moxxdesignsdbconnection.entitys.Client;
import com.mycompany.moxxdesignsdbconnection.entitys.JobType;
import com.mycompany.moxxdesignsdbconnection.entitys.Material;
import com.mycompany.moxxdesignsdbconnection.entitys.User;
import com.mycompany.moxxdesignsdbconnection.repository.ClientRepositoryImpl;
import com.mycompany.moxxdesignsdbconnection.repository.IClientRepository;
import com.mycompany.moxxdesignsdbconnection.repository.IJobTypeRepository;
import com.mycompany.moxxdesignsdbconnection.repository.IMaterialRepository;
import com.mycompany.moxxdesignsdbconnection.repository.IUserRepository;
import com.mycompany.moxxdesignsdbconnection.repository.JobTypeRepositoryImpl;
import com.mycompany.moxxdesignsdbconnection.repository.MaterialRepositoryImpl;
import com.mycompany.moxxdesignsdbconnection.repository.UserRepositoryImpl;
import com.mycompany.moxxdesignsdbconnection.services.ClientService;
import com.mycompany.moxxdesignsdbconnection.services.JobTypeService;
import com.mycompany.moxxdesignsdbconnection.services.MaterialService;
import com.mycompany.moxxdesignsdbconnection.services.UserService;

/**
 *
 * @author USER
 */
public class Inserts {
    public static void main(String[]args){
       try{
           IUserRepository userRepository=new UserRepositoryImpl();
           UserService userService=new UserService(userRepository);
          User usuario=userService.registerNewUser(new User("Admin", "Jesús", "Pedro2"));
        IClientRepository clientRepository=new ClientRepositoryImpl();
        ClientService clientService =new ClientService(clientRepository);
        clientService.registerNewClient(new Client("Jesús", "Tapia", "6443334444"));
        clientService.registerNewClient(new Client("María", "López", "6625551234"));
        clientService.registerNewClient(new Client("Carlos", "Gómez", "3318887777"));
        clientService.registerNewClient(new Client("Ana", "Rodríguez", "5549996666"));
        clientService.registerNewClient(new Client("Javier", "Pérez", "8121115555"));
        clientService.registerNewClient(new Client("Sofía", "Martínez", "4497772222"));
        clientService.registerNewClient(new Client("Ricardo", "Sánchez", "9982228888"));
        
        IJobTypeRepository jobTypeRepository=new JobTypeRepositoryImpl();
        JobTypeService jobTypeService=new JobTypeService(jobTypeRepository);
        jobTypeService.registerNewJobType(new JobType("Auto", "Son autos"));
        jobTypeService.registerNewJobType(new JobType("Carteles", "Cartel"));
        
        IMaterialRepository materialRepository=new MaterialRepositoryImpl();
        MaterialService materialService=new MaterialService(materialRepository);
        materialService.registerNewMaterial(new Material("Madera", "Es madera", 5,"m",2.5f));
        materialService.registerNewMaterial(new Material("Acero Inoxidable", "Aleación de hierro y cromo", 100, "kg", 8.0f));
        materialService.registerNewMaterial(new Material("Madera de Pino", "Ideal para construcción ligera", 50, "m³", 0.5f));
        materialService.registerNewMaterial(new Material("Cemento Portland", "Ligante hidráulico para hormigón", 150, "saco", 40.0f));
        materialService.registerNewMaterial(new Material("Vidrio Templado", "Resistente a impactos y altas temperaturas", 10, "unidad", 15.0f));
        materialService.registerNewMaterial(new Material("Ladrillo Rojo", "Elemento cerámico para muros", 500, "unidad", 1.5f));
        materialService.registerNewMaterial(new Material("Tubo de PVC", "Plástico para fontanería y desagües", 20, "m", 0.3f));
        materialService.registerNewMaterial(new Material("Pintura Acrílica", "Revestimiento para paredes y techos", 5, "galón", 4.0f));
        materialService.registerNewMaterial(new Material("Cable de Cobre", "Conductor eléctrico de alta eficiencia", 50, "m", 0.1f));
        materialService.registerNewMaterial(new Material("Arena Fina", "Agregado para mortero y revoques", 200, "ton", 1000.0f));
        materialService.registerNewMaterial(new Material("Placa de Yeso", "Panel para tabiquería seca", 30, "m²", 7.0f));
    }catch(Exception e){
           System.out.println("Inserts Fallidos");
    }   
    }
        
    
    
    
    
    
}
