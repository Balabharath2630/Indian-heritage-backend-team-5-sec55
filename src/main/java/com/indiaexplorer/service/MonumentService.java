package com.indiaexplorer.service;

import com.indiaexplorer.model.Monument;
import com.indiaexplorer.repository.MonumentRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MonumentService {

    private final MonumentRepository repo;
    private final S3Service s3Service; // Added S3Service

    public MonumentService(MonumentRepository repo, S3Service s3Service) {
        this.repo = repo;
        this.s3Service = s3Service;
    }

    public List<Monument> getByRegion(String region) {
        return repo.findByRegion(region);
    }

    public List<Monument> getAll() {
        return repo.findAll();
    }

    public Monument save(Monument monument) {
        return repo.save(monument);
    }

    public Monument getById(Long id) {
        return repo.findById(id).orElse(null);
    }

    // ✅ FIXED: Delete logic with S3 cleanup
    public void delete(Long id) {
        Monument monument = repo.findById(id).orElse(null);
        
        if (monument != null) {
            try {
                // Try to remove the file from S3 if an image URL exists
                String imageUrl = monument.getImageUrl();
                if (imageUrl != null && imageUrl.contains("amazonaws.com")) {
                    // Extract the filename from the end of the URL
                    String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                    s3Service.deleteFile(fileName);
                }
            } catch (Exception e) {
                // If S3 deletion fails (e.g. file not found), we just log it
                System.err.println("Could not delete S3 file, proceeding with DB delete: " + e.getMessage());
            }

            // Always delete the record from MySQL
            repo.deleteById(id);
        }
    }
}