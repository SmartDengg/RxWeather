package com.joker.rxweather.views.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout;
import com.joker.rxweather.MyApplication;
import com.joker.rxweather.R;
import com.joker.rxweather.adapter.ForecastAdapter;
import com.joker.rxweather.common.Constants;
import com.joker.rxweather.common.Utils;
import com.joker.rxweather.common.event.FinishActEvent;
import com.joker.rxweather.common.rx.rxAndroid.SimpleObserver;
import com.joker.rxweather.common.rx.rxbus.RxBus;
import com.joker.rxweather.model.entities.MainEntity;
import com.joker.rxweather.presenter.ListPresenter;
import com.joker.rxweather.presenter.ListPresenterImp;
import com.joker.rxweather.ui.GridDecoration;
import com.joker.rxweather.ui.ProgressLayout;
import com.joker.rxweather.ui.animation.SupportAnimator;
import com.joker.rxweather.ui.animation.ViewAnimationUtils;
import com.joker.rxweather.ui.widget.RevealFrameLayout;
import com.joker.rxweather.views.ListView;
import com.trello.rxlifecycle.ActivityEvent;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.List;
import rx.Observable;
import rx.Single;
import rx.SingleSubscriber;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Joker on 2015/10/29.
 */
public class ListActivity extends BaseActivity
    implements ListView<Observable<List<MainEntity>>>, ForecastAdapter.Callback {

  private static final String TAG = ListActivity.class.getSimpleName();

  @Nullable @Bind(R.id.list_layout_root_view) RelativeLayout rootView;
  @Nullable @Bind(R.id.toolbar_layout_root_view) Toolbar toolbar;
  @Nullable @Bind(R.id.toolbar_layout_title_tv) TextView titleTv;

  @Nullable @Bind(R.id.list_layout_reveal_view) RevealFrameLayout revealFrameLayout;
  @Nullable @Bind(R.id.list_layout_pl) ProgressLayout progressLayout;
  @Nullable @Bind(R.id.list_layout_srl) SwipeRefreshLayout swipeRefreshLayout;
  @Nullable @Bind(R.id.list_layout_rl) RecyclerView recyclerView;

  private ListPresenter<ListView<Observable<List<MainEntity>>>> listPresenter;
  private ForecastAdapter forecastAdapter;
  private Observable<List<MainEntity>> cacheObservable;

  private GridLayoutManager gridLayoutManager;
  private Point globalOffset = null;
  private RxBus rxBus;

  private WeakReference<AppCompatActivity> weakReference;

  private CompositeSubscription compositeSubscription = new CompositeSubscription();

  private View.OnClickListener retryClickListener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      ListActivity.this.listPresenter.loadData();
    }
  };

  public static void navigateToList(AppCompatActivity startingActivity) {
    Intent intent = new Intent(startingActivity, ListActivity.class);
    startingActivity.startActivity(intent);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    /*do nothing O__O "…*/
  }

  @Override protected int getLayoutId() {
    return R.layout.activity_main;
  }

  @Override protected void onCreated() {
    listPresenter = new ListPresenterImp();
    listPresenter.attachView(ListActivity.this);
  }

  @Override protected void initView(Bundle savedInstanceState) {
    ListActivity.this.setSupportActionBar(toolbar);
    ListActivity.this.getSupportActionBar().setTitle("");
    titleTv.setText(getResources().getString(R.string.bottom_bar_forecast));

    ListActivity.this.setupAdapter();

    if (savedInstanceState == null) {
      revealFrameLayout.getViewTreeObserver()
          .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override public boolean onPreDraw() {
              revealFrameLayout.getViewTreeObserver().removeOnPreDrawListener(this);
              ListActivity.this.runEnterAnim();
              return true;
            }
          });
    } else {

      List<MainEntity> entityList =
          (List<MainEntity>) savedInstanceState.getSerializable(Constants.CACHE);
      if (entityList != null) {

        this.cacheObservable = Observable.just(entityList).cache();

        this.compositeSubscription.add(this.cacheObservable.compose(
            ListActivity.this.<List<MainEntity>>bindUntilEvent(ActivityEvent.DESTROY))
            .subscribe(forecastAdapter));
      } else {
        listPresenter.loadData();
      }
    }
  }

  private void setupAdapter() {

    swipeRefreshLayout.setColorSchemeResources(Constants.colors);

    this.compositeSubscription.add(RxSwipeRefreshLayout.refreshes(swipeRefreshLayout)
        .compose(ListActivity.this.<Void>bindUntilEvent(ActivityEvent.DESTROY))
        .subscribe(new SimpleObserver<Void>() {
          @Override public void onNext(Void o) {
           /*refresh*/
            listPresenter.loadData();
          }
        }));

    gridLayoutManager = new GridLayoutManager(ListActivity.this, 2);
    gridLayoutManager.setSmoothScrollbarEnabled(true);
    gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
      @Override public int getSpanSize(int position) {
        /*position == 0*/
        return forecastAdapter.isHeader(position) ? gridLayoutManager.getSpanCount() : 1;
      }
    });

    forecastAdapter = new ForecastAdapter(ListActivity.this);
    forecastAdapter.setCallback(ListActivity.this);

    recyclerView.setLayoutManager(gridLayoutManager);
    recyclerView.setHasFixedSize(true);
    recyclerView.setAdapter(forecastAdapter);
    recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
    recyclerView.addItemDecoration(new GridDecoration(ListActivity.this));
  }

  @Override protected void onSaveInstanceState(final Bundle outState) {
    super.onSaveInstanceState(outState);

    Single.just("1").subscribe(new SingleSubscriber<String>() {
      @Override public void onSuccess(String value) {

      }

      @Override public void onError(Throwable error) {

      }
    });

    if (cacheObservable != null) {
      this.compositeSubscription.add(cacheObservable.compose(
          ListActivity.this.<List<MainEntity>>bindUntilEvent(ActivityEvent.DESTROY))
          .subscribe(new SimpleObserver<List<MainEntity>>() {
            @Override public void onNext(List<MainEntity> mainEntities) {
              outState.putSerializable(Constants.CACHE, (Serializable) mainEntities);
            }
          }));
    }
  }

  private void runEnterAnim() {

    final Rect bounds = new Rect();
    revealFrameLayout.getHitRect(bounds);

    ListActivity.this.revealFrameLayout.setLayerType(View.LAYER_TYPE_HARDWARE, null);

    SupportAnimator revealAnimator =
        ViewAnimationUtils.createCircularReveal(revealFrameLayout.getChildAt(0), 0, bounds.left, 0,
            Utils.pythagorean(bounds.width(), bounds.height()));
    revealAnimator.setDuration(Constants.MILLISECONDS_400);
    revealAnimator.setInterpolator(new AccelerateInterpolator());
    revealAnimator.addListener(new SupportAnimator.SimpleAnimatorListener() {
      @Override public void onAnimationEnd() {
        ListActivity.this.revealFrameLayout.setLayerType(View.LAYER_TYPE_NONE, null);
        listPresenter.loadData();
      }
    });
    revealAnimator.start();
  }

  @Override public void showLoading() {
    progressLayout.showLoading();
  }

  @Override public void showContent() {
    RxSwipeRefreshLayout.refreshing(swipeRefreshLayout).call(false);
    if (!progressLayout.isContent()) progressLayout.showContent();
  }

  @Override public void showError(int messageId) {
    RxSwipeRefreshLayout.refreshing(swipeRefreshLayout).call(false);
    if (!progressLayout.isError()) progressLayout.showError(messageId, retryClickListener);
  }

  @Override public Context getContext() {

    if (weakReference == null) {
      weakReference = new WeakReference<AppCompatActivity>(ListActivity.this);
    }

    return weakReference.get();
  }

  @Override public void showForecasts(Observable<List<MainEntity>> listObservable) {

    this.cacheObservable = listObservable;

    this.compositeSubscription.add(this.cacheObservable.compose(
        ListActivity.this.<List<MainEntity>>bindUntilEvent(ActivityEvent.DESTROY))
        .subscribe(forecastAdapter));
  }

  @Override public boolean isContent() {
    return progressLayout.isContent() || swipeRefreshLayout.isRefreshing();
  }

  @Override public void onItemClick(MainEntity entity, int position) {

    View target;
    if (position == 0) {
      target = gridLayoutManager.findViewByPosition(position)
          .findViewById(R.id.list_item_header_temp_iv);
    } else {
      target =
          gridLayoutManager.findViewByPosition(position).findViewById(R.id.list_item_grid_temp_iv);
    }

    Rect startBounds = new Rect();
    target.getGlobalVisibleRect(startBounds);

    if (globalOffset == null) {
      globalOffset = new Point();
      Rect rootRect = new Rect();
      rootView.getGlobalVisibleRect(rootRect, globalOffset);
    }
    startBounds.offset(-globalOffset.x, -globalOffset.y);

    if (this.rxBus == null) {
      this.rxBus = MyApplication.get().getRxBus();
    }
    this.rxBus.postStickEvent(entity);

    DetailActivity.navigateToDetail(ListActivity.this, startBounds, globalOffset);
    overridePendingTransition(0, 0);
  }

  @OnClick(R.id.bottom_bar_search_tv) void onSearchClick() {
    SearchActivity.navigateToSearch(ListActivity.this);
    overridePendingTransition(0, 0);
  }

  @Override protected void exit() {

    if (this.rxBus == null) this.rxBus = MyApplication.get().getRxBus();
    if (this.rxBus.hasObservers()) this.rxBus.postEvent(new FinishActEvent());
  }

  @Override protected void onDestroy() {
    if (this.compositeSubscription.hasSubscriptions()) this.compositeSubscription.clear();
    super.onDestroy();

    this.listPresenter.detachView();
  }
}
