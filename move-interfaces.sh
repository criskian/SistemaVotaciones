#!/bin/bash

# Crear el directorio de destino
TARGET_DIR="interfaces/src/main/java/com/sistemaelectoral/interfaces"
mkdir -p "$TARGET_DIR"

# Mover los archivos de interfaz desde portal
mv portal/src/main/java/com/sistemaelectoral/portal/Consultoria.java "$TARGET_DIR/"
mv portal/src/main/java/com/sistemaelectoral/portal/ConsultoriaPrx.java "$TARGET_DIR/"
mv portal/src/main/java/com/sistemaelectoral/portal/AccesoDatos.java "$TARGET_DIR/"
mv portal/src/main/java/com/sistemaelectoral/portal/AccesoDatosPrx.java "$TARGET_DIR/"
mv portal/src/main/java/com/sistemaelectoral/portal/Seguridad.java "$TARGET_DIR/"
mv portal/src/main/java/com/sistemaelectoral/portal/SeguridadPrx.java "$TARGET_DIR/"
mv portal/src/main/java/com/sistemaelectoral/portal/_AccesoDatosPrxI.java "$TARGET_DIR/"
mv portal/src/main/java/com/sistemaelectoral/portal/_ConsultoriaPrxI.java "$TARGET_DIR/"
mv portal/src/main/java/com/sistemaelectoral/portal/_SeguridadPrxI.java "$TARGET_DIR/"

# Actualizar el paquete en los archivos movidos
find "$TARGET_DIR" -name "*.java" -type f -exec sed -i '' "1s/package com.sistemaelectoral.portal/package com.sistemaelectoral.interfaces/" {} \;

echo "Interfaces movidas exitosamente al módulo interfaces." 