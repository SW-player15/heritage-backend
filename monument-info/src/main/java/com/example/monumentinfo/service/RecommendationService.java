package com.example.monumentinfo.service;

import com.example.monumentinfo.dto.RecommendationResponse;
import com.example.monumentinfo.model.Place;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final HaversineService haversineService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    // Weights for scoring
    private static final double W_RATING   = 0.5;
    private static final double W_DISTANCE = 0.3;
    private static final double W_PRICE    = 0.2;

    public RecommendationService(HaversineService haversineService) {
        this.haversineService = haversineService;
    }

    public RecommendationResponse getRecommendations(double lat, double lng) {

        // Fetch all three categories in parallel for speed
        CompletableFuture<List<Place>> restaurantFuture =
                CompletableFuture.supplyAsync(() -> fetchPlaces(lat, lng, "restaurant", 2000));

        CompletableFuture<List<Place>> hotelFuture =
                CompletableFuture.supplyAsync(() -> fetchPlaces(lat, lng, "hotel", 5000));

        CompletableFuture<List<Place>> attractionFuture =
                CompletableFuture.supplyAsync(() -> fetchPlaces(lat, lng, "attraction", 10000));

        // Wait for all to complete
        CompletableFuture.allOf(restaurantFuture, hotelFuture, attractionFuture).join();

        List<Place> restaurants = scoreAndSort(restaurantFuture.join(), lat, lng, 2.0);
        List<Place> hotels      = scoreAndSort(hotelFuture.join(), lat, lng, 5.0);
        List<Place> attractions = scoreAndSort(attractionFuture.join(), lat, lng, 10.0);

        // Return top 5 per category
        return new RecommendationResponse(
                restaurants.stream().limit(5).collect(Collectors.toList()),
                hotels.stream().limit(5).collect(Collectors.toList()),
                attractions.stream().limit(5).collect(Collectors.toList())
        );
    }

    private List<Place> fetchPlaces(double lat, double lng, String type, int radiusMeters) {
        String query = buildQuery(lat, lng, type, radiusMeters);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://overpass-api.de/api/interpreter"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString("data=" + query))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return parseResponse(response.body(), type, lat, lng);

        } catch (Exception e) {
            // If Overpass fails, return empty list — don't crash the app
            return Collections.emptyList();
        }
    }

    private String buildQuery(double lat, double lng, String type, int radius) {
        String tag = switch (type) {
            case "restaurant" -> "\"amenity\"=\"restaurant\"";
            case "hotel"      -> "\"tourism\"=\"hotel\"";
            case "attraction" -> "\"tourism\"=\"attraction\"";
            default           -> "\"amenity\"=\"restaurant\"";
        };

        return String.format(
                "[out:json];node[%s](around:%d,%.6f,%.6f);out body;",
                tag, radius, lat, lng
        );
    }

    private List<Place> parseResponse(String json, String type,
                                      double userLat, double userLng) {
        List<Place> places = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode elements = root.get("elements");

            if (elements == null) return places;

            for (JsonNode el : elements) {
                JsonNode tags = el.get("tags");
                if (tags == null) continue;

                String name = tags.has("name")
                        ? tags.get("name").asText()
                        : "Unknown";

                // Skip unnamed places
                if (name.equals("Unknown")) continue;

                double placeLat = el.get("lat").asDouble();
                double placeLng = el.get("lon").asDouble();

                double distance = haversineService.calculateDistance(
                        userLat, userLng, placeLat, placeLng
                );

                Place place = new Place();
                place.setName(name);
                place.setType(type);
                place.setLatitude(placeLat);
                place.setLongitude(placeLng);
                place.setDistanceKm(Math.round(distance * 100.0) / 100.0);

                // Rating — Overpass doesn't have ratings, default to 3.0
                // (Can be upgraded later with real rating data)
                place.setRating(3.0);

                // Price level from OSC tags if available
                if (tags.has("price_level")) {
                    place.setPriceLevel(tags.get("price_level").asInt());
                } else {
                    place.setPriceLevel(2); // assume mid-range
                }

                // Cuisine for restaurants
                if (tags.has("cuisine")) {
                    place.setCuisine(tags.get("cuisine").asText());
                }

                places.add(place);
            }

        } catch (Exception e) {
            // Return whatever was parsed so far
        }
        return places;
    }

    private List<Place> scoreAndSort(List<Place> places, double lat,
                                     double lng, double maxDistanceKm) {
        for (Place place : places) {
            double ratingScore   = place.getRating() / 5.0;
            double distanceScore = 1.0 - (place.getDistanceKm() / maxDistanceKm);
            double priceScore    = 1.0 - (place.getPriceLevel() / 4.0);

            // Clamp distanceScore between 0 and 1
            distanceScore = Math.max(0, Math.min(1, distanceScore));

            double score = (W_RATING * ratingScore)
                    + (W_DISTANCE * distanceScore)
                    + (W_PRICE * priceScore);

            place.setScore(Math.round(score * 1000.0) / 1000.0);
        }

        // Sort by score descending
        places.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        return places;
    }
}