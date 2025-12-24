package fr.huiitre.tools.config;

import fr.huiitre.tools.infrastructure.security.JwtAuthenticationFilter;
import fr.huiitre.tools.infrastructure.security.JwtProvider;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

import fr.huiitre.tools.infrastructure.security.RestAccessDeniedHandler;
import fr.huiitre.tools.infrastructure.security.RestAuthenticationEntryPoint;
import fr.huiitre.tools.infrastructure.security.SecurityHeadersProperties;

@Configuration
public class SecurityConfig {

    private final SecurityHeadersProperties headersProperties;

    public SecurityConfig(SecurityHeadersProperties headersProperties) {
        this.headersProperties = headersProperties;
    }

    @Bean
    SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        JwtProvider jwtProvider
    ) throws Exception {

        /* ===============================
           API STATELESS (JWT READY)
           =============================== */
        http.sessionManagement(sm ->
            sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        /* ===============================
           CORS
           =============================== */
        http.cors(Customizer.withDefaults());

        /* ===============================
           CSRF (API stateless)
           =============================== */
        http.csrf(csrf -> csrf.disable());

        /* ===============================
           HTTP SECURITY HEADERS
           =============================== */
        http.headers(headers -> headers
            .contentTypeOptions(Customizer.withDefaults()) // X-Content-Type-Options: nosniff
            .frameOptions(frame -> frame.deny())            // X-Frame-Options: DENY
            .referrerPolicy(ref -> ref.policy(
                ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER
            ))
            .contentSecurityPolicy(csp -> csp
                .policyDirectives(headersProperties.getPolicy())
            )
        );

        /* ===============================
           SECURITY ERROR HANDLING (401 / 403)
           =============================== */
        http.exceptionHandling(ex -> ex
            .authenticationEntryPoint(new RestAuthenticationEntryPoint())
            .accessDeniedHandler(new RestAccessDeniedHandler())
        );

        /* ===============================
           AUTHORIZATION RULES
           =============================== */
        http.authorizeHttpRequests(auth -> auth
            /* .requestMatchers(
                "/ping",
                "/dev/jwt"
            ).permitAll() */
            .requestMatchers(
                "/auth/**",
                "/error",
                "/v3/api-docs/**",
                "/swagger-ui/**"
            ).permitAll()
            .anyRequest().authenticated()
        );

        /* ===============================
           JWT AUTHENTICATION FILTER
           =============================== */
        http.addFilterBefore(
            new JwtAuthenticationFilter(jwtProvider),
            UsernamePasswordAuthenticationFilter.class
        );

        /* ===============================
           DISABLED LEGACY AUTH
           =============================== */
        // http.httpBasic(Customizer.withDefaults());
        http.formLogin(form -> form.disable());

        return http.build();
    }
}
