#!/bin/bash

# Directorio de interfaces
INTERFACES_DIR="interfaces/src/main/java/com/sistemaelectoral/interfaces"

# Actualizar el paquete en todos los archivos Java
find "$INTERFACES_DIR" -name "*.java" -type f -exec sed -i '' "s/package Portal;/package com.sistemaelectoral.interfaces;/" {} \;
find "$INTERFACES_DIR" -name "*.java" -type f -exec sed -i '' "s/::Portal::/::com::sistemaelectoral::interfaces::/g" {} \;

echo "Paquetes actualizados en las interfaces." 