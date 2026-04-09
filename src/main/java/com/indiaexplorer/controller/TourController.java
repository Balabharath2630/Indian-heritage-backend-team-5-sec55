package com.indiaexplorer.controller;

import com.indiaexplorer.model.Tour;
import com.indiaexplorer.repository.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tours")
@CrossOrigin(origins = "*")
public class TourController {

    @Autowired
    private TourRepository tourRepository;

    // Get all tours
    @GetMapping
    public List<Tour> getAllTours() {
        return tourRepository.findAll();
    }

    // Create new tour
    @PostMapping
    public Tour createTour(@RequestBody Tour tour) {
        return tourRepository.save(tour);
    }
}