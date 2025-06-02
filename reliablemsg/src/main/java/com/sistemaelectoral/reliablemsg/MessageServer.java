package com.sistemaelectoral.reliablemsg;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Identity;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;
import com.zeroc.Ice.Object;

public class MessageServer {
    private static VotoQueueImpl votoQueue;
    
    public static void main(String[] args) {
        try (Communicator communicator = Util.initialize(args)) {
            // Crear el adaptador
            ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints(
                "MessageAdapter", "tcp -h localhost -p 10004");
                
            // Crear e instalar el servant
            votoQueue = new VotoQueueImpl();
            Identity id = Util.stringToIdentity("VotoQueue");
            adapter.add((Object)votoQueue, id);
            
            // Activar el adaptador
            adapter.activate();
            
            System.out.println("Servidor de Mensajería iniciado...");
            
            // Configurar shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (votoQueue != null) {
                    votoQueue.shutdown();
                }
            }));
            
            // Esperar a que se detenga el servidor
            communicator.waitForShutdown();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            if (votoQueue != null) {
                votoQueue.shutdown();
            }
        }
    }
} 