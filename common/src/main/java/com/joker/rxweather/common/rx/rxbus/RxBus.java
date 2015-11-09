package com.joker.rxweather.common.rx.rxbus;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;

/**
 * Created by Joker on 2015/10/31.
 */
public class RxBus {

  private static final String TAG = RxBus.class.getSimpleName();

  private SerializedSubject<Object, Object> rxStandardBus;
  private SerializedSubject<Object, Object> rxStickBus;

  private RxBus() {
    rxStandardBus = new SerializedSubject<>(PublishSubject.create());
    rxStickBus = new SerializedSubject<>(BehaviorSubject.create());

  }

  private static class SingletonHolder {
    private static RxBus instance = new RxBus();
  }

  public static RxBus getInstance() {
    return SingletonHolder.instance;
  }

  public void postEvent(Object event) {
    rxStandardBus.onNext(event);
  }

  public void postStickEvent(Object event) {
    rxStickBus.onNext(event);
  }

  public <T> Observable<T> toObservable() {
    return (Observable<T>) rxStandardBus.asObservable().onBackpressureBuffer();
  }

  public <T> Observable<T> toStickObservable() {
    return (Observable<T>) rxStickBus.asObservable().share().onBackpressureBuffer();
  }

  public boolean hasObservers() {
    return rxStandardBus.hasObservers();
  }

  public boolean hasStickObservers() {
    return rxStickBus.hasObservers();
  }
}
