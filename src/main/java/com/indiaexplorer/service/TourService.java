package com.indiaexplorer.service;

import com.indiaexplorer.model.Tour;
import com.indiaexplorer.repository.TourRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TourService {

    private final TourRepository tourRepository;

    // Manual Constructor (This replaces @RequiredArgsConstructor)
    public TourService(TourRepository tourRepository) {
        this.tourRepository = tourRepository;
    }

    public List<Tour> getAllTours() {
        return tourRepository.findAll();
    }

    public Tour createTour(Tour tour) {
        return tourRepository.save(tour);
    }
}