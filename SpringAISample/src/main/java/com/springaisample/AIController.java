package com.springaisample;

import com.springaisample.entity.User;
import com.springaisample.exception.ChatServiceException;
import com.springaisample.service.ChatService;
import com.springaisample.service.RagTrainingService;
import com.springaisample.service.TrainingDataCollector;
import com.springaisample.service.UserService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AIController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AIController.class);
    private final ChatClient chatClient;
    private final ChatService chatService;
    private final RagTrainingService ragTrainingService;
    private final TrainingDataCollector trainingCollector;
    private final UserService userService;

    public AIController(ChatClient.Builder builder,
                        ChatService chatService,
                        RagTrainingService ragTrainingService,
                        TrainingDataCollector trainingCollector,
                        UserService userService) {

        this.chatClient = builder.build();
        this.chatService = chatService;
        this.ragTrainingService = ragTrainingService;
        this.trainingCollector = trainingCollector;
        this.userService = userService;
    }

    @GetMapping(value = "/stream/{message}", produces = "text/event-stream")
    public Flux<String> streamAnswer(@PathVariable String message) {
        try {
            if (message == null || message.trim().isEmpty()) {
                return Flux.error(new IllegalArgumentException("Message cannot be empty"));
            }
            return chatClient
                    .prompt()
                    .user(message)
                    .stream()
                    .content()
                    .onErrorResume(error ->
                            Flux.just("Error generating response: " + error.getMessage()));
        } catch (Exception e) {
            return Flux.error(new RuntimeException("Streaming failed: " + e.getMessage(), e));
        }
    }

    @GetMapping("/{message}")
    public ResponseEntity<?> getAnswer(@PathVariable String message) {
        try {
            if (message == null || message.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Message cannot be empty", 400));
            }
            String response = chatClient
                    .prompt()
                    .user(message)
                    .call()
                    .content();
            return ResponseEntity.ok(new ChatResponse(response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Invalid input: " + e.getMessage(), 400));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to generate response: " + e.getMessage(), 500));
        }
    }

    @PostMapping("/chat")
    public CompletableFuture<?> chat(@RequestBody ChatRequest request) {

        if (request == null || request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.badRequest().body(new ErrorResponse("Message cannot be empty", 400)));
        }

        if (request.getMessage().length() > 5000) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.badRequest().body(new ErrorResponse("Message exceeds maximum length of 5000 characters", 400)));
        }

        String sessionId = request.getSessionId() != null ? request.getSessionId() : request.getUserId();
        String tenantId  = request.getTenantId()  != null ? request.getTenantId()  : "default";

        return chatService.chat(sessionId, tenantId, request.getMessage())
                .handle((response, throwable) -> {
                    if (throwable != null) {
                        Throwable cause = throwable.getCause() != null ? throwable.getCause() : throwable;
                        if (cause instanceof ChatServiceException cse) {
                            return ResponseEntity.status(cse.getStatusCode())
                                    .body(new ErrorResponse(cse.getMessage(), cse.getStatusCode()));
                        }
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new ErrorResponse("Unexpected error occurred", 500));
                    }
                    String content = response.getResult().getOutput().getText();
                    return ResponseEntity.ok(new ChatResponse(content, sessionId));
                });
    }

    @PostMapping("/feedback")
    public ResponseEntity<?> submitFeedback(@RequestBody FeedbackRequest request) {
        try {
            if (request.getSessionId() == null || request.getSessionId().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Session ID is required", 400));
            }
            trainingCollector.updateWithFeedback(
                    request.getSessionId(),
                    request.getRating(),
                    Boolean.TRUE.equals(request.getIsRephrased())
            );
            return ResponseEntity.ok(Map.of("message", "Feedback recorded successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to record feedback: " + e.getMessage(), 500));
        }
    }

    @GetMapping("/training/stats")
    public ResponseEntity<?> getTrainingStats() {
        try {
            var stats = ragTrainingService.getTrainingStats();
            return ResponseEntity.ok(Map.of(
                    "totalConversations", stats.totalConversations(),
                    "highQualityCount", stats.highQualityCount(),
                    "qualityThreshold", stats.qualityThreshold(),
                    "qualityPercentage", stats.getQualityPercentage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to get training stats: " + e.getMessage(), 500));
        }
    }

    @PostMapping("/training/trigger")
    public ResponseEntity<?> triggerTraining() {
        try {
            ragTrainingService.triggerTrainingCycle();
            return ResponseEntity.ok(Map.of("message", "Training cycle triggered successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to trigger training: " + e.getMessage(), 500));
        }
    }

    @DeleteMapping("/session/{sessionId}")
    public ResponseEntity<?> clearSession(@PathVariable String sessionId) {
        try {
            chatService.clearSession(sessionId);
            return ResponseEntity.ok(Map.of("message", "Session cleared successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to clear session: " + e.getMessage(), 500));
        }
    }

    @GetMapping("/auth/user")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("User not authenticated", 401));
            }

            Object idAttr = principal.getAttribute("id");
            if (idAttr == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("GitHub ID not found in OAuth2 token", 400));
            }

            String githubId = idAttr.toString();
            User user = userService.findByGithubId(githubId)
                    .orElseThrow(() -> new RuntimeException("User not found in database"));

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("username", user.getUsername());
            userMap.put("name", user.getName());
            userMap.put("email", user.getEmail());
            userMap.put("avatarUrl", user.getAvatarUrl());
            userMap.put("githubUrl", user.getGithubUrl());
            userMap.put("location", user.getLocation());
            userMap.put("company", user.getCompany());
            userMap.put("followers", user.getFollowers());
            userMap.put("publicRepos", user.getPublicRepos());
            userMap.put("lastLoginAt", user.getLastLoginAt());
            return ResponseEntity.ok(userMap);

        } catch (RuntimeException e) {
            log.error("Failed to get user info: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to get user information: " + e.getMessage(), 500));
        }
    }

    @GetMapping("/admin/users/stats")
    public ResponseEntity<?> getUserStats() {
        try {
            var stats = userService.getUserStatistics();
            return ResponseEntity.ok(Map.of(
                    "totalUsers", stats.totalUsers(),
                    "averageFollowers", stats.averageFollowers(),
                    "averageRepos", stats.averageRepos()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to get user statistics: " + e.getMessage(), 500));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "timestamp", System.currentTimeMillis(),
                "service", "SpringAISample"
        ));
    }

    // ── DTOs ──────────────────────────────────────────────────────────────────

    public static class ChatRequest {
        private String message;
        private String userId;
        private String sessionId;
        private String tenantId;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
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
    }

    public static class ChatResponse {
        private final String response;
        private final long timestamp = System.currentTimeMillis();
        private final String sessionId;

        public ChatResponse(String response) {
            this.response = response;
            this.sessionId = null;
        }

        public ChatResponse(String response, String sessionId) {
            this.response = response;
            this.sessionId = sessionId;
        }

        public String getResponse() {
            return response;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public String getSessionId() {
            return sessionId;
        }
    }

    public static class ErrorResponse {
        private final String message;
        private final int status;
        private final long timestamp = System.currentTimeMillis();

        public ErrorResponse(String message, int status) {
            this.message = message;
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public int getStatus() {
            return status;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }

    public static class FeedbackRequest {
        private String sessionId;
        private Integer rating;
        private Boolean isRephrased;

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public Integer getRating() {
            return rating;
        }

        public void setRating(Integer rating) {
            this.rating = rating;
        }

        public Boolean getIsRephrased() {
            return isRephrased;
        }

        public void setIsRephrased(Boolean isRephrased) {
            this.isRephrased = isRephrased;
        }
    }
}
