package com.brian.testandroid.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.brian.testandroid.R;

/**
 * dialog
 * Created by huamm on 2016/10/31 0031.
 */
public class CommonDialogFragment extends DialogFragment {

    private String tag = "";
    private FragmentManager mFragmentManager;

    private CharSequence mTitleText = "提示";
    private CharSequence mContentText = "";
    private CharSequence mCancelText = "";
    private CharSequence mConfirmText = "确定";

    private boolean mCancelable = false;

    private DialogInterface.OnClickListener mConfirmListener;
    private DialogInterface.OnClickListener mCancelListener;

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
                .setPositiveButton(mConfirmText, mConfirmListener)
                .setNegativeButton(mCancelText, mCancelListener);
        return builder.create();
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

    public CommonDialogFragment setTitleText(CharSequence text) {
        mTitleText = text;
        return this;
    }

    public CommonDialogFragment setContentText(CharSequence text) {
        mContentText = text;
        return this;
    }

    public CommonDialogFragment setPositiveBtnText(CharSequence text) {
        mConfirmText = text;
        return this;
    }

    public CommonDialogFragment setNegativeBtnText(CharSequence text) {
        mCancelText = text;
        return this;
    }

    public CommonDialogFragment setDialogCancelable(boolean cancelable) {
        mCancelable = cancelable;
        return this;
    }

    public CommonDialogFragment setPositiveBtnListener(DialogInterface.OnClickListener listener) {
        mConfirmListener = listener;
        return this;
    }

    public CommonDialogFragment setNegativeBtnListener(DialogInterface.OnClickListener listener) {
        mCancelListener = listener;
        return this;
    }

    public void show() {
        super.show(mFragmentManager, tag);
    }
}
