@echo off
echo =================================================
echo  PRUEBAS DE INTEGRACION - SISTEMA DE VOTACIONES
echo =================================================
echo.

echo [INTEGRATION] Verificando que todos los servicios esten corriendo...
echo.

:: Verificar Nodo de Seguridad (Puerto 10005)
echo [TEST 1] Probando conexion al Nodo de Seguridad (puerto 10005)...
telnet 127.0.0.1 10005 2>nul | findstr "Connected" >nul
if %errorlevel%==0 (
    echo   [OK] Nodo de Seguridad ACTIVO
    set SECURITY_OK=1
) else (
    echo   [ERROR] Nodo de Seguridad NO RESPONDE
    set SECURITY_OK=0
)

:: Verificar Estacion de Votacion (Puerto 10003)
echo [TEST 2] Probando conexion a Estacion de Votacion (puerto 10003)...
netstat -an | findstr :10003 | findstr LISTENING >nul
if %errorlevel%==0 (
    echo   [OK] Estacion de Votacion ACTIVA
    set STATION_OK=1
) else (
    echo   [ERROR] Estacion de Votacion NO ACTIVA
    set STATION_OK=0
)

:: Verificar Gestion de Mesas (Puerto 10004)
echo [TEST 3] Probando conexion a Gestion de Mesas (puerto 10004)...
netstat -an | findstr :10004 | findstr LISTENING >nul
if %errorlevel%==0 (
    echo   [OK] Gestion de Mesas ACTIVA
    set MANAGEMENT_OK=1
) else (
    echo   [ERROR] Gestion de Mesas NO ACTIVA
    set MANAGEMENT_OK=0
)

echo.
echo [SUMMARY] Estado de los servicios:
echo   - Nodo de Seguridad (10005): %SECURITY_OK%
echo   - Estacion de Votacion (10003): %STATION_OK%  
echo   - Gestion de Mesas (10004): %MANAGEMENT_OK%
echo.

if %SECURITY_OK%==1 (
    echo [INTEGRATION] Ejecutando cliente de pruebas del nodo de seguridad...
    echo.
    cd seguridad
    call run-security-test.bat
    cd ..
) else (
    echo [ERROR] No se pueden ejecutar las pruebas sin el nodo de seguridad
)

echo.
echo [INFO] Para monitorear en tiempo real los logs:
echo [INFO] - Seguridad: tail -f seguridad/logs/security.log
echo [INFO] - Estacion: tail -f estacion-de-votacion-local/logs/application.log
echo [INFO] - Gestion: tail -f sistema-de-gestion-mesas/logs/application.log
echo.
pause 