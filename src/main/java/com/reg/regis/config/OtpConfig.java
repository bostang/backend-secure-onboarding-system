package com.reg.regis.config;
// FirebaseConfig.java

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class OtpConfig {

    // ambil dari applicatin.properties
    @Value("${firebase.config.path}")       
    private String firebaseConfigPath;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        // Path to your service account key file in src/main/resources
        InputStream serviceAccount = new ClassPathResource(firebaseConfigPath).getInputStream();

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                // You can also set a database URL if you're using Realtime Database or Firestore
                // .setDatabaseUrl("https://your-project-id.firebaseio.com")
                .build();

        if (FirebaseApp.getApps().isEmpty()) { // Ensure only one FirebaseApp instance is initialized
            return FirebaseApp.initializeApp(options);
        } else {
            return FirebaseApp.getInstance();
        }
    }
}