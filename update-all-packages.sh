#!/bin/bash

# Lista de módulos
MODULES=("portal" "votacion" "fiscalia" "seguridad" "electoraldb" "reliablemsg" "common" "interfaces")

# Actualizar paquetes para cada módulo
for MODULE in "${MODULES[@]}"
do
    # Directorio donde están los archivos Java
    JAVA_DIR="$MODULE/src/main/java/com/sistemaelectoral/$MODULE"
    
    if [ -d "$JAVA_DIR" ]; then
        # Actualizar el paquete en todos los archivos Java
        find "$JAVA_DIR" -name "*.java" -type f -exec sed -i '' "1s/package [^;]*/package com.sistemaelectoral.$MODULE/" {} \;
        
        # Actualizar imports de Portal a com.sistemaelectoral.interfaces
        find "$JAVA_DIR" -name "*.java" -type f -exec sed -i '' "s/import Portal\./import com.sistemaelectoral.interfaces./g" {} \;
        
        # Actualizar referencias a ::Portal:: en el código
        find "$JAVA_DIR" -name "*.java" -type f -exec sed -i '' "s/::Portal::/::com::sistemaelectoral::interfaces::/g" {} \;
    fi
done

echo "Paquetes actualizados en todos los módulos." 