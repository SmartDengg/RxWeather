package com.joker.rxweather.model.service;

import android.support.annotation.CheckResult;
import com.joker.rxweather.model.response.LocationResponse;
import java.util.HashMap;
import retrofit.http.GET;
import retrofit.http.QueryMap;
import rx.Observable;

/**
 * Created by Joker on 2015/10/31.
 */
public interface LocationApi {

  /*location=40,116&output=json&key=28bcdd84fae25699606ffad27f8da77b*/
  @CheckResult @GET("/geocoder") Observable<LocationResponse> getCityByLocation(
      @QueryMap HashMap<String, String> params);
}
