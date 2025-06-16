@echo off
echo ======================================
echo    NODO DE SEGURIDAD - SERVIDOR
echo    Sistema de Votaciones Electronicas
echo ======================================
echo.

echo [INFO] Iniciando servidor de seguridad en puerto 10005...
echo [INFO] Presiona Ctrl+C para detener el servidor
echo.

cd /d "%~dp0"

:: Obtener todas las dependencias del classpath
for /f "delims=" %%i in ('gradlew.bat :seguridad:printClasspath 2^>nul') do set CLASSPATH=%%i

:: Si no se pudo obtener el classpath con gradle, usar método alternativo
if "%CLASSPATH%"=="" (
    echo [INFO] Construyendo classpath desde cache de Gradle...
    set GRADLE_CACHE=%USERPROFILE%\.gradle\caches\modules-2\files-2.1
    
    :: Buscar JARs necesarios
    for /r "%GRADLE_CACHE%" %%f in (ice-3.7.6.jar) do set ICE_JAR=%%f
    for /r "%GRADLE_CACHE%" %%f in (postgresql-42.6.0.jar) do set POSTGRES_JAR=%%f
    for /r "%GRADLE_CACHE%" %%f in (HikariCP-5.0.1.jar) do set HIKARI_JAR=%%f
    for /r "%GRADLE_CACHE%" %%f in (slf4j-api-2.0.7.jar) do set SLF4J_JAR=%%f
    for /r "%GRADLE_CACHE%" %%f in (logback-classic-1.4.8.jar) do set LOGBACK_JAR=%%f
    for /r "%GRADLE_CACHE%" %%f in (logback-core-1.4.8.jar) do set LOGBACK_CORE_JAR=%%f
    
    :: Construir classpath
    set CLASSPATH=build\classes\java\main;build\resources\main
    set CLASSPATH=%CLASSPATH%;%ICE_JAR%
    set CLASSPATH=%CLASSPATH%;%POSTGRES_JAR%
    set CLASSPATH=%CLASSPATH%;%HIKARI_JAR%
    set CLASSPATH=%CLASSPATH%;%SLF4J_JAR%
    set CLASSPATH=%CLASSPATH%;%LOGBACK_JAR%
    set CLASSPATH=%CLASSPATH%;%LOGBACK_CORE_JAR%
)

echo [INFO] Verificando que PostgreSQL esté corriendo...
echo [INFO] Base de datos: sistema_votaciones
echo [INFO] Usuario: postgres
echo.

:: Crear directorio de logs si no existe
if not exist "logs" mkdir logs

echo [SECURITY] Iniciando nodo de seguridad...
echo [SECURITY] Funcionalidades habilitadas:
echo [SECURITY] - E8: Validacion mesa/zona asignada
echo [SECURITY] - E12: Verificacion antecedentes criminales
echo [SECURITY] - E13: Deteccion doble votacion
echo [SECURITY] - Cache inteligente para fiscalia
echo [SECURITY] - Sistema de alertas en tiempo real
echo.

java -cp "%CLASSPATH%" ^
    -Djava.util.logging.config.file=logging.properties ^
    -Dfile.encoding=UTF-8 ^
    -Xms256m ^
    -Xmx512m ^
    -server ^
    com.votaciones.seguridad.SecurityServer

echo.
echo [INFO] Servidor de seguridad detenido
pause 