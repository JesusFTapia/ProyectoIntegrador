/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.moxxdesignsdbconnection.entitys;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author USER
 */
@Entity
public class Appointment implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private Date appointmentDate;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String notes;
    
    @Column(nullable = false)
    private String hora;
    
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    public Appointment() {
    }

    public Appointment(Date appointmentDate, String name, String notes, String hora, User user) {
        this.appointmentDate = appointmentDate;
        this.name = name;
        this.notes = notes;
        this.hora = hora;
        this.user = user;
    }

    public Appointment(long id, Date appointmentDate, String name, String notes, String hora, User user) {
        this.id = id;
        this.appointmentDate = appointmentDate;
        this.name = name;
        this.notes = notes;
        this.hora = hora;
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    
    
}
