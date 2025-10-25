/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.moxxdesignsdbconnection.repository;
import com.mycompany.moxxdesignsdbconnection.entitys.JobType;
import javax.persistence.*;
import java.util.List;
import java.util.Optional;
import com.mycompany.moxxdesignsdbconnection.repository.IJobTypeRepository;

public class JobTypeRepositoryImpl implements IJobTypeRepository {
    private static EntityManagerFactory emf;
    
    static {
        emf = Persistence.createEntityManagerFactory("com.mycompany_MoxxDesignsDBConnection_jar_1.0-SNAPSHOTPU");
    }

    @Override
    public JobType save(JobType jobType) {
        EntityManager em = emf.createEntityManager();
        JobType result = null;
        try {
            em.getTransaction().begin();
            if (jobType.getId() == 0) {
                em.persist(jobType);
                result = jobType;
            } else {
                result = em.merge(jobType);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al guardar/actualizar el tipo de trabajo: " + e.getMessage(), e);
        } finally {
            em.close();
        }
        return result;
    }

    @Override
    public Optional<JobType> findById(long id) {
        EntityManager em = emf.createEntityManager();
        JobType jobType = null;
        try {
            jobType = em.find(JobType.class, id);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return Optional.ofNullable(jobType);
    }
    
    @Override
    public Optional<JobType> findByName(String name) {
        EntityManager em = emf.createEntityManager();
        JobType jobType = null;
        try {
            String jpql = "SELECT j FROM JobType j WHERE j.name = :name";

            TypedQuery<JobType> query = em.createQuery(jpql, JobType.class);
        
            query.setParameter("name", name);
            
            jobType = query.getSingleResult();
        } catch (NoResultException e) {
            jobType = null; 
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return Optional.ofNullable(jobType);
    }

    @Override
    public List<JobType> findAll() {
        EntityManager em = emf.createEntityManager();
        List<JobType> jobTypes = List.of();
        try {
            TypedQuery<JobType> query = em.createQuery("SELECT jt FROM JobType jt", JobType.class);
            jobTypes = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return jobTypes;
    }

    @Override
    public void delete(JobType jobType) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            JobType managedJobType = em.contains(jobType) ? jobType : em.merge(jobType);
            em.remove(managedJobType);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al borrar el tipo de trabajo: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteById(long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            JobType jobType = em.find(JobType.class, id);
            if (jobType != null) {
                em.remove(jobType);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al borrar el tipo de trabajo por ID: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
