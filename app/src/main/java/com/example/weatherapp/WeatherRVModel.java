package com.example.weatherapp;

/**
 * Recycler view model
 */
public class WeatherRVModel {

    // define variable we'll be requiring
    private String time, temperature, description;

    /**
     * Recycler view model
     *
     * @param time        Time in city
     * @param temperature Temperature at given time
     * @param description Weather description at given time
     */
    public WeatherRVModel(String time, String temperature, String description) {
        this.time = time;
        this.temperature = temperature;
        this.description = description;
    }

    /**
     * @return Time in city
     */
    public String getTime() {
        return time;
    }

    /**
     * @return Temperature at given time
     */
    public String getTemperature() {
        return temperature;
    }

    /**
     * @return Weather description at given time
     */
    public String getDescription() {
        return description;
    }
}
