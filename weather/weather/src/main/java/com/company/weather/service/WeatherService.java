package com.company.weather.service;

import com.company.weather.constants.Constants;
import com.company.weather.dto.WeatherDto;
import com.company.weather.dto.WeatherResponse;
import com.company.weather.model.WeatherEntity;
import com.company.weather.repository.WeatherRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class WeatherService {
    private static final String URL_Link = "http://api.weatherstack.com/current?access_key=bdcd9dd8ae1f57476a9dd4786941ca48&query=";
    private final WeatherRepository weatherRepositor;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger  logger= LoggerFactory.getLogger(WeatherService.class);

    public WeatherService(WeatherRepository weatherRepositor, RestTemplate restTemplate) {
        this.weatherRepositor = weatherRepositor;
        this.restTemplate = restTemplate;
    }
    @Cacheable(key = "#city")
    public WeatherDto getWeatherByCityName(String city) {
        logger.info("Requested"+city);
        Optional<WeatherEntity> weatherEntity = weatherRepositor.findFirstByRequestedCityNameOrderByUpdatedTimeDesc(city);

        if (!weatherEntity.isPresent()) {
            return WeatherDto.convert(getWeatherFromWeatherStack(city));
        }
        if (weatherEntity.get().getUpdatedTime().isBefore(LocalDateTime.now().minusMinutes(30))) {
            return WeatherDto.convert(getWeatherFromWeatherStack(city));
        }

        return WeatherDto.convert(weatherEntity.get());
    }

    @CacheEvict(allEntries = true)
    @PostConstruct
    @Scheduled(fixedRateString = "10000")
    public void clearcache(){
        logger.info("Cache cleared");
    }

    public WeatherEntity getWeatherFromWeatherStack(String city) {
        ResponseEntity<String> response = restTemplate.getForEntity(getWeatherStackUrl(city), String.class);
        try {
            WeatherResponse weatherResponse = objectMapper.readValue(response.getBody(), WeatherResponse.class);
            return saveWeatherEntity(city, weatherResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    private String getWeatherStackUrl(String city) {
        return Constants.WEATHER_STACK_API_BASE_URL + Constants.WEATHER_STACK_API_ACCESS_KEY_PARAM + Constants.API_KEY + Constants.WEATHER_STACK_API_QUERY_PARAM + city;
    }

    private WeatherEntity saveWeatherEntity(String city, WeatherResponse weatherResponse) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        WeatherEntity entity = new WeatherEntity(city, weatherResponse.location().name(), weatherResponse.location().country(),
                weatherResponse.current().temperature(),
                LocalDateTime.now(), LocalDateTime.parse(weatherResponse.location().localtime(), dateTimeFormatter));
        return weatherRepositor.save(entity);
    }
}