/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PruebasCapaDatos;

import com.mycompany.moxxdesignsdbconnection.entitys.Job;
import repository.JobRepositoryImpl;
import repository.IJobRepository;

/**
 *
 * @author USER
 */
public class PruebaEditarTrabajo {
    public static void main (String[]args){
        IJobRepository jobRepository=new JobRepositoryImpl();
        Job job=jobRepository.findById(1).get();
        job.setDescription("Estamos cambiando la descripcion");
        jobRepository.save(job);
        
    }
}
