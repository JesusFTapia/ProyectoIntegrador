/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.moxxdesignsdbconnection.entitys;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;
import javax.persistence.OneToMany;

/**
 *
 * @author USER
 */
@Entity
@Table(name = "users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    private String role;
    
    private String userName;
    
    private String password;
    
    @OneToMany(mappedBy="user")
    List<Job> jobs;
    
    @OneToMany(mappedBy="user")
    List<Appointment> appointments;
    

    public User() {
    }

    public User(String role, String userName, String password) {
        this.role = role;
        this.userName = userName;
        this.password = password;
    }

    public User(long id, String role, String userName, String password, List<Job> jobs, List<Appointment> appointments) {
        this.id = id;
        this.role = role;
        this.userName = userName;
        this.password = password;
        this.jobs = jobs;
        this.appointments = appointments;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", role=" + role + ", userName=" + userName + ", password=" + password + ", jobs=" + jobs + ", appointments=" + appointments + '}';
    }
    
    
    
    
}
