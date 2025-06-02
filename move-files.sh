#!/bin/bash

# Lista de módulos
MODULES=("portal" "votacion" "fiscalia" "seguridad" "electoraldb" "reliablemsg" "common" "interfaces")

# Mover archivos para cada módulo
for MODULE in "${MODULES[@]}"
do
    # Crear el directorio de destino si no existe
    TARGET_DIR="$MODULE/src/main/java/com/sistemaelectoral/$MODULE"
    mkdir -p "$TARGET_DIR"
    
    # Mover todos los archivos .java del directorio raíz del módulo al directorio correcto
    find "$MODULE" -maxdepth 1 -name "*.java" -exec mv {} "$TARGET_DIR/" \;
    
    # Si hay archivos .ice, moverlos a resources
    find "$MODULE" -maxdepth 1 -name "*.ice" -exec mv {} "$MODULE/src/main/resources/" \;
done

echo "Archivos movidos exitosamente a la estructura de directorios correcta." 