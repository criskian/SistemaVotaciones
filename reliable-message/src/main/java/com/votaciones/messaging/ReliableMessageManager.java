package com.votaciones.messaging;

import com.votaciones.messaging.model.ReliableVotingMessage;
import com.votaciones.messaging.model.VotingMessage;
import com.zeroc.Ice.ObjectPrx;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

public class ReliableMessageManager extends Thread {
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_INTERVAL = 5000; // 5 seconds
    
    private final Map<String, ReliableVotingMessage> pendingMessages;
    private final Map<String, ReliableVotingMessage> sentMessages;
    private final AtomicLong sequenceNumber;
    private final Object lock;
    private volatile boolean running;
    private final ObjectPrx destination;
    private final Function<VotingMessage, Boolean> messageSender;
    
    public ReliableMessageManager(ObjectPrx destination, Function<VotingMessage, Boolean> messageSender) {
        this.pendingMessages = new ConcurrentHashMap<>();
        this.sentMessages = new ConcurrentHashMap<>();
        this.sequenceNumber = new AtomicLong(0);
        this.lock = new Object();
        this.running = true;
        this.destination = destination;
        this.messageSender = messageSender;
    }
    
    public void sendMessage(VotingMessage message) {
        synchronized (lock) {
            ReliableVotingMessage reliableMessage = new ReliableVotingMessage(message, sequenceNumber.getAndIncrement());
            pendingMessages.put(reliableMessage.getUuid(), reliableMessage);
        }
    }
    
    public void confirmMessage(String messageId) {
        ReliableVotingMessage message = sentMessages.remove(messageId);
        if (message != null) {
            message.setState(ReliableVotingMessage.CONFIRMED);
            System.out.println("Message confirmed: " + messageId);
        }
    }
    
    @Override
    public void run() {
        while (running) {
            try {
                processPendingMessages();
                checkSentMessages();
                Thread.sleep(RETRY_INTERVAL);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("Error in message processing: " + e.getMessage());
            }
        }
    }
    
    private void processPendingMessages() {
        pendingMessages.forEach((id, message) -> {
            try {
                boolean sent = messageSender.apply(message.getMessage());
                if (sent) {
                    message.setState(ReliableVotingMessage.SENT);
                    pendingMessages.remove(id);
                    sentMessages.put(id, message);
                }
            } catch (Exception e) {
                System.err.println("Failed to send message " + id + ": " + e.getMessage());
            }
        });
    }
    
    private void checkSentMessages() {
        long currentTime = System.currentTimeMillis();
        sentMessages.forEach((id, message) -> {
            if (currentTime - message.getTimestamp() > RETRY_INTERVAL) {
                if (message.getRetryCount() < MAX_RETRIES) {
                    message.incrementRetryCount();
                    message.setTimestamp(currentTime);
                    try {
                        boolean sent = messageSender.apply(message.getMessage());
                        if (sent) {
                            message.setState(ReliableVotingMessage.SENT);
                            sentMessages.remove(id);
                        }
                    } catch (Exception e) {
                        System.err.println("Failed to retry message " + id + ": " + e.getMessage());
                    }
                } else {
                    System.err.println("Message " + id + " failed after " + MAX_RETRIES + " retries");
                    sentMessages.remove(id);
                }
            }
        });
    }
    
    public void shutdown() {
        running = false;
        interrupt();
    }
} 