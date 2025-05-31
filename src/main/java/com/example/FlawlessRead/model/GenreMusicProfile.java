package com.example.FlawlessRead.model;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;
import java.util.Map;

public class GenreMusicProfile {
    private Map<String, GenreInfo> genres;

    public Map<String, GenreInfo> getGenres() {
        return genres;
    }

    public void setGenres(Map<String, GenreInfo> genres) {
        this.genres = genres;
    }
}

class GenreInfo {
    private List<String> music_styles;
    private AudioFeatures audio_features;

    public List<String> getMusic_styles() {
        return music_styles;
    }

    public void setMusic_styles(List<String> music_styles) {
        this.music_styles = music_styles;
    }

    public AudioFeatures getAudio_features() {
        return audio_features;
    }

    public void setAudio_features(AudioFeatures audio_features) {
        this.audio_features = audio_features;
    }
}

@JsonDeserialize(using = RangeDeserializer.class)
class Range {
    private double min;
    private double max;

    public Range() {}

    public Range(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }
}

