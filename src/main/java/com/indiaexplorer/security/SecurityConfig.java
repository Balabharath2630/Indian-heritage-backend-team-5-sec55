package com.indiaexplorer.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                // ✅ Public APIs
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/monuments/**").permitAll()
                .requestMatchers("/api/posts/**").permitAll()
                .requestMatchers("/api/tours/**").permitAll()
                .requestMatchers("/api/comments/**").permitAll()

                // 🔒 Any other request requires authentication
                .anyRequest().authenticated()
            );

        return http.build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // ✅ Allow frontend (local + deployed)
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:5173",
            "https://indian-heritage-frontend-team-5-sec.vercel.app"
        ));

        // ✅ Allowed HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        // ✅ Allow all headers (important for preflight)
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // ✅ Allow credentials (cookies/auth if needed)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}