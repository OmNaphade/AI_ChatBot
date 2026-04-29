package com.springaisample.repository;

import com.springaisample.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for user data access
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by GitHub ID
     */
    Optional<User> findByGithubId(String githubId);

    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Find active users
     */
    List<User> findByIsActiveTrue();

    /**
     * Find users who haven't logged in recently
     */
    @Query("SELECT u FROM User u WHERE u.lastLoginAt < :since AND u.isActive = true")
    List<User> findInactiveUsers(@Param("since") LocalDateTime since);

    /**
     * Count total active users
     */
    long countByIsActiveTrue();

    /**
     * Find users by location
     */
    List<User> findByLocationContainingIgnoreCase(String location);

    /**
     * Find users by company
     */
    List<User> findByCompanyContainingIgnoreCase(String company);

    /**
     * Get user statistics
     */
    @Query("SELECT COUNT(u) as totalUsers, AVG(u.followers) as avgFollowers, AVG(u.publicRepos) as avgRepos FROM User u WHERE u.isActive = true")
    Object[] getUserStatistics();
}
