package com.rxweather.domain.usercase;

import com.joker.rxweather.model.entities.AddressEntity;
import com.joker.rxweather.model.entities.SearchEntity;
import com.joker.rxweather.model.service.ServiceRest;
import rx.Observable;

/**
 * Created by Joker on 2015/10/31.
 */
public class SearchUseCase extends UseCase<SearchEntity, AddressEntity> {

  private static final String TAG = SearchUseCase.class.getSimpleName();

  public SearchUseCase() {
  }

  @Override protected Observable<SearchEntity> interactor(AddressEntity addressEntity) {

    return ServiceRest.getInstance().searchWeatherByCityName(addressEntity.city);
  }
}
