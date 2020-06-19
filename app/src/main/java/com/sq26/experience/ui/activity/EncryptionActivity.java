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
    //公用密钥
    @BindView(R.id.key)
    TextInputEditText key;
    //明文
    @BindView(R.id.plaintext)
    TextInputEditText plaintext;
    //密文
    @BindView(R.id.ciphertext)
    TextInputEditText ciphertext;
    //用时
    @BindView(R.id.timing)
    TextView timing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encryption);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        //获取密钥
        key.setText(Base64.encodeToString(Encrypt.getRawKey(1000, 256, "PBKDF2withHmacSHA1And8BIT", "123456"), Base64.DEFAULT));
        //明文设置一些字符
        plaintext.setText("987654321");
    }

    @OnClick({R.id.encryption, R.id.decrypt})
    public void onViewClicked(View view) {
        long time = System.currentTimeMillis();
        switch (view.getId()) {
            //加密
            case R.id.encryption:
                new Encrypt.Builder()
                        .Algorithm(Encrypt.Algorithm_ChaCha20)
                        .Modes(Encrypt.Modes_NONE)
                        .Paddings(Encrypt.Paddings_NoPadding)
                        .Key(Base64.decode(key.getText().toString(), Base64.DEFAULT))
                        .Plaintext(plaintext.getText().toString().getBytes())
                        .setOpmode(Cipher.ENCRYPT_MODE)
                        .setOnComplete(new Encrypt.OnComplete() {
                            @Override
                            public void complete(byte[] cipherText) {
                                ciphertext.setText(Base64.encodeToString(cipherText, Base64.DEFAULT));
                                timing.setText("" + (System.currentTimeMillis() - time));
                            }
                        }).start();

                break;
            //解密
            case R.id.decrypt:
                new Encrypt.Builder()
                        .Algorithm(Encrypt.Algorithm_ChaCha20)
                        .Modes(Encrypt.Modes_NONE)
                        .Paddings(Encrypt.Paddings_NoPadding)
                        .Key(Base64.decode(key.getText().toString(), Base64.DEFAULT))
                        .Plaintext(Base64.decode(ciphertext.getText().toString().getBytes(), Base64.DEFAULT))
                        .setOpmode(Cipher.DECRYPT_MODE)
                        .setOnComplete(new Encrypt.OnComplete() {
                            @Override
                            public void complete(byte[] cipherText) {
                                plaintext.setText(new String(cipherText));
                                timing.setText("" + (System.currentTimeMillis() - time));
                            }
                        }).start();
                break;
        }
    }
}
