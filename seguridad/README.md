# 🔒 Nodo de Seguridad - Sistema de Votaciones

## **📋 DESCRIPCIÓN**

El **Nodo de Seguridad** es un componente crítico del sistema de votaciones que implementa los requerimientos de seguridad E8, E12 y E13:

- **E8**: Validar que un ciudadano solo vote una vez desde su mesa asignada
- **E12**: Verificar antecedentes criminales consultando la API de fiscalía
- **E13**: Detectar intentos de doble votación y generar alertas

---

## **🏗️ ARQUITECTURA**

### **Componentes Principales:**

1. **SecurityServiceI** - Implementación de la interfaz ICE principal
2. **ControladorDatos** - Gestión de datos de ciudadanos y validaciones
3. **ProxyCacheFiscalia** - Cache inteligente para consultas de fiscalía (TTL: 1 hora)
4. **ConexionSistemaFiscal** - Conexión simulada con sistema fiscal externo
5. **SistemaAlertas** - Procesamiento asíncrono de alertas de seguridad
6. **SecurityDatabaseConnection** - Pool de conexiones PostgreSQL optimizado

### **Modelos de Datos:**

- **CiudadanoInfo** - Información completa del ciudadano
- **AlertaSeguridad** - Eventos de seguridad con diferentes severidades

---

## **🔗 CONEXIONES ICE**

- **Puerto**: `10005`
- **Servicios expuestos**:
  - `SecurityService::validateSecurity(string document) -> bool`
  - `SecurityService::checkVotingStatus(string document) -> bool`

### **Clientes que se conectan:**
- **Estación de Votación Local** (puerto 10003)
- **Sistema de Gestión de Mesas** (puerto 10004)

---

## **📊 BASE DE DATOS**

### **Conexión PostgreSQL:**
```
Host: localhost:5432
Database: sistema_votaciones
Usuario: postgres
Contraseña: postgres
```

### **Tablas utilizadas:**
- `ciudadanos` - Información de ciudadanos registrados
- `votos` - Registro de votos emitidos
- `sospechosos` - Lista de ciudadanos con alertas de seguridad

---

## **⚙️ COMPILACIÓN Y EJECUCIÓN**

### **Pre-requisitos:**
1. **Java 11+** instalado
2. **PostgreSQL** ejecutándose con la base `sistema_votaciones`
3. **ICE 3.7.x** instalado (opcional para slice2java)

### **Compilar el proyecto:**
```bash
# Desde el directorio raíz del proyecto
./gradlew :seguridad:build
```

### **Ejecutar el servidor de seguridad:**

#### **Opción 1: Con Gradle**
```bash
./gradlew :seguridad:runSecurityServer
```

#### **Opción 2: JAR directo**
```bash
java -jar seguridad/build/libs/seguridad.jar
```

#### **Opción 3: Con classpath completo**
```bash
cd seguridad
java -cp "build/classes/java/main:build/resources/main:~/.gradle/caches/modules-2/files-2.1/**/*.jar" com.votaciones.seguridad.SecurityServer
```

---

## **🧪 PRUEBAS**

### **Ejecutar cliente de prueba:**
```bash
# Asegurar que el servidor esté ejecutándose en puerto 10005
java -cp "build/classes/java/main:..." com.votaciones.seguridad.SecurityTestClient
```

### **Casos de prueba incluidos:**
1. ✅ **Ciudadano válido** - Documento: `567890123`
2. ⚠️ **Ciudadano con antecedentes** - Documento: `111111111`
3. ❌ **Ciudadano inexistente** - Documento: `000000000`
4. 🗳️ **Verificación de estado de votación**
5. 🚨 **Simulación de doble votación**
6. 👥 **Pruebas de rendimiento con múltiples ciudadanos**

---

## **📝 LOGS**

### **Archivos de log generados:**
- `logs/seguridad.log` - Log general del sistema
- `logs/security-alerts.log` - Solo alertas críticas
- **Consola** - Output en tiempo real

