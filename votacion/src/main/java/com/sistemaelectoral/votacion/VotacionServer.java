package com.sistemaelectoral.votacion;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Identity;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;
import com.zeroc.Ice.Object;

public class VotacionServer {
    public static void main(String[] args) {
        try (Communicator communicator = Util.initialize(args)) {
            // Crear el adaptador
            ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints(
                "MesaVotacionAdapter", "tcp -h localhost -p 10000");
                
            // Crear e instalar el servant
            MesaVotacionImpl servant = new MesaVotacionImpl();
            Identity id = Util.stringToIdentity("MesaVotacion");
            adapter.add((Object)servant, id);
            
            // Activar el adaptador
            adapter.activate();
            
            System.out.println("Servidor de Mesa de Votación iniciado...");
            
            // Esperar a que se detenga el servidor
            communicator.waitForShutdown();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 