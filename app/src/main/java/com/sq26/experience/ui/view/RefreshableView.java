package com.sq26.experience.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.sq26.experience.R;

public class RefreshableView extends LinearLayout implements View.OnTouchListener {

    private View header;

    public RefreshableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        header = LayoutInflater.from(context).inflate(R.layout.layout_header, null);
        setOrientation(VERTICAL);
        addView(header, 0);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            RecyclerView recyclerView = (RecyclerView) getChildAt(1);
            recyclerView.setOnTouchListener(this);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {


        return false;
    }
}
