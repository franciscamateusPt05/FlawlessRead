package com.example.FlawlessRead.model;

public class AudioFeatures {
    private double danceability;
    private double energy;
    private double valence;
    private double acousticness;
    private double instrumentalness;

    public AudioFeatures(double danceability, double energy, double valence, double acousticness, double instrumentalness) {
        this.danceability = danceability;
        this.energy = energy;
        this.valence = valence;
        this.acousticness = acousticness;
        this.instrumentalness = instrumentalness;
    }

    public double getDanceability() {
        return danceability;
    }

    public void setDanceability(double danceability) {
        this.danceability = danceability;
    }

    public double getEnergy() {
        return energy;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public double getValence() {
        return valence;
    }

    public void setValence(double valence) {
        this.valence = valence;
    }

    public double getAcousticness() {
        return acousticness;
    }

    public void setAcousticness(double acousticness) {
        this.acousticness = acousticness;
    }

    public double getInstrumentalness() {
        return instrumentalness;
    }

    public void setInstrumentalness(double instrumentalness) {
        this.instrumentalness = instrumentalness;
    }
}
