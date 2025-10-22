/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import com.mycompany.moxxdesignsdbconnection.entitys.JobType;
import exceptions.DuplicateException;
import exceptions.IncompleteDataException;
import java.util.List;
import java.util.Optional;
import repository.IJobTypeRepository;
/**
 *
 * @author USER
 */
public class JobTypeService {

    private final IJobTypeRepository jobTypeRepository;

    public JobTypeService(IJobTypeRepository repo) {
        this.jobTypeRepository = repo;
    }

    public JobType registerNewJobType(JobType jobType) throws DuplicateException, IncompleteDataException {
        
        if (invalidJobType(jobType)) {
            throw new IncompleteDataException("El tipo de trabajo debe tener un nombre y una descripción.");
        }

        Optional<JobType> existingJobType = jobTypeRepository.findByName(jobType.getName());
        if (existingJobType.isPresent()) {
            throw new DuplicateException("Ya existe un tipo de trabajo con el nombre: " + jobType.getName());
        }

        return jobTypeRepository.save(jobType);
    }

    public List<JobType> getAllJobTypes() {
        return jobTypeRepository.findAll();
    }

    private boolean invalidJobType(JobType jobType) {
        if (jobType.getName() == null || jobType.getName().trim().isEmpty() ||
            jobType.getDescripcion() == null || jobType.getDescripcion().trim().isEmpty()) {
            return true;
        }
        return false;
    }
}