package com.joker.rxweather.presenter;

import com.joker.rxweather.views.LoadingView;

/**
 * Created by Joker on 2015/11/4.
 */
public interface SearchPresenter<R extends LoadingView> extends Presenter<R> {

  void search(String cityName);
}
