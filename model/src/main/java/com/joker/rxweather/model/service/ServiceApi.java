package com.joker.rxweather.model.service;

import android.support.annotation.CheckResult;
import com.joker.rxweather.model.response.ForecastResponse;
import java.util.HashMap;
import retrofit.http.GET;
import retrofit.http.QueryMap;
import rx.Observable;

/**
 * Created by Joker on 2015/10/31.
 */
public interface ServiceApi {

  /*https://api.heweather.com/x3/weather?cityid=城市ID&key=你的认证key*/
  /*https://api.heweather.com/x3/weather?city=城市&key=你的认证key*/
  @CheckResult @GET("/weather") Observable<ForecastResponse> getForecastByKey(
      @QueryMap HashMap<String, String> params);
}
