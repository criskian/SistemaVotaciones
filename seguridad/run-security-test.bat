@echo off
echo ======================================
echo    CLIENTE DE PRUEBAS - SEGURIDAD
echo    Sistema de Votaciones Electronicas
echo ======================================
echo.

echo [TEST] Iniciando pruebas del nodo de seguridad...
echo [TEST] Conectando al servidor en puerto 10005...
echo.

cd /d "%~dp0"

:: Construir classpath (igual que el servidor)
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

echo [TEST] PRUEBAS INCLUIDAS:
echo [TEST] 1. Validacion ciudadano valido
echo [TEST] 2. Ciudadano con antecedentes criminales
echo [TEST] 3. Ciudadano inexistente
echo [TEST] 4. Estado de votacion
echo [TEST] 5. Simulacion doble votacion
echo [TEST] 6. Validacion multiple ciudadanos
echo [TEST] 7. Pruebas de rendimiento
echo [TEST] 8. Deteccion de fraude
echo.

java -cp "%CLASSPATH%" ^
    -Dfile.encoding=UTF-8 ^
    -Xms128m ^
    -Xmx256m ^
    com.votaciones.seguridad.SecurityTestClient

echo.
echo [TEST] Pruebas completadas
pause 