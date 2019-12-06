package com.sq26.experience.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.sq26.experience.R;

public class PullToRefreshView extends LinearLayout {
    public PullToRefreshView(Context context) {
        this(context, null);
    }

    public PullToRefreshView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initHeaderView(context);
    }

    private void initHeaderView(Context context) {
        View view = View.inflate(context, R.layout.view_refresh_header, null);
        addView(view, 0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

        }
        return super.onTouchEvent(event);
    }
}
