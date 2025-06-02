[["java:package:com.sistemaelectoral.interfaces"]]

module SistemaElectoral {
    sequence<string> StringSeq;
    sequence<int> IntSeq;

    interface Consultoria {
        StringSeq listarCandidatos();
        IntSeq verConteoVotos();
    };
    interface AccesoDatos {
        bool registrarVoto(string cedula, string candidato);
        StringSeq obtenerResultados();
    };
    interface Seguridad {
        bool verificarPermisos(string username, string token);
        string obtenerToken(string username, string password);
    };

    interface Consulta {
        StringSeq obtenerResultados();
        StringSeq listarCandidatos();
    };
}; 