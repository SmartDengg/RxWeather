package com.joker.rxweather.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.joker.rxweather.R;
import com.joker.rxweather.common.Constants;
import com.joker.rxweather.common.Utils;
import com.joker.rxweather.common.rx.rxAndroid.schedulers.AndroidSchedulers;
import com.joker.rxweather.model.entities.MainEntity;
import com.joker.rxweather.model.entities.WeatherEntity;
import com.joker.rxweather.views.activity.BaseActivity;
import com.trello.rxlifecycle.ActivityEvent;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Observer;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;

/**
 * Created by Joker on 2015/11/3.
 */
public class ForecastAdapter extends RecyclerView.Adapter implements Observer<List<MainEntity>> {

  private static final String TAG = ForecastAdapter.class.getSimpleName();
  private BaseActivity activity;
  private List<MainEntity> mainEntities;
  private Callback callback;
  private final SerializedSubject<Integer, Integer> serializedSubject;

  public static final int TYPE_HEADER = 0;
  public static final int TYPE_GRID = 1;

  public ForecastAdapter(BaseActivity activity) {
    this.activity = activity;
    this.serializedSubject = PublishSubject.<Integer>create().toSerialized();

    serializedSubject//
        .throttleFirst(Constants.MILLISECONDS_400, TimeUnit.MILLISECONDS,
            AndroidSchedulers.mainThread())
        .compose(activity.<Integer>bindUntilEvent(ActivityEvent.DESTROY))
        .forEach(new Action1<Integer>() {
          @Override public void call(Integer position) {
            if (callback != null) callback.onItemClick(mainEntities.get(position), position);
          }
        });
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

    if (viewType == TYPE_HEADER) {
      return new HeaderViewHolder(
          LayoutInflater.from(activity).inflate(R.layout.i_list_item_header, parent, false));
    } else {
      return new GridViewHolder(
          LayoutInflater.from(activity).inflate(R.layout.i_list_item_grid, parent, false));
    }
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    int viewType = this.getItemViewType(position);
    if (viewType == TYPE_HEADER) {
      this.bindHeaderValue((HeaderViewHolder) holder, position);
    } else {
      this.bindGridValue((GridViewHolder) holder, position);
    }
  }

  private void bindHeaderValue(HeaderViewHolder holder, int position) {

    holder.rootView.setTag(position);
    MainEntity mainEntity = mainEntities.get(position);

    WeatherEntity weatherEntity = mainEntity.getWeatherEntity();

    holder.locationTv.setText(weatherEntity.cityName + "  :  " + weatherEntity.weatherText);
    Glide.with(activity)
        .load(Constants.ICON_URL + weatherEntity.weatherCode + ".png")
        .dontAnimate()
        .placeholder(R.drawable.holding_icon)
        .error(R.drawable.holding_icon)
        .diskCacheStrategy(DiskCacheStrategy.RESULT)
        .into(holder.tempIv);
    holder.tempTv.setText("实时天气  :  " +
        Utils.formatTemp(weatherEntity.currentTemp) + " ，" + weatherEntity.windDescription);
    holder.suggestTv.setText(weatherEntity.drsgDescription);
  }

  private void bindGridValue(GridViewHolder holder, int position) {

    holder.rootView.setTag(position);
    MainEntity mainEntity = mainEntities.get(position);

    WeatherEntity weatherEntity = mainEntity.getWeatherEntity();

    holder.locationTv.setText(weatherEntity.cityName + Utils.formatTemp(weatherEntity.currentTemp));
    Glide.with(activity)
        .load(Constants.ICON_URL + weatherEntity.weatherCode + ".png")
        .dontAnimate()
        .placeholder(R.drawable.holding_icon)
        .error(R.drawable.holding_icon)
        .diskCacheStrategy(DiskCacheStrategy.RESULT)
        .into(holder.tempIv);
  }

  @Override public int getItemCount() {
    return (this.mainEntities != null) ? this.mainEntities.size() : 0;
  }

  @Override public int getItemViewType(int position) {
    return position == 0 ? TYPE_HEADER : TYPE_GRID;
  }

  @Override public void onCompleted() {
  /*never invoked*/
  }

  @Override public void onError(Throwable e) {
    e.printStackTrace();
  }

  @Override public void onNext(List<MainEntity> weatherEntities) {

    this.mainEntities = weatherEntities;
    ForecastAdapter.this.notifyDataSetChanged();
  }

  public class HeaderViewHolder extends RecyclerView.ViewHolder {

    @Nullable @Bind(R.id.list_item_header_root_view) CardView rootView;
    @Nullable @Bind(R.id.list_item_header_location_tv) TextView locationTv;
    @Nullable @Bind(R.id.list_item_header_temp_iv) ImageView tempIv;
    @Nullable @Bind(R.id.list_item_header_temp_tv) TextView tempTv;
    @Nullable @Bind(R.id.list_item_header_suggest_tv) TextView suggestTv;

    public HeaderViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(HeaderViewHolder.this, itemView);
    }

    @Nullable @OnClick(R.id.list_item_header_root_view) void onItemClick(ViewGroup rootView) {
      serializedSubject.onNext((Integer) rootView.getTag());
    }
  }

  public class GridViewHolder extends RecyclerView.ViewHolder {

    @Nullable @Bind(R.id.list_item_grid_root_view) CardView rootView;
    @Nullable @Bind(R.id.list_item_grid_location_tv) TextView locationTv;
    @Nullable @Bind(R.id.list_item_grid_temp_iv) ImageView tempIv;

    public GridViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(GridViewHolder.this, itemView);
    }

    @Nullable @OnClick(R.id.list_item_grid_root_view) void onItemClick(ViewGroup rootView) {
      serializedSubject.onNext((Integer) rootView.getTag());
    }
  }

  public boolean isHeader(int position) {
    return position == 0;
  }

  public void setCallback(Callback callback) {
    this.callback = callback;
  }

  public interface Callback {

    void onItemClick(MainEntity entity, int position);
  }
}