### **Niveles de log:**
- **DEBUG** - Información detallada de funcionamiento
- **INFO** - Eventos normales del sistema
- **WARN** - Situaciones sospechosas
- **ERROR** - Alertas críticas de seguridad

---

## **🚨 SISTEMA DE ALERTAS**

### **Tipos de alertas:**
- **CRITICA** - Doble votación, fraude detectado
- **ALTA** - Antecedentes criminales
- **MEDIA** - Mesa/zona incorrecta
- **BAJA** - Eventos informativos

### **Procesamiento:**
- **Asíncrono** con cola de mensajes
- **Historial** de últimas 1000 alertas
- **Notificaciones** automáticas a administradores

---

## **⚡ RENDIMIENTO**

### **Optimizaciones implementadas:**
- **Cache de ciudadanos** en memoria (ConcurrentHashMap)
- **Cache de consultas fiscalía** con TTL
- **Pool de conexiones** HikariCP optimizado
- **Consultas SQL** preparadas y optimizadas

### **Métricas esperadas:**
- **Latencia** < 3 segundos (requerimiento E1)
- **Throughput** > 2.666 consultas/segundo
- **Disponibilidad** 99.9%

---

## **🔧 CONFIGURACIÓN**

### **Parámetros configurables:**
```java
// Puerto ICE
--Ice.Default.Port=10005

// Pool de conexiones
MaximumPoolSize=10
MinimumIdle=2
ConnectionTimeout=30000ms

// Cache TTL
CACHE_TTL_HOURS=1

// Simulación fiscalía
simulacionActivada=true
tiempoRespuestaMs=500
```

---

## **🐛 SOLUCIÓN DE PROBLEMAS**

### **Error: "No se pudo conectar a PostgreSQL"**
```bash
# Verificar PostgreSQL está corriendo
sudo systemctl status postgresql

# Verificar credenciales
psql -h localhost -U postgres -d sistema_votaciones
```

### **Error: "ICE connection refused"**
```bash
# Verificar puerto no esté ocupado
netstat -tlnp | grep 10005

# Verificar firewall
sudo ufw status
```

### **Error: "Clases ICE no encontradas"**
```bash
# Generar clases ICE manualmente
slice2java --output-dir src/main/java src/main/slice/SecurityModule.ice
```

---

## **🔗 INTEGRACIÓN CON OTROS NODOS**

### **Con Estación de Votación Local:**
```java
// Validar antes de permitir voto
boolean valid = securityService.validateSecurity(documento);
if (!valid) {
    // Mostrar error y bloquear voto
}
```

### **Con Sistema de Gestión de Mesas:**
```java
// Verificar estado antes de procesar voto
boolean canVote = securityService.checkVotingStatus(documento);
```

---

## **📊 MONITOREO**

### **Métricas disponibles:**
- Cantidad de validaciones por minuto
- Tiempo promedio de respuesta
- Alertas generadas por tipo
- Cache hit ratio
- Conexiones de base de datos activas

### **Dashboards recomendados:**
- Grafana + InfluxDB para métricas en tiempo real
- ELK Stack para análisis de logs
- Prometheus para alerting

---

## **🔐 SEGURIDAD**

### **Medidas implementadas:**
- **Validación estricta** de parámetros de entrada
- **Sanitización** de consultas SQL
- **Logging** de todos los eventos de seguridad
- **Rate limiting** implícito por cache
- **Timeout** en consultas externas

### **Recomendaciones adicionales:**
- Ejecutar en red privada/VPN
- Usar HTTPS para APIs externas
- Implementar autenticación mutua ICE
- Monitoreo continuo de logs de seguridad

---

## **📞 SOPORTE**

Para problemas o preguntas sobre el nodo de seguridad:

1. **Revisar logs** en `logs/seguridad.log`
2. **Verificar conexiones** de red y base de datos
3. **Ejecutar cliente de prueba** para diagnóstico
4. **Consultar documentación** de arquitectura general

---

**🔒 Sistema de Seguridad - Protegiendo la integridad electoral** 