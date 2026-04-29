package com.springaisample.service;

import com.springaisample.entity.Conversation;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for scoring conversation quality for training data selection
 */
@Component
public class ConversationQualityScorer {

    private static final Logger log = LoggerFactory.getLogger(ConversationQualityScorer.class);

    /**
     * Score a conversation for training suitability
     * Returns value between 0.0 and 1.0
     */
    public double score(Conversation conv) {
        double score = 1.0;

        // Penalize very short responses (likely unhelpful)
        if (conv.getAssistantMessage() != null && conv.getAssistantMessage().length() < 50) {
            score -= 0.3;
        }

        // Reward explicit positive user signals
        if (conv.getUserRating() != null && conv.getUserRating() >= 4) {
            score += 0.3;
        }

        // Penalize if user immediately rephrased the same question (sign of failure)
        if (Boolean.TRUE.equals(conv.getIsFollowedByRephrasing())) {
            score -= 0.4;
        }

        // Penalize error responses
        if (conv.getAssistantMessage() != null && conv.getAssistantMessage().contains("[ERROR]")) {
            score -= 0.5;
        }

        // Ensure score is within bounds
        double finalScore = Math.max(0.0, Math.min(1.0, score));

        log.debug("Scored conversation {} with quality {}", conv.getId(), finalScore);
        return finalScore;
    }

    /**
     * Check if conversation meets minimum quality threshold
     */
    public boolean meetsThreshold(Conversation conv, double threshold) {
        return score(conv) >= threshold;
    }
}
