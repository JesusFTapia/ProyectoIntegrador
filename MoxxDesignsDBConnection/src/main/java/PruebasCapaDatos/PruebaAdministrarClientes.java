/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PruebasCapaDatos;

import com.mycompany.moxxdesignsdbconnection.entitys.Client;
import com.mycompany.moxxdesignsdbconnection.repository.ClientRepositoryImpl;
import com.mycompany.moxxdesignsdbconnection.repository.IClientRepository;
import java.util.List;

public class PruebaAdministrarClientes {

    public static void main(String[] args) {
        IClientRepository clientRepo = new ClientRepositoryImpl();

        System.out.println("=== PRUEBA DE REPOSITORIO: CLIENTE ===");

        // 1. GUARDAR 
        System.out.println("\n[1] Guardando nuevo cliente...");
        Client cliente = new Client();
        
        cliente.setName("Maria"); 
        cliente.setLastName("Lopez"); 
        
        cliente.setPhoneNumber("555-6789");
        
        try {
            clientRepo.save(cliente); 
            System.out.println("Cliente guardado en BD.");
        } catch (Exception e) {
            System.out.println("Error al guardar: " + e.getMessage());
            e.printStackTrace();
            return; 
        }

        // 2. LISTAR 
        System.out.println("\n[2] Consultando clientes...");
        List<Client> lista = clientRepo.findAll();
        
        Client clienteRecuperado = null;
        for (Client c : lista) {
            System.out.println(" - ID: " + c.getId() + " | Nombre: " + c.getName() + " " + c.getLastName());
            
            if (c.getName().equals("Maria") && c.getLastName().equals("Lopez")) {
                clienteRecuperado = c;
            }
        }

        // 3. ACTUALIZAR 
        if (clienteRecuperado != null) {
            System.out.println("\n[3] Actualizando cliente ID: " + clienteRecuperado.getId());
            clienteRecuperado.setName("Maria Fernanda"); 
            
            Client clientAct = clientRepo.save(clienteRecuperado); 
            System.out.println("Cliente actualizado.");
            System.out.println(" - ID: " + clientAct.getId() + " | Nombre: " + clientAct.getName() + " " + clientAct.getLastName());
        }
    }
}