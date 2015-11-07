package com.joker.rxweather.ui;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.joker.rxweather.R;

public class GridDecoration extends RecyclerView.ItemDecoration {
  private int margin;

  public GridDecoration(Context context) {
    margin = context.getResources().getDimensionPixelSize(R.dimen.material_8dp);
  }

  @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
      RecyclerView.State state) {

    int position = parent.getChildAdapterPosition(view);

    if (position > 0) {
      if (position % 2 == 1) {
        outRect.set(margin, margin, margin / 2, margin);
      } else {
        outRect.set(margin / 2, margin, margin, margin);
      }
    } else {
      outRect.set(0, 0, 0, margin);
    }
  }
}