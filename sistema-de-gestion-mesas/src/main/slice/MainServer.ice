#ifndef MAIN_SERVER_ICE
#define MAIN_SERVER_ICE

module VotingSystem {
    
    // Estructura para información de mesa
    struct MesaInfo {
        int id;
        string nombreColegio;
        string direccion;
        int numeroMesa;
        string estado;
    };
    
    // Estructura para candidatos
    struct Candidato {
        int id;
        string nombre;
        string partido;
    };
    
    // Estructura para votos
    struct Voto {
        string idVotante;
        int idCandidato;
    };
    
    // Estructura para lotes de votos
    sequence<Voto> VotoSeq;
    struct LoteVotos {
        VotoSeq votos;
    };
    
    // Estructura para alertas
    struct AlertaInfo {
        int id;
        string tipo;
        string mensaje;
        string fecha;
    };
    
    // Estructura para estadísticas
    struct Estadisticas {
        int totalVotos;
        int totalMesas;
        int mesasActivas;
    };
    
    // Interface principal del servidor de gestión
    sequence<MesaInfo> MesaInfoSeq;
    sequence<Candidato> CandidatoSeq;
    sequence<AlertaInfo> AlertaInfoSeq;
    interface MainServer {
        // Gestión de mesas
        MesaInfoSeq listarMesas();
        
        // Gestión de votos
        bool validarVoto(string idVotante);
        bool registrarVoto(string idVotante, int idCandidato);
        bool verificarEstado(string idVotante);
        bool addLoteVotos(LoteVotos lote);
        
        // Gestión de candidatos
        CandidatoSeq listarCandidatos();
        
        // Gestión de alertas
        void registrarAlerta(AlertaInfo alerta);
        AlertaInfoSeq listarAlertas();
        
        // Estadísticas
        Estadisticas obtenerEstadisticas();
        
        // Verificar estado de votación en una zona
        bool verificarEstadoZona(string idVotante, string zona);
    };
};

#endif 