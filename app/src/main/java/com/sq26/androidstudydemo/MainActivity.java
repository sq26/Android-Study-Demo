package com.sq26.androidstudydemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;

import com.sq26.androidstudydemo.implement.DrawerListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements DrawerListener {

    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    //    DrawerContentFragment中设置的接口回调
    @Override
    public void setStartDrawer(boolean isStart) {
        Log.i("t", "被调用了");
        if (isStart) {
            drawerLayout.openDrawer(Gravity.START);
        }else{
            drawerLayout.closeDrawers();
        }
    }
}
