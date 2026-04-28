package com.indiaexplorer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    @Value("${RESEND_API_KEY}")
    private String apiKey;

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            String jsonBody = "{\n" +
                    "  \"from\": \"Incredible India <otp@incredibleindiaweb.online>\",\n" +
                    "  \"to\": [\"" + toEmail + "\"],\n" +
                    "  \"subject\": \"Password Reset OTP - Incredible India\",\n" +
                    "  \"html\": \"<h2>Your OTP is: " + otp + "</h2>" +
                    "<p>Use this OTP to reset your password.</p>" +
                    "<p><b>This OTP will expire in 5 minutes.</b></p>\"\n" +
                    "}";

            URL url = new URL("https://api.resend.com/emails");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey.trim());
            conn.setRequestProperty("Content-Type", "application/json");

            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setDoOutput(true);

            // Send request
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
                os.flush();
            }

            int responseCode = conn.getResponseCode();

            if (responseCode == 200 || responseCode == 202) {
                System.out.println("✅ OTP email sent successfully!");
            } else {
                System.out.println("❌ Resend failed. HTTP Code: " + responseCode);
            }

        } catch (Exception e) {
            System.out.println("❌ Email sending error: " + e.getMessage());
        }
    }
}