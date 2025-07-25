package com.reg.regis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableTransactionManagement
public class RegistrationAbsoluteApplication {

    public static void main(String[] args) {
        // LAKUKAN KETIKA PENGEMBANGAN LOCAL
        // KETIKA MENGGUNAKAN DOCKER, .env DIKOMENTARI
        // KARENA NILAINYA DIAMBIL DARI MANIFEST (.YAML) DI KUBERNETES
        // Load .env variables
        // Dotenv dotenv = Dotenv.load();
        // dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

        ConfigurableApplicationContext context = SpringApplication.run(RegistrationAbsoluteApplication.class, args);
        
        // Dapatkan nilai properti app.base-url dari Spring Environment
        String appBaseUrl = context.getEnvironment().getProperty("app.base-url");
        String dukcapilServiceUrl = context.getEnvironment().getProperty("app.dukcapil.base-url");

        System.out.println("");
        System.out.println("🔐 CUSTOMER REGISTRATION SERVICE STARTED!");
        System.out.println("🌐 Service URL: " + (appBaseUrl != null ? appBaseUrl : "URL not available"));
        System.out.println("");
        System.out.println("📊 Health Checks:");
        System.out.println("   " + (appBaseUrl != null ? appBaseUrl : "URL not available") + "/auth/health");
        System.out.println("   " + (appBaseUrl != null ? appBaseUrl : "URL not available") + "/verification/health");
        System.out.println("");
        System.out.println("🔗 Main Endpoints:");
        System.out.println("   POST " + (appBaseUrl != null ? appBaseUrl : "URL not available") + "/auth/register");
        System.out.println("   POST " + (appBaseUrl != null ? appBaseUrl : "URL not available") + "/auth/login");
        System.out.println("   POST " + (appBaseUrl != null ? appBaseUrl : "URL not available") + "/verification/nik");
        System.out.println("");
        System.out.println("📈 Statistics:");
        System.out.println("   " + (appBaseUrl != null ? appBaseUrl : "URL not available") + "/auth/stats");
        System.out.println("   " + (appBaseUrl != null ? appBaseUrl : "URL not available") + "/verification/stats");
        System.out.println("");
        System.out.println("✅ Ready to serve customer registration requests!");
        System.out.println("🔗 Connected to Dukcapil Service: " + (dukcapilServiceUrl != null ? dukcapilServiceUrl : "URL not available"));
        System.out.println("");
    }
}