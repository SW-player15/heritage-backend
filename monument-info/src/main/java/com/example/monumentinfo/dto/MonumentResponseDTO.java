package com.example.monumentinfo.dto;

public class MonumentResponseDTO {
    private Long id;
    private String name;
    private String description;
    private double latitude;
    private double longitude;
    private String locationName;
    private String imageUrl;

    public MonumentResponseDTO() {}

    public MonumentResponseDTO(Long id, String name, String description,
                               double latitude, double longitude,
                               String locationName, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationName = locationName;
        this.imageUrl = imageUrl;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getLocationName() { return locationName; }
    public String getImageUrl() { return imageUrl; }
}
