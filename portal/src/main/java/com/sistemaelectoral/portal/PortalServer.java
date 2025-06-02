package com.sistemaelectoral.portal;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Identity;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;
import com.zeroc.Ice.Object;

public class PortalServer {
    public static void main(String[] args) {
        try (Communicator communicator = Util.initialize(args)) {
            // Crear el adaptador
            ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints(
                "PortalAdapter", "tcp -h localhost -p 10003");
                
            // Crear e instalar los servants
            ConsultoriaImpl consultoriaServant = new ConsultoriaImpl();
            SeguridadImpl seguridadServant = new SeguridadImpl();
            AccesoDatosImpl accesoDatosServant = new AccesoDatosImpl();
            
            // Registrar los servants con sus respectivas identidades
            adapter.add((Object)consultoriaServant, Util.stringToIdentity("Consultoria"));
            adapter.add((Object)seguridadServant, Util.stringToIdentity("Seguridad"));
            adapter.add((Object)accesoDatosServant, Util.stringToIdentity("AccesoDatos"));
            
            // Activar el adaptador
            adapter.activate();
            
            System.out.println("Servidor del Portal iniciado...");
            
            // Esperar a que se detenga el servidor
            communicator.waitForShutdown();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}