package com.joker.rxweather.views;

/**
 * Created by Joker on 2015/10/29.
 */
public interface ListView<T> extends LoadingView {

  void showForecasts(T t);

  boolean isContent();
}
