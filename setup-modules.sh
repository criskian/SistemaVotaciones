#!/bin/bash

# Lista de módulos
MODULES=("portal" "votacion" "fiscalia" "seguridad" "electoraldb" "reliablemsg" "common" "interfaces")

# Crear estructura para cada módulo
for MODULE in "${MODULES[@]}"
do
    # Crear estructura de directorios
    mkdir -p "$MODULE/src/main/java/com/sistemaelectoral/$MODULE"
    mkdir -p "$MODULE/src/main/resources"
    mkdir -p "$MODULE/src/test/java/com/sistemaelectoral/$MODULE"
    mkdir -p "$MODULE/src/test/resources"
    
    # Crear archivo placeholder para mantener la estructura de directorios en git
    touch "$MODULE/src/main/java/com/sistemaelectoral/$MODULE/.gitkeep"
    touch "$MODULE/src/main/resources/.gitkeep"
    touch "$MODULE/src/test/java/com/sistemaelectoral/$MODULE/.gitkeep"
    touch "$MODULE/src/test/resources/.gitkeep"
done 