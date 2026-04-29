package com.springaisample.config;

import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;

/**
 * Configuration for Java 21 Virtual Threads
 */
@Configuration
public class VirtualThreadConfig {

    /**
     * Enable virtual threads for Tomcat to handle thousands of concurrent chat sessions
     */
    @Bean
    public TomcatProtocolHandlerCustomizer<?> virtualThreadCustomizer() {
        return handler -> handler.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
    }
}
