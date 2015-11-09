package com.joker.rxweather.model.entities;

import java.io.Serializable;

/**
 * Created by Joker on 2015/11/4.
 */
public class SearchEntity implements Serializable {

  public String currentTemp;

  public String weatherText;
  public String weatherCode;

  public SearchEntity(String currentTemp, String weatherText, String weatherCode) {
    this.currentTemp = currentTemp;
    this.weatherText = weatherText;
    this.weatherCode = weatherCode;
  }

  @Override public String toString() {
    return "SearchEntity{" +
        "currentTemp='" + currentTemp + '\'' +
        ", weatherText='" + weatherText + '\'' +
        ", weatherCode='" + weatherCode + '\'' +
        '}';
  }
}
