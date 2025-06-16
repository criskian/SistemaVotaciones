package com.votaciones.seguridad.model;

import java.time.LocalDateTime;

public class AlertaSeguridad {
    private String id;
    private String tipo;
    private String mensaje;
    private String severidad;
    private LocalDateTime fechaHora;
    private boolean procesada;
    private String origen;
    
    public AlertaSeguridad() {
        this.fechaHora = LocalDateTime.now();
        this.procesada = false;
        this.origen = "SISTEMA_SEGURIDAD";
    }
    
    public AlertaSeguridad(String tipo, String mensaje, String severidad) {
        this();
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.severidad = severidad;
        this.id = generarId();
    }
    
    private String generarId() {
        return "ALT-" + System.currentTimeMillis() + "-" + hashCode();
    }
    
    // Getters
    public String getId() { return id; }
    public String getTipo() { return tipo; }
    public String getMensaje() { return mensaje; }
    public String getSeveridad() { return severidad; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public boolean isProcesada() { return procesada; }
    public String getOrigen() { return origen; }
    
    // Setters
    public void setId(String id) { this.id = id; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public void setSeveridad(String severidad) { this.severidad = severidad; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public void setProcesada(boolean procesada) { this.procesada = procesada; }
    public void setOrigen(String origen) { this.origen = origen; }
    
    public boolean esCritica() {
        return "CRITICA".equals(severidad);
    }
    
    public boolean esAlta() {
        return "ALTA".equals(severidad);
    }
    
    @Override
    public String toString() {
        return String.format("AlertaSeguridad{id='%s', tipo='%s', severidad='%s', mensaje='%s', fechaHora=%s}", 
                           id, tipo, severidad, mensaje, fechaHora);
    }
} 