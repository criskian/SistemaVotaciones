[["java:package:com.sistemaelectoral.reliablemsg"]]

module ReliableMsg {
    interface VotoQueue {
        void enviarComando(string comando);
        string getVoto();
        void publicarEvento(string evento);
    };
}; 