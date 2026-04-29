package com.springaisample.config;

import com.springaisample.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SecurityConfig.class);

    private final UserService userService;

    @Value("${app.auth.enabled:true}")
    private boolean authEnabled;

    @Value("${app.security.cors.allowed-origins:http://localhost:3000}")
    private String allowedOrigins;

    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        if (!authEnabled) {
            log.warn("Authentication is DISABLED! This should only be used in development.");
            http
                    .csrf(AbstractHttpConfigurer::disable)
                    .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                    .authorizeHttpRequests(authz -> authz
                            .anyRequest().permitAll()
                    );
        } else {
            http
                    .csrf(AbstractHttpConfigurer::disable)
                    .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                    .authorizeHttpRequests(authz -> authz
                            .requestMatchers("/", "/login/**", "/oauth2/**", "/error",
                                    "/webjars/**", "/css/**", "/js/**").permitAll()
                            .requestMatchers("/api/health", "/api/stream/**").permitAll()
                            .anyRequest().authenticated()
                    )
                    .exceptionHandling(ex -> ex
                            .authenticationEntryPoint((request, response, authException) -> {
                                String path = request.getRequestURI();
                                if (path.startsWith("/api/")) {
                                    // Return 401 JSON instead of redirecting — stops the loop
                                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                    response.setContentType("application/json");
                                    response.getWriter().write(
                                            "{\"error\":\"Unauthorized\",\"status\":401}"
                                    );
                                } else {
                                    response.sendRedirect("/oauth2/authorization/github");
                                }
                            })
                    )
                    .oauth2Login(oauth2 -> oauth2
                            .loginPage("/oauth2/authorization/github")
                            .userInfoEndpoint(userInfo -> userInfo
                                    .userService(oAuth2UserService()))
                            .successHandler(authenticationSuccessHandler())
                            .failureUrl("/login?error=true")
                    )
                    .logout(logout -> logout
                            .logoutSuccessHandler(logoutSuccessHandler())
                            .invalidateHttpSession(true)
                            .clearAuthentication(true)
                            .deleteCookies("JSESSIONID")
                    )
                    .sessionManagement(session -> session
                            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                            .maximumSessions(1)
                            .maxSessionsPreventsLogin(false)
                    );
        }

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList(allowedOrigins.split(",")));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request,
                                                HttpServletResponse response,
                                                Authentication authentication) throws IOException {
                OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
                try {
                    userService.findOrCreateUser(oauth2User);
                    String username = oauth2User.getAttribute("login");
                    String email = oauth2User.getAttribute("email");
                    log.info("User authenticated successfully: username={}, email={}", username, email);
                    response.sendRedirect(allowedOrigins + "/?login=success");
                } catch (Exception e) {
                    log.error("Failed to process user authentication", e);
                    response.sendRedirect(allowedOrigins + "/?login=error");
                }
            }
        };
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return (request, response, authentication) -> {
            if (authentication != null) {
                log.info("User logged out: {}", authentication.getName());
            }
            response.sendRedirect(allowedOrigins + "/?logout=success");
        };
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        return request -> {
            OAuth2User user = new org.springframework.security.oauth2.client.userinfo
                    .DefaultOAuth2UserService().loadUser(request);
            log.debug("OAuth2 user loaded: {}", user.getAttributes());
            return user;
        };
    }
}
