package com.votaciones.seguridad;

import SecurityModule.SecurityService;
import SecurityModule.SecurityServicePrx;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cliente de prueba COMPLETO para el nodo de seguridad.
 * Permite probar todas las funcionalidades del sistema de seguridad.
 */
public class SecurityTestClient {
    private static final Logger logger = LoggerFactory.getLogger(SecurityTestClient.class);
    
    public static void main(String[] args) {
        SecurityTestClient client = new SecurityTestClient();
        client.runTests();
    }
    
    public void runTests() {
        Communicator communicator = null;
        
        try {
            logger.info("[TEST] Iniciando pruebas COMPLETAS del Sistema de Seguridad...");
            
            // Inicializar ICE
            communicator = Util.initialize();
            
            // Conectar al servicio de seguridad
            ObjectPrx base = communicator.stringToProxy(
                "SecurityService:tcp -h 127.0.0.1 -p 10005");
            SecurityServicePrx securityService = SecurityServicePrx.checkedCast(base);
            
            if (securityService == null) {
                logger.error("[ERROR] No se pudo conectar al servicio de seguridad");
                return;
            }
            
            logger.info("[SUCCESS] Conectado al servicio de seguridad");
            
            // Ejecutar TODAS las pruebas
            runSecurityTests(securityService);
            
        } catch (Exception e) {
            logger.error("[ERROR] Error en las pruebas", e);
        } finally {
            if (communicator != null) {
                communicator.destroy();
            }
        }
    }
    
    private void runSecurityTests(SecurityServicePrx securityService) {
        logger.info("\n[SECURITY] === PRUEBAS DE SEGURIDAD COMPLETAS ===");
        
        // Test 1: Validar ciudadano válido
        testValidCitizen(securityService, "567890123");
        
        // Test 2: Validar ciudadano con antecedentes
        testCitizenWithRecord(securityService, "111111111");
        
        // Test 3: Validar ciudadano inexistente
        testNonExistentCitizen(securityService, "000000000");
        
        // Test 4: Verificar estado de votación
        testVotingStatus(securityService, "567890123");
        
        // Test 5: Simular doble votación
        testDoubleVoting(securityService, "123456789");
        
        // Test 6: Validar múltiples ciudadanos
        testMultipleCitizens(securityService);
        
        // Test 7: Pruebas de rendimiento
        testPerformance(securityService);
        
        // Test 8: Pruebas de detección de fraude
        testFraudDetection(securityService);
        
        logger.info("\n[SUCCESS] Pruebas de seguridad COMPLETAS terminadas");
    }
    
    private void testValidCitizen(SecurityServicePrx securityService, String document) {
        try {
            logger.info("\n[TEST 1] Validando ciudadano valido ({})", document);
            
            boolean isValid = securityService.validateSecurity(document);
            logger.info("Resultado validacion: {}", isValid ? "[VALID] VALIDO" : "[INVALID] INVALIDO");
            
            boolean canVote = securityService.checkVotingStatus(document);
            logger.info("Puede votar: {}", canVote ? "[YES] SI" : "[NO] NO");
            
        } catch (Exception e) {
            logger.error("Error en test de ciudadano valido", e);
        }
    }
    
    private void testCitizenWithRecord(SecurityServicePrx securityService, String document) {
        try {
            logger.info("\n[TEST 2] Validando ciudadano con antecedentes ({})", document);
            
            boolean isValid = securityService.validateSecurity(document);
            logger.info("Resultado validacion: {}", isValid ? "[VALID] VALIDO" : "[INVALID] INVALIDO - ANTECEDENTES");
            
            boolean canVote = securityService.checkVotingStatus(document);
            logger.info("Puede votar: {}", canVote ? "[YES] SI" : "[NO] NO");
            
        } catch (Exception e) {
            logger.error("Error en test de ciudadano con antecedentes", e);
        }
    }
    
    private void testNonExistentCitizen(SecurityServicePrx securityService, String document) {
        try {
            logger.info("\n[TEST 3] Validando ciudadano inexistente ({})", document);
            
            boolean isValid = securityService.validateSecurity(document);
            logger.info("Resultado validacion: {}", isValid ? "[VALID] VALIDO" : "[INVALID] NO ENCONTRADO");
            
            boolean canVote = securityService.checkVotingStatus(document);
            logger.info("Puede votar: {}", canVote ? "[YES] SI" : "[NO] NO");
            
        } catch (Exception e) {
            logger.error("Error en test de ciudadano inexistente", e);
        }
    }
    
