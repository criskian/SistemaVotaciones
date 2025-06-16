package com.votaciones.seguridad.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class ProxyCacheFiscalia {
    private static final Logger logger = LoggerFactory.getLogger(ProxyCacheFiscalia.class);
    
    // Cache con TTL de 1 hora
    private static final long CACHE_TTL_HOURS = 1;
    
    private final Map<String, CacheEntry> cache;
    
    public ProxyCacheFiscalia() {
        this.cache = new ConcurrentHashMap<>();
        logger.info("ProxyCacheFiscalia inicializado con TTL de {} horas", CACHE_TTL_HOURS);
    }
    
    public Boolean consultarCache(String documento) {
        CacheEntry entry = cache.get(documento);
        if (entry == null) {
            logger.debug("Cache miss para documento: {}", documento);
            return null;
        }
        
        // Verificar si el cache ha expirado
        if (entry.isExpired()) {
            cache.remove(documento);
            logger.debug("Cache expirado para documento: {}", documento);
            return null;
        }
        
        logger.debug("Cache hit para documento: {} -> {}", documento, entry.tieneAntecedentes);
        return entry.tieneAntecedentes;
    }
    
    public void guardarEnCache(String documento, boolean tieneAntecedentes) {
        CacheEntry entry = new CacheEntry(tieneAntecedentes, LocalDateTime.now());
        cache.put(documento, entry);
        logger.debug("Guardado en cache: {} -> {}", documento, tieneAntecedentes);
    }
    
    public void limpiarCache() {
        cache.clear();
        logger.info("Cache de fiscalía limpiado");
    }
    
    public void limpiarExpirados() {
        int sizeBefore = cache.size();
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
        int sizeAfter = cache.size();
        int removidos = sizeBefore - sizeAfter;
        if (removidos > 0) {
            logger.info("Removidas {} entradas expiradas del cache", removidos);
        }
    }
    
    public int getTamanoCache() {
        return cache.size();
    }
    
    public double getTasaHit() {
        // TODO: Implementar estadísticas de hit rate
        return 0.0;
    }
    
    private static class CacheEntry {
        private final boolean tieneAntecedentes;
        private final LocalDateTime timestamp;
        
        public CacheEntry(boolean tieneAntecedentes, LocalDateTime timestamp) {
            this.tieneAntecedentes = tieneAntecedentes;
            this.timestamp = timestamp;
        }
        
        public boolean isExpired() {
            return ChronoUnit.HOURS.between(timestamp, LocalDateTime.now()) >= CACHE_TTL_HOURS;
        }
    }
} 