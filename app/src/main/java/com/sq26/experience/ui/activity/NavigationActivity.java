package com.sq26.experience.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavHostController;
import androidx.navigation.Navigation;

import com.sq26.experience.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
//Navigation的基本使用
public class NavigationActivity extends AppCompatActivity {

    @BindView(R.id.button1)
    Button button1;
    @BindView(R.id.button2)
    Button button2;
    //声明NavController,整个 Navigation 架构中 最重要的核心类，我们所有的导航行为都由 NavController 处理
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        ButterKnife.bind(this);
        //获取navController
        navController = Navigation.findNavController(this, R.id.fragment);
    }

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                //跳转到blankFragment
                navController.navigate(R.id.blankFragment);
                break;
            case R.id.button2:
                //跳转到blank2Fragment
                navController.navigate(R.id.blank2Fragment);
                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        //接管activity的返回
        return navController.navigateUp();
    }
}