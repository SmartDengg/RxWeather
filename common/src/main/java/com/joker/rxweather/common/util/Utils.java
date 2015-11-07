package com.joker.rxweather.common.util;

import android.os.Looper;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Joker on 2015/7/25.
 */
public class Utils {

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

  public static String dateFormat(long time) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("", Locale.SIMPLIFIED_CHINESE);
    simpleDateFormat.applyPattern("MMddHHmmss");
    return simpleDateFormat.format(time);
  }

  public static boolean isUiThread() {
    return Looper.getMainLooper() != Looper.myLooper();
  }

  public static void checkUiThread() {
    if (Looper.getMainLooper() != Looper.myLooper()) {
      throw new IllegalStateException(
          "Must be called from the main thread. Was: " + Thread.currentThread());
    }
  }
}