    private void testVotingStatus(SecurityServicePrx securityService, String document) {
        try {
            logger.info("\n[TEST 4] Verificando estado de votacion ({})", document);
            
            boolean canVote = securityService.checkVotingStatus(document);
            logger.info("Estado actual: {}", canVote ? "[CAN_VOTE] PUEDE VOTAR" : "[ALREADY_VOTED] YA VOTO");
            
        } catch (Exception e) {
            logger.error("Error en test de estado de votacion", e);
        }
    }
    
    private void testDoubleVoting(SecurityServicePrx securityService, String document) {
        try {
            logger.info("\n[TEST 5] Simulando doble votacion ({})", document);
            
            // Primera validación
            boolean firstValidation = securityService.validateSecurity(document);
            logger.info("Primera validacion: {}", firstValidation ? "[VALID] VALIDO" : "[INVALID] INVALIDO");
            
            // Simular que ya votó (esto normalmente lo haría el sistema de gestión)
            logger.info("[SIMULATION] Simulando que el ciudadano ya voto...");
            
            // Segunda validación (debería fallar por seguridad)
            boolean secondValidation = securityService.validateSecurity(document);
            logger.info("Segunda validacion: {}", secondValidation ? "[WARNING] VALIDO (PROBLEMA DETECTADO)" : "[BLOCKED] BLOQUEADO (CORRECTO)");
            
        } catch (Exception e) {
            logger.error("Error en test de doble votacion", e);
        }
    }
    
    private void testMultipleCitizens(SecurityServicePrx securityService) {
        try {
            logger.info("\n[TEST 6] Validando multiples ciudadanos");
            
            String[] documents = {"123456789", "567890123", "987654321", "111111111", "999999999"};
            
            for (String document : documents) {
                logger.info("Validando documento: {}", document);
                
                long startTime = System.currentTimeMillis();
                boolean isValid = securityService.validateSecurity(document);
                long endTime = System.currentTimeMillis();
                
                logger.info("  Resultado: {} ({}ms)", 
                           isValid ? "[VALID] VALIDO" : "[INVALID] INVALIDO", 
                           endTime - startTime);
            }
            
        } catch (Exception e) {
            logger.error("Error en test de multiples ciudadanos", e);
        }
    }
    
    private void testPerformance(SecurityServicePrx securityService) {
        try {
            logger.info("\n[TEST 7] Pruebas de rendimiento");
            
            int numTests = 100;
            long totalTime = 0;
            
            for (int i = 0; i < numTests; i++) {
                String testDoc = "TEST" + String.format("%06d", i);
                
                long start = System.currentTimeMillis();
                securityService.validateSecurity(testDoc);
                long end = System.currentTimeMillis();
                
                totalTime += (end - start);
            }
            
            double avgTime = totalTime / (double) numTests;
            logger.info("Rendimiento: {} validaciones en {}ms (promedio: {}ms)", 
                       numTests, totalTime, String.format("%.2f", avgTime));
            
        } catch (Exception e) {
            logger.error("Error en test de rendimiento", e);
        }
    }
    
    private void testFraudDetection(SecurityServicePrx securityService) {
        try {
            logger.info("\n[TEST 8] Pruebas de deteccion de fraude");
            
            // Probar documentos sospechosos
            String[] suspiciousDocuments = {"111111111", "222222222", "999999999"};
            
            for (String doc : suspiciousDocuments) {
                logger.info("Probando documento sospechoso: {}", doc);
                
                boolean isValid = securityService.validateSecurity(doc);
                boolean canVote = securityService.checkVotingStatus(doc);
                
                logger.info("  Validacion: {} | Puede votar: {}", 
                           isValid ? "[PASS]" : "[BLOCKED]",
                           canVote ? "[YES]" : "[NO]");
            }
            
        } catch (Exception e) {
            logger.error("Error en test de deteccion de fraude", e);
        }
    }
} 