package com.votaciones.messaging;

import com.votaciones.messaging.model.VotingMessage;

public class ReliableMessenger extends Thread {
    private final VoteQueue voteQueue;
    private final MessageSender sender;
    private volatile boolean running = true;

    public ReliableMessenger(VoteQueue voteQueue, MessageSender sender) {
        this.voteQueue = voteQueue;
        this.sender = sender;
    }

    @Override
    public void run() {
        while (running) {
            try {
                VotingMessage vote = voteQueue.getVote();
                boolean sent = false;
                int retries = 0;
                while (!sent && retries < 3) {
                    sent = sender.send(vote);
                    if (!sent) {
                        retries++;
                        Thread.sleep(2000);
                    }
                }
                if (!sent) {
                    System.err.println("Failed to send vote after retries: " + vote.getContent());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void shutdown() {
        running = false;
        this.interrupt();
    }

    public interface MessageSender {
        boolean send(VotingMessage vote);
    }
} 