#!/bin/bash

# Limpiar directorio de generación
rm -rf interfaces/src/main/java/com/sistemaelectoral/interfaces/*

# Crear directorios si no existen
mkdir -p interfaces/src/main/java/com/sistemaelectoral/interfaces

# Generar código Ice con las opciones correctas
cd interfaces/src/main/resources
for file in *.ice; do
    slice2java --output-dir=../java/com/sistemaelectoral/interfaces --compat "$file"
done

# Volver al directorio raíz
cd ../../../.. 