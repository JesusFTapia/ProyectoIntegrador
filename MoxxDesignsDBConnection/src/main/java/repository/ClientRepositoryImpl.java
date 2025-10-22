/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;
import com.mycompany.moxxdesignsdbconnection.entitys.Client;
import javax.persistence.*;
import java.util.List;
import java.util.Optional;
import repository.IClientRepository;

public class ClientRepositoryImpl implements IClientRepository {
    private static EntityManagerFactory emf;
    
    static {
        emf = Persistence.createEntityManagerFactory("com.mycompany_MoxxDesignsDBConnection_jar_1.0-SNAPSHOTPU");
    }

    @Override
    public Client save(Client client) {
        EntityManager em = emf.createEntityManager();
        Client result = null;
        try {
            em.getTransaction().begin();
            if (client.getId() == null || client.getId() == 0 ) {
                em.persist(client);
                result = client;
            } else {
                result = em.merge(client);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al guardar/actualizar el cliente: " + e.getMessage(), e);
        } finally {
            em.close();
        }
        return result;
    }

    @Override
    public Optional<Client> findById(long id) {
        EntityManager em = emf.createEntityManager();
        Client client = null;
        try {
            client = em.find(Client.class, id);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return Optional.ofNullable(client);
    }
    
    @Override
    public Optional<Client> findByPhoneNumber(String phoneNumber) {
        EntityManager em = emf.createEntityManager();
        Client client = null;
        try {
            String jpql = "SELECT c FROM Client c WHERE c.phoneNumber = :phoneNumber";

            TypedQuery<Client> query = em.createQuery(jpql, Client.class);
        
            query.setParameter("phoneNumber", phoneNumber);
            
            client = query.getSingleResult();
        } catch (NoResultException e) {
            client = null; 
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return Optional.ofNullable(client);
    }

    @Override
    public List<Client> findAll() {
        EntityManager em = emf.createEntityManager();
        List<Client> clients = List.of();
        try {
            TypedQuery<Client> query = em.createQuery("SELECT c FROM Client c", Client.class);
            clients = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return clients;
    }

    @Override
    public void delete(Client client) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Client managedClient = em.contains(client) ? client : em.merge(client);
            em.remove(managedClient);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al borrar el cliente: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteById(long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Client client = em.find(Client.class, id);
            if (client != null) {
                em.remove(client);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al borrar el cliente por ID: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

}
