/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;
import com.mycompany.moxxdesignsdbconnection.entitys.JobType;
import java.util.List;
import java.util.Optional;

public interface IJobTypeRepository {
    JobType save(JobType jobType);
    Optional<JobType> findById(long id);
    Optional<JobType> findByName(String name);
    List<JobType> findAll();
    void delete(JobType jobType);
    void deleteById(long id);
}
