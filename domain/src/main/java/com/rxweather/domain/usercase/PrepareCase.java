package com.rxweather.domain.usercase;

import android.content.res.AssetManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.util.SparseArray;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.rxbinding.internal.MainThreadSubscription;
import com.joker.rxweather.common.Constants;
import com.joker.rxweather.common.rx.rxAndroid.SchedulersCompat;
import com.joker.rxweather.model.entities.AddressEntity;
import com.joker.rxweather.model.entities.RequestCitiesEntity;
import com.joker.rxweather.model.service.ServiceRest;
import com.rxweather.domain.LocationListenerAdapter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.json.JSONObject;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Created by Joker on 2015/10/31.
 */
public class PrepareCase extends UseCase<SparseArray> {

  private static final String TAG = PrepareCase.class.getSimpleName();

  private LocationManager locationManager;
  private AssetManager assetManager;
  private HandlerThread handlerThread;

  private RequestCitiesEntity requestCitiesEntity = null;

  public PrepareCase(LocationManager locationManager, AssetManager assetManager) {
    this.locationManager = locationManager;
    this.assetManager = assetManager;
  }

  @Override protected Observable<SparseArray> interactor() {

    this.handlerThread = new HandlerThread("backgroundThread");
    this.handlerThread.start();

    return Observable.zip(PrepareCase.this.getLocationObservable(),
        PrepareCase.this.getRequestCitiesObservable(),
        new Func2<AddressEntity, List<RequestCitiesEntity.RequestCity>, SparseArray>() {
          @Override public SparseArray call(AddressEntity locationEntity,
              List<RequestCitiesEntity.RequestCity> requestCities) {

            SparseArray sparseArray = new SparseArray(2);
            sparseArray.put(Constants.LOCATION_TAG, locationEntity);
            sparseArray.put(Constants.FORECAST_TAG, requestCities);

            return sparseArray;
          }
        });
  }

  @NonNull private Observable<AddressEntity> getLocationObservable() {

    return Observable.create(new Observable.OnSubscribe<Location>() {

      @Override public void call(final Subscriber<? super Location> subscriber) {

        final LocationListener locationListener = new LocationListenerAdapter() {
          public void onLocationChanged(final Location location) {
            if (!subscriber.isUnsubscribed()) {
              subscriber.onNext(location);
              subscriber.onCompleted();
            }
            locationManager.removeUpdates(this);
            handlerThread.getLooper().quit();
          }
        };

        subscriber.add(new MainThreadSubscription() {
          @Override protected void onUnsubscribe() {
            locationManager.removeUpdates(locationListener);
            handlerThread.getLooper().quit();
          }
        });

        final Criteria locationCriteria = new Criteria();
        locationCriteria.setAccuracy(Criteria.ACCURACY_COARSE);
        locationCriteria.setPowerRequirement(Criteria.POWER_LOW);
        final String locationProvider = locationManager.getBestProvider(locationCriteria, true);

        locationManager.requestSingleUpdate(locationProvider, locationListener,
            handlerThread.getLooper());
      }
    }).concatMap(new Func1<Location, Observable<AddressEntity>>() {
      @Override public Observable<AddressEntity> call(Location location) {

        return ServiceRest.getInstance().getCityByCoordinate(location);
      }
    });
  }

  private Observable<List<RequestCitiesEntity.RequestCity>> getRequestCitiesObservable() {

    return Observable.create(new Observable.OnSubscribe<List<RequestCitiesEntity.RequestCity>>() {
      @Override
      public void call(final Subscriber<? super List<RequestCitiesEntity.RequestCity>> subscriber) {

        if (!subscriber.isUnsubscribed()) {
          if (requestCitiesEntity == null) {
            InputStream in = null;
            try {
              in = assetManager.open("city.txt");
              byte[] arrayOfByte = new byte[in.available()];
              in.read(arrayOfByte);
              JSONObject jsonObject = new JSONObject(new String(arrayOfByte, "UTF-8"));
              Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                  .serializeNulls()
                  .create();
              requestCitiesEntity = gson.fromJson(jsonObject.toString(), RequestCitiesEntity.class);
            } catch (Exception e) {
              subscriber.onError(e);
            } finally {
              if (in != null) {
                try {
                  in.close();
                } catch (IOException e) {
                  subscriber.onError(e);
                }
              }
            }
          }
          subscriber.onNext(requestCitiesEntity.getRequestCityList());
          subscriber.onCompleted();
        }
      }
    }).compose(SchedulersCompat.<List<RequestCitiesEntity.RequestCity>>applyIoSchedulers());
  }
}
