package VotingSystem;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyCacheDBCiudadServer {
    private static final Logger logger = LoggerFactory.getLogger(ProxyCacheDBCiudadServer.class);

    public static void main(String[] args) {
        try {
            // Inicializar el comunicador Ice con las propiedades del archivo
            String[] iceArgs = new String[]{"--Ice.Config=src/main/resources/config/ice.properties"};
            Communicator communicator = Util.initialize(iceArgs);

            // Crear el adaptador usando la configuración del archivo
            ObjectAdapter adapter = communicator.createObjectAdapter("ProxyCacheDBCiudad");
            
            // Crear el servant
            ProxyCacheDBCiudadI servant = new ProxyCacheDBCiudadI();
            
            // Activar el adaptador y el servant
            adapter.add(servant, Util.stringToIdentity("ProxyCacheDBCiudad"));
            adapter.activate();

            logger.info("ProxyCacheDBCiudadServer corriendo en el puerto 10000...");
            
            // Esperar a que se cierre el servidor
            communicator.waitForShutdown();
        } catch (Exception e) {
            logger.error("Error en el servidor", e);
            System.exit(1);
        }
    }
} 