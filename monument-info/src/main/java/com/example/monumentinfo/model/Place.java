package com.example.monumentinfo.model;

public class Place {

    private String name;
    private String type;       // restaurant, hotel, attraction
    private double latitude;
    private double longitude;
    private double distanceKm;
    private double rating;     // from overpass if available, else 0
    private int priceLevel;    // 0-4, 0 if unknown
    private String cuisine;    // for restaurants
    private double score;      // computed recommendation score

    public Place() {}

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getPriceLevel() { return priceLevel; }
    public void setPriceLevel(int priceLevel) { this.priceLevel = priceLevel; }

    public String getCuisine() { return cuisine; }
    public void setCuisine(String cuisine) { this.cuisine = cuisine; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
}