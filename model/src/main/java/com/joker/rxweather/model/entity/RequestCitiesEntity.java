package com.joker.rxweather.model.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by Joker on 2015/10/31.
 */
public class RequestCitiesEntity {

    /*{
        "citise": [
            {
                "_id": 1797929,
                "name": "Qingdao",
                "country": "CN",
                "coord": {
                    "lon": 120.371941,
                    "lat": 36.09861
                }
            }
        ]
    }*/

  @Expose @SerializedName("citise") private List<RequestCity> requestCityList;

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
