package com.indiaexplorer.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())

            // ✅ Enable CORS with custom config
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            .authorizeHttpRequests(auth -> auth
                // ✅ Public APIs
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/monuments/**").permitAll()
                .requestMatchers("/api/posts/**").permitAll()
                .requestMatchers("/api/tours/**").permitAll()
                .requestMatchers("/api/comments/**").permitAll()

                // 🔒 Others need authentication
                .anyRequest().authenticated()
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // ✅ IMPORTANT: Add ALL your domains
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:5173",
                "https://*.vercel.app",
                "https://incredibleindiaweb.online"
        ));

        // ✅ Methods
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        // ✅ Headers
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // ✅ Allow credentials (needed if using cookies/auth)
        configuration.setAllowCredentials(true);

        // ✅ (Optional but recommended)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}