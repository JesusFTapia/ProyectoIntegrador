/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.moxxdesignsdbconnection.repository;
import com.mycompany.moxxdesignsdbconnection.entitys.Client;
import java.util.List;
import java.util.Optional;

public interface IClientRepository {
    Client save(Client client);
    Optional<Client> findById(long id);
    Optional<Client> findByPhoneNumber(String phoneNumber);
    List<Client> findAll();
    void delete(Client client);
    void deleteById(long id);
}
