package com.joker.rxweather.presenter;

import com.joker.rxweather.views.LoadingView;

/**
 * Created by Joker on 2015/10/29.
 */
public interface ListPresenter<R extends LoadingView> extends Presenter<R> {

  void loadData();
}
