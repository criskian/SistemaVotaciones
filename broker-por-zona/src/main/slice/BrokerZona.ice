module VotingSystem {
    struct Voto {
        string idMesa;
        string zonaId;
        string datosVoto;
        long timestamp;
    };

    interface BrokerZona {
        void enviarVoto(Voto voto);
    };
}; 