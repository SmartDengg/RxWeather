package com.joker.rxweather.ui;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.joker.rxweather.R;

public class InsertDecoration extends RecyclerView.ItemDecoration {
  private int margin;

  public InsertDecoration(Context context) {
    margin = context.getResources().getDimensionPixelSize(R.dimen.material_8dp);
  }

  @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
      RecyclerView.State state) {
    outRect.set(margin/2, 0, margin/2, margin);
  }
}