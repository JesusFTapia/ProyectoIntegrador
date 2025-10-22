/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;
import com.mycompany.moxxdesignsdbconnection.entitys.Job;
import java.util.List;
import java.util.Optional;

public interface IJobRepository {
    Job save(Job job);
    Optional<Job> findById(long id);
    List<Job> findAll();
    void delete(Job job);
    void deleteById(long id);
}
