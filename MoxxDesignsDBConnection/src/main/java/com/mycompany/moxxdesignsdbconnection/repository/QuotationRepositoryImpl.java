/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.moxxdesignsdbconnection.repository;


import com.mycompany.moxxdesignsdbconnection.entitys.Quotation;
import javax.persistence.*;
import java.util.List;
import java.util.Optional;

public class QuotationRepositoryImpl implements IQuotationRepository {

    private static EntityManagerFactory emf;

    static {
        emf = Persistence.createEntityManagerFactory("com.mycompany_MoxxDesignsDBConnection_jar_1.0-SNAPSHOTPU");
    }

    @Override
    public Quotation save(Quotation quotation) {
        EntityManager em = emf.createEntityManager();
        Quotation result = null;
        try {
            em.getTransaction().begin();
            if (quotation.getId() == 0) {
                em.persist(quotation);
                result = quotation;
            } else {
                result = em.merge(quotation);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al guardar/actualizar la cotización: " + e.getMessage(), e);
        } finally {
            em.close();
        }
        return result;
    }

    @Override
    public List<Quotation> findAll() {
        EntityManager em = emf.createEntityManager();
        List<Quotation> quotations = List.of();
        try {
            TypedQuery<Quotation> query = em.createQuery(
                "SELECT DISTINCT q FROM Quotation q " +
                "LEFT JOIN FETCH q.job j " +
                "LEFT JOIN FETCH j.client c " +
                "LEFT JOIN FETCH q.quotationMaterialDetails qmd " +
                "LEFT JOIN FETCH qmd.material", 
                Quotation.class
            );
            quotations = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return quotations;
    }

    @Override
    public Optional<Quotation> findById(long id) {
        EntityManager em = emf.createEntityManager();
        Quotation quotation = null;
        try {
            quotation = em.find(Quotation.class, id);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return Optional.ofNullable(quotation);
    }

    @Override
    public void delete(Quotation quotation) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Quotation managedQuotation = em.contains(quotation) ? quotation : em.merge(quotation);
            em.remove(managedQuotation);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al eliminar la cotización: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
