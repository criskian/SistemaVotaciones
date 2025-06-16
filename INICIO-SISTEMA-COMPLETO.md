# 🗳️ GUÍA COMPLETA: SISTEMA DE VOTACIONES CON SEGURIDAD

## 📋 **REQUISITOS PREVIOS**

1. **PostgreSQL corriendo** en puerto 5432
   - Usuario: `postgres`
   - Contraseña: `postgres`
   - Base de datos: `sistema_votaciones`

2. **Puertos disponibles:**
   - 10003: Estación de Votación
   - 10004: Gestión de Mesas
   - 10005: **Nodo de Seguridad** (NUEVO)

---

## 🚀 **PASO 1: VERIFICAR PUERTOS**

```bash
# Ejecutar desde la raíz del proyecto
check-ports.bat
```

✅ **Debe mostrar todos los puertos LIBRES** (excepto PostgreSQL que debe estar ACTIVO)

---

## 🔐 **PASO 2: INICIAR NODO DE SEGURIDAD (PRIMERO)**

**Ventana 1 - Seguridad:**
```bash
cd seguridad
run-security-server.bat
```

✅ **Señales de éxito:**
- `[SECURITY] Iniciando nodo de seguridad...`
- `[INFO] Conexión a PostgreSQL exitosa`
- `[INFO] Servidor de seguridad iniciado en puerto 10005`
- `[SECURITY] Sistema de alertas activado`

---

## 🗳️ **PASO 3: INICIAR ESTACIÓN DE VOTACIÓN**

**Ventana 2 - Estación:**
```bash
cd estacion-de-votacion-local
# Usar el script existente
run-server.bat
```

✅ **Debe conectarse automáticamente al nodo de seguridad**

---

## 📊 **PASO 4: INICIAR GESTIÓN DE MESAS**

**Ventana 3 - Gestión:**
```bash
cd sistema-de-gestion-mesas  
# Usar el script existente
run-server.bat
```

---

## 🧪 **PASO 5: VERIFICAR INTEGRACIÓN COMPLETA**

**Ventana 4 - Pruebas:**
```bash
# Desde la raíz del proyecto
test-integration.bat
```

✅ **Debe mostrar:**
- ✅ Nodo de Seguridad ACTIVO
- ✅ Estación de Votación ACTIVA  
- ✅ Gestión de Mesas ACTIVA
- ✅ Pruebas de seguridad EXITOSAS

---

## 🔍 **VERIFICACIÓN MANUAL**

### **Comprobar procesos Java:**
```bash
jps -l
```
Debes ver 3 procesos:
- `com.votaciones.seguridad.SecurityServer`
- `com.votaciones.estacion.EstacionVotacionServer`
- `com.votaciones.gestion.GestionMesasServer`

### **Comprobar puertos en uso:**
```bash
netstat -an | findstr "10003 10004 10005"
```
Debes ver los 3 puertos en estado `LISTENING`

---

## 📊 **MONITOREO EN TIEMPO REAL**

### **Logs del Nodo de Seguridad:**
```bash
# Windows
type seguridad\logs\security.log

# Ver logs en vivo (si tienes tail)
tail -f seguridad\logs\security.log
```

### **Logs de Alertas de Seguridad:**
```bash
type seguridad\logs\security-alerts.log
```

---

## 🔬 **PRUEBAS ESPECÍFICAS DE SEGURIDAD**

### **Prueba 1: Validación básica**
```bash
cd seguridad
run-security-test.bat
```

### **Prueba 2: Integración con otros módulos**
1. **Ir a la estación de votación** (interfaz web)
2. **Intentar votar con cédula:** `111111111` 
   - ❌ **Debe ser BLOQUEADO** (antecedentes)
3. **Intentar votar con cédula:** `567890123`
   - ✅ **Debe ser PERMITIDO** (ciudadano válido)

---

## 🚨 **REQUERIMIENTOS IMPLEMENTADOS**

### **✅ E8: Mesa correcta**
- El sistema valida que el ciudadano vote en su mesa/zona asignada
- Se bloquea el voto si intenta votar en mesa incorrecta

### **✅ E12: Antecedentes criminales**  
- Consulta automática a sistema de fiscalía
- Bloquea votos de ciudadanos con antecedentes
- Cache inteligente para optimizar consultas

### **✅ E13: Detección doble votación**
- Rastrea en tiempo real quién ya votó
- Bloquea intentos de voto múltiple
- Alertas automáticas por intentos de fraude

---

## 🔧 **SOLUCIÓN DE PROBLEMAS**

### **Error: "No se pudo conectar al nodo de seguridad"**
1. Verificar que PostgreSQL está corriendo
2. Verificar que el puerto 10005 está libre
3. Reiniciar el nodo de seguridad

### **Error: "Puerto ya en uso"**
```bash
# Encontrar qué está usando el puerto
netstat -ano | findstr :10005
# Matar el proceso (reemplazar PID)
taskkill /PID 1234 /F
```

### **Error de base de datos**
1. Verificar contraseña de PostgreSQL
2. Crear base de datos: `CREATE DATABASE sistema_votaciones;`
3. El sistema crea automáticamente las tablas necesarias

---

## 📈 **MONITOREO DE RENDIMIENTO**

### **Estadísticas en tiempo real:**
- Validaciones por segundo
- Cache hits de fiscalía  
- Alertas generadas
- Conexiones activas

### **Métricas de seguridad:**
- Ciudadanos bloqueados
- Intentos de doble votación
- Validaciones de mesa/zona
- Tiempo promedio de respuesta

---

## 🎯 **CONFIGURACIÓN PARA PRODUCCIÓN**

### **Variables de entorno recomendadas:**
```bash
# Base de datos
DB_HOST=localhost
DB_PORT=5432
DB_NAME=sistema_votaciones
DB_USER=postgres
DB_PASSWORD=postgres

# Seguridad
SECURITY_PORT=10005
CACHE_TTL=3600
ALERT_QUEUE_SIZE=1000
```

### **Recursos mínimos:**
- **RAM:** 512MB para el nodo de seguridad
- **CPU:** 2 cores mínimo
- **Disco:** 1GB para logs y cache
- **Red:** 1Mbps para comunicación ICE

---

## ✅ **VALIDACIÓN FINAL**

El sistema está **FUNCIONANDO CORRECTAMENTE** cuando:

1. ✅ Los 3 módulos inician sin errores
2. ✅ Las pruebas de integración pasan
3. ✅ Los logs no muestran errores críticos
4. ✅ El cliente de pruebas ejecuta exitosamente
5. ✅ Se pueden realizar votaciones con validación de seguridad

---

## 📞 **SOPORTE**

Si encuentras problemas:
1. Revisar logs en `seguridad/logs/`
2. Ejecutar `test-integration.bat`
3. Verificar que PostgreSQL está funcionando
4. Comprobar que no hay conflictos de puertos 