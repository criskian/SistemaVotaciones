package com.votaciones.seguridad.model;

public class CiudadanoInfo {
    private String documento;
    private String nombres;
    private String apellidos;
    private int ciudadId;
    private int zonaId;
    private int mesaId;
    private boolean yaVoto;
    private boolean esSospechoso;
    
    public CiudadanoInfo() {}
    
    public CiudadanoInfo(String documento, String nombres, String apellidos, 
                        int ciudadId, int zonaId, int mesaId) {
        this.documento = documento;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.ciudadId = ciudadId;
        this.zonaId = zonaId;
        this.mesaId = mesaId;
        this.yaVoto = false;
        this.esSospechoso = false;
    }
    
    // Getters
    public String getDocumento() { return documento; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public int getCiudadId() { return ciudadId; }
    public int getZonaId() { return zonaId; }
    public int getMesaId() { return mesaId; }
    public boolean isYaVoto() { return yaVoto; }
    public boolean isEsSospechoso() { return esSospechoso; }
    
    // Setters
    public void setDocumento(String documento) { this.documento = documento; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public void setCiudadId(int ciudadId) { this.ciudadId = ciudadId; }
    public void setZonaId(int zonaId) { this.zonaId = zonaId; }
    public void setMesaId(int mesaId) { this.mesaId = mesaId; }
    public void setYaVoto(boolean yaVoto) { this.yaVoto = yaVoto; }
    public void setEsSospechoso(boolean esSospechoso) { this.esSospechoso = esSospechoso; }
    
    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }
    
    @Override
    public String toString() {
        return String.format("CiudadanoInfo{documento='%s', nombre='%s %s', mesa=%d, zona=%d, yaVoto=%s}", 
                           documento, nombres, apellidos, mesaId, zonaId, yaVoto);
    }
} 