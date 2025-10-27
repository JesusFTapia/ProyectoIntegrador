/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.moxxdesignsdbconnection.entitys;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 *
 * @author USER
 */
@Entity
@Table(name = "generaljobs") 
@PrimaryKeyJoinColumn(name = "job_id") 
public class GeneralJob extends Job implements Serializable {

    public GeneralJob() {
    }

    public GeneralJob(Date deliveryDate, String state, String description, String fileDirection, List<Quotation> quotations, JobType jobType, User user, Client client) {
        super(deliveryDate, state, description, fileDirection, quotations, jobType, user, client);
    }
    
}
