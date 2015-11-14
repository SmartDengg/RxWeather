package com.joker.rxweather.common.rx.rxbus;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;

/**
 * Created by Joker on 2015/10/31.
 */
public class RxBus<T, R> {

  private static final String TAG = RxBus.class.getSimpleName();

  private SerializedSubject<T, R> rxStandardBus;
  private SerializedSubject<T, R> rxStickBus;

  private RxBus() {
    rxStandardBus = new SerializedSubject(PublishSubject.<T>create());
    rxStickBus = new SerializedSubject(BehaviorSubject.<T>create());
  }

  private static class SingletonHolder {
    private static RxBus<Object, Object> instance = new RxBus<>();
  }

  public static RxBus getInstance() {
    return SingletonHolder.instance;
  }

  public void postEvent(T event) {
    rxStandardBus.onNext(event);
  }

  public void postStickEvent(T event) {
    rxStickBus.onNext(event);
  }

  public Observable<R> toObservable() {
    return rxStandardBus.asObservable().onBackpressureBuffer();
  }

  public Observable<R> toStickObservable() {
    return rxStickBus.asObservable().share().onBackpressureBuffer();
  }

  public boolean hasObservers() {
    return rxStandardBus.hasObservers();
  }

  public boolean hasStickObservers() {
    return rxStickBus.hasObservers();
  }
}
