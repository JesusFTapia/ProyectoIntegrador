/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.moxxdesignsdbconnection.entitys;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
/**
 *
 * @author USER
 */
@Entity
@Table(name="jobtypes")
public class JobType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String descripcion;
    
    @OneToMany(mappedBy="jobType")
    private List<Job>  jobs;

    public JobType() {
    }

    public JobType(String name, String descripcion) {
        this.name = name;
        this.descripcion = descripcion;
    }

    public JobType(long id, String name, String descripcion, List<Job> jobs) {
        this.id = id;
        this.name = name;
        this.descripcion = descripcion;
        this.jobs = jobs;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }

    @Override
    public String toString() {
        return "JobType{" + "id=" + id + ", name=" + name + ", descripcion=" + descripcion + ", jobs=" + jobs + '}';
    }
    
    
    
}
