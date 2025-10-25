/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.moxxdesignsdbconnection.repository;

import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import com.mycompany.moxxdesignsdbconnection.entitys.User;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import com.mycompany.moxxdesignsdbconnection.repository.IUserRepository;

/**
 *
 * @author USER
 */
public class UserRepositoryImpl implements IUserRepository {

    private static EntityManagerFactory emf;

    static {
        emf = Persistence.createEntityManagerFactory("com.mycompany_MoxxDesignsDBConnection_jar_1.0-SNAPSHOTPU");
    }

    public UserRepositoryImpl() {
    }

    @Override
    public User save(User user) {
        EntityManager entityManager = emf.createEntityManager();
        User result = null;

        try {
            entityManager.getTransaction().begin();

            if (user.getId() == 0) {
                entityManager.persist(user);
                result = user;
            } else {
                result = entityManager.merge(user);
            }

            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Error al guardar/actualizar el usuario: " + e.getMessage(), e);
        } finally {
            entityManager.close();
        }
        return result;
    }

    @Override
    public Optional<User> findById(long id) {
        EntityManager entityManager = emf.createEntityManager();
        User user = null;
        
        try {
            user = entityManager.find(User.class, id);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
        
        return Optional.ofNullable(user);
    }
    
    @Override
    public Optional<User> findByUserName(String userName) {
        EntityManager em = emf.createEntityManager();
        User user = null;
        try {
            String jpql = "SELECT u FROM User u WHERE u.userName = :userName";

            TypedQuery<User> query = em.createQuery(jpql, User.class);
        
            query.setParameter("userName", userName);
            
            user = query.getSingleResult();
        } catch (NoResultException e) {
            user = null; 
        }catch (Exception e) {
            e.printStackTrace();
            
        } finally {
            em.close();
        }
        return Optional.ofNullable(user);
    }

    @Override
    public List<User> findAll() {
        EntityManager entityManager = emf.createEntityManager();
        List<User> users = List.of();
        
        try {
            TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u", User.class);
            users = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
        
        return users;
    }

    @Override
    public void delete(User user) {
        EntityManager entityManager = emf.createEntityManager();
        
        try {
            entityManager.getTransaction().begin();
            
            User managedUser = entityManager.contains(user) ? user : entityManager.merge(user);
            
            entityManager.remove(managedUser);
            
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Error al borrar el usuario: " + e.getMessage(), e);
        } finally {
            entityManager.close();
        }
    }
    
    @Override
    public void deleteById(long id) {
        EntityManager entityManager = emf.createEntityManager();
        
        try {
            entityManager.getTransaction().begin();
            
            User user = entityManager.find(User.class, id);
            
            if (user != null) {
                entityManager.remove(user);
            }
            
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Error al borrar el usuario por ID: " + e.getMessage(), e);
        } finally {
            entityManager.close();
        }
    }
}