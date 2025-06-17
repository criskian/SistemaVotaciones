package com.votaciones.estacion.lotes;

import com.votaciones.estacion.GestionMesasProxy;
import com.votaciones.estacion.BrokerZonaProxy;
import VotingSystem.Voto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Gestor de lotes: agrupa votos validados en lotes de 20 o cada 15 minutos.
 * Almacena los lotes en memoria temporal hasta que se envían al sistema central.
 */
public class GestorLotes {
    private static final int TAMANO_LOTE = 20;
    private static final long TIEMPO_MAXIMO = 15 * 60 * 1000; // 15 minutos en milisegundos
    
    private List<Voto> votosPendientes;
    private List<Voto> votosPendientesEnvio; // Votos que no se pudieron enviar
    private final Timer timer;
    private long ultimoEnvio;

    public GestorLotes() {
        this.votosPendientes = new ArrayList<>();
        this.votosPendientesEnvio = new ArrayList<>();
        this.timer = new Timer(true);
        this.ultimoEnvio = System.currentTimeMillis();
        
        System.out.println("[GestorLotes] Inicializado con almacenamiento en memoria");
        
        // Timer para envío automático cada 15 minutos
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                enviarLoteSiCorresponde(true, null);
            }
        }, TIEMPO_MAXIMO, TIEMPO_MAXIMO);
    }

    public synchronized void agregarVoto(Voto voto) {
        votosPendientes.add(voto);
        System.out.println("[GestorLotes] Voto agregado. Total pendientes: " + votosPendientes.size());
        enviarLoteSiCorresponde(false, null);
    }

    public synchronized void enviarLoteSiCorresponde(boolean forzadoPorTiempo, GestionMesasProxy proxy) {
        if (votosPendientes.isEmpty() && votosPendientesEnvio.isEmpty()) {
            return;
        }
        
        // Si es forzado por tiempo o se alcanzó el tamaño del lote
        if (forzadoPorTiempo || votosPendientes.size() >= TAMANO_LOTE) {
            
            if (!votosPendientes.isEmpty()) {
                System.out.println("[GestorLotes] Creando lote con " + votosPendientes.size() + " votos");
                
                // Mover votos pendientes a la cola de envío
                votosPendientesEnvio.addAll(votosPendientes);
                votosPendientes.clear();
                
                System.out.println("[GestorLotes] Lote creado y guardado en memoria. Total en cola de envío: " + 
                                 votosPendientesEnvio.size());
            }
        }
    }

    public void enviarLotesPendientes(GestionMesasProxy proxy, BrokerZonaProxy brokerProxy, int mesaId, String zona) {
        if (votosPendientesEnvio.isEmpty()) {
            System.out.println("[GestorLotes] No hay votos pendientes para enviar");
            return;
        }
        if (brokerProxy != null) {
            System.out.println("[GestorLotes] Enviando votos al broker de zona...");
            enviarVotosAlBroker(brokerProxy);
        } else {
            System.out.println("[GestorLotes] Enviando votos directamente al sistema central...");
            try {
                boolean exito = proxy.enviarLoteVotos(votosPendientesEnvio, mesaId, zona);
                if (exito) {
                    System.out.println("[GestorLotes] Lote enviado exitosamente. Limpiando cola.");
                    votosPendientesEnvio.clear();
                    ultimoEnvio = System.currentTimeMillis();
                } else {
                    System.err.println("[GestorLotes] Error al enviar lote. Los votos permanecen en cola.");
                }
            } catch (Exception e) {
                System.err.println("[GestorLotes] Error al enviar lotes pendientes: " + e.getMessage());
            }
        }
    }
    
    /**
     * Obtener estadísticas del gestor
     */
    public String getEstadisticas() {
        return String.format("Votos pendientes: %d, Votos en cola de envío: %d", 
                           votosPendientes.size(), votosPendientesEnvio.size());
    }
    
    /**
     * Limpiar todas las colas (útil para testing)
     */
    public synchronized void limpiar() {
        votosPendientes.clear();
        votosPendientesEnvio.clear();
        System.out.println("[GestorLotes] Todas las colas limpiadas");
    }
    
    /**
     * Cerrar el gestor y el timer
     */
    public void close() {
        if (timer != null) {
            timer.cancel();
            System.out.println("[GestorLotes] Timer cancelado");
        }
    }

    public void enviarVotosAlBroker(BrokerZonaProxy brokerProxy) {
        if (votosPendientesEnvio.isEmpty()) {
            System.out.println("[GestorLotes] No hay votos pendientes para enviar al broker");
            return;
        }
        System.out.println("[GestorLotes] Enviando " + votosPendientesEnvio.size() + " votos al broker");
        try {
            for (Voto voto : votosPendientesEnvio) {
                brokerProxy.enviarVoto(voto);
            }
            votosPendientesEnvio.clear();
            ultimoEnvio = System.currentTimeMillis();
        } catch (Exception e) {
            System.err.println("[GestorLotes] Error al enviar votos al broker: " + e.getMessage());
        }
    }
} 