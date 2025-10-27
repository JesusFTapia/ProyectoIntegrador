/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.moxxdesignsdbconnection.entitys;

import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author USER
 */
@Entity
@Table(name ="jobs")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Job implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @Column(nullable = false)
    private Date deliveryDate;
    
    @Column(nullable = false)
    private String state;
    
    @Column(nullable = false)
    private String description;
    
    @Column
    private String fileDirection;
    
    @OneToMany(mappedBy="job",cascade = CascadeType.ALL)
    List<Quotation> quotations;
    
    @ManyToOne
    @JoinColumn(name ="jobType_id")       
    private JobType jobType;
    
    @ManyToOne
    @JoinColumn(name ="user_id")
    private User user;
    
    @ManyToOne
    @JoinColumn(name ="client_id")       
    private Client client;

    public Job() {
    }

    public Job(Date deliveryDate, String state, String description, String fileDirection,List<Quotation> quotations, JobType jobType, User user, Client client) {
        this.deliveryDate = deliveryDate;
        this.state = state;
        this.description = description;
        this.fileDirection = fileDirection;
        this.quotations = quotations;
        this.jobType = jobType;
        this.user = user;
        this.client = client;
    }

    public Job(long id, Date deliveryDate, String state, String description, String fileDirection, List<Quotation> quotations, JobType jobType, User user, Client client) {
        this.id = id;
        this.deliveryDate = deliveryDate;
        this.state = state;
        this.description = description;
        this.fileDirection = fileDirection;
        this.quotations = quotations;
        this.jobType = jobType;
        this.user = user;
        this.client = client;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getFileDirection() {
        return fileDirection;
    }

    public void setFileDirection(String fileDirection) {
        this.fileDirection = fileDirection;
    }
    public List<Quotation> getQuotations() {
        return quotations;
    }

    public void setQuotations(List<Quotation> quotations) {
        this.quotations = quotations;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return "Job{" + "id=" + id + ", deliveryDate=" + deliveryDate + ", state=" + state + ", description=" + description + ", fileDirection=" + fileDirection + ", quotations=" + quotations + ", jobType=" + jobType + ", user=" + user + ", client=" + client + '}';
    }

    

    

    
    
    
    
    
}
