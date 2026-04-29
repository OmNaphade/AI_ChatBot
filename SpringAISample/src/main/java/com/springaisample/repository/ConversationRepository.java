package com.springaisample.repository;

import com.springaisample.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for conversation data access
 */
@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    /**
     * Find conversations by session ID
     */
    List<Conversation> findBySessionIdOrderByCreatedAtAsc(String sessionId);

    /**
     * Find conversations with quality score above threshold
     */
    List<Conversation> findByQualityScoreGreaterThan(double threshold);

    /**
     * Find conversations by tenant
     */
    List<Conversation> findByTenantIdOrderByCreatedAtDesc(String tenantId);

    /**
     * Find recent conversations for training
     */
    @Query("SELECT c FROM Conversation c WHERE c.createdAt > :since AND c.qualityScore > :minScore ORDER BY c.createdAt DESC")
    List<Conversation> findRecentHighQualityConversations(
        @Param("since") LocalDateTime since,
        @Param("minScore") double minScore);

    /**
     * Count conversations by session
     */
    long countBySessionId(String sessionId);

    /**
     * Average quality score for a session
     */
    @Query("SELECT AVG(c.qualityScore) FROM Conversation c WHERE c.sessionId = :sessionId AND c.qualityScore IS NOT NULL")
    Double getAverageQualityScoreForSession(@Param("sessionId") String sessionId);
}
