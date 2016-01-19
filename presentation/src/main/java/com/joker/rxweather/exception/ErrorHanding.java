package com.joker.rxweather.exception;

import android.util.Log;
import com.joker.rxweather.R;
import com.joker.rxweather.model.service.exception.UnknowCityException;
import com.joker.rxweather.model.service.exception.WebServiceException;
import java.util.concurrent.TimeoutException;
import retrofit.RetrofitError;

public class ErrorHanding {

  private static final String TAG = ErrorHanding.class.getSimpleName();

  private ErrorHanding() {
    /*never invoked*/
  }

  public static int propagate(Throwable throwable) {

    int messageId;

    if (throwable instanceof UnknowCityException) {
      messageId = R.string.unknow_city_exception_message;
    } else if (throwable instanceof WebServiceException) {
      messageId = R.string.service_exception_message;
    } else if (throwable instanceof TimeoutException) {
      messageId = R.string.timeout_exception_message;
    } else if (throwable instanceof RetrofitError) {
      Log.e(TAG, ((RetrofitError) throwable).getKind().name());
      messageId = R.string.six_word_exception_message;
    } else {
      throwable.printStackTrace();
      throw new RuntimeException("See inner exception");
    }

    return messageId;
  }
}
