/*
 * Copyright (c) 2020.  Wuhan Highway Technology Corp.
 */

package cn.com.hwtc.blurbehind;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import static cn.com.hwtc.blurbehind.BlurTask.KEY_CACHE_BLURRED_BACKGROUND_IMAGE;
import static cn.com.hwtc.blurbehind.BlurTask.mImageCache;

/**
 * @author: caoyuan
 * @date: 2020/3/12
 */
public class BlurBehind {

    private static final String TAG = BlurBehind.class.getSimpleName();

    public static void with(final Context context, final ImageView view) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            Window window = activity.getWindow();
            if (window != null) {
                final long startMs = System.currentTimeMillis();
                // 获取截图
                final View activityView = window.getDecorView();
                activityView.post(new Runnable() {
                    @Override
                    public void run() {
                        activityView.setDrawingCacheEnabled(true);
                        activityView.destroyDrawingCache();
                        activityView.buildDrawingCache();
                        final Bitmap bmp = activityView.getDrawingCache();
                        Log.d("BlurTask","getDrawingCache bitmap:"+bmp);
                        Composer composer = new Composer(context);
                        BitmapComposer bitmapComposer = composer.fromScreenshot(new IScreenshot() {
                            @Override
                            public Bitmap takeScreenshot() {
                                return bmp;
                            }

                            @Override
                            public int width() {
                                return bmp.getWidth();
                            }

                            @Override
                            public int height() {
                                return bmp.getHeight();
                            }
                        });
                        bitmapComposer.into(view);
                        Log.d(TAG, "getDrawingCache take away:" + (System.currentTimeMillis() - startMs) + "ms");
                    }
                });
            }
        }
    }

    public static Composer with(final Context context) {
        return new Composer(context);
    }

    public static class Composer {

        private BlurFactor factor;

        private Context context;

        public Composer(Context context) {
            factor = new BlurFactor();
            this.context = context;
        }

        public BitmapComposer fromScreenshot(int width, int height) {
            return new BitmapComposer(context, width, height, factor);
        }

        public BitmapComposer fromScreenshot(IScreenshot iSurfaceControl) {
            return new BitmapComposer(context, iSurfaceControl, factor);
        }
    }

    public static class BitmapComposer {
        private BlurFactor factor;
        private int bitmapWidth, bitmapHeight;
        private Context mContext;
        private IScreenshot iSurfaceControl;

        public BitmapComposer(Context context, int bitmapWidth, int bitmapHeight, BlurFactor factor) {
            this.mContext = context;
            this.factor = factor;
            this.bitmapWidth = bitmapWidth;
            this.bitmapHeight = bitmapHeight;
        }

        public BitmapComposer(Context context, IScreenshot iSurfaceControlt, BlurFactor factor) {
            this.mContext = context;
            this.factor = factor;
            this.iSurfaceControl = iSurfaceControlt;
            this.bitmapWidth = iSurfaceControlt.width();
            this.bitmapHeight = iSurfaceControlt.height();
        }

        public void into(final ImageView view) {
            factor.width = bitmapWidth;
            factor.height = bitmapHeight;
            BlurTask task = new BlurTask(mContext, factor, iSurfaceControl, new BlurTask.Callback() {
                @Override
                public void doneBlur(BitmapDrawable drawable) {
                    if (view != null) {
                        view.setVisibility(View.VISIBLE);
                        view.setImageBitmap(drawable.getBitmap());
                        mImageCache.remove(KEY_CACHE_BLURRED_BACKGROUND_IMAGE);
                    }
                }
            });
            task.execute();
        }
    }
}
