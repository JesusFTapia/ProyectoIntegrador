/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PruebasCapaDatos;

import com.mycompany.moxxdesignsdbconnection.entitys.Client;
import com.mycompany.moxxdesignsdbconnection.repository.ClientRepositoryImpl;
import com.mycompany.moxxdesignsdbconnection.repository.IClientRepository;
import com.mycompany.moxxdesignsdbconnection.services.ClientService;

/**
 *
 * @author USER
 */
public class ClientsInsert {
    public static void main(String[]args){
       try{
        IClientRepository clientRepository=new ClientRepositoryImpl();
        ClientService clientService =new ClientService(clientRepository);
        clientService.registerNewClient(new Client("Jesús", "Tapia", "6443334444"));
        clientService.registerNewClient(new Client("María", "López", "6625551234"));
        clientService.registerNewClient(new Client("Carlos", "Gómez", "3318887777"));
        clientService.registerNewClient(new Client("Ana", "Rodríguez", "5549996666"));
        clientService.registerNewClient(new Client("Javier", "Pérez", "8121115555"));
        clientService.registerNewClient(new Client("Sofía", "Martínez", "4497772222"));
        clientService.registerNewClient(new Client("Ricardo", "Sánchez", "9982228888"));
        
    }catch(Exception e){
           System.out.println("Inserts Fallidos");
    }   
    }
        
    
    
    
    
    
}
