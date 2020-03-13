/*
 * Copyright (c) 2020.  Wuhan Highway Technology Corp.
 */

package cn.com.hwtc.blurbehind;

import android.graphics.Bitmap;

/**
 * @author: caoyuan
 * @date: 2020/3/13
 */
public interface IScreenshot {
    Bitmap takeScreenshot();
    int width();
    int height();
}
