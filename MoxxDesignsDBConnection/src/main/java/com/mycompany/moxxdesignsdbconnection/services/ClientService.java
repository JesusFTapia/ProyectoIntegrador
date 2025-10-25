/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.moxxdesignsdbconnection.services;

import com.mycompany.moxxdesignsdbconnection.exceptions.IncompleteDataException;
import com.mycompany.moxxdesignsdbconnection.exceptions.DuplicateException;
import com.mycompany.moxxdesignsdbconnection.repository.IClientRepository;
import com.mycompany.moxxdesignsdbconnection.entitys.Client;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author USER
 */
public class ClientService {
    
    private final IClientRepository clientRepository; 
    
    public ClientService(IClientRepository repo) {
        this.clientRepository = repo; 
    }

    public Client registerNewClient(Client client) throws DuplicateException, IncompleteDataException {
       if(invalidClient(client)){
           throw new IncompleteDataException("El cliente debe tener todos sus datos.");
       }
       Optional<Client> existingClient = clientRepository.findByPhoneNumber(client.getPhoneNumber());
       if(existingClient.isPresent()){
           throw new DuplicateException("El cliente con este número ya está registrado.");
       }
        return clientRepository.save(client);
    }
    
    public List<Client> getAllClients(){
        List<Client> clients = clientRepository.findAll();
        return clients;
    }
    
    private boolean invalidClient(Client client){
        if(client.getName()==null || client.getName().trim().isEmpty() ||
           client.getLastName()==null || client.getLastName().trim().isEmpty() ||
           client.getPhoneNumber()==null || client.getPhoneNumber().trim().isEmpty()){
            return true;
        }else{
            return false;
        }
    }
}
