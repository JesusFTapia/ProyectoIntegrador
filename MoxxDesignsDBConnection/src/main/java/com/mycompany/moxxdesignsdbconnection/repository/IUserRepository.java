/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.moxxdesignsdbconnection.repository;
import com.mycompany.moxxdesignsdbconnection.entitys.User;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author USER
 */
public interface IUserRepository {
    User save(User user); 
    Optional<User> findById(long id);
    Optional<User> findByUserName(String userName);
    List<User> findAll();
    void delete(User user);
    void deleteById(long id);
}
