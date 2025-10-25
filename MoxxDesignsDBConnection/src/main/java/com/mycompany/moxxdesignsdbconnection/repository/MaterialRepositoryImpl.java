/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.moxxdesignsdbconnection.repository;
import com.mycompany.moxxdesignsdbconnection.entitys.Material;
import javax.persistence.*;
import java.util.List;
import java.util.Optional;
import com.mycompany.moxxdesignsdbconnection.repository.IMaterialRepository;

public class MaterialRepositoryImpl implements IMaterialRepository {
    private static EntityManagerFactory emf;
    
    static {
        emf = Persistence.createEntityManagerFactory("com.mycompany_MoxxDesignsDBConnection_jar_1.0-SNAPSHOTPU");
    }

    @Override
    public Material save(Material material) {
        EntityManager em = emf.createEntityManager();
        Material result = null;
        try {
            em.getTransaction().begin();
            if (material.getId() == 0) {
                em.persist(material);
                result = material;
            } else {
                result = em.merge(material);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al guardar/actualizar el material: " + e.getMessage(), e);
        } finally {
            em.close();
        }
        return result;
    }

    @Override
    public Optional<Material> findById(long id) {
        EntityManager em = emf.createEntityManager();
        Material material = null;
        try {
            material = em.find(Material.class, id);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return Optional.ofNullable(material);
    }
    
    @Override
    public Optional<Material> findByName(String name) {
        EntityManager em = emf.createEntityManager();
        Material material = null;
        try {
            String jpql = "SELECT m FROM Material m WHERE m.name = :name";

            TypedQuery<Material> query = em.createQuery(jpql, Material.class);
        
            query.setParameter("name", name);
            
            material = query.getSingleResult();
        } catch (NoResultException e) {
            material = null; 
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return Optional.ofNullable(material);
    }

    @Override
    public List<Material> findAll() {
        EntityManager em = emf.createEntityManager();
        List<Material> materials = List.of();
        try {
            TypedQuery<Material> query = em.createQuery("SELECT m FROM Material m", Material.class);
            materials = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return materials;
    }

    @Override
    public void delete(Material material) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Material managedMaterial = em.contains(material) ? material : em.merge(material);
            em.remove(managedMaterial);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al borrar el material: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteById(long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Material material = em.find(Material.class, id);
            if (material != null) {
                em.remove(material);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al borrar el material por ID: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
