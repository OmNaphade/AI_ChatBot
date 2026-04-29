package com.springaisample.service;

import com.springaisample.entity.Conversation;
import com.springaisample.exception.DataIngestionException;
import com.springaisample.repository.ConversationRepository;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * RAG-based training service that continuously learns from high-quality conversations
 */
@Service
public class RagTrainingService {

    private final VectorStore vectorStore;
    private final ConversationRepository conversationRepo;
    private final ConversationQualityScorer qualityScorer;

    @Value("${app.training.quality-threshold:0.85}")
    private double qualityThreshold;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RagTrainingService.class);

    public RagTrainingService(VectorStore vectorStore, ConversationRepository conversationRepo, ConversationQualityScorer qualityScorer) {
        this.vectorStore = vectorStore;
        this.conversationRepo = conversationRepo;
        this.qualityScorer = qualityScorer;
    }

    /**
     * Scheduled task to ingest high-quality conversations into vector store
     * Runs every 30 minutes as configured
     */
    @Scheduled(fixedDelayString = "${app.training.interval-minutes:30}000") // Convert to milliseconds
    public void ingestHighQualityConversations() {
        log.info("Starting RAG training cycle");

        try {
            // Find conversations with quality score above threshold
            List<Conversation> candidates = conversationRepo.findByQualityScoreGreaterThan(qualityThreshold);

            if (candidates.isEmpty()) {
                log.info("No high-quality conversations found for training");
                return;
            }

            log.info("Found {} high-quality conversations for training", candidates.size());

            // Convert to documents for vector store
            List<Document> documents = candidates.stream()
                .map(this::conversationToDocument)
                .toList();

            // Ingest into vector store
            vectorStore.add(documents);

            log.info("Successfully ingested {} documents into vector store", documents.size());

        } catch (Exception e) {
            log.error("Failed to complete RAG training cycle", e);
            throw new DataIngestionException("Failed to ingest training batch", e);
        }
    }

    /**
     * Convert conversation to document for vector store
     */
    private Document conversationToDocument(Conversation conv) {
        String content = conv.toTrainingText();

        Map<String, Object> metadata = Map.of(
            "source", "user-feedback",
            "session_id", conv.getSessionId(),
            "tenant_id", conv.getTenantId(),
            "timestamp", conv.getCreatedAt().toString(),
            "quality_score", conv.getQualityScore(),
            "user_rating", conv.getUserRating(),
            "conversation_id", conv.getId()
        );

        return new Document(content, metadata);
    }

    /**
     * Manually trigger training cycle (for testing/admin)
     */
    public void triggerTrainingCycle() {
        log.info("Manually triggering training cycle");
        ingestHighQualityConversations();
    }

    /**
     * Get training statistics
     */
    public TrainingStats getTrainingStats() {
        long totalConversations = conversationRepo.count();
        long highQualityCount = conversationRepo.findByQualityScoreGreaterThan(qualityThreshold).size();

        return new TrainingStats(totalConversations, highQualityCount, qualityThreshold);
    }

    /**
     * Training statistics record
     */
    public record TrainingStats(long totalConversations, long highQualityCount, double qualityThreshold) {
        public double getQualityPercentage() {
            return totalConversations > 0 ? (double) highQualityCount / totalConversations * 100 : 0;
        }
    }
}
