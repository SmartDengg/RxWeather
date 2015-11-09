package com.joker.rxweather.model.entities;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Joker on 2015/10/31.
 */
public class MainEntity implements Serializable {

  private WeatherEntity weatherEntity;
  private List<ForecastWeatherEntity> forecastWeatherEntityList;

  public MainEntity(WeatherEntity weatherEntity,
      List<ForecastWeatherEntity> forecastWeatherEntityList) {
    this.weatherEntity = weatherEntity;
    this.forecastWeatherEntityList = forecastWeatherEntityList;
  }

  public WeatherEntity getWeatherEntity() {
    return weatherEntity;
  }

  public List<ForecastWeatherEntity> getForecastWeatherEntityList() {
    return forecastWeatherEntityList;
  }

  @Override public String toString() {
    return "MainEntity{" +
        "forecastWeatherEntityList=" + forecastWeatherEntityList +
        ", weatherEntity=" + weatherEntity +
        '}';
  }
}
