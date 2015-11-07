package com.joker.rxweather.presenter;

/**
 * Created by Joker on 2015/10/29.
 */
public interface Presenter<R> {

  void attachView(R view);

  void detachView();
}
