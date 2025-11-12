/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.moxxdesignsdbconnection.repository;
import com.mycompany.moxxdesignsdbconnection.entitys.Job;
import javax.persistence.*;
import java.util.List;
import java.util.Optional;
import com.mycompany.moxxdesignsdbconnection.repository.IJobRepository;

public class JobRepositoryImpl implements IJobRepository {
    private static EntityManagerFactory emf;
    
    static {
        emf = Persistence.createEntityManagerFactory("com.mycompany_MoxxDesignsDBConnection_jar_1.0-SNAPSHOTPU");
    }

    @Override
    public Job save(Job job) {
        EntityManager em = emf.createEntityManager();
        Job result = null;
        try {
            em.getTransaction().begin();
            if (job.getId() == 0) {
                em.persist(job);
                result = job;
            } else {
                result = em.merge(job);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al guardar/actualizar el trabajo: " + e.getMessage(), e);
        } finally {
            em.close();
        }
        return result;
    }

    @Override
    public Optional<Job> findById(long id) {
        EntityManager em = emf.createEntityManager();
        Job job = null;
        try {
            job = em.find(Job.class, id);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return Optional.ofNullable(job);
    }

    @Override
    public List<Job> findAll() {
        EntityManager em = emf.createEntityManager();
        List<Job> jobs = List.of();
        try {
            TypedQuery<Job> query = em.createQuery(
            "SELECT DISTINCT j FROM Job j " +
            "LEFT JOIN FETCH j.quotations q " +
            "LEFT JOIN FETCH q.quotationMaterialDetails qmd " +
            "LEFT JOIN FETCH qmd.material " +
            "LEFT JOIN FETCH j.client " +
            "LEFT JOIN FETCH j.jobType", 
            Job.class
        );
            jobs = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return jobs;
    }
    
    @Override
    public void changeState(long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Job job = em.find(Job.class, id);
            if (job != null) {
                job.setState("Cancelado");
                em.merge(job);
            } else {
                throw new RuntimeException("No se encontró el trabajo con ID: " + id);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al cambiar el estado del trabajo: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }


    @Override
    public void delete(Job job) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Job managedJob = em.contains(job) ? job : em.merge(job);
            em.remove(managedJob);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al borrar el trabajo: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteById(long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Job job = em.find(Job.class, id);
            if (job != null) {
                em.remove(job);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al borrar el trabajo por ID: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}