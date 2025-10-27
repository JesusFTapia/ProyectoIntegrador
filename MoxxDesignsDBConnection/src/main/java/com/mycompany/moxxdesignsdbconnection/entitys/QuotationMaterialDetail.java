/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.moxxdesignsdbconnection.entitys;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author USER
 */
@Entity
@Table(name="quotationMaterialDetails")
public class QuotationMaterialDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @ManyToOne
    @JoinColumn(name = "quotation_id", nullable = false)
    private Quotation quotation;
    
    @ManyToOne
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;
    
    @Column(nullable = false)
    private int quantity;
    
    @Column(nullable = false)
    private double unitPrice;

    public QuotationMaterialDetail() {
    }

    public QuotationMaterialDetail(Material material, int quantity, double unitPrice) {
        this.material = material;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public QuotationMaterialDetail(Quotation quotation, Material material, int quantity, double unitPrice) {
        this.quotation = quotation;
        this.material = material;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    
    public QuotationMaterialDetail(long id, Quotation quotation, Material material, int quantity, double unitPrice) {
        this.id = id;
        this.quotation = quotation;
        this.material = material;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
    
    

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Quotation getQuotation() {
        return quotation;
    }

    public void setQuotation(Quotation quotation) {
        this.quotation = quotation;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Override
    public String toString() {
        return "QuotationMaterialDetail{" + "id=" + id + ", material=" + material + ", quantity=" + quantity + ", unitPrice=" + unitPrice + '}';
    }

    
    
    

   
    
    
}
