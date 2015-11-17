package com.joker.rxweather.model.request;

import com.joker.rxweather.model.entities.AddressEntity;
import com.joker.rxweather.model.entities.RequestCitiesEntity;
import java.util.List;

/**
 * Created by Joker on 2015/11/18.
 */
public class ListRequest {

  public AddressEntity addressEntity;
  public List<RequestCitiesEntity.RequestCity> requestCityList;

  public ListRequest(AddressEntity addressEntity,
      List<RequestCitiesEntity.RequestCity> requestCityList) {
    this.addressEntity = addressEntity;
    this.requestCityList = requestCityList;
  }
}
