package br.com.tagarelas.application.config;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

import io.github.resilience4j.ratelimiter.RateLimiterRegistry;

@Configuration 
@EnableWebSecurity
public class SpringSecurity {

    private final String jwtToken;
    private final String Algorithm = "HmacSHA256";
    private final IpRateLimiterFilter ipRateLimiterFilter;

    public SpringSecurity(
        @Value("${jwt.secret}") String jwtToken,
        IpRateLimiterFilter ipRateLimiterFilter) {
        this.jwtToken = jwtToken;
        this.ipRateLimiterFilter = ipRateLimiterFilter;
    }

    @Bean
    public RateLimiterRegistry rateLimiterRegistry() {
        return RateLimiterRegistry.ofDefaults();
    }

    @Bean 
    public JwtDecoder jwtDecoder(){
        var secretKey = new SecretKeySpec(jwtToken.getBytes(), Algorithm);
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }

    @Bean
    public JwtEncoder jwtEncoder(){
        var secretKey = new SecretKeySpec(jwtToken.getBytes(), Algorithm);
        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
    }

    @Bean 
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth ->
                auth.requestMatchers(
                    "/api/v1/user/register",
                    "/api/v1/user/activate/**",
                    "/api/v1/user/resend/**",
                    "/api/v1/auth/**"
                ).permitAll().anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
            // Adiciona o filtro de Rate Limiting antes da autenticação
            .addFilterBefore(ipRateLimiterFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}