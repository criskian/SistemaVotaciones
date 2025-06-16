package com.votaciones.portalwebconsulta.model;

public class Ciudadano {
    private String cedula;
    private String nombre;
    private String zonaVotacion;
    private String mesaAsignada;

    public Ciudadano(String cedula, String nombre, String zonaVotacion, String mesaAsignada) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.zonaVotacion = zonaVotacion;
        this.mesaAsignada = mesaAsignada;
    }

    // Getters y setters
    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getZonaVotacion() {
        return zonaVotacion;
    }

    public void setZonaVotacion(String zonaVotacion) {
        this.zonaVotacion = zonaVotacion;
    }

    public String getMesaAsignada() {
        return mesaAsignada;
    }

    public void setMesaAsignada(String mesaAsignada) {
        this.mesaAsignada = mesaAsignada;
    }
}