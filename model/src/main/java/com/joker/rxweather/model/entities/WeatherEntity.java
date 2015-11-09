package com.joker.rxweather.model.entities;

import java.io.Serializable;

/**
 * Created by Joker on 2015/10/31.
 */
public class WeatherEntity implements Serializable {

  public String cityId;
  public String cityName;

  public String weatherCode;
  public String weatherText;

  public String currentTemp;

  public String windDescription;

  public String drsgDescription;

  public WeatherEntity(String cityId, String cityName, String weatherCode, String weatherText,
      String currentTemp, String windDescription, String drsgDescription) {
    this.cityId = cityId;
    this.cityName = cityName;
    this.weatherCode = weatherCode;
    this.weatherText = weatherText;
    this.currentTemp = currentTemp;
    this.windDescription = windDescription;
    this.drsgDescription = drsgDescription;
  }

  @Override public String toString() {
    return "WeatherEntity{" +
        "cityId='" + cityId + '\'' +
        ", cityName='" + cityName + '\'' +
        ", weatherCode='" + weatherCode + '\'' +
        ", weatherText='" + weatherText + '\'' +
        ", currentTemp='" + currentTemp + '\'' +
        ", windDescription='" + windDescription + '\'' +
        ", drsgDescription='" + drsgDescription + '\'' +
        '}';
  }
}
