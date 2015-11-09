package com.joker.rxweather.common;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Looper;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by Joker on 2015/7/25.
 */
public class Utils {



  private static int screenHeight;

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

  public static boolean isPortrait(Configuration configuration) {
    return configuration.orientation == Configuration.ORIENTATION_PORTRAIT;
  }

  /**
   * 勾股定理
   */
  public static float pythagorean(int width, int height) {
    return (float) Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2));
  }

  /**
   * 温度格式化
   */
  public static String formatTemp(String temperature) {
    Float temp = Float.parseFloat(temperature);
    return String.valueOf(Math.round(temp)) + "°";
  }

  public static void checkUiThread() {
    if (Looper.getMainLooper() != Looper.myLooper()) {
      throw new IllegalStateException(
          "Must be called from the main thread. Was: " + Thread.currentThread());
    }
  }
}
