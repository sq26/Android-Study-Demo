package com.sq26.experience.ui.activity;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.sq26.experience.R;
import com.sq26.experience.util.Encrypt;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EncryptionActivity extends AppCompatActivity {

    @BindView(R.id.key)
    TextInputEditText key;
    @BindView(R.id.plaintext)
    TextInputEditText plaintext;
    @BindView(R.id.ciphertext)
    TextInputEditText ciphertext;
    @BindView(R.id.timing)
    TextView timing;

    private byte[] plainByte;
    private byte[] cipherByte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encryption);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {

        key.setText(Base64.encodeToString(Encrypt.getRawKey(), Base64.DEFAULT));

        plaintext.setText("987654321");
        plainByte = new String("987654321").getBytes();

    }

    @OnClick({R.id.encryption, R.id.decrypt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.encryption:
                Log.d("123", Base64.encodeToString(Base64.decode(key.getText().toString(), Base64.DEFAULT), Base64.DEFAULT));
                new Encrypt.Builder()
                        .Algorithm(Encrypt.Algorithm_AES)
                        .Modes(Encrypt.Modes_CBC)
                        .Paddings(Encrypt.Paddings_PKCS5Padding)
                        .Key(Base64.decode(key.getText().toString(), Base64.DEFAULT))
                        .Plaintext(plaintext.getText().toString().getBytes())
                        .setOpmode(Cipher.ENCRYPT_MODE)
                        .setOnComplete(new Encrypt.OnComplete() {
                            @Override
                            public void complete(byte[] cipherText) {
                                cipherByte = cipherText;
                                ciphertext.setText(Base64.encodeToString(cipherText, Base64.DEFAULT));
                            }
                        }).start();

                break;
            case R.id.decrypt:
                new Encrypt.Builder()
                        .Algorithm(Encrypt.Algorithm_AES)
                        .Modes(Encrypt.Modes_CBC)
                        .Paddings(Encrypt.Paddings_PKCS5Padding)
                        .Key(Base64.decode(key.getText().toString(), Base64.DEFAULT))
                        .Plaintext(cipherByte)
                        .setOpmode(Cipher.DECRYPT_MODE)
                        .setOnComplete(new Encrypt.OnComplete() {
                            @Override
                            public void complete(byte[] cipherText) {
                                plainByte = cipherText;
                                plaintext.setText(new String(cipherText));
                            }
                        }).start();
                break;
        }
    }
}
