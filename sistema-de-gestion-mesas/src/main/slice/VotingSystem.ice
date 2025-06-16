module VotingSystem {
    // Estructuras existentes
    struct AlertaInfo {
        int id;
        string tipo;
        string mensaje;
        int mesaId;
        string severidad;
        string fechaHora;
    };

    struct EstadisticasZona {
        int totalMesas;
        int mesasActivas;
        int totalCiudadanos;
        int totalVotos;
    };

    struct ZonaInfo {
        int id;
        string nombre;
        string codigo;
        string ciudad;
        int totalMesas;
        int totalCiudadanos;
    };

    sequence<ZonaInfo> ZonaInfoSeq;

    // Interfaz del servidor principal
    interface MainServer {
        string[] obtenerEstadoMesas();
        string obtenerEstadoMesa(int mesaId);
        void actualizarEstadoMesa(int mesaId, string estado);
        int obtenerTotalVotos();
        string[] generarResultadosParciales();
        string[] generarResultadosFinales();
        void registrarAlerta(string tipo, string mensaje, int mesaId, string severidad);
        void cerrarVotacion();
        string[] obtenerZonasElectorales();
        bool validarMesaZonaAsignada(string documento, int zonaId, int mesaId);
        void registrarCedula(string documento, int mesaId);
        string[] obtenerEstadisticasZona(int zonaId);
    };
}; 