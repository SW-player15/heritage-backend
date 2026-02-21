package com.example.monumentinfo.controller;

import com.example.monumentinfo.dto.RecommendationResponse;
import com.example.monumentinfo.service.RecommendationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping
    public RecommendationResponse getRecommendations(
            @RequestParam double lat,
            @RequestParam double lng) {

        return recommendationService.getRecommendations(lat, lng);
    }
}

