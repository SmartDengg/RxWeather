package com.joker.rxweather.model.service;

import android.location.Location;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.joker.rxweather.common.Constants;
import com.joker.rxweather.common.rx.rxAndroid.SchedulersCompat;
import com.joker.rxweather.model.OkClientInstance;
import com.joker.rxweather.model.entities.AddressEntity;
import com.joker.rxweather.model.entities.ForecastWeatherEntity;
import com.joker.rxweather.model.entities.MainEntity;
import com.joker.rxweather.model.entities.RequestCitiesEntity;
import com.joker.rxweather.model.entities.SearchEntity;
import com.joker.rxweather.model.entities.WeatherEntity;
import com.joker.rxweather.model.response.ForecastResponse;
import com.joker.rxweather.model.response.LocationResponse;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.converter.GsonConverter;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Created by Joker on 2015/10/31.
 */
public class ServiceRest {

  private static final String TAG = ServiceRest.class.getSimpleName();
  private final LocationApi locationApi;
  private final ServiceApi serviceApi;

  private ForecastWeatherEntity forecastEntity = new ForecastWeatherEntity();

  private static class SingletonHolder {
    private static ServiceRest instance = new ServiceRest();
  }

  public static ServiceRest getInstance() {
    return SingletonHolder.instance;
  }

  public ServiceRest() {

    RequestInterceptor requestInterceptor = new RequestInterceptor() {
      @Override public void intercept(RequestFacade request) {
        request.addHeader("Accept-Encoding", "application/json");
      }
    };

    Gson gson = new GsonBuilder()//
        .excludeFieldsWithoutExposeAnnotation() //不导出实体中没有用@Expose注解的属性
        .enableComplexMapKeySerialization() //支持Map的key为复杂对象的形式
        .serializeNulls().create();

    RestAdapter locationRestAdapter =
        new RestAdapter.Builder().setEndpoint(Constants.LOCATION_BASE_URL)
            .setRequestInterceptor(requestInterceptor)
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setLog(new AndroidLog("_RxWeather_Log"))
            .setClient(OkClientInstance.getInstance())
            .setConverter(new GsonConverter(gson))
            .build();

    RestAdapter serviceRestAdapter =
        new RestAdapter.Builder().setEndpoint(Constants.FORECAST_BASE_URL)
            .setRequestInterceptor(requestInterceptor)
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setLog(new AndroidLog("_RxWeather_Log"))
            .setClient(OkClientInstance.getInstance())
            .setConverter(new GsonConverter(gson))
            .build();

    locationApi = locationRestAdapter.create(LocationApi.class);
    serviceApi = serviceRestAdapter.create(ServiceApi.class);
  }

  /**
   * 获取天气预报
   */
  public Observable<List<MainEntity>> getWeatherByCityOrCityId(AddressEntity locationEntity,
      List<RequestCitiesEntity.RequestCity> requestCities) {

    return Observable.zip(ServiceRest.this.getForecastByLocation(locationEntity),
        getForecastByCity(requestCities),
        new Func2<MainEntity, List<MainEntity>, List<MainEntity>>() {
          @Override
          public List<MainEntity> call(MainEntity mainEntity, List<MainEntity> mainEntities) {

            mainEntities.add(0, mainEntity);
            return mainEntities;
          }
        }).compose(SchedulersCompat.<List<MainEntity>>applyExecutorSchedulers());
  }

