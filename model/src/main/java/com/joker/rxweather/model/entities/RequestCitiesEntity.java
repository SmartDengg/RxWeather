package com.joker.rxweather.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by Joker on 2015/10/31.
 */
public class RequestCitiesEntity {

    /*{
        "cities": [
            {
                "_id": CN101090501,
                "name": "Tangshan",
                "country": "CN",
                "coord": {
                    "lon": 119.066383,
                    "lat": 32.05389
                }
            }
        ]
    }*/

  @Expose @SerializedName("cities") private List<RequestCity> requestCityList;

  public List<RequestCity> getRequestCityList() {
    return requestCityList;
  }

  public class RequestCity {

    @Expose @SerializedName("_id") public String id;
    @Expose @SerializedName("name") public String cityName;

    @Override public String toString() {
      return "RequestCity{" +
          "id='" + id + '\'' +
          ", cityName='" + cityName + '\'' +
          '}';
    }
  }

  @Override public String toString() {
    return "RequestCitiesEntity{" +
        "requestCityList=" + requestCityList +
        '}';
  }
}
