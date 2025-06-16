package com.votaciones.messaging;

import com.votaciones.messaging.model.VotingMessage;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class VoteQueue {
    private final BlockingQueue<VotingMessage> queue = new LinkedBlockingQueue<>();

    public void addVote(VotingMessage vote) {
        queue.add(vote);
    }

    public VotingMessage getVote() throws InterruptedException {
        return queue.take();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
} 