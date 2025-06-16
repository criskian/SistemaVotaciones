@echo off
echo ===============================================
echo    ESTACION DE VOTACION LOCAL - SISTEMA INTEGRADO
echo ===============================================
echo.
echo Integracion completa de 3 nodos:
echo - Estacion de Votacion Local (este nodo)
echo - Sistema de Gestion de Mesas (puerto 10004)
echo - Nodo de Seguridad (puerto 10005)
echo.
echo Validaciones habilitadas:
echo - E8: Mesa/Zona asignada
echo - E12: Antecedentes criminales (fiscalia)
echo - E13: Doble votacion
echo.

REM Navegar al directorio del proyecto
cd /d "%~dp0"

REM Verificar que existe el archivo JAR
if not exist "estacion-de-votacion-local\build\libs\estacion-de-votacion-local-1.0-SNAPSHOT.jar" (
    echo ERROR: No se encontro el JAR de la estacion de votacion
    echo Ejecuta primero: gradlew :estacion-de-votacion-local:build
    pause
    exit /b 1
)

REM Verificar que existe mesas.properties
if not exist "mesas.properties" (
    echo ERROR: No se encontro mesas.properties
    echo Creando archivo de ejemplo...
    echo # Configuracion de Mesas de Votacion > mesas.properties
    echo # Formato: "Colegio|NumeroMesa=IdMesa,Puerto,IdZona" >> mesas.properties
    echo Colegio Test^|1=1,10010,1 >> mesas.properties
    echo Archivo creado. Puedes editarlo si es necesario.
    echo.
)

echo Configuraciones disponibles en mesas.properties:
type mesas.properties | findstr /v "#"
echo.

set /p colegio="Ingresa el nombre del colegio (ej: Colegio Test): "
set /p mesa="Ingresa el numero de mesa (ej: 1): "

if "%colegio%"=="" set colegio=Colegio Test
if "%mesa%"=="" set mesa=1

echo.
echo ===============================================
echo Iniciando Estacion de Votacion...
echo Colegio: %colegio%
echo Mesa: %mesa%
echo ===============================================
echo.

REM Ejecutar la estacion de votacion
java -cp estacion-de-votacion-local\build\libs\estacion-de-votacion-local-1.0-SNAPSHOT.jar com.votaciones.estacion.ui.EstacionVotacionUI "%colegio%" %mesa%

echo.
echo Estacion de votacion cerrada.
pause 