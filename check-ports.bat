@echo off
echo =============================================
echo  VERIFICACION DE PUERTOS - SISTEMA VOTACION
echo =============================================
echo.

echo [CHECK] Verificando puertos necesarios...
echo.

echo Puerto 10003 (Estacion de Votacion):
netstat -an | findstr :10003 >nul
if %errorlevel%==0 (
    echo   [OCUPADO] Puerto 10003 ya esta en uso
) else (
    echo   [LIBRE] Puerto 10003 disponible
)

echo Puerto 10004 (Gestion de Mesas):
netstat -an | findstr :10004 >nul
if %errorlevel%==0 (
    echo   [OCUPADO] Puerto 10004 ya esta en uso
) else (
    echo   [LIBRE] Puerto 10004 disponible
)

echo Puerto 10005 (Nodo de Seguridad):
netstat -an | findstr :10005 >nul
if %errorlevel%==0 (
    echo   [OCUPADO] Puerto 10005 ya esta en uso
) else (
    echo   [LIBRE] Puerto 10005 disponible
)

echo Puerto 5432 (PostgreSQL):
netstat -an | findstr :5432 >nul
if %errorlevel%==0 (
    echo   [ACTIVO] PostgreSQL corriendo en puerto 5432
) else (
    echo   [ERROR] PostgreSQL NO detectado en puerto 5432
)

echo.
echo [INFO] Para iniciar el sistema completo:
echo [INFO] 1. Ejecutar: seguridad\run-security-server.bat
echo [INFO] 2. Ejecutar: estacion-de-votacion-local\run-server.bat  
echo [INFO] 3. Ejecutar: sistema-de-gestion-mesas\run-server.bat
echo.
pause 