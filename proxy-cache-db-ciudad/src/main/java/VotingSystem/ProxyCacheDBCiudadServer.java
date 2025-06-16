package VotingSystem;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;

public class ProxyCacheDBCiudadServer {
    public static void main(String[] args) {
        try (Communicator communicator = Util.initialize(args)) {
            ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints(
                "ProxyCacheDBCiudadAdapter", "default -p 10000");
            ProxyCacheDBCiudadI servant = new ProxyCacheDBCiudadI();
            adapter.add(servant, Util.stringToIdentity("ProxyCacheDBCiudad"));
            adapter.activate();
            System.out.println("ProxyCacheDBCiudadServer corriendo en el puerto 10000...");
            communicator.waitForShutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 