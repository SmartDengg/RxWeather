/**
 * Copyright (C) 2015 Fernando Cejas Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
