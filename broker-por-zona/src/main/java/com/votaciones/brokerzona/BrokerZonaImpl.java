package com.votaciones.brokerzona;

import VotingSystem.BrokerZona;
import VotingSystem.Voto;
import VotingSystem.LoteVotos;
import VotingSystem.MainServerPrx;
import com.zeroc.Ice.Current;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BrokerZonaImpl implements BrokerZona {
    private static final int TAMANO_LOTE = 20;
    private static final long TIEMPO_MAXIMO = 15 * 60 * 1000; // 15 minutos
    private final List<Voto> bufferVotos = new ArrayList<>();
    private final Timer timer;
    private MainServerPrx mainServerPrx;
    private Communicator communicator;
    private final String zona;

    public BrokerZonaImpl(String zona) {
        this.zona = zona;
        timer = new Timer(true);
        // Timer para envío automático cada 15 minutos
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                enviarLote();
            }
        }, TIEMPO_MAXIMO, TIEMPO_MAXIMO);
        inicializarMainServerPrx();
    }

    private void inicializarMainServerPrx() {
        try {
            // Configuración del endpoint del sistema de gestión de mesas
            String proxyStr = "MainServer:default -h 127.0.0.1 -p 10004";
            communicator = Util.initialize();
            ObjectPrx base = communicator.stringToProxy(proxyStr);
            mainServerPrx = VotingSystem.MainServerPrx.checkedCast(base);
            if (mainServerPrx == null) {
                throw new RuntimeException("No se pudo obtener el proxy de MainServer");
            }
        } catch (Exception e) {
            System.err.println("[BrokerZona] Error inicializando MainServerPrx: " + e.getMessage());
        }
    }

    @Override
    public synchronized void enviarVoto(Voto voto, Current current) {
        bufferVotos.add(voto);
        System.out.println("[BrokerZona] Voto recibido: idVotante=" + voto.idVotante + ", idCandidato=" + voto.idCandidato);
        if (bufferVotos.size() >= TAMANO_LOTE) {
            enviarLote();
        }
    }

    private synchronized void enviarLote() {
        if (bufferVotos.isEmpty()) {
            return;
        }
        try {
            System.out.println("[BrokerZona] Enviando lote de " + bufferVotos.size() + " votos al sistema de gestión de mesas...");
            LoteVotos lote = new LoteVotos();
            lote.votos = bufferVotos.toArray(new Voto[0]);
            boolean exito = mainServerPrx.addLoteVotos(lote);
            if (exito) {
                System.out.println("[BrokerZona] Lote enviado exitosamente.");
                bufferVotos.clear();
            } else {
                System.err.println("[BrokerZona] Error al enviar lote. Se reintentará en el próximo ciclo.");
            }
        } catch (Exception e) {
            System.err.println("[BrokerZona] Error al enviar lote: " + e.getMessage());
        }
    }
} 