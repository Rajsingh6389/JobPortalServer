package jobportalapplication.jobportalapplication.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JwtAuthFilter jwtFilter = new JwtAuthFilter(jwtUtil);

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth

                        // Auth APIs
                        .requestMatchers("/api/auth/login", "/api/auth/signup", "/api/auth/all").permitAll()

                        // Resume API (public)
                        .requestMatchers("/api/resume/**").permitAll()

                        // Payment API (public)
                        .requestMatchers("/api/payment/**").permitAll()

                        // Jobs (public)
                        .requestMatchers("/jobportal/jobs", "/jobportal/jobs/**", "/jobportal/jobs/apply").permitAll()

                        // Admin jobs
                        .requestMatchers("/jobportal/jobs/admin/**").hasRole("ADMIN")

                        // Profile (protected)
                        .requestMatchers("/api/profile/**").authenticated()

                        .anyRequest().permitAll()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    // ⭐ FIXED CORS CONFIG FOR LOCAL + VERCEL + RAILWAY
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);

<<<<<<< HEAD
        config.setAllowedOrigins(List.of(
                "http://localhost:5173",  // React local dev
                "https://sparkling-medovik-f868d7.netlify.app", // your Netlify site
                "https://jobportalbyrrr.netlify.app", // FIXED missing protocol
                "https://jobportalapplication-production.up.railway.app" // your real backend
=======
        // 🔥 FIX: use allowedOriginPatterns instead of allowedOrigins
        config.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "http://localhost:5173",
                "https://*.netlify.app",
                "https://sparkling-medovik-f868d7.netlify.app",
                "jobportalbyrrr.netlify.app",
                        "https://jobportalbyrrr.netlify.app",   // ✅ CORRECT ORIGIN (important)

                "https://jobportalserver-production-0346.up.railway.app",
                "*"   // ← optional for testing; remove in production if needed
>>>>>>> aff41aeffee11146f870aa00bbd0ee196b232d0d
        ));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }


    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManager.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
