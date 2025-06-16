package com.votaciones.estacion;

import VotingSystem.MainServerPrx;
import VotingSystem.Candidato;
import VotingSystem.LoteVotos;
import VotingSystem.Voto;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;
import java.util.*;

/**
 * Proxy para comunicación con el sistema de gestión de mesas vía ICE.
 * Encapsula la lógica de llamadas remotas y validación de votos.
 */
public class GestionMesasProxy {
    private MainServerPrx proxy;
    private Communicator communicator;

    public GestionMesasProxy() {
        try {
            // Inicializar ICE
            String[] args = new String[] {
                "--Ice.Default.Protocol=tcp",
                "--Ice.Default.Host=127.0.0.1",
                "--Ice.Default.Port=10004"
            };
            communicator = Util.initialize(args);
            
            // Obtener el proxy
            ObjectPrx base = communicator.stringToProxy("MainServer:default -h 127.0.0.1 -p 10004");
            proxy = MainServerPrx.checkedCast(base);
            
            if (proxy == null) {
                System.err.println("[GestionMesasProxy] Error: No se pudo obtener el proxy");
                throw new RuntimeException("No se pudo obtener el proxy");
            }
            
            System.out.println("[GestionMesasProxy] Conexión establecida exitosamente");
        } catch (Exception e) {
            System.err.println("[GestionMesasProxy] Error inicializando ICE: " + e.getMessage());
            throw new RuntimeException("Error inicializando ICE", e);
        }
    }

    /**
     * Obtiene la lista de candidatos desde el sistema central.
     */
    public List<Candidato> listarCandidatos() {
        Candidato[] candidatos = proxy.listarCandidatos();
        return Arrays.asList(candidatos);
    }

    /**
     * Valida si un votante puede votar (true = puede votar, false = no puede).
     */
    public boolean validarVotante(String idVotante) {
        return proxy.validarVoto(idVotante);
    }

    /**
     * Verifica el estado de un votante (true = puede votar, false = no puede).
     */
    public boolean verificarEstado(String idVotante) {
        return proxy.verificarEstado(idVotante);
    }

    /**
     * Verifica si un votante puede votar en una zona específica.
     */
    public boolean verificarEstadoZona(String idVotante, String zona) {
        return proxy.verificarEstadoZona(idVotante, zona);
    }

    /**
     * Registra un voto en el sistema central, pasando zona y mesa como contexto.
     */
    public boolean registrarVoto(String idVotante, int idCandidato, int mesaId, String zona) {
        try {
            java.util.Map<String, String> ctx = new java.util.HashMap<>();
            ctx.put("mesaId", String.valueOf(mesaId));
            ctx.put("zona", zona);
            
            // Intentar registrar el voto directamente (la validación ya se hizo en la UI)
            boolean resultado = proxy.registrarVoto(idVotante, idCandidato, ctx);
            if (!resultado) {
                System.err.println("[GestionMesasProxy] Error al registrar el voto en el sistema central");
                return false;
            }
            
            return true;
        } catch (Exception e) {
            System.err.println("[GestionMesasProxy] Error al registrar voto: " + e.getMessage());
            return false;
        }
    }

    /**
     * Envía un lote de votos al sistema central, pasando zona y mesa como contexto para cada voto.
     */
    public boolean enviarLoteVotos(List<VotingSystem.Voto> votos, int mesaId, String zona) {
        VotingSystem.LoteVotos lote = new VotingSystem.LoteVotos();
        lote.votos = votos.toArray(new VotingSystem.Voto[0]);
        // El contexto se pasa por cada voto al registrar individualmente en el backend
        // Aquí solo se pasa el contexto general
        java.util.Map<String, String> ctx = new java.util.HashMap<>();
        ctx.put("mesaId", String.valueOf(mesaId));
        ctx.put("zona", zona);
        return proxy.addLoteVotos(lote, ctx);
    }

    public void close() {
        if (communicator != null) {
            communicator.destroy();
        }
    }
} 