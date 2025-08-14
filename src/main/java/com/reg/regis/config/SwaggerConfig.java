package com.reg.regis.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                // ===== GANTI INFO INI SESUAI ANDA =====
                .title("🚀 X-MEN - Secure Onboarding Registration")
                .version("v1.0")
                .description("""
                    # Secure Customer Registration System
                    
                    **👨‍💻 Developed by:** X-MEN
                    **📅 Project Date:** July 2025
                    **🎯 Purpose:** RAKAMIN FUNPRO🤬
                    
                    ## 🔥 Key Features:
                    - 🔐 JWT Authentication & Authorization
                    - 📝 Real-time NIK verification via Dukcapil Integration
                    - 💳 Auto-generate Virtual Debit Cards (Silver, Gold, Platinum, Batik Air)
                    - 🛡️ OWASP Security Implementation
                    - ⚡ Rate Limiting & Input Validation
                    - 📊 Comprehensive API Documentation
                    
                    ## 🔧 Tech Stack:
                    - **Backend:** Spring Boot 3.5.3 + Java 21
                    - **Database:** PostgreSQL
                    - **Security:** JWT + Spring Security
                    - **Documentation:** SpringDoc OpenAPI 3
                    - **DevSecOps:** OWASP, Rate Limiting, Security Headers
                    
                    ## 📋 Available Endpoints:
                    - **Registration:** Customer signup with KTP verification
                    - **Authentication:** Login/logout with JWT
                    - **Verification:** Email, Phone, NIK validation
                    - **Profile:** Customer profile management
                    """)
                .contact(new Contact()
                    // ===== GANTI CONTACT INFO INI =====
                    .name("X-MEN - DevSecOps Engineer")
                    .email("xmen-company")
                    .url("")
                ))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080/api")
                    .description("🔧 Development Server"),
                new Server()
                    .url("NOT READY")
                    .description("🚀 Production Server")
            ))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            .components(new Components()
                .addSecuritySchemes("bearerAuth", 
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT token untuk autentikasi. Format: Bearer {your-jwt-token}")));
    }
}