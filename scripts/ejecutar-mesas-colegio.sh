#!/bin/bash

# Verificar si se proporcionó el nombre del colegio
if [ -z "$1" ]; then
    echo "Uso: ./ejecutar-mesas-colegio.sh 'Nombre del Colegio'"
    echo "Ejemplo: ./ejecutar-mesas-colegio.sh 'Colegio San Pedro'"
    exit 1
fi

NOMBRE_COLEGIO="$1"
JAR_PATH="estacion-de-votacion-local/build/libs/estacion-de-votacion-local-1.0-SNAPSHOT.jar"

# Verificar si el archivo JAR existe
if [ ! -f "$JAR_PATH" ]; then
    echo "Error: No se encontró el archivo JAR en $JAR_PATH"
    echo "Asegúrate de haber compilado el proyecto primero con ./gradlew build"
    exit 1
fi

echo "Iniciando 25 mesas para el colegio: $NOMBRE_COLEGIO"

# Ejecutar las 25 mesas en segundo plano
for i in {1..25}
do
    echo "Iniciando mesa $i para $NOMBRE_COLEGIO"
    java -jar "$JAR_PATH" "$NOMBRE_COLEGIO" "$i" &
    # Pequeña pausa para evitar sobrecarga
    sleep 1
done

echo "Todas las mesas han sido iniciadas"
echo "Puedes verificar los logs en la carpeta logs/" 