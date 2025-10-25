/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.moxxdesignsdbconnection.repository;
import com.mycompany.moxxdesignsdbconnection.entitys.Material;
import java.util.List;
import java.util.Optional;

public interface IMaterialRepository {
    Material save(Material material);
    Optional<Material> findById(long id);
    Optional<Material> findByName(String name);
    List<Material> findAll();
    void delete(Material material);
    void deleteById(long id);
}