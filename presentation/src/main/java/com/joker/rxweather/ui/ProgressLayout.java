package com.joker.rxweather.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.joker.rxweather.R;
import java.util.ArrayList;
import java.util.List;

public class ProgressLayout extends RelativeLayout {

  private static final String LOADING_TAG = "ProgressLayout.LOADING_TAG";
  private static final String ERROR_TAG = "ProgressLayout.ERROR_TAG";

  private LayoutInflater inflater;
  private LayoutParams layoutParams;
  private View loadingGroup;
  private View errorGroup;

  private RelativeLayout loadingLayout;
  private RelativeLayout errorLayout;
  private TextView errorTextView;
  private Button errorButton;

  private List<View> contentViews = new ArrayList<>();

  private enum State {
    LOADING, CONTENT, ERROR
  }

  private State currentState = State.LOADING;

  public ProgressLayout(Context context) {
    super(context);
  }

  public ProgressLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(attrs);
  }

  public ProgressLayout(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init(attrs);
  }

  private void init(AttributeSet attrs) {
    inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  }

  @Override public void addView(View child, int index, ViewGroup.LayoutParams params) {
    super.addView(child, index, params);

    if (child.getTag() == null || (!child.getTag().equals(LOADING_TAG) && !child.getTag()
        .equals(ERROR_TAG))) {
      contentViews.add(child);
    }
  }

  public void showLoading() {

    ProgressLayout.this.showLoadingView();
    ProgressLayout.this.hideErrorView();
    ProgressLayout.this.setContentVisibility(false);
  }

  public void showContent() {

    ProgressLayout.this.hideLoadingView();
    ProgressLayout.this.hideErrorView();
    ProgressLayout.this.setContentVisibility(true);
  }

  public void showError(@StringRes int stringId, @NonNull OnClickListener onClickListener) {

    ProgressLayout.this.hideLoadingView();
    ProgressLayout.this.showErrorView();

    errorTextView.setText(getResources().getString(stringId));
    errorButton.setOnClickListener(onClickListener);
    ProgressLayout.this.setContentVisibility(false);
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    if (errorButton != null) errorButton.setOnClickListener(null);
  }

  public State getCurrentState() {
    return currentState;
  }

  public boolean isContent() {
    return currentState == State.CONTENT;
  }

  public boolean isLoading() {
    return currentState == State.LOADING;
  }

  public boolean isError() {
    return currentState == State.ERROR;
  }

  private void showLoadingView() {

    if (loadingGroup == null) {
      loadingGroup = inflater.inflate(R.layout.progress_loading_view, null);
      loadingLayout = (RelativeLayout) loadingGroup.findViewById(R.id.loadingStateRelativeLayout);
      loadingLayout.setTag(LOADING_TAG);

      layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT);
      layoutParams.addRule(CENTER_IN_PARENT);

      ProgressLayout.this.addView(loadingLayout, layoutParams);
    } else {
      loadingLayout.setVisibility(VISIBLE);
    }
  }

  private void showErrorView() {

    if (errorGroup == null) {
      errorGroup = inflater.inflate(R.layout.progress_error_layout, null);
      errorLayout = (RelativeLayout) errorGroup.findViewById(R.id.progress_error_layout_rl);
      errorLayout.setTag(ERROR_TAG);

      errorTextView = (TextView) errorGroup.findViewById(R.id.progress_error_tv);
      errorButton = (Button) errorGroup.findViewById(R.id.progress_error_btn);

      layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT);
      layoutParams.addRule(CENTER_IN_PARENT);

      ProgressLayout.this.addView(errorLayout, layoutParams);
    } else {
      errorLayout.setVisibility(VISIBLE);
    }
  }

  private void hideLoadingView() {
    if (loadingLayout != null && loadingLayout.getVisibility() != GONE) {
      loadingLayout.setVisibility(GONE);
    }
  }

  private void hideErrorView() {
    if (errorLayout != null && errorLayout.getVisibility() != GONE) {
      errorLayout.setVisibility(GONE);
    }
  }

  private void setContentVisibility(boolean visible) {
    for (View contentView : contentViews) {
      contentView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
  }
}