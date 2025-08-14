package com.reg.regis.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;

@Component
public class DukcapilWebClient {

    private final WebClient webClient;

    @Value("${dukcapil.api.key}")
    private String apiKey;

    public DukcapilWebClient(@Value("${dukcapil.service.url:http://localhost:8081}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("X-API-Key", apiKey)
                .defaultHeader("User-Agent", "Customer-Service/1.0")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    /**
     * Verifikasi NIK dan nama ke Dukcapil Service - METHOD UTAMA
     */
    public DukcapilVerificationResponse verifyNikAndName(String nik, String namaLengkap) {
        try {
            System.out.println("🌐 Calling Dukcapil Service - NIK: " + nik + ", Name: " + namaLengkap);

            Map<String, String> request = Map.of(
                "nik", nik,
                "namaLengkap", namaLengkap
            );

            Map<String, Object> response = webClient
                .post()
                .uri("/api/dukcapil/verify-nik")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .timeout(Duration.ofSeconds(10))
                .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(1)))
                .doOnSuccess(result -> System.out.println("✅ Dukcapil Response: " + result.get("valid")))
                .doOnError(error -> System.err.println("❌ Dukcapil Error: " + error.getMessage()))
                .block(); // Convert to blocking for simplicity

            if (response != null) {
                boolean valid = (Boolean) response.get("valid");
                String message = (String) response.get("message");
                // Apply @SuppressWarnings to this specific line
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) response.get("data");

                return new DukcapilVerificationResponse(valid, message, data);
            }

            return new DukcapilVerificationResponse(false, "No response from Dukcapil service");

        } catch (WebClientResponseException e) {
            System.err.println("Dukcapil service HTTP error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return new DukcapilVerificationResponse(
                false,
                "Dukcapil service error: " + e.getStatusCode() + " - " + e.getMessage()
            );
        } catch (Exception e) {
            System.err.println("Error calling Dukcapil service: " + e.getMessage());
            return new DukcapilVerificationResponse(
                false,
                "Cannot connect to Dukcapil service: " + e.getMessage()
            );
        }
    }

    /**
     * Check NIK existence only
     */
    public boolean checkNikExists(String nik) {
        try {
            Map<String, String> request = Map.of("nik", nik);

            Map<String, Object> response = webClient
                .post()
                .uri("/api/dukcapil/check-nik")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .timeout(Duration.ofSeconds(5))
                .retryWhen(Retry.fixedDelay(1, Duration.ofSeconds(1)))
                .block();

            return response != null && (Boolean) response.get("exists");

        } catch (Exception e) {
            System.err.println("Error checking NIK: " + e.getMessage());
            return false;
        }
    }

    /**
     * Health check Dukcapil service
     */
    public boolean isServiceAvailable() {
        try {
            Map<String, Object> response = webClient
                .get()
                .uri("/api/dukcapil/health")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .timeout(Duration.ofSeconds(5))
                .block();

            return response != null && "OK".equals(response.get("status"));

        } catch (Exception e) {
            System.err.println("Dukcapil service not available: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get Dukcapil service stats
     */
    public Map<String, Object> getDukcapilStats() {
        try {
            return webClient
                .get()
                .uri("/api/dukcapil/stats")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .timeout(Duration.ofSeconds(5))
                .block();

        } catch (Exception e) {
            System.err.println("Error getting Dukcapil stats: " + e.getMessage());
            return Map.of(
                "error", "Cannot get stats from Dukcapil service",
                "available", false
            );
        }
    }

    /**
     * Response class untuk verifikasi Dukcapil
     */
    public static class DukcapilVerificationResponse {
        private boolean valid;
        private String message;
        private Map<String, Object> data; // Keep this as Map<String, Object> for now

        public DukcapilVerificationResponse(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
            this.data = null;
        }

        public DukcapilVerificationResponse(boolean valid, String message, Map<String, Object> data) {
            this.valid = valid;
            this.message = message;
            this.data = data;
        }

        // Getters
        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
        public Map<String, Object> getData() { return data; }
    }
}