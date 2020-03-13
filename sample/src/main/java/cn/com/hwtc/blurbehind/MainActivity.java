package cn.com.hwtc.blurbehind;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //默认方式模糊背景
        BlurBehind.with(this,(ImageView) findViewById(R.id.image));
        //设置截图的大小模糊背景
        BlurBehind.with(this).fromScreenshot(1920,1200).into((ImageView) findViewById(R.id.image));
        //自定义截图方式模糊背景
        BlurBehind.with(this).fromScreenshot(new IScreenshot() {
            @Override
            public Bitmap takeScreenshot() {
                return null;
            }

            @Override
            public int width() {
                return 0;
            }

            @Override
            public int height() {
                return 0;
            }
        }).into((ImageView) findViewById(R.id.image));
    }
}
