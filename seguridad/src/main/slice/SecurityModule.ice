#ifndef SECURITY_MODULE_ICE
#define SECURITY_MODULE_ICE

module SecurityModule {
    interface SecurityService {
        bool validateSecurity(string document);
        bool checkVotingStatus(string document);
        bool validateMesaZonaAsignada(string document, int mesaId, int zonaId);
    };
};

#endif 