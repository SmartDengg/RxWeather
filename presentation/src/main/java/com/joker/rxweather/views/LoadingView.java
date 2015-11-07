package com.joker.rxweather.views;

import android.content.Context;

/**
 * Created by Joker on 2015/10/29.
 */
public interface LoadingView {

  void showLoading();

  void showContent();

  void showError(int messageId);

  Context getContext();
}
