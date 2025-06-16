# Portal Web de Consulta Pública

Este módulo implementa una interfaz gráfica para que los ciudadanos puedan consultar información relacionada con el proceso de votación.

## Funcionalidades

- Consulta de mesa y zona de votación asignada mediante número de cédula
- Listado de candidatos registrados
- Visualización del conteo de votos en tiempo real
- Visualización gráfica de resultados

## Tecnologías Utilizadas

- Java 21
- ZeroC Ice 3.7.9 para comunicación distribuida
- Swing para la interfaz gráfica
- JFreeChart para visualización de gráficos
- Gradle para gestión de dependencias y construcción

## Estructura del Proyecto

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── votaciones/
│   │           └── portalwebconsulta/
│   │               ├── controller/    # Controladores
│   │               ├── model/         # Modelos de datos
│   │               ├── service/       # Servicios (Ice)
│   │               └── ui/           # Interfaz de usuario
│   └── resources/
│       └── Consulta.ice    # Definiciones de interfaces Ice
```

## Configuración

1. Asegúrese de tener instalado Java 21 o superior
2. Configure los endpoints de Ice en el archivo `application.properties`:
   - AccesoDatos: localhost:10000
   - Seguridad: localhost:10001

## Compilación y Ejecución

Para compilar el proyecto:
```bash
./gradlew build
```

Para ejecutar la aplicación:
```bash
./gradlew run
```

## Dependencias

- com.zeroc:ice:3.7.9
- org.jfree:jfreechart:1.5.4
- ch.qos.logback:logback-classic:1.4.14

## Comunicación Distribuida

El módulo utiliza ZeroC Ice para la comunicación con otros componentes del sistema:

- **AccesoDatos**: Proporciona acceso a la información de ciudadanos, mesas y votos
- **Seguridad**: Maneja la validación de usuarios y permisos

## Interfaz de Usuario

La interfaz gráfica está implementada usando Swing y proporciona:

- Campo para ingreso de cédula
- Selector de tipo de consulta
- Área de resultados
- Visualización de gráficos estadísticos

## Manejo de Errores

- Validación de formato de cédula
- Manejo de excepciones de comunicación
- Mensajes de error amigables para el usuario 