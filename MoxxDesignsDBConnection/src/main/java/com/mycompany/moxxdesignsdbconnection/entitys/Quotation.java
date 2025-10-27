/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.moxxdesignsdbconnection.entitys;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author USER
 */
@Entity
@Table(name="quotations")
public class Quotation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @Column(nullable = false)
    private float total;
    
    @Column(nullable = false)
    private Date emisionDate;
    
    @Column(nullable = false)
    private float laborCost;
    
    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

    @OneToMany(mappedBy = "quotation", cascade = CascadeType.ALL)
    private List<QuotationMaterialDetail> quotationMaterialDetails;
    
    public Quotation() {
    }

    public Quotation(float total, Date emisionDate, float laborCost) {
        this.total = total;
        this.emisionDate = emisionDate;
        this.laborCost = laborCost;
    }

    public Quotation(long id, float total, Date emisionDate, float laborCost, Job job) {
        this.id = id;
        this.total = total;
        this.emisionDate = emisionDate;
        this.laborCost = laborCost;
        this.job = job;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public Date getEmisionDate() {
        return emisionDate;
    }

    public void setEmisionDate(Date emisionDate) {
        this.emisionDate = emisionDate;
    }

    public float getLaborCost() {
        return laborCost;
    }

    public void setLaborCost(float laborCost) {
        this.laborCost = laborCost;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public List<QuotationMaterialDetail> getQuotationMaterialDetails() {
        return quotationMaterialDetails;
    }

    public void setQuotationMaterialDetails(List<QuotationMaterialDetail> quotationMaterialDetails) {
        this.quotationMaterialDetails = quotationMaterialDetails;
    }

    @Override
    public String toString() {
        return "Quotation{" + "id=" + id + ", total=" + total + ", emisionDate=" + emisionDate + ", laborCost=" + laborCost + ", job=" + job + ", quotationMaterialDetails=" + quotationMaterialDetails + '}';
    }

    

    
    
    
    
}
