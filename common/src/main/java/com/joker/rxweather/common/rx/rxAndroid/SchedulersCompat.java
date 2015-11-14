package com.joker.rxweather.common.rx.rxAndroid;

import com.joker.rxweather.common.rx.rxAndroid.schedulers.AndroidSchedulers;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by Joker on 2015/8/10.
 */
public class SchedulersCompat {

  private static final Observable.Transformer computationTransformer =
      new Observable.Transformer() {
        @Override public Object call(Object observable) {
          return ((Observable) observable).subscribeOn(Schedulers.computation())
              .observeOn(AndroidSchedulers.mainThread());
        }
      };

  private static final Observable.Transformer ioTransformer = new Observable.Transformer() {
    @Override public Object call(Object observable) {
      return ((Observable) observable).subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread());
    }
  };
  private static final Observable.Transformer newTransformer = new Observable.Transformer() {
    @Override public Object call(Object observable) {
      return ((Observable) observable).subscribeOn(Schedulers.newThread())
          .observeOn(AndroidSchedulers.mainThread());
    }
  };
  private static final Observable.Transformer trampolineTransformer = new Observable.Transformer() {
    @Override public Object call(Object observable) {
      return ((Observable) observable).subscribeOn(Schedulers.trampoline())
          .observeOn(AndroidSchedulers.mainThread());
    }
  };

  private static final Observable.Transformer executorTransformer = new Observable.Transformer() {
    @Override public Object call(Object observable) {
      return ((Observable) observable).subscribeOn(Schedulers.from(JobExecutor.eventExecutor))
          .observeOn(AndroidSchedulers.mainThread());
    }
  };

  /**
   * Don't break the chain: use RxJava's compose() operator
   */
  public static <T> Observable.Transformer<T, T> applyComputationSchedulers() {
    return (Observable.Transformer<T, T>) computationTransformer;
  }

  public static <T> Observable.Transformer<T, T> applyIoSchedulers() {
    return (Observable.Transformer<T, T>) ioTransformer;
  }

  public static <T> Observable.Transformer<T, T> applyNewSchedulers() {
    return (Observable.Transformer<T, T>) newTransformer;
  }

  public static <T> Observable.Transformer<T, T> applyTrampolineSchedulers() {
    return (Observable.Transformer<T, T>) trampolineTransformer;
  }

  public static <T> Observable.Transformer<T, T> applyExecutorSchedulers() {
    return (Observable.Transformer<T, T>) executorTransformer;
  }
}
