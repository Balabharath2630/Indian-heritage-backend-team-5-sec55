package com.indiaexplorer.repository;

import com.indiaexplorer.model.Monument;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MonumentRepository extends JpaRepository<Monument, Long> {
    // ✅ This magic method filters by the 'region' column automatically
    List<Monument> findByRegion(String region);
}