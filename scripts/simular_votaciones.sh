#!/bin/bash

# Configuración
TOTAL_CIUDADANOS=4000  # 1000 por colegio * 4 colegios
MESAS_POR_COLEGIO=25
TOTAL_COLEGIOS=4
JAR_PATH="estacion-de-votacion-local/build/libs/estacion-de-votacion-local-1.0-SNAPSHOT.jar"

# Verificar que el JAR existe
if [ ! -f "$JAR_PATH" ]; then
    echo "Error: No se encontró el archivo JAR en $JAR_PATH"
    echo "Asegúrate de haber compilado el proyecto primero con ./gradlew build"
    exit 1
fi

# Función para activar una mesa
activar_mesa() {
    local colegio=$1
    local mesa=$2
    echo "Activando mesa $mesa en $colegio"
    java -jar "$JAR_PATH" "$colegio" "$mesa" &
    sleep 2  # Esperar a que la mesa se active
}

# Función para cerrar una mesa
cerrar_mesa() {
    local colegio=$1
    local mesa=$2
    echo "Cerrando mesa $mesa en $colegio"
    # Aquí iría el comando para cerrar la mesa
    # Por ahora solo simulamos el proceso
    sleep 1
}

# Función para simular votos en una mesa
simular_votos_mesa() {
    local colegio=$1
    local mesa=$2
    local ciudadanos_por_mesa=$((TOTAL_CIUDADANOS / (TOTAL_COLEGIOS * MESAS_POR_COLEGIO)))
    
    echo "Simulando $ciudadanos_por_mesa votos en mesa $mesa de $colegio"
    
    # Simular votos distribuidos entre los 4 candidatos
    for ((i=1; i<=ciudadanos_por_mesa; i++)); do
        # Seleccionar candidato aleatorio (1-4)
        candidato=$((RANDOM % 4 + 1))
        # Generar documento de ciudadano único
        documento=$((1000000000 + (mesa * 1000) + i))
        
        echo "Ciudadano $documento votando por candidato $candidato en mesa $mesa"
        # Aquí iría la lógica para enviar el voto al sistema
        # Por ahora solo simulamos el proceso
        sleep 0.1  # Pequeña pausa para no sobrecargar
    done
}

# Lista de colegios (solo 4)
colegios=(
    "Colegio San Pedro"
    "Instituto Técnico Sur"
    "Liceo Central"
    "Colegio Oriental"
)

echo "Iniciando simulación de votaciones..."
echo "Total de ciudadanos: $TOTAL_CIUDADANOS"
echo "Mesas por colegio: $MESAS_POR_COLEGIO"
echo "Total de colegios: $TOTAL_COLEGIOS"

# Activar todas las mesas y simular votos
for colegio in "${colegios[@]}"; do
    echo "=========================================="
    echo "Procesando colegio: $colegio"
    echo "=========================================="
    
    # Activar todas las mesas del colegio
    echo "Activando mesas..."
    for ((mesa=1; mesa<=MESAS_POR_COLEGIO; mesa++)); do
        activar_mesa "$colegio" "$mesa"
    done
    
    # Esperar a que todas las mesas estén activas
    echo "Esperando a que las mesas estén activas..."
    sleep 5
    
    # Simular votos en cada mesa
    echo "Iniciando proceso de votación..."
    for ((mesa=1; mesa<=MESAS_POR_COLEGIO; mesa++)); do
        simular_votos_mesa "$colegio" "$mesa"
    done
    
    # Cerrar todas las mesas del colegio
    echo "Cerrando mesas..."
    for ((mesa=1; mesa<=MESAS_POR_COLEGIO; mesa++)); do
        cerrar_mesa "$colegio" "$mesa"
    done
    
    echo "Completado procesamiento de $colegio"
    echo "Esperando 5 segundos antes de procesar el siguiente colegio..."
    sleep 5
done

echo "=========================================="
echo "Simulación de votaciones completada"
echo "Puedes verificar los resultados en la base de datos"
echo "==========================================" 