package com.sistemaelectoral.consulta;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Identity;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;
import com.zeroc.Ice.Object;

public class ConsultaServer {
    public static void main(String[] args) {
        try (Communicator communicator = Util.initialize(args)) {
            // Crear el adaptador
            ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints(
                "ConsultaCiudadanoAdapter", "tcp -h localhost -p 10001");
                
            // Crear e instalar el servant
            ConsultaCiudadanoImpl servant = new ConsultaCiudadanoImpl();
            Identity id = Util.stringToIdentity("ConsultaCiudadano");
            adapter.add((Object)servant, id);
            
            // Activar el adaptador
            adapter.activate();
            
            System.out.println("Servidor de Consulta Ciudadano iniciado...");
            
            // Esperar a que se detenga el servidor
            communicator.waitForShutdown();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 