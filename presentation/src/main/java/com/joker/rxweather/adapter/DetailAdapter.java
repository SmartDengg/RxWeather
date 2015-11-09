package com.joker.rxweather.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.joker.rxweather.R;
import com.joker.rxweather.common.Constants;
import com.joker.rxweather.common.Utils;
import com.joker.rxweather.model.entities.ForecastWeatherEntity;
import java.util.List;
import rx.Observer;

/**
 * Created by Joker on 2015/11/3.
 */
public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ViewHolder>
    implements Observer<List<ForecastWeatherEntity>> {

  private Context context;
  private List<ForecastWeatherEntity> forecastWeatherEntities;
  private boolean animationsLocked = false;
  private int lastAnimatedPosition = -1;

  public DetailAdapter(Context context) {
    this.context = context;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new ViewHolder(
        LayoutInflater.from(context).inflate(R.layout.i_detail_item, parent, false));
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {

    DetailAdapter.this.bindValue(holder, forecastWeatherEntities.get(position));
    DetailAdapter.this.runEnterAnimation(holder.itemView, position);
  }

  private void bindValue(ViewHolder holder, ForecastWeatherEntity entity) {

    holder.dateTv.setText(entity.getDate());
    holder.windTv.setText(entity.getWindDescription());

    holder.dayTv.setText(entity.getDayText());
    holder.nightTv.setText(entity.getNightText());

    holder.dayDegreeTv.setText(Utils.formatTemp(entity.getMaxTemp()));
    holder.nightDegreeTv.setText(Utils.formatTemp(entity.getMinTemp()));

    Glide.with(context)
        .load(Constants.ICON_URL + entity.getDayCode() + ".png")
        .dontAnimate()
        .placeholder(R.drawable.holding_icon)
        .error(R.drawable.holding_icon)
        .diskCacheStrategy(DiskCacheStrategy.RESULT)
        .into(holder.dayIv);

    Glide.with(context)
        .load(Constants.ICON_URL + entity.getNightCode() + ".png")
        .dontAnimate()
        .placeholder(R.drawable.holding_icon)
        .error(R.drawable.holding_icon)
        .diskCacheStrategy(DiskCacheStrategy.RESULT)
        .into(holder.nightIv);
  }

  private void runEnterAnimation(View itemView, int position) {

    if (animationsLocked) return;

    if (position > lastAnimatedPosition) {
      DetailAdapter.this.lastAnimatedPosition = position;

      ViewCompat.setTranslationY(itemView, 100);
      ViewCompat.setAlpha(itemView, 0.0f);

      ViewCompat.animate(itemView)
          .translationY(0.0f)
          .alpha(1.0f)
          .setStartDelay(position * 20)
          .setInterpolator(new DecelerateInterpolator(2.0f))
          .setDuration(Constants.MILLISECONDS_300)
          .withLayer()
          .setListener(new ViewPropertyAnimatorListenerAdapter() {
            @Override public void onAnimationEnd(View view) {
              DetailAdapter.this.animationsLocked = true;
            }
          })
          .start();
    }
  }

  @Override public int getItemCount() {
    return (this.forecastWeatherEntities != null) ? this.forecastWeatherEntities.size() : 0;
  }

  @Override public void onCompleted() {
    /*never invoked*/
  }

  @Override public void onError(Throwable e) {
    e.printStackTrace();
  }

  @Override public void onNext(List<ForecastWeatherEntity> mainEntities) {
    this.forecastWeatherEntities = mainEntities;
    DetailAdapter.this.notifyDataSetChanged();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    @Nullable @Bind(R.id.detail_item_date_tv) TextView dateTv;
    @Nullable @Bind(R.id.detail_item_wind_tv) TextView windTv;

    @Nullable @Bind(R.id.detail_item_weather_day_tv) TextView dayTv;
    @Nullable @Bind(R.id.detail_item_weather_day_degree_tv) TextView dayDegreeTv;
    @Nullable @Bind(R.id.detail_item_weather_day_iv) ImageView dayIv;

    @Nullable @Bind(R.id.detail_item_weather_night_tv) TextView nightTv;
    @Nullable @Bind(R.id.detail_item_weather_night_degree_tv) TextView nightDegreeTv;
    @Nullable @Bind(R.id.detail_item_weather_night_iv) ImageView nightIv;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(ViewHolder.this, itemView);
    }
  }
}
