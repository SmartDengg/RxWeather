package com.rxweather.domain.usercase;

import com.joker.rxweather.model.entities.AddressEntity;
import com.joker.rxweather.model.entities.MainEntity;
import com.joker.rxweather.model.entities.RequestCitiesEntity;
import com.joker.rxweather.model.service.ServiceRest;
import java.util.List;
import rx.Observable;

/**
 * Created by Joker on 2015/10/31.
 */
public class ListUseCase extends UseCase<List<MainEntity>> {

  private ServiceRest serviceRest;
  private AddressEntity addressEntity;
  private List<RequestCitiesEntity.RequestCity> requestCities;

  public ListUseCase(AddressEntity addressEntity,
      List<RequestCitiesEntity.RequestCity> requestCities) {
    this.addressEntity = addressEntity;
    this.requestCities = requestCities;
    this.serviceRest = ServiceRest.getInstance();
  }

  @Override protected Observable<List<MainEntity>> interactor() {

    return serviceRest.getWeatherByCityOrCityId(addressEntity, requestCities);
  }
}
