package com.springaisample.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class OllamaWarmup {

    private static final Logger log = LoggerFactory.getLogger(OllamaWarmup.class);
    private final ChatClient chatClient;

    public OllamaWarmup(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void warmup() {
        // Run in background so it doesn't block startup
        Thread.ofVirtual().start(() -> {
            try {
                log.info("Warming up Ollama model...");
                chatClient.prompt()
                        .user("hi")
                        .call()
                        .content();
                log.info("Ollama model warmed up successfully.");
            } catch (Exception e) {
                log.warn("Ollama warmup failed (model may load on first request): {}", e.getMessage());
            }
        });
    }
}
