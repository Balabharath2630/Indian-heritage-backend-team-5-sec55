package com.indiaexplorer;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IndiaExplorerApiApplication {

    public static void main(String[] args) {
        // 1. Configure and load the .env file
        // .ignoreIfMissing() prevents the app from crashing if the file isn't found
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        // 2. Iterate through all entries in the .env file
        // and set them as System Properties so Spring can find them
        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });

        // 3. Start the Spring Boot Application
        SpringApplication.run(IndiaExplorerApiApplication.class, args);
    }
}