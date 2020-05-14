package com.sq26.experience.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sq26.experience.R;
import com.sq26.experience.aidl.IAidlInterface;
import com.sq26.experience.aidl.ICallbackAidlInterface;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AIDLActivity extends AppCompatActivity {

    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.editText)
    EditText editText;
    @BindView(R.id.button)
    Button button;

    private IAidlInterface iAidlInterface;
    private StringBuilder stringBuilder = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName("com.sq26.experience", "com.sq26.experience.service.AIDLService");
        intent.setComponent(componentName);

        ICallbackAidlInterface.Stub iCallbackAidlInterface = new ICallbackAidlInterface.Stub() {
            @Override
            public void pull(String text) throws RemoteException {
                stringBuilder.append(text);
                stringBuilder.append("\n");
                textView.setText(stringBuilder.toString());
            }
        };

        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                iAidlInterface = IAidlInterface.Stub.asInterface(iBinder);
                try {
                    iAidlInterface.registerCallback(getPackageName(), iCallbackAidlInterface);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };


        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);


    }

    @OnClick(R.id.button)
    public void onViewClicked() {
        if (!editText.getText().toString().isEmpty()) {
            try {
                iAidlInterface.pushText(getPackageName() + ":" + editText.getText().toString());
                editText.setText("");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            iAidlInterface.unregisterCallback(getPackageName());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
