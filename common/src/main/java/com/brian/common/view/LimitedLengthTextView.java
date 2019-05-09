package com.brian.common.view;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextDirectionHeuristic;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;

import com.brian.common.util.LogUtil;

public class LimitedLengthTextView extends AppCompatTextView {

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
        final String name = "卡卡西的超级超级超级小跟班";
        final String str = "欢迎 %s 来到 超级心动女友";
        CharSequence text = TextUtils.ellipsize(String.format(str, name), getPaint(), getWidth(), TextUtils.TruncateAt.END, false, new TextUtils.EllipsizeCallback() {
            @Override
            public void ellipsized(int start, int end) {
                LogUtil.d("start=" + start + "; end=" + end);
                String cutOut = String.format(str, name).substring(start, end);
                float avail = getPaint().measureText(name) - getPaint().measureText(cutOut);
                CharSequence n = TextUtils.ellipsize(name, getPaint(), avail, TextUtils.TruncateAt.END);
                setText(String.format(str, n));
            }
        });
        LogUtil.d("textView.getText()=" + getText());
        LogUtil.d("text=" + text);
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

}
