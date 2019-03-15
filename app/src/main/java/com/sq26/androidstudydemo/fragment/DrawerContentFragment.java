package com.sq26.androidstudydemo.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sq26.androidstudydemo.R;
import com.sq26.androidstudydemo.implement.DrawerListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DrawerContentFragment extends Fragment {
    @BindView(R.id.toobar)
    Toolbar toobar;
    Unbinder unbinder;

    //声明内部定义的回调接口
    DrawerListener drawerListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                 //通过getActivity()获取用于回调修改文本方法的接口
        drawerListener = (DrawerListener) getActivity();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drawer_content, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        toobar.setTitle("首页");
        toobar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toobar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerListener.setStartDrawer(true);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
