[["java:package:com.sistemaelectoral.interfaces"]]

module SistemaElectoral {
    sequence<string> StringSeq;
    sequence<int> IntSeq;

    interface Consultoria {
        StringSeq listarCandidatos();
        IntSeq verConteoVotos();
    };

    interface Seguridad {
        bool verificarPermisos(string username, string token);
        string obtenerToken(string username, string password);
    };

    interface AccesoDatos {
        bool registrarVoto(string cedula, string candidato);
        StringSeq obtenerResultados();
    };

    interface Portal {
        string login(string usuario, string password);
        void logout(string usuario);
    };
};