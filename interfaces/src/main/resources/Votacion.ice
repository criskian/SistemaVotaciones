[["java:package:com.sistemaelectoral.interfaces"]]

module SistemaElectoral {
    sequence<string> StringSeq;

    exception VotacionException {
        string reason;
    };

    dictionary<string, int> ConteoVotos;

    class EstadisticasVotacion {
        ConteoVotos conteoVotos;
    };

    interface MesaVotacion {
        bool registrarVoto(string cedula, string candidato, string mesa) throws VotacionException;
        StringSeq obtenerResultadosMesa(string mesa) throws VotacionException;
        StringSeq listarMesas() throws VotacionException;
        string obtenerMesaAsignada(string cedula) throws VotacionException;
        bool verificarCedula(string cedula) throws VotacionException;
        bool validarVotante(string cedula, string mesa) throws VotacionException;
        EstadisticasVotacion obtenerEstadisticas() throws VotacionException;
    };
};