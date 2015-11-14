package com.rxweather.domain.usercase;

import android.support.annotation.CheckResult;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;

/**
 * Created by Joker on 2015/10/31.
 */
public abstract class UseCase<T> {

  private Subscription subscription = Subscriptions.empty();

  @SuppressWarnings("unchecked") public void subscribe(Observer<T> UseCaseSubscriber) {

    UseCase.this.subscription = this.interactor()//
        .onBackpressureBuffer()//
        .take(1)//
        .filter(new Func1<T, Boolean>() {
          @Override public Boolean call(T t) {
            return !subscription.isUnsubscribed();
          }
        }).subscribe(UseCaseSubscriber);
  }

  public void unsubscribe() {
    if (!subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
  }

  @CheckResult protected abstract Observable<T> interactor();
}
