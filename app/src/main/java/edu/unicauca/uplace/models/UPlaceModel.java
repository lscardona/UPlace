package edu.unicauca.uplace.models;

import java.io.Serializable;

public class UPlaceModel implements Serializable {
    public  int id;
    public String title;
    public String image;
    public String description;
    public String date;
    public String location;
    public double latitude;
    public double longitude;

    public UPlaceModel(int id, String title, String image, String description, String date, String location, double latitude, double longitude) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.description = description;
        this.date = date;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
