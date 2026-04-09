package com.indiaexplorer.controller;

import com.indiaexplorer.model.Monument;
import com.indiaexplorer.service.MonumentService;
import com.indiaexplorer.service.S3Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/monuments")
@CrossOrigin(
    origins = "http://localhost:5173", 
    allowedHeaders = "*", 
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}
) 
public class MonumentController {

    private final MonumentService monumentService;
    private final S3Service s3Service;

    public MonumentController(MonumentService monumentService, S3Service s3Service) {
        this.monumentService = monumentService;
        this.s3Service = s3Service;
    }

    @GetMapping("/generate-presigned-url")
    public ResponseEntity<String> getPresignedUrl(@RequestParam String fileName) {
        try {
            String url = s3Service.generatePresignedUrl(fileName);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error generating S3 link: " + e.getMessage());
        }
    }

    @PostMapping("/save-metadata")
    public ResponseEntity<?> saveMetadata(@RequestBody Monument monument) {
        try {
            Monument saved = monumentService.save(monument);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error saving to database: " + e.getMessage());
        }
    }

    @GetMapping("/region/{regionName}")
    public ResponseEntity<List<Monument>> getByRegion(@PathVariable String regionName) {
        try {
            List<Monument> monuments = monumentService.getByRegion(regionName);
            return ResponseEntity.ok(monuments);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping
    public List<Monument> getAllMonuments() {
        try {
            List<Monument> monuments = monumentService.getAll();
            return (monuments != null) ? monuments : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @GetMapping("/{id}")
    public Monument getMonumentById(@PathVariable Long id) {
        return monumentService.getById(id);
    }

    /**
     * ✅ UPDATED DELETE LOGIC
     * This now ensures the DB record is deleted even if S3 fails.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMonument(@PathVariable Long id) {
        try {
            Monument monument = monumentService.getById(id);
            
            if (monument == null) {
                return ResponseEntity.status(404).body("Monument not found");
            }

            // 1. Attempt to delete the image from S3
            if (monument.getImageUrl() != null && !monument.getImageUrl().isEmpty()) {
                try {
                    s3Service.deleteFile(monument.getImageUrl());
                    System.out.println("☁️ S3 file deletion requested for: " + monument.getImageUrl());
                } catch (Exception s3Ex) {
                    // Log the error but DON'T stop the process
                    System.err.println("❌ S3 Deletion Error (Handled): " + s3Ex.getMessage());
                }
            }

            // 2. Always delete from the MySQL database
            monumentService.delete(id);
            System.out.println("🗑️ Database record deleted for ID: " + id);
            
            return ResponseEntity.ok("✨ Monument deleted from platform!");
            
        } catch (Exception e) {
            // This captures real crashes (like DB connection issues)
            e.printStackTrace(); 
            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
        }
    }
}