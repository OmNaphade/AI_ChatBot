package com.springaisample.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Entity representing authenticated users
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_github_id", columnList = "githubId", unique = true),
    @Index(name = "idx_username", columnList = "username", unique = true)
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "github_id", nullable = false, unique = true)
    private String githubId;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "github_url")
    private String githubUrl;

    @Column(name = "location")
    private String location;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "company")
    private String company;

    @Column(name = "blog")
    private String blog;

    @Column(name = "twitter_username")
    private String twitterUsername;

    @Column(name = "public_repos")
    private Integer publicRepos;

    @Column(name = "public_gists")
    private Integer publicGists;

    @Column(name = "followers")
    private Integer followers;

    @Column(name = "following")
    private Integer following;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        isActive = true;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Update user information from GitHub OAuth2 data
     */
    public void updateFromGitHubData(java.util.Map<String, Object> attributes) {
        this.username = (String) attributes.get("login");
        this.name = (String) attributes.get("name");
        this.email = (String) attributes.get("email");
        this.avatarUrl = (String) attributes.get("avatar_url");
        this.githubUrl = (String) attributes.get("html_url");
        this.location = (String) attributes.get("location");
        this.bio = (String) attributes.get("bio");
        this.company = (String) attributes.get("company");
        this.blog = (String) attributes.get("blog");
        this.twitterUsername = (String) attributes.get("twitter_username");
        this.publicRepos = (Integer) attributes.get("public_repos");
        this.publicGists = (Integer) attributes.get("public_gists");
        this.followers = (Integer) attributes.get("followers");
        this.following = (Integer) attributes.get("following");
        this.lastLoginAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGithubId() {
        return githubId;
    }

    public void setGithubId(String githubId) {
        this.githubId = githubId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getBlog() {
        return blog;
    }

    public void setBlog(String blog) {
        this.blog = blog;
    }

    public String getTwitterUsername() {
        return twitterUsername;
    }

    public void setTwitterUsername(String twitterUsername) {
        this.twitterUsername = twitterUsername;
    }

    public Integer getPublicRepos() {
        return publicRepos;
    }

    public void setPublicRepos(Integer publicRepos) {
        this.publicRepos = publicRepos;
    }

    public Integer getPublicGists() {
        return publicGists;
    }

    public void setPublicGists(Integer publicGists) {
        this.publicGists = publicGists;
    }

    public Integer getFollowers() {
        return followers;
    }

    public void setFollowers(Integer followers) {
        this.followers = followers;
    }

    public Integer getFollowing() {
        return following;
    }

    public void setFollowing(Integer following) {
        this.following = following;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean isActive() {
        return isActive != null ? isActive : false;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Constructors
    public User() {}

    public User(Long id, String githubId, String username, String email, String name, String avatarUrl, String githubUrl, String location, String bio, String company, String blog, String twitterUsername, Integer publicRepos, Integer publicGists, Integer followers, Integer following, Boolean isActive, LocalDateTime lastLoginAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.githubId = githubId;
        this.username = username;
        this.email = email;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.githubUrl = githubUrl;
        this.location = location;
        this.bio = bio;
        this.company = company;
        this.blog = blog;
        this.twitterUsername = twitterUsername;
        this.publicRepos = publicRepos;
        this.publicGists = publicGists;
        this.followers = followers;
        this.following = following;
        this.isActive = isActive;
        this.lastLoginAt = lastLoginAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String githubId;
        private String username;
        private String email;
        private String name;
        private String avatarUrl;
        private String githubUrl;
        private String location;
        private String bio;
        private String company;
        private String blog;
        private String twitterUsername;
        private Integer publicRepos;
        private Integer publicGists;
        private Integer followers;
        private Integer following;
        private Boolean isActive;
        private LocalDateTime lastLoginAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder githubId(String githubId) {
            this.githubId = githubId;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder avatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        public Builder githubUrl(String githubUrl) {
            this.githubUrl = githubUrl;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder bio(String bio) {
            this.bio = bio;
            return this;
        }

        public Builder company(String company) {
            this.company = company;
            return this;
        }

        public Builder blog(String blog) {
            this.blog = blog;
            return this;
        }

        public Builder twitterUsername(String twitterUsername) {
            this.twitterUsername = twitterUsername;
            return this;
        }

        public Builder publicRepos(Integer publicRepos) {
            this.publicRepos = publicRepos;
            return this;
        }

        public Builder publicGists(Integer publicGists) {
            this.publicGists = publicGists;
            return this;
        }

        public Builder followers(Integer followers) {
            this.followers = followers;
            return this;
        }

        public Builder following(Integer following) {
            this.following = following;
            return this;
        }

        public Builder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public Builder lastLoginAt(LocalDateTime lastLoginAt) {
            this.lastLoginAt = lastLoginAt;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public User build() {
            return new User(id, githubId, username, email, name, avatarUrl, githubUrl, location, bio, company, blog, twitterUsername, publicRepos, publicGists, followers, following, isActive, lastLoginAt, createdAt, updatedAt);
        }
    }
}
