package com.joker.rxweather.model.request;

import android.content.res.AssetManager;
import android.location.LocationManager;

/**
 * Created by Joker on 2015/11/18.
 */
public class PrepareRequest {

  public LocationManager locationManager;
  public AssetManager assetManager;

  public PrepareRequest(LocationManager locationManager, AssetManager assetManager) {
    this.locationManager = locationManager;
    this.assetManager = assetManager;
  }
}
