package com.votaciones.estacion;

import com.votaciones.estacion.cli.VotacionCLI;
import com.votaciones.estacion.lotes.GestorLotes;

public class Main {
    public static void main(String[] args) {
        String zona = "Zona por defecto";
        int mesa = 1;
        
        if (args.length >= 2) {
            zona = args[0];
            try {
                mesa = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Número de mesa inválido, usando valor por defecto: 1");
            }
        }

        try {
            GestionMesasProxy proxy = new GestionMesasProxy();
            GestorLotes gestorLotes = new GestorLotes();
            
            // Enviar lotes pendientes con zona y mesa
            gestorLotes.enviarLotesPendientes(proxy, mesa, zona);
            
            // Punto de entrada: inicializa CLI y servicios ICE
            System.out.println("Estación de Votación Local iniciando...");
            // Inicializar el CLI
            VotacionCLI cli = new VotacionCLI(proxy, gestorLotes, mesa);
            cli.iniciar();
            
            proxy.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
} 