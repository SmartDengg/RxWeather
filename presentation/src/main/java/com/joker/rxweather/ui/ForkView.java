package com.joker.rxweather.ui;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.joker.rxweather.common.Constants;

/**
 * Created by Administrator on 2015/11/5.
 */

@CoordinatorLayout.DefaultBehavior(ForkView.Behavior.class) public class ForkView extends TextView {

  private static final String TAG = ForkView.class.getSimpleName();

  public ForkView(Context context) {
    super(context);
  }

  public ForkView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public ForkView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public static class Behavior
      extends android.support.design.widget.CoordinatorLayout.Behavior<ForkView> {

    private boolean isOuting;

    public Behavior() {

    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, ForkView child, View dependency) {
      return dependency instanceof AppBarLayout;
    }

    @Override public boolean onDependentViewChanged(CoordinatorLayout parent, ForkView child,
        View dependency) {
      if (dependency instanceof AppBarLayout) {
        Behavior.this.updateVisibility(parent, (AppBarLayout) dependency, child);
      }
      return false;
    }

    private boolean updateVisibility(CoordinatorLayout parent, AppBarLayout appBarLayout,
        ForkView child) {

      CoordinatorLayout.LayoutParams layoutParams =
          (CoordinatorLayout.LayoutParams) child.getLayoutParams();

      if (layoutParams.getAnchorId() != appBarLayout.getId()) {
        return false;
      }

      int distanceToScroll = Behavior.this.calculateTranslation(child);

      /*other animation......*/
      //ViewCompat.setTranslationY(child, Math.abs(y) - distanceToScroll);

      if (appBarLayout.getY() == 0 && !Behavior.this.isOuting && child.getVisibility() != GONE) {
        ViewCompat.setTranslationY(child, -distanceToScroll);
        child.setVisibility(GONE);
      } else {
        if (Math.abs(appBarLayout.getY()) >= getMinimumHeightForVisibleOverlappingContent(
            appBarLayout)) {

          if (child.getVisibility() != VISIBLE) {
            this.animateIn(child, distanceToScroll);
          }
        } else {

          if (!Behavior.this.isOuting && child.getVisibility() == View.VISIBLE) {
            this.animateOut(child, -distanceToScroll);
          }
        }
      }
      return true;
    }

    final int getMinimumHeightForVisibleOverlappingContent(AppBarLayout appBarLayout) {
      int topInset = 0;
      int minHeight = ViewCompat.getMinimumHeight(appBarLayout);
      if (minHeight != 0) {
        return minHeight * 2 + topInset;
      } else {
        int childCount = appBarLayout.getChildCount();
        return childCount >= 1 ? ViewCompat.getMinimumHeight(
            appBarLayout.getChildAt(childCount - 1)) * 2 + topInset : 0;
      }

     /* try {
        Method method =
            AppBarLayout.class.getDeclaredMethod("getMinimumHeightForVisibleOverlappingContent");
        method.setAccessible(true);
        method.invoke(appBarLayout);
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }*/

    }

    private void animateIn(ForkView child, float distanceToScroll) {

      child.setVisibility(VISIBLE);
      ViewCompat.animate(child)
          .alpha(1.0f)
          .translationY(distanceToScroll)
          .setDuration(Constants.MILLISECONDS_400)
          .setInterpolator(new FastOutSlowInInterpolator())
          .withLayer()
          .setListener(null);
    }

    private void animateOut(final ForkView child, float distanceToScroll) {

      ViewCompat.animate(child)
          .alpha(0.4f)
          .translationY(distanceToScroll)
          .setDuration(Constants.MILLISECONDS_400)
          .setInterpolator(new FastOutLinearInInterpolator())
          .withLayer()
          .setListener(new ViewPropertyAnimatorListener() {
            public void onAnimationStart(View view) {
              Behavior.this.isOuting = true;
            }

            public void onAnimationCancel(View view) {
              Behavior.this.isOuting = false;
            }

            public void onAnimationEnd(View view) {
              Behavior.this.isOuting = false;
              view.setVisibility(View.GONE);
            }
          });
    }

    private int calculateTranslation(View view) {
      int height = view.getHeight();

      ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
      int margins = params.topMargin + params.bottomMargin;

      return height + margins;
    }
  }
}
