package com.sq26.experience.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.fastjson.JSONArray;
import com.sq26.experience.R;
import com.sq26.experience.entity.JsonArrayViewMode;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewModeActivity extends AppCompatActivity {

    @BindView(R.id.text)
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_mode);
        ButterKnife.bind(this);

        text.setText("hello world");

        //创建ViewMode的使用方法
        //ViewMode会随着activity和fragment的结束而结束
        //ViewMode中不允许存在activity,fragment和View的引用,以防止生命周期结束,内存无法释放
        //需要使用context时使用AndroidViewModelFactory创建一个带有Application的ViewMode(ViewMode要继承AndroidViewModel)
        //不需要就用NewInstanceFactory创建无参的ViewMode
        //ViewMode用于纯粹的数据管理和数据处理
        //配合liveDate做同步或异步的数据刷新
        //创建视图数据模板
        JsonArrayViewMode jsonArrayViewMode = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(JsonArrayViewMode.class);
        //在旋转屏幕时会调用onCreate,这里不会重新创建数据,但内部的观察者监听的回调会被重新调用
        jsonArrayViewMode.getJsonArray().observe(this, new Observer<JSONArray>() {
            @Override
            public void onChanged(JSONArray objects) {
                Log.i("1","1");
                //设置数据和恢复数据
                text.setText(objects.toJSONString());
            }
        });
    }
}