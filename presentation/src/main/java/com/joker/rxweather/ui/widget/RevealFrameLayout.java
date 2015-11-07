package com.joker.rxweather.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.joker.rxweather.ui.animation.RevealAnimator;
import com.joker.rxweather.ui.animation.SupportAnimator;
import com.joker.rxweather.ui.animation.ViewAnimationUtils;

public class RevealFrameLayout extends FrameLayout implements RevealAnimator {

  private Path mRevealPath;
  private final Rect mTargetBounds = new Rect();
  private RevealAnimator.RevealInfo mRevealInfo;
  private boolean mRunning;
  private float mRadius;

  public RevealFrameLayout(Context context) {
    this(context, null);
  }

  public RevealFrameLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public RevealFrameLayout(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    mRevealPath = new Path();
  }

  @Override public void onRevealAnimationStart() {
    this.mRunning = true;
  }

  @Override public void onRevealAnimationEnd() {
    this.mRunning = false;
    RevealFrameLayout.this.invalidate(mTargetBounds);
  }

  @Override public void onRevealAnimationCancel() {
    RevealFrameLayout.this.onRevealAnimationEnd();
  }

  /**
   * Circle radius size
   *
   * @hide
   */
  @Override public void setRevealRadius(float radius) {
    this.mRadius = radius;
    mRevealInfo.getTarget().getHitRect(mTargetBounds);

    /*只刷新固定区域，其他区域不变*/
    RevealFrameLayout.this.invalidate(mTargetBounds);
  }

  /**
   * Circle radius size
   *
   * @hide
   */
  @Override public float getRevealRadius() {
    return mRadius;
  }

  /**
   * @hide
   */
  @Override public void initRevealInfo(RevealInfo info) {
    mRevealInfo = info;
  }

  /**
   * @hide
   */
  @Override public SupportAnimator startReverseAnimation() {
    if (mRevealInfo != null && mRevealInfo.hasTarget() && !mRunning) {
      return ViewAnimationUtils.createCircularReveal(mRevealInfo.getTarget(), mRevealInfo.centerX,
          mRevealInfo.centerY, mRevealInfo.endRadius, mRevealInfo.startRadius);
    }
    return null;
  }

  @Override protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
    if (mRunning && child == mRevealInfo.getTarget()) {
      final int state = canvas.save();

      mRevealPath.reset();
      mRevealPath.addCircle(mRevealInfo.centerX, mRevealInfo.centerY, mRadius, Path.Direction.CW);

      canvas.clipPath(mRevealPath);

      boolean isInvalided = super.drawChild(canvas, child, drawingTime);

      /*恢复为之前堆栈保存的编号为int的Canvas状态*/
      canvas.restoreToCount(state);

      return isInvalided;
    }

    return super.drawChild(canvas, child, drawingTime);
  }
}
