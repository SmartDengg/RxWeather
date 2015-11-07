package com.joker.rxweather.model;

import retrofit.client.OkClient;

/**
 * Created by Joker on 2015/9/1.
 */
public class OkClientInstance {

  private static volatile OkClient clientSingleton;

  public static OkClient getInstance() {
    if (clientSingleton == null) {
      synchronized (OkClientInstance.class) {
        if (clientSingleton == null) {
          clientSingleton = new OkClient();
        }
      }
    }
    return clientSingleton;
  }
}
