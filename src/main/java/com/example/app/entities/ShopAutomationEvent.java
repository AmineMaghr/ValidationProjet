package com.example.app.entities;

import java.time.LocalDateTime;

public class ShopAutomationEvent {
    private int id;
    private String eventType;
    private String description;
    private String status;
    private LocalDateTime createdAt;

    public ShopAutomationEvent() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s (%s)",
            createdAt.toLocalDate(),
            eventType,
            description,
            status);
    }
}
