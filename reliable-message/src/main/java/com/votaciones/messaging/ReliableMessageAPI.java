package com.votaciones.messaging;

import com.votaciones.messaging.model.VotingMessage;

public class ReliableMessageAPI {
    private final VoteQueue voteQueue;

    public ReliableMessageAPI(VoteQueue voteQueue) {
        this.voteQueue = voteQueue;
    }

    public void publishEvent(VotingMessage vote) {
        voteQueue.addVote(vote);
    }

    public VotingMessage getVote() throws InterruptedException {
        return voteQueue.getVote();
    }
} 