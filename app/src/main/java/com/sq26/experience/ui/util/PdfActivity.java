package com.sq26.experience.ui.util;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnDrawListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.sq26.experience.R;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PdfActivity extends AppCompatActivity {

    @BindView(R.id.pdfView)
    PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        ButterKnife.bind(this);

        //获取到系统intent
        Intent intent = getIntent();
        //判断是不是从其它应用过来的
        if (Objects.equals(intent.getAction(), Intent.ACTION_VIEW)) {
            //获取pdf的设置类
            PDFView.Configurator configurator = pdfView.fromUri(intent.getData());
            //这个设置没搞清楚,如果设置false滑动会变得很卡
            configurator.enableSwipe(true);
            //是否横向滑动,设置为false就是纵向滑动,true就是横向滑动
            configurator.swipeHorizontal(false);
            //是否可以双击放大,设置为false就无法双击放大了
            configurator.enableDoubletap(true);
            //默认显示的页数下标
            configurator.defaultPage(0);
            //获取pdfView的当前显示页的画板,在其上绘制其他内容
            configurator.onDraw(new OnDrawListener() {
                @Override
                public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {
                    //这个页面的宽度
//                    Log.d("pageWidth",pageWidth+"");
                    //这个页面的高度
//                    Log.d("pageHeight",pageHeight+"");
                    //这个页面的下标
//                    Log.d("displayedPage",displayedPage+"");
                }
            });

            //设置加载完成的回调
            configurator.onLoad(new OnLoadCompleteListener() {
                @Override
                public void loadComplete(int nbPages) {
                    Log.d("总页数", pdfView.getPageCount() + "");
                }
            });
            //开始加载pdf,这行放在最后,要先设置好监听回调
            configurator.load();
        } else {

        }
    }
}