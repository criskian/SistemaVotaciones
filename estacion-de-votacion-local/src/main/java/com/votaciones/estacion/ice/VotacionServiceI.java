package com.votaciones.estacion.ice;

import com.votaciones.estacion.GestionMesasProxy;

/**
 * Servicio ICE para automatización de pruebas.
 * Implementa la interfaz remota para validar si un ciudadano puede votar.
 * Retorna true si puede votar; false si no puede
 */
public class VotacionServiceI {
    private GestionMesasProxy gestionMesasProxy;

    public VotacionServiceI(GestionMesasProxy proxy) {
        this.gestionMesasProxy = proxy;
    }

    public boolean puedeVotar(String cedula) {
        // Llama al proxy real para consultar el estado
        return gestionMesasProxy.verificarEstado(cedula);
    }
} 