package br.com.tagarelas.application.config;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class IpRateLimiterFilter extends OncePerRequestFilter {

    private final RateLimiterRegistry rateLimiterRegistry;
    private final RateLimiterConfig rateLimiterConfig;

    private static final List<String> PUBLIC_ROUTES = List.of(
            "/api/v1/user/register",
            "/api/v1/user/activate",
            "/api/v1/user/resend",
            "/api/v1/auth"
    );

    public IpRateLimiterFilter(RateLimiterRegistry rateLimiterRegistry) {
        this.rateLimiterRegistry = rateLimiterRegistry;
        
        this.rateLimiterConfig = RateLimiterConfig.custom()
                .limitForPeriod(10)
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .timeoutDuration(Duration.ZERO)
                .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        boolean isPublicRoute = PUBLIC_ROUTES.stream().anyMatch(path::startsWith);

        if (isPublicRoute) {
            String clientIp = extractClientIp(request);

            RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("ip_" + clientIp, rateLimiterConfig);

            try {
                RateLimiter.waitForPermission(rateLimiter);
            } catch (RequestNotPermitted e) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("""
                    {"error": "Limite de tentativas excedido para este IP. Tente novamente em instantes."}
                """);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        return (xf != null && !xf.isBlank()) ? xf.split(",")[0].trim() : request.getRemoteAddr();
    }
}