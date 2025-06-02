package com.sistemaelectoral.fiscalia;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Identity;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;
import com.zeroc.Ice.Object;

public class FiscaliaServer {
    public static void main(String[] args) {
        try (Communicator communicator = Util.initialize(args)) {
            // Crear el adaptador
            ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints(
                "FiscaliaAdapter", "tcp -h localhost -p 10002");
                
            // Crear e instalar el servant
            FiscaliaImpl servant = new FiscaliaImpl();
            Identity id = Util.stringToIdentity("Fiscalia");
            adapter.add((Object)servant, id);
            
            // Activar el adaptador
            adapter.activate();
            
            System.out.println("Servidor de Fiscalía iniciado...");
            
            // Esperar a que se detenga el servidor
            communicator.waitForShutdown();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 