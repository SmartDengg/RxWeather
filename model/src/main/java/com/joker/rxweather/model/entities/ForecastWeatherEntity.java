package com.joker.rxweather.model.entities;

import java.io.Serializable;

/**
 * Created by Joker on 2015/10/31.
 */
public class ForecastWeatherEntity implements Serializable, Cloneable {

  private String dayCode;
  private String nightCode;
  private String dayText;
  private String nightText;

  private String date;

  private String maxTemp;
  private String minTemp;

  private String windDescription;

  public String getDayCode() {
    return dayCode;
  }

  public void setDayCode(String dayCode) {
    this.dayCode = dayCode;
  }

  public String getNightCode() {
    return nightCode;
  }

  public void setNightCode(String nightCode) {
    this.nightCode = nightCode;
  }

  public String getDayText() {
    return dayText;
  }

  public void setDayText(String dayText) {
    this.dayText = dayText;
  }

  public String getNightText() {
    return nightText;
  }

  public void setNightText(String nightText) {
    this.nightText = nightText;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getMaxTemp() {
    return maxTemp;
  }

  public void setMaxTemp(String maxTemp) {
    this.maxTemp = maxTemp;
  }

  public String getMinTemp() {
    return minTemp;
  }

  public void setMinTemp(String minTemp) {
    this.minTemp = minTemp;
  }

  public String getWindDescription() {
    return windDescription;
  }

  public void setWindDescription(String windDescription) {
    this.windDescription = windDescription;
  }

  @Override public boolean equals(Object obj) {
    if (obj == null || this.getClass() != obj.getClass() || !this.getDate()
        .equals(((ForecastWeatherEntity) obj).getDate())) {
      return false;
    }
    return true;
  }

  public ForecastWeatherEntity newInstance() {

    try {
      return (ForecastWeatherEntity) super.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override protected Object clone() throws CloneNotSupportedException {
    ForecastWeatherEntity clone = (ForecastWeatherEntity) super.clone();
    return clone;
  }

  @Override public String toString() {
    return "ForecastWeatherEntity{" +
        "dayCode='" + dayCode + '\'' +
        ", nightCode='" + nightCode + '\'' +
        ", dayText='" + dayText + '\'' +
        ", nightText='" + nightText + '\'' +
        ", date='" + date + '\'' +
        ", maxTemp='" + maxTemp + '\'' +
        ", minTemp='" + minTemp + '\'' +
        ", windDescription='" + windDescription + '\'' +
        '}';
  }
}
