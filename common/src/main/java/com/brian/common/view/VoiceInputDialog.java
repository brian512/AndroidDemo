package com.brian.common.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brian.common.R;
import com.brian.common.util.LogUtil;

/**
 * Created by huamm on 2018/7/25.
 */

public class VoiceInputDialog extends Dialog {
    private final static String TAG = "VoiceInputDialog";

    public interface OnInputDialogListener {
        void onWordsSend(String text);

        void onInputActive(boolean active, int layoutDelta);

        void onDismiss();
    }

    private InputLayout mInputLayout;

    private InputMethodManager mInputMethodManager;

    private OnInputDialogListener mOnInputDialogListener;

    private boolean mActive = false;

    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        public void onGlobalLayout() {
            possiblyResizeChildOfContent();
        }
    };
    private FrameLayout.LayoutParams mFrameLayoutParams;
    private int mUsableHeightPrevious;

    private int mScreenHeight = 0;

    private int mMaxBottom = mScreenHeight;

    private int mOffset = 0;

    public VoiceInputDialog(Context context, @NonNull OnInputDialogListener inputDialogListener) {
        super(context, R.style.BaseDialogStyle);
        mOnInputDialogListener = inputDialogListener;

        if (mInputMethodManager == null) {
            mInputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mInputLayout = new InputLayout(getContext()));

        mFrameLayoutParams = (FrameLayout.LayoutParams) mInputLayout.getLayoutParams();

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.x = 0;
        lp.y = 0;
        lp.height = mScreenHeight = getScreenHeight();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;
        lp.gravity = Gravity.TOP;
        getWindow().setAttributes(lp);

        mMaxBottom = mScreenHeight;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mInputLayout.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mInputLayout.getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
        }
    }

    @Override
    public void show() {
        super.show();

        if(!isSoftShow()) {
            showSoftInput();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (isSoftShow()) {
            hideSoftInput();
        }

        if (mActive) {
            mOnInputDialogListener.onInputActive(mActive = false, 0);
        }
        mOnInputDialogListener.onDismiss();
    }

    public void clearText() {
        mInputLayout.mEditText.setText("");
    }


    private void showSoftInput() {
        mInputLayout.mEditText.requestFocus();
        mInputLayout.mEditText.post(new Runnable() {
            @Override
            public void run() {
                mInputMethodManager.showSoftInput(mInputLayout.mEditText, InputMethodManager.SHOW_FORCED);
            }
        });
    }

    private void hideSoftInput() {
        mInputMethodManager.hideSoftInputFromWindow(mInputLayout.getWindowToken(), 0);
    }

    private boolean isSoftShow() {
        Rect rec = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rec);
        mOffset = (mMaxBottom - rec.bottom); // 记录底部偏差，兼容华为手机动态隐藏导航栏
        LogUtil.d(TAG, "mOffset=" + mOffset);
        return mInputMethodManager.isActive();
    }

    private void possiblyResizeChildOfContent() {
        Rect rect = new Rect();
        mInputLayout.getWindowVisibleDisplayFrame(rect);
        if (rect.bottom > mMaxBottom) {
            mMaxBottom = rect.bottom;
        }

        int usableHeightNow = (rect.bottom - rect.top);
        if (usableHeightNow == mUsableHeightPrevious) {
            return;
        }
        LogUtil.d("mMaxBottom=" + mMaxBottom + "; rect.bottom=" + rect.bottom);
        int heightDifference = mMaxBottom - rect.bottom; // 最大底部与当前底部的间距，判断是否为键盘事件
        if (mOffset > 0) { // 矫正华为手机动态隐藏导航栏
            heightDifference = heightDifference - mOffset;
        }

        if (heightDifference > (mScreenHeight/4)) {
            // keyboard probably just became visible
            if (!mActive) {
                mOnInputDialogListener.onInputActive(mActive=true, heightDifference);
            }
        } else {
            // keyboard probably just became hidden
            if (mActive) {
                mOnInputDialogListener.onInputActive(mActive=false, 0);
                dismiss();
            }
        }
        LogUtil.d("heightDifference=" + heightDifference);
        // 全屏模式下需要手动更新布局，否则不会被顶上来
        mFrameLayoutParams.height = usableHeightNow;
        mInputLayout.requestLayout();
        mUsableHeightPrevious = usableHeightNow;
    }

    private int getScreenHeight() {
        Resources resources = getContext().getApplicationContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.heightPixels;
    }


    /**
     * UI布局
     */
    public class InputLayout extends LinearLayout {
        private VoiceChatInputBar mChatBar;

        private EditText mEditText;

        private TextView mSentBtn;

        public InputLayout(Context context) {
            this(context, null, -1);
        }
        public InputLayout(Context context, AttributeSet attrs) {
            this(context, attrs, -1);
        }
        public InputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }
        @SuppressLint("NewApi")
        public InputLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
            init();
        }

        private void init() {
            setOrientation(VERTICAL);
            setGravity(Gravity.BOTTOM);

            mChatBar = (VoiceChatInputBar) LayoutInflater.from(getContext()).inflate(R.layout.xlvoice_dialog_voice_room_input, this, false);
            mEditText = mChatBar.findViewById(R.id.msg_input);
            mSentBtn = mChatBar.findViewById(R.id.msg_send);
            addView(mChatBar, new LinearLayoutCompat.LayoutParams(-1, -2, Gravity.BOTTOM));

            initListeners();

            mEditText.requestFocus();
        }

        private void initListeners() {
            mSentBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnInputDialogListener != null) {
                        mOnInputDialogListener.onWordsSend(mEditText.getText().toString());
                    }
                }
            });

            mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == R.id.msg_send || actionId == EditorInfo.IME_NULL
                            || actionId == EditorInfo.IME_ACTION_SEND) {
                        if (mOnInputDialogListener != null) {
                            mOnInputDialogListener.onWordsSend(mEditText.getText().toString());
                        }
                        return true;
                    }
                    return false;
                }
            });

            mEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().length() == 0) {
                        mSentBtn.setEnabled(false);
                    } else {
                        mSentBtn.setEnabled(true);
                    }
                }
            });
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            boolean handle = super.dispatchTouchEvent(ev);
            if (!handle && mActive && ev.getAction() == MotionEvent.ACTION_DOWN) {
                hideSoftInput();
            }
            return handle;
        }
    }
}
