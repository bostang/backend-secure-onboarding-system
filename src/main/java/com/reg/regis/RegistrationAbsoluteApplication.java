package com.reg.regis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableTransactionManagement
public class RegistrationAbsoluteApplication {

    public static void main(String[] args) {
        // Load .env variables
        // ini berlaku untuk local development
        Dotenv dotenv = Dotenv.load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

        SpringApplication.run(RegistrationAbsoluteApplication.class, args);
        
        System.out.println("");
        System.out.println("🔐 CUSTOMER REGISTRATION SERVICE STARTED!");
        System.out.println("🌐 Service URL: http://localhost:8080");
        System.out.println("");
        System.out.println("📊 Health Checks:");
        System.out.println("   http://localhost:8080/api/auth/health");
        System.out.println("   http://localhost:8080/api/verification/health");
        System.out.println("");
        System.out.println("🔗 Main Endpoints:");
        System.out.println("   POST http://localhost:8080/api/auth/register");
        System.out.println("   POST http://localhost:8080/api/auth/login");
        System.out.println("   POST http://localhost:8080/api/verification/nik");
        System.out.println("");
        System.out.println("📈 Statistics:");
        System.out.println("   http://localhost:8080/api/auth/stats");
        System.out.println("   http://localhost:8080/api/verification/stats");
        System.out.println("");
        System.out.println("✅ Ready to serve customer registration requests!");
        System.out.println("🔗 Connected to Dukcapil Service: http://localhost:8081");
        System.out.println("");
    }
}