package com.joker.rxweather.views.activity;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.view.KeyEvent;
import butterknife.ButterKnife;
import com.joker.rxweather.MyApplication;
import com.joker.rxweather.R;
import com.joker.rxweather.common.event.FinishActEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.Subscriptions;

public abstract class BaseActivity extends RxAppCompatActivity {

  private Subscription subscribe = Subscriptions.empty();

  @Override public void setContentView(int layoutResID) {
    super.setContentView(layoutResID);
    ButterKnife.bind(BaseActivity.this);
  }

  @CallSuper @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(BaseActivity.this.getLayoutId());
    BaseActivity.this.onCreated();
    BaseActivity.this.initView(savedInstanceState);

    if (this instanceof ListActivity || this instanceof SearchActivity) {
      BaseActivity.this.SubscribeToFinish();
    }
  }

  @SuppressWarnings("unchecked") private void SubscribeToFinish() {

    /**
     * https://gist.github.com/benjchristensen/04eef9ca0851f3a5d7bf
     */
    subscribe = MyApplication
        .get()
        .getRxBus()
        .toObservable(FinishActEvent.class)
        .subscribe(new Action1<FinishActEvent>() {
          @Override public void call(FinishActEvent finishActEvent) {

            BaseActivity.this.finish();
            overridePendingTransition(R.anim.anim_none, R.anim.scale_out);
          }
        });
  }

  @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
      BaseActivity.this.exit();
    }
    return false;
  }

  @CallSuper @Override protected void onDestroy() {
    if (subscribe != null && !subscribe.isUnsubscribed()) subscribe.unsubscribe();
    super.onDestroy();

    ButterKnife.unbind(BaseActivity.this);
    MyApplication.get().getRefWatcher().watch(this);
  }

  protected abstract int getLayoutId();

  protected abstract void onCreated();

  protected abstract void initView(Bundle savedInstanceState);

  protected abstract void exit();
}
