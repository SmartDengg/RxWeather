package com.joker.rxweather.views.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.joker.rxweather.MyApplication;
import com.joker.rxweather.R;
import com.joker.rxweather.adapter.DetailAdapter;
import com.joker.rxweather.common.Constants;
import com.joker.rxweather.common.rx.rxbus.RxBus;
import com.joker.rxweather.common.Utils;
import com.joker.rxweather.model.entities.ForecastWeatherEntity;
import com.joker.rxweather.model.entities.MainEntity;
import com.joker.rxweather.model.entities.WeatherEntity;
import com.joker.rxweather.ui.ForkView;
import com.joker.rxweather.ui.InsertDecoration;
import com.trello.rxlifecycle.ActivityEvent;
import java.util.List;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Joker on 2015/11/3.
 */
public class DetailActivity extends BaseActivity {

  private static final String TAG = DetailActivity.class.getSimpleName();
  private static final String START_BOUND = "START_BOUND";
  private static final String FINAL_BOUND = "FINAL_BOUND";
  private static final String SCALE = "SCALE";
  private static int orientation = -1;

  @Nullable @Bind(R.id.detail_layout_root_view) FrameLayout rootView;
  @Nullable @Bind(R.id.detail_layout_container) CoordinatorLayout container;

  @Nullable @Bind(R.id.detail_layout_weather_tv) TextView weatherTv;
  @Nullable @Bind(R.id.detail_layout_weather_iv) ImageView weatherIv;
  @Nullable @Bind(R.id.detail_layout_suggest_tv) TextView suggestTv;

  @Nullable @Bind(R.id.detail_layout_rv) RecyclerView recyclerView;

  @Nullable @Bind(R.id.detail_layout_fv) ForkView forkView;
  @Nullable @Bind(R.id.detail_layout_anim_iv) ImageView animIv;

  private Rect startBounds;
  private Rect finalBounds = new Rect();
  private float scale;
  private AnimatorSet animatorSet;

  private MainEntity mainEntity;
  private WeatherEntity weatherEntity;
  private List<ForecastWeatherEntity> forecastWeatherEntities;
  private DetailAdapter detailAdapter;

  private CompositeSubscription compositeSubscription = new CompositeSubscription();

  private RxBus rxBus;

  private boolean cancelBack = false;
  private int x;

  public static void navigateToDetail(AppCompatActivity startingActivity, Rect startBounds,
      Point globalOffset) {
    Intent intent = new Intent(startingActivity, DetailActivity.class);

    intent.putExtra(Constants.POINT, globalOffset).putExtra(Constants.RECT, startBounds);
    startingActivity.startActivity(intent);
  }

  @Override protected int getLayoutId() {
    return R.layout.detail_layout;
  }

  @Override protected void onCreated() {

    if (DetailActivity.orientation == -1) {
      DetailActivity.orientation = getResources().getConfiguration().orientation;
    }

    if (rxBus == null) rxBus = MyApplication.get().getRxBus();

    /*按理说，任何一个粘性事件都不应该持有相应的Observable*/
    if (!rxBus.hasStickObservers() && mainEntity == null) {
      DetailActivity.this.getData();
    }
  }

  private void getData() {

    this.compositeSubscription.add(
        MyApplication.get()
            .getRxBus()
            .toStickObservable()
            .filter(new Func1<Object, Boolean>() {
              @Override public Boolean call(Object object) {
                return object instanceof MainEntity;
              }
            })
            .cast(MainEntity.class)
            .compose(DetailActivity.this.<MainEntity>bindUntilEvent(ActivityEvent.DESTROY))
            .subscribe(new Action1<MainEntity>() {
              @Override public void call(MainEntity mainEntity) {

                DetailActivity.this.mainEntity = mainEntity;

                weatherEntity = mainEntity.getWeatherEntity();
                forecastWeatherEntities = mainEntity.getForecastWeatherEntityList();
              }
            }));
  }

