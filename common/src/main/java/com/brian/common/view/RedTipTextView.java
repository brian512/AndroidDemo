package com.brian.common.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

import com.brian.common.R;

/**
 * Created by huamm on 2016/7/12 0012.
 */
public class RedTipTextView extends TextView {
    private int mTipVisibility = VISIBLE;
    private Bitmap mTipDrawable;

    public RedTipTextView(Context context) {
        this(context, null, 0);
    }

    public RedTipTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RedTipTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mTipDrawable = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_reddot_notify);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mTipVisibility == VISIBLE) {
            canvas.drawBitmap(mTipDrawable, getWidth() - mTipDrawable.getWidth(), 0, null);
        }
    }

    public void setVisibility(int visibility) {
        mTipVisibility = visibility;
        invalidate();
    }
}
