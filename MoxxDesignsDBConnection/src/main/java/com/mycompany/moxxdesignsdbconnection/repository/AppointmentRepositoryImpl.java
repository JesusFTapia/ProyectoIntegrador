/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.moxxdesignsdbconnection.repository;
import com.mycompany.moxxdesignsdbconnection.entitys.Appointment;
import javax.persistence.*;
import java.util.List;
import java.util.Optional;
import com.mycompany.moxxdesignsdbconnection.repository.IAppointmentRepository;

public class AppointmentRepositoryImpl implements IAppointmentRepository {
    private static EntityManagerFactory emf;
    
    static {
        emf = Persistence.createEntityManagerFactory("com.mycompany_MoxxDesignsDBConnection_jar_1.0-SNAPSHOTPU");
    }

    @Override
    public Appointment save(Appointment appointment) {
        EntityManager em = emf.createEntityManager();
        Appointment result = null;
        try {
            em.getTransaction().begin();
            if (appointment.getId() == 0) {
                em.persist(appointment);
                result = appointment;
            } else {
                result = em.merge(appointment);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al guardar/actualizar la cita: " + e.getMessage(), e);
        } finally {
            em.close();
        }
        return result;
    }

    @Override
    public Optional<Appointment> findById(long id) {
        EntityManager em = emf.createEntityManager();
        Appointment appointment = null;
        try {
            appointment = em.find(Appointment.class, id);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return Optional.ofNullable(appointment);
    }

    @Override
    public List<Appointment> findAll() {
        EntityManager em = emf.createEntityManager();
        List<Appointment> appointments = List.of();
        try {
            TypedQuery<Appointment> query = em.createQuery("SELECT a FROM Appointment a", Appointment.class);
            appointments = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return appointments;
    }

    @Override
    public void delete(Appointment appointment) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Appointment managedAppointment = em.contains(appointment) ? appointment : em.merge(appointment);
            em.remove(managedAppointment);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al borrar la cita: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteById(long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Appointment appointment = em.find(Appointment.class, id);
            if (appointment != null) {
                em.remove(appointment);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al borrar la cita por ID: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}