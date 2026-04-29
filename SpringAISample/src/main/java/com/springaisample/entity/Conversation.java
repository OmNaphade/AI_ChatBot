package com.springaisample.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Entity representing a conversation session
 */
@Entity
@Table(name = "conversations")
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(name = "tenant_id")
    private String tenantId;

    @Column(name = "user_message", columnDefinition = "TEXT")
    private String userMessage;

    @Column(name = "assistant_message", columnDefinition = "TEXT")
    private String assistantMessage;

    @Column(name = "user_rating")
    private Integer userRating; // 1-5 scale

    @Column(name = "quality_score")
    private Double qualityScore; // 0.0 to 1.0

    @Column(name = "is_followed_by_rephrasing")
    private Boolean isFollowedByRephrasing;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Conversation() {
    }

    public Conversation(Long id, String sessionId, String tenantId, String userMessage, String assistantMessage, Integer userRating, Double qualityScore, Boolean isFollowedByRephrasing, LocalDateTime createdAt) {
        this.id = id;
        this.sessionId = sessionId;
        this.tenantId = tenantId;
        this.userMessage = userMessage;
        this.assistantMessage = assistantMessage;
        this.userRating = userRating;
        this.qualityScore = qualityScore;
        this.isFollowedByRephrasing = isFollowedByRephrasing;
        this.createdAt = createdAt;
    }

    public static ConversationBuilder builder() {
        return new ConversationBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getAssistantMessage() {
        return assistantMessage;
    }

    public void setAssistantMessage(String assistantMessage) {
        this.assistantMessage = assistantMessage;
    }

    public Integer getUserRating() {
        return userRating;
    }

    public void setUserRating(Integer userRating) {
        this.userRating = userRating;
    }

    public Double getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(Double qualityScore) {
        this.qualityScore = qualityScore;
    }

    public Boolean getFollowedByRephrasing() {
        return isFollowedByRephrasing;
    }

    public void setFollowedByRephrasing(Boolean followedByRephrasing) {
        isFollowedByRephrasing = followedByRephrasing;
    }

    public boolean getIsFollowedByRephrasing() {
        return isFollowedByRephrasing != null ? isFollowedByRephrasing : false;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Convert conversation to training text format
     */
    public String toTrainingText() {
        return String.format("User: %s\nAssistant: %s",
            userMessage != null ? userMessage : "",
            assistantMessage != null ? assistantMessage : "");
    }

    public static class ConversationBuilder {
        private Long id;
        private String sessionId;
        private String tenantId;
        private String userMessage;
        private String assistantMessage;
        private Integer userRating;
        private Double qualityScore;
        private Boolean isFollowedByRephrasing;
        private LocalDateTime createdAt;

        ConversationBuilder() {
        }

        public ConversationBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ConversationBuilder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public ConversationBuilder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public ConversationBuilder userMessage(String userMessage) {
            this.userMessage = userMessage;
            return this;
        }

        public ConversationBuilder assistantMessage(String assistantMessage) {
            this.assistantMessage = assistantMessage;
            return this;
        }

        public ConversationBuilder userRating(Integer userRating) {
            this.userRating = userRating;
            return this;
        }

        public ConversationBuilder qualityScore(Double qualityScore) {
            this.qualityScore = qualityScore;
            return this;
        }

        public ConversationBuilder followedByRephrasing(Boolean followedByRephrasing) {
            isFollowedByRephrasing = followedByRephrasing;
            return this;
        }

        public ConversationBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Conversation build() {
            return new Conversation(id, sessionId, tenantId, userMessage, assistantMessage, userRating, qualityScore, isFollowedByRephrasing, createdAt);
        }

        public String toString() {
            return "Conversation.ConversationBuilder(id=" + this.id + ", sessionId=" + this.sessionId + ", tenantId=" + this.tenantId + ", userMessage=" + this.userMessage + ", assistantMessage=" + this.assistantMessage + ", userRating=" + this.userRating + ", qualityScore=" + this.qualityScore + ", isFollowedByRephrasing=" + this.isFollowedByRephrasing + ", createdAt=" + this.createdAt + ")";
        }
    }
}
