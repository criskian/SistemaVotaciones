#!/bin/bash

# Configuración de la base de datos
DB_NAME="sistema_votaciones"
DB_USER="postgres"
DB_PASSWORD="postgres"

# Función para ejecutar un script SQL
run_sql_script() {
    echo "Ejecutando $1..."
    PGPASSWORD=$DB_PASSWORD psql -U $DB_USER -d $DB_NAME -f "$1"
    if [ $? -eq 0 ]; then
        echo "✅ $1 ejecutado correctamente"
    else
        echo "❌ Error al ejecutar $1"
        exit 1
    fi
}

# Crear la base de datos si no existe
echo "Creando base de datos si no existe..."
PGPASSWORD=$DB_PASSWORD psql -U $DB_USER -c "CREATE DATABASE $DB_NAME;" postgres

# Ejecutar los scripts en orden
run_sql_script "scripts/create_database.sql"
run_sql_script "scripts/insert_test_data.sql"
run_sql_script "scripts/insert_test_votes.sql"

echo "✅ Proceso completado exitosamente" 