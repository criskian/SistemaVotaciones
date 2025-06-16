#ifndef QUERY_PORTAL_ICE
#define QUERY_PORTAL_ICE

module QueryPortal {
    interface QueryPortalService {
        string getVotingLocation(string document);
        string getVotingZone(string document);
    };
};

#endif 