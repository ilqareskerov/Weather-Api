package com.company.weather.service;

import com.company.weather.repository.WeatherRepository;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {
    private final WeatherRepository weatherRepositor;

    public WeatherService(WeatherRepository weatherRepositor) {
        this.weatherRepositor = weatherRepositor;
    }

    public WeatherDto getWeatherByCityName(String city) {
        return weatherRepositor;
    }
}