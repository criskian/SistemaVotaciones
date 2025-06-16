module VotingSystem {
    interface QueryStation {
        string consultVotingStation(string citizenId);
        string consultZone(string citizenId);
        string consultCandidates();
        string consultVoteCount();
    };
}; 