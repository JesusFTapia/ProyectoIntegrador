/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.moxxdesignsdbconnection.services;

import com.mycompany.moxxdesignsdbconnection.repository.IMaterialRepository;
import com.mycompany.moxxdesignsdbconnection.entitys.Material;
import com.mycompany.moxxdesignsdbconnection.exceptions.DuplicateException;
import com.mycompany.moxxdesignsdbconnection.exceptions.IncompleteDataException;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author USER
 */
public class MaterialService {
    private final IMaterialRepository materialRepository;

    public MaterialService(IMaterialRepository repo) {
        this.materialRepository = repo;
    }

    public Material registerNewMaterial(Material material) throws DuplicateException, IncompleteDataException {
        
        if (invalidMaterial(material)) {
            throw new IncompleteDataException("El material debe tener todos sus datos.");
        }

        Optional<Material> existingMaterial = materialRepository.findByName(material.getName());
        
        if (existingMaterial.isPresent()) {
            throw new DuplicateException("El material con el nombre '" + material.getName() + "' ya está registrado.");
        }

        return materialRepository.save(material);
    }

    public List<Material> getAllMaterials() {
        return materialRepository.findAll();
    }

    private boolean invalidMaterial(Material material) {
        // Validación de name y quantity (asumo quantity debe ser >= 0)
        if (material.getName() == null || material.getName().trim().isEmpty() ||
            material.getQuantity() < 0 || material.getUnitType().trim().isEmpty() ||
            material.getPrice() < 0) {
            return true;
        } else {
            return false;
        }
    }
}
