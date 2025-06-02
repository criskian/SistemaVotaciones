#!/bin/bash

# Build all modules
./gradlew clean build

# Function to run a module in a new terminal
run_module() {
    local module=$1
    local jar_file="$module/build/libs/$module.jar"
    
    if [ -f "$jar_file" ]; then
        osascript -e "tell app \"Terminal\" to do script \"cd $(pwd) && java -jar $jar_file\""
    else
        echo "Error: JAR file not found for module $module"
    fi
}

# Run each module in a new terminal window
run_module "seguridad"
sleep 2
run_module "votacion"
sleep 2
run_module "consulta"
sleep 2
run_module "portal"

echo "All modules have been started in separate terminal windows." 