package com.joker.rxweather.common.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

public class DensityUtil {

  private static final String TAG = DensityUtil.class.getSimpleName();

  private static int screenHeight;
  private static int screenWidth;
  private static int statusBarHeight;
  private static int actionBarSize;

  private static TypedValue mTmpValue = new TypedValue();

  public static int getXmlValue(Context context, int id) {
    synchronized (mTmpValue) {
      TypedValue value = mTmpValue;
      context.getResources().getValue(id, value, true);
      return (int) TypedValue.complexToFloat(value.data);
    }
  }

  /**
   * 将px值转换为sp值
   */
  public static int px2sp(float pxValue) {
    final float fontScale = Resources.getSystem().getDisplayMetrics().scaledDensity;
    return (int) (pxValue / fontScale + 0.5f);
  }

  /**
   * 将sp值转换为px值
   */
  public static int sp2px(float spValue) {
    final float fontScale = Resources.getSystem().getDisplayMetrics().scaledDensity;
    return (int) (spValue * fontScale + 0.5f);
  }

  /**
   * 获取屏幕高度
   */
  public static int getScreenHeight(Context context) {

    WindowManager wm =
        (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
    Display display = wm.getDefaultDisplay();
    Point size = new Point();
    display.getSize(size);
    screenHeight = size.y;

    return screenHeight;
  }

  /**
   * 获取屏幕宽度
   */
  public static int getScreenWidth(Context context) {

    if (screenWidth == 0) {
      WindowManager wm =
          (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
      Display display = wm.getDefaultDisplay();
      Point size = new Point();
      display.getSize(size);
      screenWidth = size.x;
    }

    return screenWidth;
  }

  /**
   * 获取状态栏高度
   */
  public static int getStatusBarHeight(Context context) {
    if (statusBarHeight == 0) {
      int resourceId = context.getApplicationContext()
          .getResources()
          .getIdentifier("status_bar_height", "dimen", "android");
      if (resourceId > 0) {
        statusBarHeight =
            context.getApplicationContext().getResources().getDimensionPixelSize(resourceId);
      }
    }
    return statusBarHeight;
  }

  /**
   * 获取ActionBarSize
   */
  public static int getActionBarSize(Context context) {
    if (actionBarSize == 0) {
      TypedArray actionbarSizeTypedArray = null;
      try {
        actionbarSizeTypedArray =
            context.obtainStyledAttributes(new int[] { android.R.attr.actionBarSize });
        actionBarSize = (int) actionbarSizeTypedArray.getDimension(0, 0);
      } finally {
        actionbarSizeTypedArray.recycle();
      }
    }

    return actionBarSize;
  }

  public static int getLocationY(View item) {

    int[] startingLocation = new int[1];
    /* 得到相对于整个屏幕的区域坐标（左上角坐标——右下角坐标）*/
    Rect viewRect = new Rect();
    item.getGlobalVisibleRect(viewRect);

    startingLocation[0] = viewRect.top + viewRect.bottom;

    return startingLocation[0] / 2 - DensityUtil.getStatusBarHeight(item.getContext());
  }

  public static float calculateScale(Rect startBounds, Rect finalBounds) {

    float scale;
    if ((float) finalBounds.width() / finalBounds.height()
        > (float) startBounds.width() / startBounds.height()) {
      /* Extend start bounds horizontally*/
      scale = (float) startBounds.height() / finalBounds.height();
      float startWidth = scale * finalBounds.width();
      float deltaWidth = (startWidth - startBounds.width()) / 2;
      startBounds.left -= deltaWidth;
      startBounds.right += deltaWidth;
    } else {
      /* Extend start bounds vertically*/
      scale = (float) startBounds.width() / finalBounds.width();
      float startHeight = scale * finalBounds.height();
      float deltaHeight = (startHeight - startBounds.height()) / 2;
      startBounds.top -= deltaHeight;
      startBounds.bottom += deltaHeight;
    }

    return scale;
  }

  public static int hideFromBottom(View view) {
    int height = view.getHeight();
    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
    int margins = params.bottomMargin;
    return height + margins;
  }

  public static boolean isPortrait(Configuration configuration) {
    return configuration.orientation == Configuration.ORIENTATION_PORTRAIT;
  }
}