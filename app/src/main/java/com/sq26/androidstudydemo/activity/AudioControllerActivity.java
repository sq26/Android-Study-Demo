package com.sq26.androidstudydemo.activity;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;

import com.sq26.androidstudydemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AudioControllerActivity extends AppCompatActivity {

    @BindView(R.id.alarmSeekBar)
    SeekBar alarmSeekBar;
    @BindView(R.id.musicSeekBar)
    SeekBar musicSeekBar;
    @BindView(R.id.notificationSeekBar)
    SeekBar notificationSeekBar;
    @BindView(R.id.ringSeekBar)
    SeekBar ringSeekBar;
    @BindView(R.id.systemSeekBar)
    SeekBar systemSeekBar;
    @BindView(R.id.voiceCallSeekBar)
    SeekBar voiceCallSeekBar;
    @BindView(R.id.DTMFSeekBar)
    SeekBar DTMFSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_controller);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        alarmSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM));
        alarmSeekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_ALARM));
        musicSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        musicSeekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        notificationSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION));
        notificationSeekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION));
        ringSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_RING));
        ringSeekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_RING));
        systemSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM));
        systemSeekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM));
        voiceCallSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL));
        voiceCallSeekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL));
        DTMFSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_DTMF));
        DTMFSeekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_DTMF));
    }
}
