package com.votaciones.mainserver;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MainClient {
    public static void main(String[] args) {
        try {
            // Get the registry
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);

            // Look up the remote object
            VotingSystem stub = (VotingSystem) registry.lookup("VotingSystem");

            // Test the connection by getting the state of all voting tables
            String[] estados = stub.obtenerEstadoMesas();
            System.out.println("Estado de las mesas:");
            for (String estado : estados) {
                System.out.println(estado);
            }

        } catch (Exception e) {
            System.err.println("Error en el cliente RMI:");
            e.printStackTrace();
        }
    }
} 