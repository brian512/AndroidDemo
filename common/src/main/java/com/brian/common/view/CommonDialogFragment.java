package com.brian.common.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.brian.common.R;

/**
 * dialog
 * Created by huamm on 2016/10/31 0031.
 */
public class CommonDialogFragment extends DialogFragment {

    private String tag = "";
    private FragmentManager mFragmentManager;

    private CharSequence mTitleText = "提示";
    private CharSequence mContentText = "";
    private CharSequence mNegativeButtonText = "";
    private CharSequence mPositiveButtonText = "确定";

    private boolean mCancelable = false;

    private DialogInterface.OnClickListener mPositiveButtonListener;
    private DialogInterface.OnClickListener mNegativeButtonListener;
    private DialogInterface.OnDismissListener mOnDismissListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_common, null);
        TextView titleTv = (TextView) view.findViewById(R.id.dialog_title);
        TextView textTv = (TextView) view.findViewById(R.id.dialog_text);
        titleTv.setText(mTitleText);
        textTv.setText(mContentText);
        builder.setView(view)
                .setCancelable(mCancelable)
                .setPositiveButton(mPositiveButtonText, mPositiveButtonListener)
                .setNegativeButton(mNegativeButtonText, mNegativeButtonListener);
        return builder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss(dialog);
        }
    }

    public static CommonDialogFragment create(FragmentManager fragmentManager) {
        return create(fragmentManager, "tag");
    }

    public static CommonDialogFragment create(FragmentManager fragmentManager, String tag) {
        CommonDialogFragment dialogFragment = new CommonDialogFragment();
        dialogFragment.mFragmentManager = fragmentManager;
        dialogFragment.tag = tag;
        return dialogFragment;
    }

    public CommonDialogFragment setTitle(CharSequence text) {
        mTitleText = text;
        return this;
    }

    public CommonDialogFragment setMessage(CharSequence text) {
        mContentText = text;
        return this;
    }

    public CommonDialogFragment setDialogCancelable(boolean cancelable) {
        mCancelable = cancelable;
        return this;
    }

//    public CommonDialogFragment setPositiveButton(int resId, DialogInterface.OnClickListener listener) {
//        mPositiveButtonListener = listener;
//        mPositiveButtonText = getResources().getText(resId);
//        return this;
//    }
//
//    public CommonDialogFragment setNegativeButton(int resId, DialogInterface.OnClickListener listener) {
//        mNegativeButtonListener = listener;
//        mNegativeButtonText = getResources().getText(resId);
//        return this;
//    }

    public CommonDialogFragment setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener) {
        mPositiveButtonListener = listener;
        mPositiveButtonText = text;
        return this;
    }

    public CommonDialogFragment setNegativeButton(CharSequence text, DialogInterface.OnClickListener listener) {
        mNegativeButtonListener = listener;
        mNegativeButtonText = text;
        return this;
    }

    public CommonDialogFragment setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
        return this;
    }

    public void show() {
        super.show(mFragmentManager, tag);
    }
}
