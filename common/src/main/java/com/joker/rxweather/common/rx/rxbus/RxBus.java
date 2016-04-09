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

    private SerializedSubject<Object, Object> rxBus;
    private SerializedSubject<Object, Object> rxStickBus;

    @SuppressWarnings("unchecked")
    private RxBus() {
        rxBus = new SerializedSubject(PublishSubject.create());
        rxStickBus = new SerializedSubject(BehaviorSubject.create());
    }

    private static class SingletonHolder {

        private static RxBus instance = new RxBus();
    }

    public static RxBus getInstance() {
        return SingletonHolder.instance;
    }

    public void postEvent(Object event) {
        if (this.hasObservers()) rxBus.onNext(event);
    }

    public void postStickEvent(Object event) {
        rxStickBus.onNext(event);
    }

    public <T> Observable<T> toObservable(Class<T> type) {
        return rxBus.asObservable().ofType(type).onBackpressureBuffer();
    }

    public <T> Observable<T> toStickObservable(Class<T> type) {
        return rxStickBus.asObservable().ofType(type).onBackpressureBuffer();
    }

    private boolean hasObservers() {
        return rxBus.hasObservers();
    }

    @Deprecated
    public boolean hasStickObservers() {
        return rxStickBus.hasObservers();
    }
}
