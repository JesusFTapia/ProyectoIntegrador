
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.moxxdesignsdbconnection.services;

/**
 *
 * @author USER
 */
import com.mycompany.moxxdesignsdbconnection.exceptions.IncompleteDataException;
import com.mycompany.moxxdesignsdbconnection.entitys.Job;
import com.mycompany.moxxdesignsdbconnection.entitys.VehicularJob;
import java.util.List;
import com.mycompany.moxxdesignsdbconnection.repository.IJobRepository;

public class JobService {

    private final IJobRepository jobRepository;

    public JobService(IJobRepository repo) {
        this.jobRepository = repo;
    }

    public Job registerNewJob(Job job) throws IncompleteDataException {
        if (invalidJob(job)) {
            throw new IncompleteDataException("El trabajo debe tener todos sus datos, incluyendo los específicos para su tipo (General o Vehicular).");
        }
        return jobRepository.save(job);
    }
    
    public Job editJob(Job job) throws IncompleteDataException {
        if (invalidJob(job)) {
            throw new IncompleteDataException("El trabajo debe tener todos sus datos, incluyendo los específicos para su tipo (General o Vehicular).");
        }
        return jobRepository.save(job);
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    private boolean invalidJob(Job job) {
        if (job instanceof VehicularJob) {
            VehicularJob vehicularJob = (VehicularJob) job;
            if (vehicularJob.getModel() == null || vehicularJob.getModel().trim().isEmpty()
                    || vehicularJob.getColor() == null || vehicularJob.getColor().trim().isEmpty()
                    || vehicularJob.getYear() <= 0) {
                return true; 
            }
        }
        if (job.getDeliveryDate() == null
                || job.getState() == null || job.getState().trim().isEmpty()
                || job.getDescription() == null || job.getDescription().trim().isEmpty()
                || job.getJobType() == null
                || job.getUser() == null
                || job.getClient() == null
                || job.getQuotations() == null || job.getQuotations().isEmpty()
                || job.getQuotations().get(0).getEmisionDate() == null
                || job.getQuotations().get(0).getLaborCost() == 0
                || job.getQuotations().get(0).getTotal() == 0
                || job.getQuotations().get(0).getQuotationMaterialDetails().isEmpty()) {

            return true; 
        }

        return false;
    }
}
