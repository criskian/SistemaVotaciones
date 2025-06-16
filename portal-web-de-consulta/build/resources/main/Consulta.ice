module Consulta {
    // Estructuras de datos
    struct CiudadanoData {
        string cedula;
        string nombre;
        string zonaVotacion;
        string mesaAsignada;
    }

    struct CandidatoData {
        string id;
        string nombre;
        string partido;
        int numeroVotos;
    }

    // Excepciones
    exception ConsultaException {
        string reason;
    }

    // Interfaces
    interface AccesoDatos {
        CiudadanoData getCiudadanoMesaZonaAsig(string cedula) throws ConsultaException;
        sequence<CandidatoData> getZonasVotacion() throws ConsultaException;
        sequence<CandidatoData> getConteoVotos() throws ConsultaException;
    }

    interface Seguridad {
        bool validarFormato(string cedula);
        bool validarUsuario(string cedula) throws ConsultaException;
    }
}; 