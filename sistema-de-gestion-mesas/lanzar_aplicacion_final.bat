@echo off
REM Lanzador de la Aplicación Final del Sistema de Gestión de Mesas
REM Conecta a PostgreSQL real y muestra datos reales

cd /d "%~dp0"

echo ========================================
echo   SISTEMA DE GESTION DE MESAS - FINAL
echo ========================================
echo.
echo Verificando requisitos...
echo - PostgreSQL debe estar en puerto 5433
echo - Base de datos 'sistema_votaciones' debe existir
echo.

REM Ejecutar la aplicación final
echo Iniciando aplicación...
javaw -cp build/classes com.votaciones.mainserver.DemoGestionMesasSimple

echo.
echo Aplicación iniciada. La ventana debería aparecer.
echo Si no aparece, verifique:
echo 1. PostgreSQL ejecutándose en puerto 5433
echo 2. Base de datos 'sistema_votaciones' existe
echo 3. Clases compiladas en build/classes/
echo.
pause 