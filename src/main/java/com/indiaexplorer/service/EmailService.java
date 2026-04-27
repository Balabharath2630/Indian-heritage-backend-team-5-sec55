package com.indiaexplorer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("balabharath.ai@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Verify Your Incredible India Account");
        message.setText("Welcome to Incredible India Explorer!\n\n" +
                        "Your verification code is: " + otp + "\n\n" +
                        "This code will expire in 5 minutes.");
        
        mailSender.send(message);
    }
}