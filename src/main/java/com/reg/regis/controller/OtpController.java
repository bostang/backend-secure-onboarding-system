package com.reg.regis.controller;

import com.reg.regis.security.JwtUtil; // Import JwtUtil
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class OtpController {

    private final JwtUtil jwtUtil; // Declare JwtUtil

    // Constructor Injection (recommended)
    public OtpController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/verify-firebase-token")
    public ResponseEntity<?> verifyFirebaseToken(@RequestBody Map<String, String> requestBody) {
        String idToken = requestBody.get("idToken");

        if (idToken == null || idToken.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "ID Token is missing."));
        }

        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String uid = decodedToken.getUid();
            UserRecord userRecord = FirebaseAuth.getInstance().getUser(uid);
            String phoneNumber = userRecord.getPhoneNumber();
            String email = decodedToken.getEmail(); // Email bisa null jika tidak ada di Firebase Auth

            System.out.println("Successfully verified Firebase ID Token for UID: " + uid);
            System.out.println("Phone Number: " + (phoneNumber != null ? phoneNumber : "N/A"));
            System.out.println("Email: " + (email != null ? email : "N/A"));


            // --- BAGIAN BARU: Generate customBackendToken ---
            // Anda bisa menggunakan UID atau email sebagai subject untuk JWT kustom Anda
            // Gunakan email jika tersedia, atau UID sebagai fallback atau jika email tidak selalu unik
            String subjectForJwt = (email != null && !email.isEmpty()) ? email : uid;
            String customBackendToken = jwtUtil.generateToken(subjectForJwt);
            // --- AKHIR BAGIAN BARU ---

            // Di sini Anda bisa melakukan logic backend lainnya:
            // 1. Cek keberadaan user di database Anda.
            // 2. Jika tidak ada, buat entri user baru.
            // 3. Update data user jika sudah ada.

            return ResponseEntity.ok(Map.of(
                    "message", "Firebase token verified successfully! Custom backend token generated.",
                    "uid", uid,
                    "phoneNumber", (phoneNumber != null ? phoneNumber : "N/A"),
                    "email", (email != null ? email : "N/A"),
                    "customBackendToken", customBackendToken // Kirimkan token kustom ini ke frontend
            ));

        } catch (FirebaseAuthException e) {
            System.err.println("Firebase token verification failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", "Invalid or expired Firebase ID Token."));
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "An internal server error occurred."));
        }
    }
}