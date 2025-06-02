#!/bin/bash

# Crear directorio de logs si no existe
mkdir -p logs

# Función para iniciar un servicio
start_service() {
    local module=$1
    local main_class=$2
    local log_file="logs/${module}.log"
    
    echo "Iniciando servicio $module..."
    java -jar $module/build/libs/$module.jar > $log_file 2>&1 &
    echo "Servicio $module iniciado. PID: $!"
    echo "Log: $log_file"
    echo
}

# Esperar a que un servicio esté disponible
wait_for_service() {
    local port=$1
    local service=$2
    local max_attempts=30
    local attempt=1
    
    echo "Esperando a que $service esté disponible en el puerto $port..."
    while ! nc -z localhost $port && [ $attempt -le $max_attempts ]; do
        sleep 1
        ((attempt++))
    done
    
    if [ $attempt -le $max_attempts ]; then
        echo "$service está disponible"
        echo
    else
        echo "$service no respondió después de $max_attempts intentos"
        echo
    fi
}

# Iniciar servicios en orden
start_service "reliablemsg" "com.sistemaelectoral.reliablemsg.MessageServer"
wait_for_service 10004 "Servicio de Mensajería"

start_service "fiscalia" "com.sistemaelectoral.fiscalia.FiscaliaServer"
wait_for_service 10005 "Servicio de Fiscalía"

start_service "portal" "com.sistemaelectoral.portal.PortalServer"
wait_for_service 10003 "Servicio de Portal"

start_service "votacion" "com.sistemaelectoral.votacion.VotacionServer"
wait_for_service 10000 "Servicio de Votación"

start_service "consulta" "com.sistemaelectoral.consulta.ConsultaServer"
wait_for_service 10001 "Servicio de Consulta"

echo "Todos los servicios han sido iniciados."
echo "Para detener los servicios, ejecute: ./stop_services.sh" 