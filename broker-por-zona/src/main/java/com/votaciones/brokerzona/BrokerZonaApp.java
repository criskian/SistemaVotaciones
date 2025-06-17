package com.votaciones.brokerzona;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class BrokerZonaApp {
    public static void main(String[] args) {
        String zona = "ZonaDefault";
        if (args.length > 0) {
            zona = args[0];
        }
        System.out.println("Broker de zona iniciado para la zona: " + zona);
        try {
            Properties iceProps = new Properties();
            try (InputStream in = BrokerZonaApp.class.getClassLoader().getResourceAsStream("broker_zona.properties")) {
                if (in == null) {
                    throw new FileNotFoundException("broker_zona.properties no encontrado en el classpath");
                }
                iceProps.load(in);
            }
            String[] iceArgs = new String[args.length + 2];
            System.arraycopy(args, 0, iceArgs, 0, args.length);
            iceArgs[iceArgs.length - 2] = "--Ice.Config=broker_zona.properties";
            iceArgs[iceArgs.length - 1] = "--Ice.MessageSizeMax=4096";
            try (Communicator communicator = com.zeroc.Ice.Util.initialize(iceArgs)) {
                ObjectAdapter adapter = communicator.createObjectAdapter("BrokerZonaAdapter");
                adapter.add(new BrokerZonaImpl(zona), com.zeroc.Ice.Util.stringToIdentity("BrokerZona1"));
                adapter.activate();
                System.out.println("BrokerZona escuchando en el puerto 10020...");
                communicator.waitForShutdown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 