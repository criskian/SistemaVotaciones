#ifndef VOTE_STATION_ICE
#define VOTE_STATION_ICE

module VoteStation {
    interface VoteStationService {
        bool validateDocument(string document);
        bool castVote(string document, string candidate);
        string getVotingStatus(string document);
    };
};

#endif 