package com.joker.rxweather.common;

/**
 * Created by Joker on 2015/10/31.
 */
public class Constants {

  public static final String LOCATION_KEY = "28bcdd84fae25699606ffad27f8da77b";
  public static final String FORECAST_KEY = "29fa754431304550b49cc3187c58cb06";

  public static final String LOCATION_BASE_URL = "http://api.map.baidu.com";
  public static final String FORECAST_BASE_URL = "https://api.heweather.com/x3";
  public static final String ICON_URL = "http://files.heweather.com/cond_icon/";

  public static final String J_URL = "https://github.com/SmartDengg/RxWeather";

  public static final int LOCATION_TAG = 0;
  public static final int FORECAST_TAG = 1;

  public static final String CACHE = "CACHE";
  public static final String CACHE_CITY = "CACHE_CITY";

  public static final String RECT = "RECT";
  public static final String POINT = "POINT";

  public static final String RESULT_STATUS_OK = "ok";
  public static final String RESULT_STATUS_UNKNOW_CITY = "unknown city";

  public static final int MILLISECONDS_300 = 300;
  public static final int MILLISECONDS_400 = 400;
  public static final int MILLISECONDS_600 = 600;

  public static final long TIME_OUT = 8 * 1000;

  public static final int[] colors = new int[] {
      android.R.color.holo_green_light, android.R.color.holo_blue_light,
      android.R.color.holo_green_light, android.R.color.holo_blue_light
  };
}
