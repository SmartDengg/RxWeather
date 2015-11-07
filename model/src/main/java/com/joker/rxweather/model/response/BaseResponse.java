package com.joker.rxweather.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.joker.rxweather.common.Constants;
import com.joker.rxweather.model.service.exception.UnknowCityException;
import com.joker.rxweather.model.service.exception.WebServiceException;
import rx.Observable;

/**
 * Created by Joker on 2015/10/31.
 */
public class BaseResponse {

  @Expose @SerializedName("status") private String resultCode;

  public Observable filterWebService() {

    if (Constants.RESULT_STATUS_OK.equalsIgnoreCase(resultCode)) {
      return Observable.just(this);
    } else if (Constants.RESULT_STATUS_UNKNOW_CITY.equals(resultCode)) {
      return Observable.error(new UnknowCityException());
    } else {
      return Observable.error(new WebServiceException());
    }
  }
}
