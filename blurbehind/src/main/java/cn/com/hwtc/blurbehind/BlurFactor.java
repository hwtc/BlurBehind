/*
 * Copyright (c) 2020.  Wuhan Highway Technology Corp.
 */

package cn.com.hwtc.blurbehind;

import android.graphics.Color;

/**
 * @author: caoyuan
 * @date: 2020/3/12
 */
class BlurFactor {

   static final int DEFAULT_RADIUS = 23;
   static final int DEFAULT_SAMPLING = 4;

   int width;
   int height;
   int radius = DEFAULT_RADIUS;
   int sampling = DEFAULT_SAMPLING;
   int color = Color.TRANSPARENT;
}
