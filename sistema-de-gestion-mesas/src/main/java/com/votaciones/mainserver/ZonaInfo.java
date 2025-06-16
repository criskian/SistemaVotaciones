package com.votaciones.mainserver;

import java.io.Serializable;

public class ZonaInfo implements Serializable {
    private int id;
    private String nombre;
    private String codigo;
    private String ciudad;
    private int totalMesas;
    private int totalCiudadanos;

    public ZonaInfo() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    
    public int getTotalMesas() { return totalMesas; }
    public void setTotalMesas(int totalMesas) { this.totalMesas = totalMesas; }
    
    public int getTotalCiudadanos() { return totalCiudadanos; }
    public void setTotalCiudadanos(int totalCiudadanos) { this.totalCiudadanos = totalCiudadanos; }
} 