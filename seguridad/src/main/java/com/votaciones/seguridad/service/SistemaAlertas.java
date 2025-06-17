package com.votaciones.seguridad.service;

import com.votaciones.seguridad.model.AlertaSeguridad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SistemaAlertas {
    private static final Logger logger = LoggerFactory.getLogger(SistemaAlertas.class);
    
    private final BlockingQueue<AlertaSeguridad> colaAlertas;
    private final ExecutorService executorService;
    private final AtomicLong contadorAlertas;
    private final List<AlertaSeguridad> historialAlertas;
    private volatile boolean activo;
    
    public SistemaAlertas() {
        this.colaAlertas = new LinkedBlockingQueue<>();
        this.executorService = Executors.newFixedThreadPool(2);
        this.contadorAlertas = new AtomicLong(0);
        this.historialAlertas = new ArrayList<>();
        this.activo = true;
        
        // Iniciar procesador de alertas
        iniciarProcesadorAlertas();
        
        logger.info("SistemaAlertas inicializado y activo");
    }
    
    public void enviarAlerta(AlertaSeguridad alerta) {
        if (!activo) {
            logger.warn("Sistema de alertas inactivo, descartando alerta: {}", alerta.getTipo());
            return;
        }
        
        try {
            alerta.setId(generarIdAlerta());
            colaAlertas.offer(alerta);
            contadorAlertas.incrementAndGet();
            
            logger.info("Alerta encolada: {} - {}", alerta.getTipo(), alerta.getSeveridad());
            
            // Si es crítica, procesarla inmediatamente
            if (alerta.esCritica()) {
                procesarAlertaCritica(alerta);
            }
            
        } catch (Exception e) {
            logger.error("Error enviando alerta", e);
        }
    }
    
    public void enviarAlerta(String tipo, String mensaje, String severidad) {
        AlertaSeguridad alerta = new AlertaSeguridad(tipo, mensaje, severidad);
        enviarAlerta(alerta);
    }
    
    public long getCantidadAlertasEnviadas() {
        return contadorAlertas.get();
    }
    
    public int getCantidadAlertasPendientes() {
        return colaAlertas.size();
    }
    
    public List<AlertaSeguridad> getHistorialAlertas() {
        synchronized (historialAlertas) {
            return new ArrayList<>(historialAlertas);
        }
    }
    
    public List<AlertaSeguridad> getAlertasCriticas() {
        synchronized (historialAlertas) {
            List<AlertaSeguridad> criticas = new ArrayList<>();
            for (AlertaSeguridad alerta : historialAlertas) {
                if (alerta.esCritica()) {
                    criticas.add(alerta);
                }
            }
            return criticas;
        }
    }
    
    public void shutdown() {
        activo = false;
        executorService.shutdown();
        logger.info("SistemaAlertas detenido");
    }
    
    private void iniciarProcesadorAlertas() {
        executorService.submit(() -> {
            while (activo) {
                try {
                    AlertaSeguridad alerta = colaAlertas.take();
                    procesarAlerta(alerta);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    logger.error("Error procesando alerta", e);
                }
            }
        });
    }
    
    private void procesarAlerta(AlertaSeguridad alerta) {
        try {
            logger.info("Procesando alerta: {}", alerta);
            
            // Guardar en historial
            synchronized (historialAlertas) {
                historialAlertas.add(alerta);
                
                // Mantener solo las últimas 1000 alertas
                if (historialAlertas.size() > 1000) {
                    historialAlertas.remove(0);
                }
            }
            
            // Persistir en base de datos para que el sistema de gestión de mesas la vea
            persistirAlertaEnBD(alerta);
            
            // Procesar según severidad
            switch (alerta.getSeveridad()) {
                case "CRITICA":
                    procesarAlertaCritica(alerta);
                    break;
                case "ALTA":
                    procesarAlertaAlta(alerta);
                    break;
                case "MEDIA":
                    procesarAlertaMedia(alerta);
                    break;
                default:
                    procesarAlertaBaja(alerta);
                    break;
            }
            
            alerta.setProcesada(true);
            
        } catch (Exception e) {
            logger.error("Error procesando alerta: " + alerta.getId(), e);
        }
    }
    
    private void procesarAlertaCritica(AlertaSeguridad alerta) {
        // Alertas críticas requieren acción inmediata
        logger.error("ALERTA CRITICA: {} - {}", alerta.getTipo(), alerta.getMensaje());
        
        // Enviar notificación inmediata a administradores
        notificarAdministradores(alerta);
        
        // Registrar en sistema de auditoría
        registrarEnAuditoria(alerta);
        
        // Si es doble votación, bloquear ciudadano inmediatamente
        if ("DOBLE_VOTACION".equals(alerta.getTipo())) {
            // TODO: Integrar con sistema de bloqueo automático
            logger.warn("Citizen should be blocked immediately for double voting attempt");
        }
    }
    
    private void procesarAlertaAlta(AlertaSeguridad alerta) {
        logger.warn("ALERTA ALTA: {} - {}", alerta.getTipo(), alerta.getMensaje());
        notificarAdministradores(alerta);
        registrarEnAuditoria(alerta);
    }
    
    private void procesarAlertaMedia(AlertaSeguridad alerta) {
        logger.warn("ALERTA MEDIA: {} - {}", alerta.getTipo(), alerta.getMensaje());
        registrarEnAuditoria(alerta);
    }
    
    private void procesarAlertaBaja(AlertaSeguridad alerta) {
        logger.info("ALERTA BAJA: {} - {}", alerta.getTipo(), alerta.getMensaje());
        registrarEnAuditoria(alerta);
    }
    
    private void notificarAdministradores(AlertaSeguridad alerta) {
        // TODO: Implementar notificaciones reales (email, SMS, dashboard)
        logger.info("Notificando administradores sobre alerta: {}", alerta.getId());
    }
    
    private void registrarEnAuditoria(AlertaSeguridad alerta) {
        // TODO: Integrar con sistema de auditoría central
        logger.debug("Registrando alerta en auditoria: {}", alerta.getId());
    }
    
    private String generarIdAlerta() {
        return "ALT-" + System.currentTimeMillis() + "-" + 
               String.format("%04d", contadorAlertas.get() % 10000);
    }
    
    // Método para persistir la alerta en la base de datos
    private void persistirAlertaEnBD(AlertaSeguridad alerta) {
        String DB_URL = "jdbc:postgresql://localhost:5432/sistema_votaciones";
        String DB_USER = "postgres";
        String DB_PASSWORD = "postgres";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO alertas (tipo, mensaje, severidad, fecha_hora) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, alerta.getTipo());
                stmt.setString(2, alerta.getMensaje());
                stmt.setString(3, alerta.getSeveridad());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            logger.error("Error al persistir alerta en la base de datos", e);
        }
    }
} 