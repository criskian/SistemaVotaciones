package com.votaciones.estacion;

import VotingSystem.BrokerZonaPrx;
import VotingSystem.Voto;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;

public class BrokerZonaProxy {
    private BrokerZonaPrx proxy;
    private Communicator communicator;

    public BrokerZonaProxy(String proxyStr, String[] iceArgs) {
        try {
            communicator = Util.initialize(iceArgs);
            ObjectPrx base = communicator.stringToProxy(proxyStr);
            proxy = BrokerZonaPrx.checkedCast(base);
            if (proxy == null) {
                throw new RuntimeException("No se pudo obtener el proxy del BrokerZona");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error inicializando ICE para BrokerZona: " + e.getMessage(), e);
        }
    }

    public void enviarVoto(Voto voto) {
        proxy.enviarVoto(voto);
    }

    public void close() {
        if (communicator != null) {
            communicator.destroy();
        }
    }
} 