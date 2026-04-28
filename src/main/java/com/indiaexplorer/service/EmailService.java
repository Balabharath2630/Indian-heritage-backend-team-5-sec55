package com.indiaexplorer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class EmailService {

    @Value("${RESEND_API_KEY}")
    private String apiKey;

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            String jsonBody = "{\n" +
                    "  \"from\": \"onboarding@resend.dev\",\n" +
                    "  \"to\": [\"" + toEmail + "\"],\n" +
                    "  \"subject\": \"Verify Your Incredible India Account\",\n" +
                    "  \"html\": \"<h2>Your OTP is: " + otp + "</h2><p>This code will expire in 5 minutes.</p>\"\n" +
                    "}";

            URL url = new URL("https://api.resend.com/emails");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes());
                os.flush();
            }

            int responseCode = conn.getResponseCode();

            if (responseCode != 200 && responseCode != 202) {
                throw new RuntimeException("Failed to send email. HTTP Code: " + responseCode);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error sending email: " + e.getMessage());
        }
    }
}