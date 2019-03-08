package com.example.netef.battleships;

public class Player {

    private String name;
    private int score;
    private String city;
    private Double lat;
    private Double lon;


    public Player() {

    }

    public Player(String name, int score, String city) {
        this.name = name;
        this.score = score;
        this.city = city;
    }

    public Player(String name, int score, String city, Double lat, Double lon) {
        this.name = name;
        this.score = score;
        this.city = city;
        this.lat = lat;
        this.lon = lon;
    }


    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public String toString() {
        return "Name: " + name + " City: " + getCity() + " Score: " + score;
    }


}
