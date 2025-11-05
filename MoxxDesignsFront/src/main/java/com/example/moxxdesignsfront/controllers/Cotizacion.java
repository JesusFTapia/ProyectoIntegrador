package com.example.moxxdesignsfront.controllers;

/*
*
* @author: Amos Heli Olguin Quiroz
*/
public class Cotizacion {
    private int id;
    private String cliente;
    private String fecha;
    private double total;

    public Cotizacion(int id, String cliente, String fecha, double total) {
        this.id = id;
        this.cliente = cliente;
        this.fecha = fecha;
        this.total = total;
    }

    public int getId() { return id; }
    public String getCliente() { return cliente; }
    public String getFecha() { return fecha; }
    public double getTotal() { return total; }

    public void setId(int id) { this.id = id; }
    public void setCliente(String cliente) { this.cliente = cliente; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public void setTotal(double total) { this.total = total; }
}
