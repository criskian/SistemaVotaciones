package com.votaciones.mainserver;

import java.io.Serializable;

public class EstadisticasZona implements Serializable {
    private int totalMesas;
    private int mesasActivas;
    private int totalCiudadanos;
    private int totalVotos;

    public EstadisticasZona() {}

    public int getTotalMesas() { return totalMesas; }
    public void setTotalMesas(int totalMesas) { this.totalMesas = totalMesas; }
    
    public int getMesasActivas() { return mesasActivas; }
    public void setMesasActivas(int mesasActivas) { this.mesasActivas = mesasActivas; }
    
    public int getTotalCiudadanos() { return totalCiudadanos; }
    public void setTotalCiudadanos(int totalCiudadanos) { this.totalCiudadanos = totalCiudadanos; }
    
    public int getTotalVotos() { return totalVotos; }
    public void setTotalVotos(int totalVotos) { this.totalVotos = totalVotos; }
} 