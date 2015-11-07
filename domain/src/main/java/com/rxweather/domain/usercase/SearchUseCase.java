package com.rxweather.domain.usercase;

import android.util.Log;
import com.joker.rxweather.model.entity.AddressEntity;
import com.joker.rxweather.model.entity.SearchEntity;
import com.joker.rxweather.model.service.ServiceRest;
import rx.Observable;
import rx.functions.Func0;

/**
 * Created by Joker on 2015/10/31.
 */
public class SearchUseCase extends UseCase<SearchEntity> {

  private static final String TAG = SearchUseCase.class.getSimpleName();

  private AddressEntity addressEntity;
  private ServiceRest serviceRest;

  public SearchUseCase(AddressEntity addressEntity) {
    this.addressEntity = addressEntity;
    this.serviceRest = ServiceRest.getInstance();
  }

  @Override protected Observable<SearchEntity> interactor() {

    Log.e(TAG, "cityName::::" + addressEntity.city);

    return Observable.defer(new Func0<Observable<SearchEntity>>() {
      @Override public Observable<SearchEntity> call() {
        return serviceRest.searchWeatherByCityName(addressEntity.city);
      }
    });
  }
}
