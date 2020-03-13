/*
 * Copyright (c) 2020.  Wuhan Highway Technology Corp.
 */

package cn.com.hwtc.blurbehind;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.animation.AlphaAnimation;

/**
 * @author: caoyuan
 * @date: 2020/3/12
 */
public final class Helper {

  public static void setBackground(View v, Drawable drawable) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      v.setBackground(drawable);
    } else {
      v.setBackgroundDrawable(drawable);
    }
  }

  public static boolean hasZero(int... args) {
    for (int num : args) {
      if (num == 0) {
        return true;
      }
    }
    return false;
  }

  public static void animate(View v, int duration) {
    AlphaAnimation alpha = new AlphaAnimation(0f, 1f);
    alpha.setDuration(duration);
    v.startAnimation(alpha);
  }
}