  @Override protected void initView(Bundle savedInstanceState) {

    DetailActivity.this.setupAnimIv();
    DetailActivity.this.setupAdapter();

    if (savedInstanceState == null) {
      rootView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
        @Override public boolean onPreDraw() {
          rootView.getViewTreeObserver().removeOnPreDrawListener(this);
          DetailActivity.this.runEnterAnimation(getIntent().getExtras());
          return true;
        }
      });
    } else {

      DetailActivity.this.startBounds = savedInstanceState.getParcelable(START_BOUND);
      DetailActivity.this.finalBounds = savedInstanceState.getParcelable(FINAL_BOUND);
      DetailActivity.this.scale = savedInstanceState.getFloat(SCALE);

      ViewCompat.setPivotX(animIv, 0.0f);
      ViewCompat.setPivotY(animIv, 0.0f);
      ViewCompat.setTranslationX(animIv, finalBounds.left);
      ViewCompat.setTranslationY(animIv, finalBounds.top);

      DetailActivity.this.initData();

      this.compositeSubscription.add(
          Observable.just(forecastWeatherEntities).subscribe(detailAdapter));
    }
  }

  private void setupAdapter() {

    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DetailActivity.this);
    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    linearLayoutManager.setSmoothScrollbarEnabled(true);

    detailAdapter = new DetailAdapter(DetailActivity.this);

    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setHasFixedSize(true);
    recyclerView.setAdapter(detailAdapter);
    recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
    recyclerView.addItemDecoration(new InsertDecoration(DetailActivity.this));
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (mainEntity != null) {
      outState.putSerializable(Constants.CACHE, mainEntity);
      outState.putParcelable(START_BOUND, startBounds);
      outState.putParcelable(FINAL_BOUND, finalBounds);
      outState.putFloat(SCALE, scale);
    }
  }

  private void runEnterAnimation(Bundle extras) {

    if (animatorSet != null) animatorSet.cancel();

    ViewCompat.setAlpha(container, 0.0f);

    Point globalOffset = extras.getParcelable(Constants.POINT);
    startBounds = extras.getParcelable(Constants.RECT);

    weatherIv.getGlobalVisibleRect(finalBounds);
    finalBounds.offset(-globalOffset.x, -globalOffset.y);
    scale = Utils.calculateScale(startBounds, finalBounds);

    this.animIv.setVisibility(View.VISIBLE);
    this.animIv.setLayerType(View.LAYER_TYPE_HARDWARE, null);

    ViewCompat.setPivotX(animIv, 0.0f);
    ViewCompat.setPivotY(animIv, 0.0f);

    AnimatorSet set = new AnimatorSet();
    set.play(ObjectAnimator.ofFloat(this.animIv, View.X, startBounds.left, finalBounds.left))
        .with(ObjectAnimator.ofFloat(this.animIv, View.Y, startBounds.top, finalBounds.top))
        .with(ObjectAnimator.ofFloat(this.animIv, View.SCALE_X, scale, 1.0f))
        .with(ObjectAnimator.ofFloat(this.animIv, View.SCALE_Y, scale, 1.0f))
        .with(ObjectAnimator.ofFloat(this.animIv, View.ALPHA, 0.4f, 1.0f));
    set.setDuration(Constants.MILLISECONDS_300);
    set.setInterpolator(new DecelerateInterpolator());
    set.addListener(new AnimatorListenerAdapter() {
      @Override public void onAnimationEnd(Animator animation) {

        animIv.setLayerType(View.LAYER_TYPE_NONE, null);
        animIv.setVisibility(View.GONE);
        if (DetailActivity.this.animatorSet != null) DetailActivity.this.initData();

        DetailActivity.this.animatorSet = null;
      }

      @Override public void onAnimationCancel(Animator animation) {
        DetailActivity.this.animatorSet = null;
      }
    });
    set.start();
    DetailActivity.this.animatorSet = set;
  }

  private void setupAnimIv() {

    Glide.with(DetailActivity.this)
        .load(Constants.ICON_URL + weatherEntity.weatherCode + ".png")
        .dontAnimate()
        .diskCacheStrategy(DiskCacheStrategy.RESULT)
        .into(animIv);
  }

  private void initData() {

    ViewCompat.setAlpha(container, 1.0f);

    weatherTv.setText(weatherEntity.cityName);
    suggestTv.setText(weatherEntity.drsgDescription);

    Glide.with(MyApplication.get())
        .load(Constants.ICON_URL + weatherEntity.weatherCode + ".png")
        .dontAnimate()
        .diskCacheStrategy(DiskCacheStrategy.RESULT)
        .into(weatherIv);

    this.compositeSubscription.add(
        Observable.just(forecastWeatherEntities).subscribe(detailAdapter));
  }

  @Override protected void exit() {

    if (DetailActivity.orientation == getResources().getConfiguration().orientation) {
      if (cancelBack) return;
      if (animatorSet != null) animatorSet.cancel();

      ViewCompat.animate(container).alpha(0.0f);

      this.animIv.setVisibility(View.VISIBLE);
      this.animIv.setLayerType(View.LAYER_TYPE_HARDWARE, null);

      AnimatorSet set = new AnimatorSet();
      set.play(ObjectAnimator.ofFloat(this.animIv, View.X, startBounds.left))
          .with(ObjectAnimator.ofFloat(this.animIv, View.Y, startBounds.top))
          .with(ObjectAnimator.ofFloat(this.animIv, View.SCALE_X, scale))
          .with(ObjectAnimator.ofFloat(this.animIv, View.SCALE_Y, scale))
          .with(ObjectAnimator.ofFloat(this.animIv, View.ALPHA, 0.1f));
      set.setDuration(Constants.MILLISECONDS_300);
      set.setInterpolator(new DecelerateInterpolator());
      set.addListener(new AnimatorListenerAdapter() {
        @Override public void onAnimationEnd(Animator animation) {

          DetailActivity.this.animatorSet = null;
          animIv.setLayerType(View.LAYER_TYPE_NONE, null);
          DetailActivity.this.finish();
          overridePendingTransition(0, 0);
        }

        @Override public void onAnimationCancel(Animator animation) {
          DetailActivity.this.cancelBack = true;
          DetailActivity.this.animatorSet = null;
        }
      });
      set.start();
      DetailActivity.this.animatorSet = set;
    } else {
      ViewCompat.animate(rootView)
          .translationY(Utils.getScreenHeight(DetailActivity.this))
          .setDuration(Constants.MILLISECONDS_300)
          .setInterpolator(new DecelerateInterpolator())
          .withLayer()
          .setListener(new ViewPropertyAnimatorListenerAdapter() {
            @Override public void onAnimationEnd(View view) {
              DetailActivity.this.finish();
              overridePendingTransition(0, 0);
            }
          });
    }
  }

  @OnClick(R.id.detail_layout_fv) void onForkViewClick() {

    /*thanks for helping,http://www.jianshu.com/p/eaae783b931f*/
    Intent intent = new Intent();
    intent.setAction(Intent.ACTION_VIEW);
    Uri content_url = Uri.parse(Constants.J_URL);
    intent.setData(content_url);
    if (intent.resolveActivity(getPackageManager()) != null) {
      DetailActivity.this.startActivity(intent);
    }
  }

  @Override protected void onDestroy() {
    if (this.compositeSubscription.hasSubscriptions()) this.compositeSubscription.clear();
    super.onDestroy();

    if (isFinishing()) DetailActivity.orientation = -1;
  }
}
