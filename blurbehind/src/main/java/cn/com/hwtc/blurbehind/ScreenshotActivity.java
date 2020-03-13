/*
 * Copyright (c) 2020.  Wuhan Highway Technology Corp.
 */

package cn.com.hwtc.blurbehind;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

/**
 * @author: caoyuan
 * @date: 2020/3/13
 */
public class ScreenshotActivity extends Activity {
    private static final String TAG = ScreenshotActivity.class.getSimpleName();
    private MediaProjectionManager mMediaProjectionManager;
    public int mWidth;
    public int mHeight;
    public int mDpi;
    private Bitmap mBitmap;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startScreenShot() {
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            mWidth = displayMetrics.widthPixels;
            mHeight = displayMetrics.heightPixels;
            mDpi = displayMetrics.densityDpi;
        }
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        if (mMediaProjectionManager != null) {
            startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), 123);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MediaProjection mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
        if (mediaProjection != null) {
            getBitmap(mediaProjection);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void getBitmap(MediaProjection mediaProjection) {
        ImageReader imageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 3);
        mediaProjection.createVirtualDisplay("screen_shot",
                mWidth, mHeight, mDpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader.getSurface(), null, null);
        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                Image image = reader.acquireNextImage();
                int width = image.getWidth();
                int height = image.getHeight();
                final Image.Plane[] planes = image.getPlanes();
                int pixelStride = planes[0].getPixelStride();
                int rowStride = planes[0].getRowStride();
                int rowPadding = rowStride - pixelStride * width;
                mBitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
                image.close();
            }
        }, null);

    }

}
