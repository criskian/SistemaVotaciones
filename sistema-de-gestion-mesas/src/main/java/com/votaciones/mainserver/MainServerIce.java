package com.votaciones.mainserver;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;

public class MainServerIce {
    private Communicator communicator;
    private ObjectAdapter adapter;
    private MainServerI mainServerI;

    public MainServerIce() {
        try {
            // Inicializar ICE con archivo de propiedades en el directorio actual
            String configFile = "main_server.properties";
            communicator = Util.initialize(new String[]{"--Ice.Config=" + configFile});
            
            // Crear el adaptador (la configuración vendrá del archivo)
            adapter = communicator.createObjectAdapter("MainServer");
            
            // Crear el servidor
            mainServerI = new MainServerI();
            
            // Agregar el servidor al adaptador
            adapter.add(mainServerI, Util.stringToIdentity("MainServer"));
            
            // Activar el adaptador
            adapter.activate();
            
            System.out.println("[MainServerIce] Servidor ICE iniciado en puerto 10004");
            
            // Esperar por la señal de terminación
            communicator.waitForShutdown();
            
        } catch (Exception e) {
            System.err.println("[MainServerIce] Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (communicator != null) {
                try {
                    communicator.destroy();
                } catch (Exception e) {
                    System.err.println("[MainServerIce] Error al destruir communicator: " + e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        new MainServerIce();
    }
} 