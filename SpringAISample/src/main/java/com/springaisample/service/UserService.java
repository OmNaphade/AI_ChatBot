package com.springaisample.service;

import com.springaisample.entity.User;
import com.springaisample.repository.UserRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for managing authenticated users
 */
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Find or create user from OAuth2 authentication
     */
    @Transactional
    public User findOrCreateUser(OAuth2User oauth2User) {
        Object idAttr = oauth2User.getAttribute("id");
        if (idAttr == null) {
            throw new IllegalArgumentException("GitHub OAuth2 user is missing 'id' attribute");
        }
        String githubId = idAttr.toString();

        Optional<User> existingUser = userRepository.findByGithubId(githubId);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.updateFromGitHubData(oauth2User.getAttributes());
            userRepository.save(user);
            log.info("Updated existing user: {}", user.getUsername());
            return user;
        } else {
            User newUser = User.builder()
                    .githubId(githubId)
                    .build();
            newUser.updateFromGitHubData(oauth2User.getAttributes());
            User savedUser = userRepository.save(newUser);
            log.info("Created new user: {}", savedUser.getUsername());
            return savedUser;
        }
    }

    /**
     * Get user by GitHub ID
     */
    public Optional<User> findByGithubId(String githubId) {
        return userRepository.findByGithubId(githubId);
    }

    /**
     * Get user by username
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Get all active users
     */
    public List<User> findAllActiveUsers() {
        return userRepository.findByIsActiveTrue();
    }

    /**
     * Deactivate user
     */
    @Transactional
    public void deactivateUser(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setIsActive(false);
            userRepository.save(user);
            log.info("Deactivated user: {}", user.getUsername());
        });
    }

    /**
     * Get user statistics
     */
    public UserStatistics getUserStatistics() {
        long totalUsers = userRepository.countByIsActiveTrue();
        Object[] stats = userRepository.getUserStatistics();

        if (stats != null && stats.length >= 3) {
            long count = ((Number) stats[0]).longValue();
            double avgFollowers = stats[1] != null ? ((Number) stats[1]).doubleValue() : 0.0;
            double avgRepos = stats[2] != null ? ((Number) stats[2]).doubleValue() : 0.0;

            return new UserStatistics(count, avgFollowers, avgRepos);
        }

        return new UserStatistics(totalUsers, 0.0, 0.0);
    }

    /**
     * Clean up inactive users (admin function)
     */
    @Transactional
    public int cleanupInactiveUsers(int daysInactive) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(daysInactive);
        List<User> inactiveUsers = userRepository.findInactiveUsers(cutoff);

        inactiveUsers.forEach(user -> user.setIsActive(false));
        userRepository.saveAll(inactiveUsers);

        log.info("Deactivated {} inactive users (not logged in since {})",
            inactiveUsers.size(), cutoff);

        return inactiveUsers.size();
    }

    /**
     * User statistics record
     */
    public record UserStatistics(long totalUsers, double averageFollowers, double averageRepos) {
    }
}