  /**
   * 根据地理位置获取天气预报
   */
  private Observable<MainEntity> getForecastByLocation(AddressEntity addressEntity) {

    HashMap<String, String> locationPrams = new HashMap<>(2);
    locationPrams.put("city", addressEntity.city);
    locationPrams.put("key", Constants.FORECAST_KEY);

    return serviceApi.getForecastByKey(locationPrams)
        .timeout(Constants.TIME_OUT, TimeUnit.MILLISECONDS)
        .retry(new Func2<Integer, Throwable, Boolean>() {
          @Override public Boolean call(Integer integer, Throwable throwable) {
            return throwable instanceof TimeoutException && integer < 1;
          }
        })
        .concatMap(new Func1<ForecastResponse, Observable<ForecastResponse.Main>>() {
          @Override
          public Observable<ForecastResponse.Main> call(ForecastResponse forecastResponse) {
            return forecastResponse.getMains().get(0).filterWebService();
          }
        })
        .concatMap(new Func1<ForecastResponse.Main, Observable<MainEntity>>() {
          @Override public Observable<MainEntity> call(ForecastResponse.Main main) {

            return Observable.combineLatest(ServiceRest.this.liftWeather(main),
                ServiceRest.this.transferForecast(main.getForecasts()),
                new Func2<WeatherEntity, List<ForecastWeatherEntity>, MainEntity>() {
                  @Override public MainEntity call(WeatherEntity weatherEntity,
                      List<ForecastWeatherEntity> forecastWeatherEntities) {

                    return new MainEntity(weatherEntity, forecastWeatherEntities);
                  }
                });
          }
        });
  }

  /**
   * 根据城市获取天气预报
   */
  private Observable<List<MainEntity>> getForecastByCity(
      final List<RequestCitiesEntity.RequestCity> requestCities) {

    return Observable.from(requestCities)
        .concatMap(new Func1<RequestCitiesEntity.RequestCity, Observable<ForecastResponse>>() {
          @Override
          public Observable<ForecastResponse> call(RequestCitiesEntity.RequestCity requestCity) {

            HashMap<String, String> cityParams = new HashMap<>(2);
            cityParams.put("cityid", requestCity.id);
            cityParams.put("key", Constants.FORECAST_KEY);

            return serviceApi.getForecastByKey(cityParams);
          }
        })
        .timeout(Constants.TIME_OUT, TimeUnit.MILLISECONDS)
        .retry(new Func2<Integer, Throwable, Boolean>() {
          @Override public Boolean call(Integer integer, Throwable throwable) {
            return throwable instanceof TimeoutException && integer < 1;
          }
        })
        .concatMap(new Func1<ForecastResponse, Observable<ForecastResponse.Main>>() {
          @Override
          public Observable<ForecastResponse.Main> call(ForecastResponse forecastResponse) {
            return forecastResponse.getMains().get(0).filterWebService();
          }
        })
        .concatMap(new Func1<ForecastResponse.Main, Observable<MainEntity>>() {
          @Override public Observable<MainEntity> call(ForecastResponse.Main main) {

            return Observable.combineLatest(ServiceRest.this.liftWeather(main),
                ServiceRest.this.transferForecast(main.getForecasts()),
                new Func2<WeatherEntity, List<ForecastWeatherEntity>, MainEntity>() {
                  @Override public MainEntity call(WeatherEntity weatherEntity,
                      List<ForecastWeatherEntity> forecastWeatherEntities) {

                    return new MainEntity(weatherEntity, forecastWeatherEntities);
                  }
                });
          }
        })
        .toList();
  }

  private Observable<List<ForecastWeatherEntity>> transferForecast(
      List<ForecastResponse.Main.Forecast> forecasts) {

    return Observable.from(forecasts)
        .map(new Func1<ForecastResponse.Main.Forecast, ForecastWeatherEntity>() {
          @Override public ForecastWeatherEntity call(ForecastResponse.Main.Forecast forecast) {

            ForecastWeatherEntity forecastWeatherEntity = forecastEntity.newInstance();
            forecastWeatherEntity.setDayCode(forecast.getCondition().dayCode);
            forecastWeatherEntity.setNightCode(forecast.getCondition().nightCode);
            forecastWeatherEntity.setDayText(forecast.getCondition().dayText);
            forecastWeatherEntity.setNightText(forecast.getCondition().nightText);

            forecastWeatherEntity.setDate(forecast.date);

            forecastWeatherEntity.setMaxTemp(forecast.getTemperature().maxTemp);
            forecastWeatherEntity.setMinTemp(forecast.getTemperature().minTemp);

            forecastWeatherEntity.setWindDescription(forecast.getWind().windGrade +
                forecast.getWind().description + "级");

            return forecastWeatherEntity;
          }
        })
        .toList();
  }

