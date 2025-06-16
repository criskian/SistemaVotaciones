module VotingSystem {
    struct Votante {
        string documento;
        string nombres;
        string apellidos;
        int ciudadId;
        int zonaId;
        int mesaId;
    };
    struct Candidato {
        int id;
        string documento;
        string nombres;
        string apellidos;
        string partido;
    };
    struct Zona {
        int id;
        string nombre;
        string codigo;
    };
    struct Voto {
        string documentoVotante;
        int candidatoId;
        int mesaId;
        string fechaHora;
    };
    struct LogEntry {
        string tipo;
        string mensaje;
        string fechaHora;
    };
    sequence<Candidato> CandidatoSeq;
    sequence<Zona> ZonaSeq;
    interface ProxyCacheDBCiudad {
        Votante ConsultarVotantePorCedula(string cedula);
        CandidatoSeq ConsultarCandidatos();
        ZonaSeq GetZonasVotacion();
        Zona ZonaMesaAsignada(string cedula);
        int IDZonaVotacion(string cedula);
        int GetConteoVotos(int mesaId);
        bool AgregarVoto(Voto voto);
        bool AgregarSospechoso(string cedula, string motivo);
        bool RegistrarLogs(LogEntry log);
        int GetConteoVotosPorCandidato(int candidatoId);
        string ConsultarMesaDescriptiva(string cedula);
    };
};