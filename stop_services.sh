#!/bin/bash

# Función para detener un servicio por su nombre de clase principal
stop_service() {
    local main_class=$1
    local pid=$(ps aux | grep "$main_class" | grep -v grep | awk '{print $2}')
    
    if [ ! -z "$pid" ]; then
        echo "Deteniendo $main_class (PID: $pid)..."
        kill $pid
        echo "Servicio detenido"
    else
        echo "$main_class no está en ejecución"
    fi
}

# Detener servicios en orden inverso
echo "Deteniendo servicios..."
stop_service "com.sistemaelectoral.consulta.ConsultaServer"
stop_service "com.sistemaelectoral.votacion.VotacionServer"
stop_service "com.sistemaelectoral.portal.PortalServer"
stop_service "com.sistemaelectoral.fiscalia.FiscaliaServer"
stop_service "com.sistemaelectoral.reliablemsg.MessageServer"

# Eliminar la aplicación de IceGrid
echo "Eliminando aplicación de IceGrid..."
icegridadmin --Ice.Config=config/grid.properties -e "application remove SistemaElectoral"

# Detener IceGrid node y registry
echo "Deteniendo servicios IceGrid..."
pkill -x "icegridnode"
pkill -x "icegridregistry"

# Detener ActiveMQ
echo "Deteniendo ActiveMQ..."
activemq stop

echo "Todos los servicios han sido detenidos." 