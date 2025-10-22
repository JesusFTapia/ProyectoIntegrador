/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.moxxdesignsdbconnection.entitys;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 *
 * @author USER
 */
@Entity
@Table(name="vehicularjobs")
@PrimaryKeyJoinColumn(name = "job_id")
public class VehicularJob extends Job implements Serializable {

    private String model;
    
    private String color;
    
    private int year;

    public VehicularJob() {
    }
    
    public VehicularJob(String model, String color, int year) {
        this.model = model;
        this.color = color;
        this.year = year;
    }

    public VehicularJob(String model, String color, int year, Date deliveryDate, String state, String description, List<Quotation> quotations, JobType jobType, User user, Client client) {
        super(deliveryDate, state, description, quotations, jobType, user, client);
        this.model = model;
        this.color = color;
        this.year = year;
    }
    
    public VehicularJob(String model, String color, int year, long id, Date deliveryDate, String state, String description, List<Quotation> quotations, JobType jobType, User user, Client client) {
        super(id, deliveryDate, state, description, quotations, jobType, user, client);
        this.model = model;
        this.color = color;
        this.year = year;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "VehicularJob{" + "model=" + model + ", color=" + color + ", year=" + year + '}';
    }

    

    
    
    
}
