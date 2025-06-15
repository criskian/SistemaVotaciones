package com.votaciones.mainserver;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class MainServer {
    public static void main(String[] args) {
        try {
            // Create and export the remote object
            MainServerImpl server = new MainServerImpl();
            VotingSystem stub = (VotingSystem) UnicastRemoteObject.exportObject(server, 0);

            // Create the registry on port 1099
            Registry registry = LocateRegistry.createRegistry(1099);

            // Bind the remote object's stub in the registry
            registry.rebind("VotingSystem", stub);

            System.out.println("Servidor RMI iniciado en puerto 1099");
        } catch (Exception e) {
            System.err.println("Error al iniciar el servidor RMI:");
            e.printStackTrace();
        }
    }
} 