module Admin {
    sequence<string> StringSeq;
    interface AdminServer {
        // Gestión de Candidatos
        void agregarCandidato(string nombre, string partido, string cargo);
        void modificarCandidato(string id, string nombre, string partido, string cargo);
        void eliminarCandidato(string id);
        StringSeq listarCandidatos();
        
        // Procesamiento de Resultados
        void procesarVotos(string zona);
        string obtenerResultadosZona(string zona);
        string obtenerResultadosGlobales();
        
        // Generación de Reportes
        void generarReporteCSV(string tipoReporte);
        
        // Gestión de Logs
        void registrarLog(string evento, string detalle);
        StringSeq obtenerLogs(string fecha);
        
        // Validación
        bool validarFormatoDatos(string datos);
        bool validarIntegridadResultados();
    };
}; 