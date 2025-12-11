/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.moxxdesignsdbconnection.repository;


import com.mycompany.moxxdesignsdbconnection.entitys.Quotation;
import java.util.List;
import java.util.Optional;

public interface IQuotationRepository {
    Quotation save(Quotation quotation);
    List<Quotation> findAll();
    Optional<Quotation> findById(long id);
    void delete(Quotation quotation);
}
