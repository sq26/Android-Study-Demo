package com.sq26.experience.util.media;

import android.net.Uri;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.sq26.experience.util.FileUtil;

public class SimpleDraweeViewUtils {
    //用于设置预览图片的大小
    public static void setDraweeController(Uri uri, SimpleDraweeView simpleDraweeView, int width, int height) {
        if (FileUtil.isAbsolutePath(uri.toString())) {
            String path = "file://" + uri.toString();
            uri = Uri.parse(path);
        }
        //创建一个ImageRequest用于获取图片内容,并设置图片链接
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                //设置图片尺寸
                .setResizeOptions(new ResizeOptions(width, height))
                //创建
                .build();
        //创建一个DraweeController,对加载显示的图片做更多的控制和定制
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                //指定配置,使用旧的配置,不新建
                .setOldController(simpleDraweeView.getController())
                .setImageRequest(request)
                .build();
        simpleDraweeView.setController(controller);
    }

    public static void setDraweeController(String uri, SimpleDraweeView simpleDraweeView, int width, int height) {
        setDraweeController(Uri.parse(uri), simpleDraweeView, width, height);
    }

    public static void setDraweeController(String uri, SimpleDraweeView simpleDraweeView, int size) {
        setDraweeController(Uri.parse(uri), simpleDraweeView, size, size);
    }

    public static void setDraweeController(Uri uri, SimpleDraweeView simpleDraweeView, int size) {
        setDraweeController(uri, simpleDraweeView, size, size);
    }
}
