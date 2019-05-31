package com.sq26.androidstudydemo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sq26.androidstudydemo.R;
import com.sq26.androidstudydemo.activity.AudioControllerActivity;
import com.sq26.androidstudydemo.activity.BluetoothManageActivity;
import com.sq26.androidstudydemo.activity.NFCActivity;
import com.sq26.androidstudydemo.activity.QrcodeActivity;
import com.sq26.androidstudydemo.activity.RecyclerViewActivity;
import com.sq26.androidstudydemo.activity.StatusBarActivity;
import com.sq26.androidstudydemo.activity.WebViewActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class DrawerLeftFragment extends Fragment {
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drawer_left, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.statusBar, R.id.qrcode, R.id.webView, R.id.recyclerView, R.id.audioController, R.id.bluetooth, R.id.nfc})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.statusBar:
                startActivity(new Intent(getActivity(), StatusBarActivity.class));
                break;
            case R.id.qrcode:
                startActivity(new Intent(getActivity(), QrcodeActivity.class));
                break;
            case R.id.webView:
                startActivity(new Intent(getActivity(), WebViewActivity.class));
                break;
            case R.id.recyclerView:
                startActivity(new Intent(getActivity(), RecyclerViewActivity.class));
                break;
            case R.id.audioController:
                startActivity(new Intent(getActivity(), AudioControllerActivity.class));
                break;
            case R.id.bluetooth:
                startActivity(new Intent(getActivity(), BluetoothManageActivity.class));
                break;
            case R.id.nfc:
                startActivity(new Intent(getActivity(), NFCActivity.class));
                break;
        }
    }

}
