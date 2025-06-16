package com.votaciones.messaging;

import com.votaciones.messaging.model.VotingMessage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageResender extends Thread {
    private final Map<String, VotingMessage> unconfirmedMessages = new ConcurrentHashMap<>();
    private final Map<String, Integer> retryCounts = new ConcurrentHashMap<>();
    private final ReliableMessenger.MessageSender sender;
    private volatile boolean running = true;

    public MessageResender(ReliableMessenger.MessageSender sender) {
        this.sender = sender;
    }

    public void addUnconfirmed(String id, VotingMessage vote) {
        unconfirmedMessages.put(id, vote);
        retryCounts.putIfAbsent(id, 0);
    }

    public void confirm(String id) {
        unconfirmedMessages.remove(id);
        retryCounts.remove(id);
    }

    @Override
    public void run() {
        while (running) {
            for (Map.Entry<String, VotingMessage> entry : unconfirmedMessages.entrySet()) {
                String id = entry.getKey();
                VotingMessage vote = entry.getValue();
                int retries = retryCounts.getOrDefault(id, 0);
                if (retries < 3) {
                    boolean sent = sender.send(vote);
                    if (sent) {
                        confirm(id);
                    } else {
                        retryCounts.put(id, retries + 1);
                    }
                } else {
                    System.err.println("Message " + id + " failed after 3 retries");
                    confirm(id);
                }
            }
            try {
                Thread.sleep(5000);
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
} 