package com.brian.common.view;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;

import com.brian.common.util.LogUtil;

public class LimitedLengthTextView extends AppCompatTextView {


    private int mFitTextStart = 0;
    private int mFitTextEnd = 0;


    private ViewTreeObserver.OnGlobalLayoutListener mLayoutListener;

    public LimitedLengthTextView(Context context) {
        this(context, null, 0);
    }

    public LimitedLengthTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LimitedLengthTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //这里未把观察者注销，是因为我的TextView宽高是随时变化的，如果是固定的最好根据需要注销掉
        mLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                analyzeProcess();
            }
        };
    }

    /**
     * 通过 layout 的 getEllipsisCount(int line) 方法，来获取被省略的部分数量
     * 为0时就是没省略
     * 在利用自定义的监听器返回给待操作的对象；
     */
    private void analyzeProcess() {
        final CharSequence originalText = getText();
        LogUtil.d("originalText=" + originalText);
        TextUtils.ellipsize(originalText, getPaint(), getWidth(), TextUtils.TruncateAt.END, false, new TextUtils.EllipsizeCallback() {
            @Override
            public void ellipsized(int start, int end) {
                LogUtil.d("start=" + start + "; end=" + end);
                if (start >= end || mFitTextStart >= mFitTextEnd) {
                    return;
                }
                if (mFitTextEnd > originalText.length()) {
                    mFitTextEnd = originalText.length();
                }
                CharSequence originalCutOut = originalText.subSequence(start, end);
                CharSequence fitText = originalText.subSequence(mFitTextStart, mFitTextEnd);
                float targetTextWidth = getPaint().measureText(fitText.toString());
                float cutOutWidth = getPaint().measureText(originalCutOut.toString());
                if (cutOutWidth > targetTextWidth) {
                }
                float avail = targetTextWidth - cutOutWidth + getPaint().measureText("\u2026");
                CharSequence newKeep = TextUtils.ellipsize(fitText, getPaint(), avail, TextUtils.TruncateAt.END);
                StringBuilder builder = new StringBuilder();
                builder.append(originalText.subSequence(0, mFitTextStart))
                        .append(newKeep)
                        .append(originalText.subSequence(mFitTextEnd, originalText.length()));
                LogUtil.d("mFitTextStart=" + originalText.subSequence(0, mFitTextStart));
                LogUtil.d("newKeep=" + newKeep);
                LogUtil.d("mFitTextEnd=" + originalText.subSequence(mFitTextEnd, originalText.length()));
                mFitTextStart = 0;
                mFitTextEnd = 0;
                setText(builder.toString());
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(mLayoutListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(mLayoutListener);
    }


    public void setFitTextRange(int start, int end) {
        if (start <= end) {
            mFitTextStart = start;
            mFitTextEnd = end;
        }
    }

}
