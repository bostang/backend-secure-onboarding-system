package com.reg.regis.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate limiting interceptor menggunakan Bucket4j.
 * Membatasi jumlah permintaan dari client berdasarkan IP.
 */
@Component
public class RateLimitConfig implements HandlerInterceptor {

    @Value("${app.rateLimit.enabled:true}")
    private boolean rateLimitEnabled;

    @Value("${app.rateLimit.capacity:10}")
    private long capacity;

    @Value("${app.rateLimit.refillRate:2}")
    private long refillRate;

    // Menyimpan bucket per client (misalnya berdasarkan IP)
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception {

        if (!rateLimitEnabled) {
            return true;
        }

        String clientId = getClientId(request);
        Bucket bucket = buckets.computeIfAbsent(clientId, this::createBucket);

        if (bucket.tryConsume(1)) {
            return true;
        } else {
            response.setStatus(429); // HTTP 429 Too Many Requests
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Too many requests. Please try again later.\"}");
            return false;
        }
    }

    /**
     * Mendapatkan ID unik client dari IP atau header.
     */
    private String getClientId(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }

        return request.getRemoteAddr();
    }

    /**
     * Membuat bucket untuk client tertentu.
     */
    private Bucket createBucket(String clientId) {
        Bandwidth limit = Bandwidth.classic(capacity, Refill.intervally(refillRate, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
