package org.relaxindia.model.firebaseModel;

public class DriverProfileModel {
    int id;
    String name;
    String phone;
    Double lat;
    Double lon;
    Boolean isLocationActive;
    Boolean isOnline;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Boolean getLocationActive() {
        return isLocationActive;
    }

    public void setLocationActive(Boolean locationActive) {
        isLocationActive = locationActive;
    }

    public Boolean getOnline() {
        return isOnline;
    }

    public void setOnline(Boolean online) {
        isOnline = online;
    }

    public DriverProfileModel(int id, String name, String phone, Double lat, Double lon, Boolean isLocationActive, Boolean isOnline) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.lat = lat;
        this.lon = lon;
        this.isLocationActive = isLocationActive;
        this.isOnline = isOnline;
    }
}
