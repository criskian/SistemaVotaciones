package com.votaciones.seguridad.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simula la conexión con el sistema fiscal externo.
 * En una implementación real, esto sería una llamada HTTP/REST a la API de fiscalía.
 */
public class ConexionSistemaFiscal {
    private static final Logger logger = LoggerFactory.getLogger(ConexionSistemaFiscal.class);
    
    private final Random random;
    private final Set<String> documentosConAntecedentes;
    private boolean simulacionActivada;
    private long tiempoRespuestaMs;
    
    public ConexionSistemaFiscal() {
        this.random = new Random();
        this.documentosConAntecedentes = ConcurrentHashMap.newKeySet();
        this.simulacionActivada = true;
        this.tiempoRespuestaMs = 500; // Simular 500ms de latencia
        
        // Precargar algunos documentos con antecedentes para pruebas
        cargarDatosPrueba();
        
        logger.info("ConexionSistemaFiscal inicializada (modo simulación: {})", simulacionActivada);
    }
    
    public boolean consultarAntecedentes(String documento) {
        try {
            logger.debug("Consultando antecedentes para documento: {}", documento);
            
            // Simular latencia de red
            if (simulacionActivada) {
                Thread.sleep(tiempoRespuestaMs);
            }
            
            boolean tieneAntecedentes;
            
            if (simulacionActivada) {
                // Modo simulación: algunos documentos predefinidos + probabilidad aleatoria
                tieneAntecedentes = false; // documentosConAntecedentes.contains(documento) || (random.nextDouble() < 0.05);
            } else {
                // Aquí iría la llamada real a la API de fiscalía
                tieneAntecedentes = consultarApiFiscalia(documento);
            }
            
            logger.debug("Resultado antecedentes para {}: {}", documento, tieneAntecedentes);
            return tieneAntecedentes;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Consulta interrumpida para documento: " + documento, e);
            return false;
        } catch (Exception e) {
            logger.error("Error consultando antecedentes para documento: " + documento, e);
            return false; // Por seguridad, en caso de error permitir el voto
        }
    }
    
    public boolean verificarDisponibilidad() {
        try {
            logger.debug("Verificando disponibilidad del sistema fiscal");
            
            if (simulacionActivada) {
                // Simular que está disponible el 95% del tiempo
                return random.nextDouble() < 0.95;
            } else {
                // Aquí iría un ping/health check a la API real
                return pingApiFiscalia();
            }
            
        } catch (Exception e) {
            logger.error("Error verificando disponibilidad del sistema fiscal", e);
            return false;
        }
    }
    
    public void agregarDocumentoConAntecedentes(String documento) {
        documentosConAntecedentes.add(documento);
        logger.info("Documento {} agregado a la lista de antecedentes", documento);
    }
    
    public void removerDocumentoConAntecedentes(String documento) {
        documentosConAntecedentes.remove(documento);
        logger.info("Documento {} removido de la lista de antecedentes", documento);
    }
    
    public void configurarSimulacion(boolean activada, long tiempoRespuestaMs) {
        this.simulacionActivada = activada;
        this.tiempoRespuestaMs = tiempoRespuestaMs;
        logger.info("Simulación configurada: activada={}, tiempoRespuesta={}ms", 
                   activada, tiempoRespuestaMs);
    }
    
    public int getCantidadDocumentosConAntecedentes() {
        return documentosConAntecedentes.size();
    }
    
    public boolean isSimulacionActivada() {
        return simulacionActivada;
    }
    
    public long getTiempoRespuestaMs() {
        return tiempoRespuestaMs;
    }
    
    private void cargarDatosPrueba() {
        // Documentos con antecedentes para pruebas - VACÍO para permitir que todos voten
        // documentosConAntecedentes.add("111111111");
        // documentosConAntecedentes.add("222222222");
        // documentosConAntecedentes.add("999999999");
        
        logger.debug("Cargados {} documentos con antecedentes para pruebas", 
                    documentosConAntecedentes.size());
    }
    
    private boolean consultarApiFiscalia(String documento) {
        // TODO: Implementar llamada real a API de fiscalía
        // Ejemplo:
        // HttpResponse response = httpClient.get("/antecedentes/" + documento);
        // return response.getStatus() == 200 && response.getBody().contains("antecedentes");
        
        logger.warn("Consulta real a API de fiscalía no implementada para: {}", documento);
        return false;
    }
    
    private boolean pingApiFiscalia() {
        // TODO: Implementar health check real
        // Ejemplo:
        // HttpResponse response = httpClient.get("/health");
        // return response.getStatus() == 200;
        
        logger.warn("Health check real a API de fiscalía no implementado");
        return false;
    }
} 