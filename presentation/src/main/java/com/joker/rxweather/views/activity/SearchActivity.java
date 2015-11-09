package com.joker.rxweather.views.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.joker.rxweather.MyApplication;
import com.joker.rxweather.R;
import com.joker.rxweather.common.Constants;
import com.joker.rxweather.common.event.FinishActEvent;
import com.joker.rxweather.common.rx.rxAndroid.SimpleObserver;
import com.joker.rxweather.common.rx.rxAndroid.schedulers.AndroidSchedulers;
import com.joker.rxweather.common.rx.rxbus.RxBus;
import com.joker.rxweather.common.Utils;
import com.joker.rxweather.model.entities.SearchEntity;
import com.joker.rxweather.presenter.SearchPresenter;
import com.joker.rxweather.presenter.SearchPresenterImp;
import com.joker.rxweather.ui.ProgressLayout;
import com.joker.rxweather.ui.animation.SupportAnimator;
import com.joker.rxweather.ui.animation.ViewAnimationUtils;
import com.joker.rxweather.ui.widget.RevealFrameLayout;
import com.joker.rxweather.views.SearchView;
import com.trello.rxlifecycle.ActivityEvent;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Joker on 2015/11/4.
 */
public class SearchActivity extends BaseActivity implements SearchView<Observable<SearchEntity>> {

  private static final String TAG = SearchActivity.class.getSimpleName();

  @Nullable @Bind(R.id.search_layout_root_view) RelativeLayout rootView;
  @Nullable @Bind(R.id.toolbar_layout_root_view) Toolbar toolbar;
  @Nullable @Bind(R.id.toolbar_layout_title_tv) TextView titleTv;

  @Nullable @Bind(R.id.search_layout_reveal_view) RevealFrameLayout revealFrameLayout;
  @Nullable @Bind(R.id.search_layout_pl) ProgressLayout progressLayout;

  @Nullable @Bind(R.id.search_layout_ill) TextInputLayout inputLayout;
  @Nullable @Bind(R.id.search_layout_et) EditText editText;
  @Nullable @Bind(R.id.search_layout_text_tv) TextView weatherTv;
  @Nullable @Bind(R.id.search_layout_text_iv) ImageView weatherIv;

  /*要查询的城市名称*/
  private String cityName;

  private SearchPresenter<SearchView<Observable<SearchEntity>>> searchPresenter;
  private Observable<SearchEntity> cacheObservable;

  private RxBus rxBus;
  private WeakReference<AppCompatActivity> weakReference;

  private CompositeSubscription compositeSubscription = new CompositeSubscription();

