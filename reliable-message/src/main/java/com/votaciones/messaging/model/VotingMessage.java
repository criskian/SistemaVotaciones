package com.votaciones.messaging.model;

import java.io.Serializable;

public class VotingMessage implements Serializable {
    private String content;
    private String type;
    private String sourceComponent;
    
    public VotingMessage(String content, String type, String sourceComponent) {
        this.content = content;
        this.type = type;
        this.sourceComponent = sourceComponent;
    }

    // Getters and setters
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getSourceComponent() { return sourceComponent; }
    public void setSourceComponent(String sourceComponent) { this.sourceComponent = sourceComponent; }
} 