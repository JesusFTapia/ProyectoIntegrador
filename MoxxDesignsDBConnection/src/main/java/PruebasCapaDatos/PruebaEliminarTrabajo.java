/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PruebasCapaDatos;

import com.mycompany.moxxdesignsdbconnection.entitys.Job;
import java.util.Optional;
import repository.JobRepositoryImpl;
import repository.IJobRepository;

/**
 *
 * @author USER
 */
public class PruebaEliminarTrabajo {
    public static void main (String[]args){
        IJobRepository jobRepository=new JobRepositoryImpl();
        Job job=jobRepository.findById(1).get();
        job.setState("Descartado/Cancelado");
        jobRepository.save(job);
        
    }
}
