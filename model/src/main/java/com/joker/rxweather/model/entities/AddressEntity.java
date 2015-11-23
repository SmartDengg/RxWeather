package com.joker.rxweather.model.entities;

/**
 * Created by Joker on 2015/10/31.
 */

public class  AddressEntity {

  public String province;
  public String city;
  public String district;

  public AddressEntity setCity(String city) {
    this.city = city;
    return AddressEntity.this;
  }

  @Override public String toString() {
    return "LocationEntity{" +
        "province='" + province + '\'' +
        ", city='" + city + '\'' +
        ", district='" + district + '\'' +
        '}';
  }
}
