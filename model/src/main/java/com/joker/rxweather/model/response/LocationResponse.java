package com.joker.rxweather.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Joker on 2015/10/31.
 */
public class LocationResponse extends BaseResponse {

  /*
  {
      "status": "OK",
      "result":
      {
        "location":
        {
          "lng": 116,
          "lat": 40
        },
        "formatted_address": "北京市门头沟区",
        "business": "",
        "addressComponent":
        {
          "city": "北京市",
          "direction": "",
          "distance": "",
          "district": "门头沟区",
          "province": "北京市",
          "street": "",
          "street_number": ""
        },
        "cityCode": 131
      }
    }*/

  @Expose @SerializedName("result") private Location location;

  public Location getLocation() {
    return location;
  }

  public class Location {

    @Expose @SerializedName("addressComponent") private Address address;

    public Address getAddress() {
      return address;
    }

    public class Address {

      @Expose public String province;
      @Expose public String city;
      @Expose public String district;

      @Override public String toString() {
        return "Address{" +
            "province='" + province + '\'' +
            ", city='" + city + '\'' +
            ", district='" + district + '\'' +
            '}';
      }
    }

    @Override public String toString() {
      return "Location{" +
          "address=" + address +
          '}';
    }
  }

  @Override public String toString() {
    return "LocationResponse{" +
        "location=" + location +
        '}';
  }
}
