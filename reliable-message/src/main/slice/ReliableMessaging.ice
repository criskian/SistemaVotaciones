#ifndef RELIABLE_MESSAGING_ICE
#define RELIABLE_MESSAGING_ICE

module ReliableMessaging {
    // Mensajes base
    class VotingMessage {
        string content;
        string type;
        string sourceComponent;
    };

    class ReliableVotingMessage {
        string uuid;
        long sequenceNumber;
        string state;
        VotingMessage message;
        long timestamp;
        int retryCount;
    };

    // Interfaces para reliable messaging
    interface MessageReceiver {
        void receiveMessage(ReliableVotingMessage message);
        void confirmMessage(string messageId);
    };

    interface MessageSender {
        void sendMessage(VotingMessage message);
    };
};

#endif 