/*
 * Copyright (c) 2020.  Wuhan Highway Technology Corp.
 */

package cn.com.hwtc.blurbehind;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.LruCache;
import android.view.SurfaceControl;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: caoyuan
 * @date: 2020/3/12
 */
class BlurTask {
    static final String KEY_CACHE_BLURRED_BACKGROUND_IMAGE = "KEY_CACHE_BLURRED_BACKGROUND_IMAGE";
    static final LruCache<String, Bitmap> mImageCache = new LruCache<String, Bitmap>(1);

    public interface Callback {
        void doneBlur(BitmapDrawable drawable);
    }

    private Resources res;
    private WeakReference<Context> contextWeakRef;
    private IScreenshot mISurfaceControl;
    private BlurFactor factor;
    private Callback callback;
    private static ExecutorService THREAD_POOL = Executors.newCachedThreadPool();

    BlurTask(Context context, BlurFactor factor, IScreenshot iSurfaceControl, Callback callback) {
        this.res = context.getResources();
        this.factor = factor;
        this.callback = callback;
        this.contextWeakRef = new WeakReference<Context>(context);
        this.mISurfaceControl = iSurfaceControl;
    }

    BlurTask(Context context, BlurFactor factor, Callback callback) {
        this.res = context.getResources();
        this.factor = factor;
        this.callback = callback;
        this.contextWeakRef = new WeakReference<Context>(context);

    }

    void execute() {
        THREAD_POOL.execute(new Runnable() {
            @Override
            public void run() {
                Context context = contextWeakRef.get();
                BitmapDrawable bitmapDrawable = null;
                if (mImageCache.size() != 0) {
                    bitmapDrawable = new BitmapDrawable(context.getResources(), mImageCache.get(KEY_CACHE_BLURRED_BACKGROUND_IMAGE));
                } else {
                    Bitmap bitmap = null;
                    if (mISurfaceControl != null) {
                        if (context instanceof Activity) {
                            Log.d("BlurTask","ActivityScreenShot bitmap:"+bitmap+",mISurfaceControl:"+mISurfaceControl);
                                bitmap = mISurfaceControl.takeScreenshot();
                                Log.d("BlurTask","ActivityScreenShot bitmap:"+bitmap);
                            //no-op
                        } else {
                            bitmap = mISurfaceControl.takeScreenshot();
                            Log.d("BlurTask","takeScreenshot bitmap:"+bitmap);
                            //Bitmap bitmap = android.view.SurfaceControl.screenshot(factor.width,factor.height); // 截屏 做模糊效果
                        }
                    }else {
                        bitmap = screenShotByReflect(factor.width, factor.height);
                        Log.d("BlurTask","screenShotByReflect bitmap:"+bitmap);
                    }
                    Log.d("BlurTask","onPanelDrag width:"+factor.width+" ,height:"+factor.height+" ,bitmap:"+bitmap);
                    if (bitmap != null) {
                        bitmapDrawable =
                                new BitmapDrawable(res, Blur.of(context, bitmap, factor));
                        mImageCache.put(KEY_CACHE_BLURRED_BACKGROUND_IMAGE, bitmap);
                    }else {
                        Log.d("Error","screenShot failure...");
                    }
                }
                if (callback != null) {
                    if (bitmapDrawable == null) return;
                    final BitmapDrawable finalBitmapDrawable = bitmapDrawable;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            callback.doneBlur(finalBitmapDrawable);
                        }
                    });
                }
            }
        });
    }


    //使用反射调用截屏
    private Bitmap screenShotByReflect(int width, int height) {
        try {
            Class<?> demo = Class.forName("android.view.SurfaceControl");
            Method method = demo.getDeclaredMethod("screenshot", int.class, int.class);
            return (Bitmap) method.invoke(null, width, height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