  private Observable<WeatherEntity> liftWeather(ForecastResponse.Main main) {

    return Observable.just(main).map(new Func1<ForecastResponse.Main, WeatherEntity>() {
      @Override public WeatherEntity call(ForecastResponse.Main main) {

        /**/
        ForecastResponse.Main.Basic basic = main.getBasic();

        /**/
        ForecastResponse.Main.CurrentWeather currentWeather = main.getCurrentWeather();
        ForecastResponse.Main.CurrentWeather.Condition condition = currentWeather.getCondition();
        ForecastResponse.Main.CurrentWeather.Wind wind = currentWeather.getWind();

        /**/
        ForecastResponse.Main.Suggestion.Drsg drsg = main.getSuggestion().getDrsg();

        return new WeatherEntity(basic.cityId, basic.cityName, condition.weatherCode,
            condition.weatherText, currentWeather.temperature,
            wind.description + wind.windGrade + "级", drsg.description);
      }
    });
  }

  /**
   * 根据经纬度获取位置信息
   */
  public Observable<AddressEntity> getCityByCoordinate(Location location) {

    HashMap<String, String> params = new HashMap<>(3);
    params.put("location", location.getLatitude() + "," + location.getLongitude());
    params.put("output", "json");
    params.put("key", Constants.LOCATION_KEY);

    return locationApi.getCityByLocation(params)
        .timeout(Constants.TIME_OUT, TimeUnit.MILLISECONDS)
        .retry(new Func2<Integer, Throwable, Boolean>() {
          @Override public Boolean call(Integer integer, Throwable throwable) {
            return throwable instanceof TimeoutException && integer < 1;
          }
        })
        .concatMap(new Func1<LocationResponse, Observable<LocationResponse>>() {
          @Override public Observable<LocationResponse> call(LocationResponse locationResponse) {
            return locationResponse.filterWebService();
          }
        })
        .map(new Func1<LocationResponse, AddressEntity>() {
          @Override public AddressEntity call(LocationResponse locationResponse) {
            LocationResponse.Location.Address address = locationResponse.getLocation().getAddress();

            /*如果你愿意，也可以用区获取天气，虽然这有可能造成无法获取到天气:)*/
            if (address.city.endsWith("市")) {
              /*don't worry about substring()*/
              address.city = address.city.substring(0, address.city.length() - 1);
            }

            AddressEntity addressEntity = new AddressEntity();
            addressEntity.province = address.province;
            addressEntity.city = address.city;
            addressEntity.district = address.district;

            return addressEntity;
          }
        })
        .compose(SchedulersCompat.<AddressEntity>applyExecutorSchedulers());
  }

  public Observable<SearchEntity> searchWeatherByCityName(final String cityName) {

    HashMap<String, String> searchPrams = new HashMap<>(2);
    searchPrams.put("city", cityName);
    searchPrams.put("key", Constants.FORECAST_KEY);

    return serviceApi.getForecastByKey(searchPrams)
        .timeout(Constants.TIME_OUT, TimeUnit.MILLISECONDS)
        .retry(new Func2<Integer, Throwable, Boolean>() {
          @Override public Boolean call(Integer integer, Throwable throwable) {
            return throwable instanceof TimeoutException && integer < 1;
          }
        })
        .concatMap(new Func1<ForecastResponse, Observable<ForecastResponse.Main>>() {
          @Override
          public Observable<ForecastResponse.Main> call(ForecastResponse forecastResponse) {
            return forecastResponse.getMains().get(0).filterWebService();
          }
        })
        .map(new Func1<ForecastResponse.Main, SearchEntity>() {
          @Override public SearchEntity call(ForecastResponse.Main main) {

            ForecastResponse.Main.CurrentWeather currentWeather = main.getCurrentWeather();
            ForecastResponse.Main.CurrentWeather.Condition condition =
                currentWeather.getCondition();

            return new SearchEntity(currentWeather.temperature, condition.weatherText,
                condition.weatherCode);
          }
        })
        .compose(SchedulersCompat.<SearchEntity>applyExecutorSchedulers());
  }
}
