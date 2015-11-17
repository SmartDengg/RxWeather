package com.rxweather.domain.usercase;

import com.joker.rxweather.model.entities.MainEntity;
import com.joker.rxweather.model.request.ListRequest;
import com.joker.rxweather.model.service.ServiceRest;
import java.util.List;
import rx.Observable;

/**
 * Created by Joker on 2015/10/31.
 */
public class ListUseCase extends UseCase<List<MainEntity>, ListRequest> {

  public ListUseCase() {
  }

  @Override protected Observable<List<MainEntity>> interactor(ListRequest listRequest) {

    return ServiceRest.getInstance()
        .getWeatherByCityOrCityId(listRequest.addressEntity, listRequest.requestCityList);
  }
}
