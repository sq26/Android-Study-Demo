package com.sq26.experience.app;

import android.app.Application;

import com.facebook.common.internal.Supplier;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //获取一套Fresco的默认配置
//        ImagePipelineConfig imagePipelineConfig = ImagePipelineConfig.newBuilder(this).build();
        //设置新的配置,其他不明所以的配置沿用以前的
//        Fresco.initialize(this, ImagePipelineConfig.newBuilder(this)
//                            .setBitmapMemoryCacheParamsSupplier(new Supplier<MemoryCacheParams>() {
//                        @Override
//                        public MemoryCacheParams get() {
//                            return new MemoryCacheParams(
//                                    imagePipelineConfig.getBitmapMemoryCacheParamsSupplier().get().maxCacheSize,//缓存的最大大小，以字节为单位。
//                                    10,//缓存中可以存在的最大项目数。
//                                    imagePipelineConfig.getBitmapMemoryCacheParamsSupplier().get().maxEvictionQueueSize,//逐出队列是一个存储区域，用于存储准备就绪的项目 *驱逐，但尚未删除。 这是该队列的最大大小（以字节为单位）
//                                    imagePipelineConfig.getBitmapMemoryCacheParamsSupplier().get().maxEvictionQueueEntries,//逐出队列中的最大条目数。
//                                    imagePipelineConfig.getBitmapMemoryCacheParamsSupplier().get().maxCacheEntrySize,//单个缓存条目的最大大小。
//                                    imagePipelineConfig.getBitmapMemoryCacheParamsSupplier().get().paramsCheckIntervalMs);//检查参数更新值之间的间隔（以毫秒为单位）。;
//                        }
//                }).build());

        Fresco.initialize(this);
    }
}
