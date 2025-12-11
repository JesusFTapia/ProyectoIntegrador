/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.moxxdesignsdbconnection.services;

import com.mycompany.moxxdesignsdbconnection.repository.ClientRepositoryImpl;
import com.mycompany.moxxdesignsdbconnection.repository.IClientRepository;
import com.mycompany.moxxdesignsdbconnection.repository.IJobRepository;
import com.mycompany.moxxdesignsdbconnection.repository.IJobTypeRepository;
import com.mycompany.moxxdesignsdbconnection.repository.IMaterialRepository;
import com.mycompany.moxxdesignsdbconnection.repository.JobRepositoryImpl;
import com.mycompany.moxxdesignsdbconnection.repository.JobTypeRepositoryImpl;
import com.mycompany.moxxdesignsdbconnection.repository.MaterialRepositoryImpl;
import com.mycompany.moxxdesignsdbconnection.entitys.Client;
import com.mycompany.moxxdesignsdbconnection.entitys.User;
import com.mycompany.moxxdesignsdbconnection.entitys.Material;
import com.mycompany.moxxdesignsdbconnection.entitys.Job;
import com.mycompany.moxxdesignsdbconnection.entitys.JobType;
import com.mycompany.moxxdesignsdbconnection.entitys.Quotation;
import com.mycompany.moxxdesignsdbconnection.repository.IQuotationRepository;
import com.mycompany.moxxdesignsdbconnection.repository.IUserRepository;
import com.mycompany.moxxdesignsdbconnection.repository.QuotationRepositoryImpl;
import com.mycompany.moxxdesignsdbconnection.repository.UserRepositoryImpl;
import java.util.List;

/**
 *
 * @author USER
 */
public class QuotationService {
    private ClientService clientService;
    private JobService jobService;
    private JobTypeService jobTypeService;
    private MaterialService materialService;
    private IQuotationRepository quotationRepository;
    
    public QuotationService(){
        IClientRepository clientRepository=new ClientRepositoryImpl();
        IJobRepository jobRepository=new JobRepositoryImpl();
        IJobTypeRepository jobTypeRepository=new JobTypeRepositoryImpl();
        IMaterialRepository materialRepository=new MaterialRepositoryImpl();
        clientService=new ClientService(clientRepository);
        jobService=new JobService(jobRepository);
        jobTypeService =new JobTypeService(jobTypeRepository);
        materialService = new MaterialService(materialRepository);
        this.quotationRepository = new QuotationRepositoryImpl();
    }
    
    public List<Quotation> getAllQuotations() {
        // Asegúrate de que tu repositorio tenga un método findAll() o similar
        return quotationRepository.findAll(); 
    }

    public void save(Quotation quotation) throws Exception {
        try {
            // Asegúrate de que tu repositorio tenga un método save()
            quotationRepository.save(quotation); 
        } catch (Exception e) {
            throw new Exception("Error al guardar la cotización: " + e.getMessage());
        }
    }
    
    public User getUser1(){
        try{
            IUserRepository userRepository=new UserRepositoryImpl();
            UserService userService=new UserService(userRepository);
            return userService.getAllUsers().get(0);
        }catch(Exception e){
            System.out.println("Error en el usuario");
        }
        return null;
    }
    
    public Job registerNewJob(Job job) throws Exception{
        try{
            return jobService.registerNewJob(job);
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }
    public Client registerNewClient(Client client) throws Exception{
        try{
            return clientService.registerNewClient(client);
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }
    public List<Client> getAllClients(){
        return clientService.getAllClients();
    }
    
    public List<Material> getAllMaterials(){
        return materialService.getAllMaterials();
    }
    public List<JobType> getAllJobTypes(){
        return jobTypeService.getAllJobTypes();
    }
    
    
}
