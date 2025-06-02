#!/bin/bash

# Lista de módulos
MODULES=("portal" "votacion" "fiscalia" "seguridad" "electoraldb" "reliablemsg" "common" "interfaces")

# Actualizar paquetes para cada módulo
for MODULE in "${MODULES[@]}"
do
    # Directorio donde están los archivos Java
    JAVA_DIR="$MODULE/src/main/java/com/sistemaelectoral/$MODULE"
    
    # Si el directorio existe y contiene archivos Java
    if [ -d "$JAVA_DIR" ]; then
        # Encontrar todos los archivos Java y actualizar el paquete
        find "$JAVA_DIR" -name "*.java" -type f -exec sed -i '' "1s/package [^;]*/package com.sistemaelectoral.$MODULE/" {} \;
    fi
done

echo "Paquetes actualizados exitosamente." 