package com.springaisample.service;

import com.springaisample.exception.RateLimitExceededException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate limiter using token bucket algorithm per tenant
 */
@Component
public class RateLimiter {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RateLimiter.class);

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final int requestsPerMinute;

    public RateLimiter(@Value("${app.rate-limit.requests-per-minute:100}") int requestsPerMinute) {
        this.requestsPerMinute = requestsPerMinute;
    }

    /**
     * Check if tenant can make a request
     */
    public void checkLimit(String tenantId) {
        Bucket bucket = buckets.computeIfAbsent(tenantId, k ->
                Bucket.builder()
                        .addLimit(Bandwidth.builder()
                                .capacity(requestsPerMinute)
                                .refillGreedy(requestsPerMinute, Duration.ofMinutes(1))
                                .build()).build()
        );

        if (!bucket.tryConsume(1)) {
            log.warn("Rate limit exceeded for tenant: {}", tenantId);
            throw new RateLimitExceededException(
                "Tenant " + tenantId + " exceeded " + requestsPerMinute + " requests/minute");
        }
    }

    /**
     * Get remaining tokens for a tenant
     */
    public long getRemainingTokens(String tenantId) {
        Bucket bucket = buckets.get(tenantId);
        return bucket != null ? bucket.getAvailableTokens() : requestsPerMinute;
    }

    /**
     * Reset rate limit for a tenant (admin function)
     */
    public void resetLimit(String tenantId) {
        buckets.remove(tenantId);
        log.info("Rate limit reset for tenant: {}", tenantId);
    }
}
