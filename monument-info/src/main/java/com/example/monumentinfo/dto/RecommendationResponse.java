package com.example.monumentinfo.dto;

import com.example.monumentinfo.model.Place;
import java.util.List;

public class RecommendationResponse {

    private List<Place> restaurants;
    private List<Place> hotels;
    private List<Place> attractions;

    public RecommendationResponse(List<Place> restaurants,
                                  List<Place> hotels,
                                  List<Place> attractions) {
        this.restaurants = restaurants;
        this.hotels = hotels;
        this.attractions = attractions;
    }

    public List<Place> getRestaurants() { return restaurants; }
    public List<Place> getHotels() { return hotels; }
    public List<Place> getAttractions() { return attractions; }
}