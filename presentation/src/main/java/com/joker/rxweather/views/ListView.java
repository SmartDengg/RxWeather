package com.joker.rxweather.views;

/**
 * Created by Joker on 2015/10/29.
 */
public interface ListView<T> extends LoadingView, ContentView {

  void showForecasts(T t);
}
