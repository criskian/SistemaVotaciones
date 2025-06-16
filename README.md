Carlos Felipe Sanchez A00404134
Joshua Rivera A00399847
Santiago Morales Cerón A00400130
Cristian camilo molina vides A00404853
David Alejandro Troya A00399865


## Comandos para Ejecutar

### 1. Ejecutar sistema de gestión de mesas
```sh
./gradlew :sistema-de-gestion-mesas:run
```

### 2. Ejecutar proxy-cache-db-ciudad
```sh
./gradlew :proxy-cache-db-ciudad:run
```

### 3. Ejecutar portal web de consulta
```sh
./gradlew :portal-web-de-consulta:run
```

### 4. Ejecutar mesa de votaciones local

```sh
./gradlew :estacion-de-votacion-local:run --args="ZonaEjemplo 1"
```
- Reemplaza `ZonaEjemplo` por el nombre de la zona y `1` por el número de mesa.

### 5. Consultar la base de datos con psql
```sh
psql -U postgres -d sistema_votaciones -c "\d+ zonas_electorales"
```

---