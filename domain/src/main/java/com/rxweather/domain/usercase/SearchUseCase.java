package com.rxweather.domain.usercase;

import com.joker.rxweather.model.entities.AddressEntity;
import com.joker.rxweather.model.entities.SearchEntity;
import com.joker.rxweather.model.service.ServiceRest;
import rx.Observable;

/**
 * Created by Joker on 2015/10/31.
 */
public class SearchUseCase extends UseCase<SearchEntity> {

  private static final String TAG = SearchUseCase.class.getSimpleName();

  private AddressEntity addressEntity;

  public SearchUseCase(AddressEntity addressEntity) {
    this.addressEntity = addressEntity;
  }

  @Override protected Observable<SearchEntity> interactor() {

    return ServiceRest.getInstance().searchWeatherByCityName(addressEntity.city);
  }
}
