#ifndef MAIN_SERVER_ICE
#define MAIN_SERVER_ICE

module VotingSystem {
    
    // Estructura para información de mesa
    struct MesaInfo {
        int mesaId;
        string zona;
        string estado;
        int totalVotos;
        string ultimoVoto;
        int alertas;
        string colegio;
    };
    
    // Estructura para alertas
    struct AlertaInfo {
        string tipo;
        string mensaje;
        string timestamp;
        int mesaId;
        string severidad;
    };
    
    // Secuencias para listas
    sequence<MesaInfo> MesaInfoList;
    sequence<AlertaInfo> AlertaInfoList;
    
    // Interface principal del servidor de gestión
    interface MainServer {
        // Gestión de mesas
        MesaInfoList obtenerEstadoMesas();
        MesaInfo obtenerEstadoMesa(int mesaId);
        void actualizarEstadoMesa(int mesaId, string estado);
        
        // Gestión de votos
        int obtenerTotalVotos();
        int obtenerVotosMesa(int mesaId);
        string generarResultadosParciales(int mesaId);
        string generarResultadosFinales();
        
        // Gestión de alertas
        AlertaInfoList obtenerAlertas();
        void registrarAlerta(AlertaInfo alerta);
        void limpiarAlertas();
        
        // Control de votación
        void cerrarVotacion();
        void cerrarMesa(int mesaId);
        bool validarMesaActiva(int mesaId);
        
        // Estadísticas
        string obtenerEstadisticas();
        int contarMesasActivas();
    };
    
    // Interface para comunicación con estaciones
    interface VoteStation {
        int registrarVoto(string documento, int candidatoId);
        bool validarVotante(string documento);
        string obtenerEstadoEstacion();
        void cerrarEstacion();
        int obtenerTotalVotosEstacion();
    };
    
    // Interface para notificaciones de estaciones a servidor principal
    interface StationNotifier {
        void notificarVoto(int mesaId, string timestamp);
        void notificarError(int mesaId, string error);
        void notificarAlerta(int mesaId, string alerta);
        void notificarCambioEstado(int mesaId, string nuevoEstado);
    };
};

#endif 