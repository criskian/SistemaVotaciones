@echo off
REM Lanzador silencioso - Solo abre la ventana del Sistema de Gestión de Mesas
REM Sin mostrar ninguna consola (como el Portal Web)

cd /d "%~dp0"

REM Usar javaw (no java) para evitar ventana de consola
javaw -cp "lib/*;build/classes" com.votaciones.mainserver.DemoGestionMesas 