  private View.OnClickListener retryClickListener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      SearchActivity.this.searchPresenter.search(cityName);
    }
  };

  public static void navigateToSearch(AppCompatActivity startingActivity) {
    Intent intent = new Intent(startingActivity, SearchActivity.class);
    startingActivity.startActivity(intent);
  }

  @Override protected int getLayoutId() {
    return R.layout.search_layout;
  }

  @Override protected void onCreated() {
    searchPresenter = new SearchPresenterImp();
    searchPresenter.attachView(SearchActivity.this);
  }

  @Override protected void initView(Bundle savedInstanceState) {
    SearchActivity.this.setSupportActionBar(toolbar);
    SearchActivity.this.getSupportActionBar().setTitle("");

    this.titleTv.setText(getResources().getString(R.string.bottom_bar_search));
    this.inputLayout.setHint(getResources().getString(R.string.hint));

    SearchActivity.this.setListener();

    if (savedInstanceState == null) {
      rootView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
        @Override public boolean onPreDraw() {
          rootView.getViewTreeObserver().removeOnPreDrawListener(this);
          SearchActivity.this.runEnterAnim();
          return true;
        }
      });
    } else {

      SearchEntity searchEntity =
          (SearchEntity) savedInstanceState.getSerializable(Constants.CACHE);
      SearchActivity.this.cityName =
          (String) savedInstanceState.getSerializable(Constants.CACHE_CITY);

      if (searchEntity != null) {
        SearchActivity.this.showResult(Observable.just(searchEntity));
      } else if (!TextUtils.isEmpty(cityName)) {
        searchPresenter.search(cityName);
      }
    }
  }

  private void setListener() {

    this.compositeSubscription.add(RxTextView.textChanges(editText)
        .skip(1)
        .debounce(Constants.MILLISECONDS_600, TimeUnit.MILLISECONDS)
        .onBackpressureLatest()
        .observeOn(AndroidSchedulers.mainThread())
        .compose(SearchActivity.this.<CharSequence>bindUntilEvent(ActivityEvent.DESTROY))
        .subscribe(new SimpleObserver<CharSequence>() {
          @Override public void onNext(CharSequence charSequence) {

            String temp = charSequence.toString();
            if (!TextUtils.isEmpty(temp.trim()) && !temp.equals(SearchActivity.this.cityName)) {

              SearchActivity.this.cityName = charSequence.toString().trim();
              SearchActivity.this.searchPresenter.search(cityName);
            }
          }
        }));
  }

  @Override protected void onSaveInstanceState(final Bundle outState) {
    super.onSaveInstanceState(outState);

    if (cacheObservable != null) {

      this.compositeSubscription.add(cacheObservable//
          .compose(SearchActivity.this.<SearchEntity>bindUntilEvent(ActivityEvent.DESTROY))
          .subscribe(new SimpleObserver<SearchEntity>() {
            @Override public void onNext(SearchEntity searchEntity) {
              outState.putSerializable(Constants.CACHE, searchEntity);
              outState.putSerializable(Constants.CACHE_CITY, SearchActivity.this.cityName);
            }
          }));
    }
  }

  private void runEnterAnim() {

    final Rect bounds = new Rect();
    revealFrameLayout.getHitRect(bounds);

    SearchActivity.this.revealFrameLayout.setLayerType(View.LAYER_TYPE_HARDWARE, null);

    SupportAnimator revealAnimator =
        ViewAnimationUtils.createCircularReveal(revealFrameLayout.getChildAt(0), 0, bounds.left, 0,
            Utils.pythagorean(bounds.width(), bounds.height()));
    revealAnimator.setDuration(Constants.MILLISECONDS_400);
    revealAnimator.setInterpolator(new AccelerateInterpolator());
    revealAnimator.addListener(new SupportAnimator.SimpleAnimatorListener() {
      @Override public void onAnimationEnd() {
        SearchActivity.this.revealFrameLayout.setLayerType(View.LAYER_TYPE_NONE, null);
      }
    });
    revealAnimator.start();
  }

  @OnClick(R.id.bottom_bar_forecast_tv) void onForecastClick() {
    SearchActivity.this.finish();
    overridePendingTransition(0, 0);
  }

  @Override public void showSearchResult(Observable<SearchEntity> searchEntityObservable) {
    SearchActivity.this.showResult(searchEntityObservable);
  }

  private void showResult(Observable<SearchEntity> searchEntityObservable) {

    this.cacheObservable = searchEntityObservable;

    this.compositeSubscription.add(cacheObservable.compose(
        SearchActivity.this.<SearchEntity>bindUntilEvent(ActivityEvent.DESTROY))
        .subscribe(new SimpleObserver<SearchEntity>() {
          @Override public void onNext(SearchEntity searchEntity) {

            SearchActivity.this.weatherTv.setText(
                Utils.formatTemp(searchEntity.currentTemp) + "  ,  " + searchEntity.weatherText);

            Glide.with(SearchActivity.this)
                .load(Constants.ICON_URL + searchEntity.weatherCode + ".png")
                .dontAnimate()
                .placeholder(R.drawable.holding_icon)
                .error(R.drawable.holding_icon)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(SearchActivity.this.weatherIv);
          }
        }));
  }

  @Override public boolean isContent() {
    return progressLayout.isContent();
  }

  @Override public void showLoading() {
    progressLayout.showLoading();
  }

  @Override public void showContent() {
    if (!progressLayout.isContent()) progressLayout.showContent();
  }

  @Override public void showError(int messageId) {
    if (!progressLayout.isError()) progressLayout.showError(messageId, retryClickListener);
  }

  @Override public Context getContext() {

    if (weakReference == null) {
      weakReference = new WeakReference<AppCompatActivity>(SearchActivity.this);
    }

    return weakReference.get();
  }

  @Override protected void exit() {

    if (rxBus == null) rxBus = MyApplication.get().getRxBus();
    if (rxBus.hasObservers()) rxBus.postEvent(new FinishActEvent());
  }

  @Override protected void onDestroy() {
    if (this.compositeSubscription.hasSubscriptions()) this.compositeSubscription.clear();
    super.onDestroy();

    this.searchPresenter.detachView();
  }
}
