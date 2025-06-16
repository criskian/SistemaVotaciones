@echo off
REM Script para lanzar el Sistema de Gestión de Mesas sin consola
REM Similar al comportamiento del Portal Web de Consulta

cd /d "%~dp0"

REM Lanzar la aplicación Java sin ventana de consola
start "Sistema de Gestión de Mesas" /B javaw -cp "lib/*;build/classes" com.votaciones.mainserver.DemoGestionMesas

REM Mensaje informativo (opcional)
echo Sistema de Gestión de Mesas iniciado...
echo La ventana debería aparecer en unos segundos.
echo.
echo Si no aparece, verifique que:
echo - PostgreSQL esté ejecutándose en puerto 5433
echo - Las librerías estén en la carpeta lib/
echo - El archivo esté compilado en build/classes/
echo.
pause 