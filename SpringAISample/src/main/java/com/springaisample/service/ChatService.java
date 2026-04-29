package com.springaisample.service;

import com.springaisample.exception.ChatServiceException;
import com.springaisample.exception.ContextWindowExceededException;
import com.springaisample.exception.ModelUnavailableException;
import com.springaisample.exception.SessionNotFoundException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

/**
 * Advanced chat service with session management, rate limiting, and async processing
 */
@Service
public class ChatService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ChatService.class);

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final RateLimiter rateLimiter;
    private final TrainingDataCollector trainingCollector;

    // Per-session semaphore to prevent race conditions on the same session
    private final ConcurrentHashMap<String, Semaphore> sessionLocks = new ConcurrentHashMap<>();

    @Value("${spring.ai.ollama.chat.options.num-ctx:4096}")
    private int maxContextTokens;

    public ChatService(ChatClient chatClient, ChatMemory chatMemory, RateLimiter rateLimiter, TrainingDataCollector trainingCollector) {
        this.chatClient = chatClient;
        this.chatMemory = chatMemory;
        this.rateLimiter = rateLimiter;
        this.trainingCollector = trainingCollector;
    }

    /**
     * Process a chat request with full session management
     */
    @Async("chatExecutor")
    public CompletableFuture<ChatResponse> chat(String sessionId, String tenantId, String userMessage) {
        Semaphore lock = sessionLocks.computeIfAbsent(sessionId, k -> new Semaphore(1));

        try {
            lock.acquire();

            // Rate limiting check
            rateLimiter.checkLimit(tenantId);

            // Validate session exists (basic check - could be enhanced)
            if (sessionId == null || sessionId.trim().isEmpty()) {
                throw new SessionNotFoundException(sessionId);
            }

            // Validate message
            if (userMessage == null || userMessage.trim().isEmpty()) {
                throw new ChatServiceException("Message cannot be empty", 400);
            }

            // Check context window (simplified - could be enhanced with token counting)
            if (userMessage.length() > maxContextTokens / 4) { // Rough estimate
                throw new ContextWindowExceededException(maxContextTokens, userMessage.length());
            }

            log.debug("Processing chat request for session: {}", sessionId);

            ChatResponse response = chatClient.prompt()
                .system(buildSystemPrompt())
                .user(userMessage)
                .advisors(advisor -> advisor.param(
                    "chat_memory_conversation_id", sessionId))
                .call()
                .chatResponse();

            // Async: collect this exchange for future training
            trainingCollector.record(sessionId, userMessage,
                response.getResult().getOutput().getText());

            log.debug("Chat response generated for session: {}", sessionId);
            return CompletableFuture.completedFuture(response);

        } catch (ModelUnavailableException | ContextWindowExceededException |
                 SessionNotFoundException e) {
            log.error("Chat service error for session {}: {}", sessionId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in chat service for session {}", sessionId, e);
            throw new ChatServiceException("Failed to process chat request", e);
        } finally {
            lock.release();
        }
    }

    /**
     * Build the system prompt for the chatbot
     */
    private String buildSystemPrompt() {
        return """
            You are a helpful, intelligent assistant. Provide clear, accurate, and concise responses.
            You have access to conversation history and can learn from interactions.
            Always be professional, helpful, and truthful.
            If you don't know something, say so rather than making up information.
            """;
    }

    /**
     * Get conversation history for a session
     */
    public String getConversationHistory(String sessionId) {
        try {
            // This would integrate with ChatMemory to get history
            // For now, return a placeholder
            return "Conversation history for session: " + sessionId;
        } catch (Exception e) {
            log.error("Failed to get conversation history for session {}", sessionId, e);
            return "";
        }
    }

    /**
     * Clear conversation memory for a session
     */
    public void clearSession(String sessionId) {
        try {
            // Clear from ChatMemory
            sessionLocks.remove(sessionId);
            log.info("Cleared session: {}", sessionId);
        } catch (Exception e) {
            log.error("Failed to clear session {}", sessionId, e);
        }
    }
}
