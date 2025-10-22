/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;
import com.mycompany.moxxdesignsdbconnection.entitys.Appointment;
import java.util.List;
import java.util.Optional;

public interface IAppointmentRepository {
    Appointment save(Appointment appointment);
    Optional<Appointment> findById(long id);
    List<Appointment> findAll();
    void delete(Appointment appointment);
    void deleteById(long id);
}
