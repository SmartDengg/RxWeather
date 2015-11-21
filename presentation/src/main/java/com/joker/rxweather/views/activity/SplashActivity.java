package com.joker.rxweather.views.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.joker.rxweather.R;

public class SplashActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    SplashActivity.this.setTheme(R.style.AppTheme_WhiteActivity);
    super.onCreate(savedInstanceState);

    ListActivity.navigateToList(SplashActivity.this);
    SplashActivity.this.finish();
    overridePendingTransition(0, 0);
  }
}
