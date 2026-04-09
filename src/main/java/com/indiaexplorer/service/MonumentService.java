package com.indiaexplorer.service;

import com.indiaexplorer.model.Monument;
import com.indiaexplorer.repository.MonumentRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MonumentService {

    private final MonumentRepository repo;

    public MonumentService(MonumentRepository repo) {
        this.repo = repo;
    }

    // ✅ NEW: Filter monuments by region
    // This connects the Controller's request to the Repository's query
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

    public void delete(Long id) {
        repo.deleteById(id);
    }
}