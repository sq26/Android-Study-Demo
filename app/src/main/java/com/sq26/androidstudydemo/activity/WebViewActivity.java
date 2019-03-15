package com.sq26.androidstudydemo.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.sq26.androidstudydemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WebViewActivity extends AppCompatActivity {

    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.imageView)
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
//启用JavaScript
        webView.getSettings().setJavaScriptEnabled(true);
        //设置可以手势缩放
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        //去掉滚动条
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        //设置允许js打开新的窗口
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        //设置页面渲染(设置后可以弹出alert)
        webView.setWebChromeClient(new WebChromeClient());
        //设置不缓存页面
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        Log.i("w", getWindow().getDecorView().getWidth() + "");
        Log.i("w1", webView.getWidth() + "");

        webView.loadUrl("http://192.168.0.144:8080/index.html" + "?text=123456");

    }

    @OnClick(R.id.button)
    public void onViewClicked() {
        Bitmap bitmap = capture(webView, webView.getWidth(), webView.getHeight(), false, Bitmap.Config.ARGB_8888);
        imageView.setImageBitmap(bitmap);

    }

    public static Bitmap capture(View view, float width, float height, boolean scroll, Bitmap.Config config) {
//        if (!view.isDrawingCacheEnabled()) {
//            view.setDrawingCacheEnabled(true);
//        }
        Bitmap bitmap = Bitmap.createBitmap((int) width, (int) height, config);
        bitmap.eraseColor(Color.WHITE);
        Canvas canvas = new Canvas(bitmap);
        int left = view.getLeft();
        int top = view.getTop();
        if (scroll) {
            left = view.getScrollX();
            top = view.getScrollY();
        }
        int status = canvas.save();
        canvas.translate(-left, -top);
        float scale = width / view.getWidth();
        canvas.scale(scale, scale, left, top);
        view.draw(canvas);
        canvas.restoreToCount(status);
        Paint alphaPaint = new Paint();
        alphaPaint.setColor(Color.TRANSPARENT);
        canvas.drawRect(0f, 0f, 1f, height, alphaPaint);
        canvas.drawRect(width - 1f, 0f, width, height, alphaPaint);
        canvas.drawRect(0f, 0f, width, 1f, alphaPaint);
        canvas.drawRect(0f, height - 1f, width, height, alphaPaint);
        canvas.setBitmap(null);
        return bitmap;
    }

}
