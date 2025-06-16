package com.votaciones.estacion;

import com.votaciones.estacion.cli.VotacionCLI;
import com.votaciones.estacion.lotes.GestorLotes;

public class Main {
    public static void main(String[] args) {
        // Punto de entrada: inicializa CLI y servicios ICE
        System.out.println("Estación de Votación Local iniciando...");
        // Inicializar el proxy de gestión de mesas
        GestionMesasProxy proxy = new GestionMesasProxy();
        // Inicializar el gestor de lotes
        GestorLotes gestorLotes = new GestorLotes();
        // ID de la mesa (puedes pedirlo por consola o dejarlo fijo para pruebas)
        int mesaId = 1;
        // Inicializar el CLI
        VotacionCLI cli = new VotacionCLI(proxy, gestorLotes, mesaId);
        cli.iniciar();
        // Al finalizar, intentar enviar lotes pendientes
        gestorLotes.enviarLotesPendientes(proxy);
        proxy.close();
    }
} 