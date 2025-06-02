[["java:package:com.sistemaelectoral.interfaces"]]

module SistemaElectoral {
    interface Fiscalia {
        bool verificarAntecedentes(string cedula);
        void reportarFraude(string mesa);
    };
}; 