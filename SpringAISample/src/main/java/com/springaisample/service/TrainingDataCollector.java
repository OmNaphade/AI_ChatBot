package com.springaisample.service;

import com.springaisample.entity.Conversation;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service for collecting and preparing training data from conversations
 */
@Service
public class TrainingDataCollector {

    private final ConversationQualityScorer qualityScorer;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TrainingDataCollector.class);

    public TrainingDataCollector(ConversationQualityScorer qualityScorer) {
        this.qualityScorer = qualityScorer;
    }

    /**
     * Record a conversation exchange for potential training
     */
    @Async("trainingExecutor")
    public void record(String sessionId, String userMessage, String assistantMessage) {
        try {
            // Create conversation entity
            Conversation conversation = Conversation.builder()
                .sessionId(sessionId)
                .userMessage(userMessage)
                .assistantMessage(assistantMessage)
                .build();

            // Score the conversation quality
            double qualityScore = qualityScorer.score(conversation);
            conversation.setQualityScore(qualityScore);

            // TODO: Save to database via repository
            // conversationRepository.save(conversation);

            log.debug("Recorded conversation for session {} with quality score {}",
                sessionId, qualityScore);

        } catch (Exception e) {
            log.error("Failed to record conversation for session {}", sessionId, e);
        }
    }

    /**
     * Update conversation with user feedback
     */
    public void updateWithFeedback(String sessionId, Integer rating, boolean isRephrased) {
        try {
            // TODO: Find and update conversation in database
            // Conversation conv = conversationRepository.findTopBySessionIdOrderByCreatedAtDesc(sessionId);
            // if (conv != null) {
            //     conv.setUserRating(rating);
            //     conv.setIsFollowedByRephrasing(isRephrased);
            //     double newScore = qualityScorer.score(conv);
            //     conv.setQualityScore(newScore);
            //     conversationRepository.save(conv);
            // }

            log.info("Updated feedback for session {}: rating={}, rephrased={}",
                sessionId, rating, isRephrased);

        } catch (Exception e) {
            log.error("Failed to update feedback for session {}", sessionId, e);
        }
    }
}
