package com.votaciones.messaging.model;

import java.io.Serializable;
import java.util.UUID;

public class ReliableVotingMessage implements Serializable {
    public static final String PENDING = "PENDING";
    public static final String SENT = "SENT";
    public static final String CONFIRMED = "CONFIRMED";
    
    private String uuid;
    private long sequenceNumber;
    private String state;
    private VotingMessage message;
    private long timestamp;
    private int retryCount;
    
    public ReliableVotingMessage(VotingMessage message, long sequenceNumber) {
        this.uuid = UUID.randomUUID().toString();
        this.sequenceNumber = sequenceNumber;
        this.state = PENDING;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
        this.retryCount = 0;
    }
    
    // Getters and setters
    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }
    public long getSequenceNumber() { return sequenceNumber; }
    public void setSequenceNumber(long sequenceNumber) { this.sequenceNumber = sequenceNumber; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public VotingMessage getMessage() { return message; }
    public void setMessage(VotingMessage message) { this.message = message; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public int getRetryCount() { return retryCount; }
    public void incrementRetryCount() { this.retryCount++; }
} 