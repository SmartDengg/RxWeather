package com.joker.rxweather.presenter;

import android.content.Context;
import android.content.res.AssetManager;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.util.SparseArray;
import com.fernandocejas.frodo.annotation.RxLogSubscriber;
import com.joker.rxweather.common.Constants;
import com.joker.rxweather.exception.ErrorHanding;
import com.joker.rxweather.model.entities.AddressEntity;
import com.joker.rxweather.model.entities.MainEntity;
import com.joker.rxweather.model.entities.RequestCitiesEntity;
import com.joker.rxweather.views.ListView;
import com.rxweather.domain.usercase.ListUseCase;
import com.rxweather.domain.usercase.PrepareCase;
import com.rxweather.domain.usercase.UseCase;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by Joker on 2015/10/29.
 */
public class ListPresenterImp implements ListPresenter<ListView<Observable<List<MainEntity>>>> {

  private static final String TAG = ListPresenterImp.class.getSimpleName();

  private ListView<Observable<List<MainEntity>>> listView;

  private UseCase<SparseArray> prepareCase;
  private UseCase<List<MainEntity>> listUseCase;

  private AddressEntity addressEntity = new AddressEntity();
  private List<RequestCitiesEntity.RequestCity> requestCities = new ArrayList<>();
  private LocationManager locationManager;
  private AssetManager assetManager;

  public ListPresenterImp() {
  }

  @Override public void attachView(@NonNull ListView<Observable<List<MainEntity>>> view) {
    this.listView = view;

    this.locationManager =
        (LocationManager) this.listView.getContext().getSystemService(Context.LOCATION_SERVICE);
    this.assetManager = this.listView.getContext().getAssets();

    this.prepareCase = new PrepareCase(locationManager, assetManager);
    this.listUseCase = new ListUseCase(addressEntity, requestCities);
  }

  @Override public void loadData() {
    ListPresenterImp.this.showLoading();
    prepareCase.subscribe(new PrepareSubscriber());
  }

  private void showLoading() {
    if (!listView.isContent()) {
      this.listView.showLoading();
    }
  }

  private void showContent(List<MainEntity> mainEntities) {
     /*这和.publish().replay()有些不同，如果考虑GC pressure，还是换一种实现比较好:)
    https://raw.githubusercontent.com/wiki/ReactiveX/RxJava/images/rx-operators/cache.png*/
    this.listView.showForecasts(Observable.just(mainEntities).cache());
  }

  private void showError(int messageId) {
    this.listView.showError(messageId);
  }

  /*准备工作Subscriber，位置，城市列表等*/
  @RxLogSubscriber private final class PrepareSubscriber extends Subscriber<SparseArray> {

    @Override public void onCompleted() {
      ListPresenterImp.this.listUseCase.subscribe(new ListSubscriber());
    }

    @Override public void onError(Throwable e) {
      ListPresenterImp.this.showError(ErrorHanding.propagate(e));
    }

    @Override public void onNext(SparseArray sparseArray) {
      addressEntity.city = ((AddressEntity) sparseArray.get(Constants.LOCATION_TAG)).city;
      requestCities.clear();
      requestCities.addAll(
          (List<RequestCitiesEntity.RequestCity>) sparseArray.get(Constants.FORECAST_TAG));
    }
  }

  /*城市天气预报表*/
  @RxLogSubscriber private final class ListSubscriber extends Subscriber<List<MainEntity>> {

    @Override public void onCompleted() {
      ListPresenterImp.this.listView.showContent();
    }

    @Override public void onError(Throwable e) {
      ListPresenterImp.this.showError(ErrorHanding.propagate(e));
    }

    @Override public void onNext(List<MainEntity> mainEntities) {
      ListPresenterImp.this.showContent(mainEntities);
    }
  }

  @Override public void detachView() {
    this.prepareCase.unsubscribe();
    this.listUseCase.unsubscribe();

    this.locationManager = null;
    this.assetManager = null;
  }
}
