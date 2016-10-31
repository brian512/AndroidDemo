package com.brian.testandroid.view;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.brian.testandroid.R;

/**
 * dialog
 * Created by huamm on 2016/10/31 0031.
 */
public class CommonDialogFragment extends DialogFragment {

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
////        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
//        return inflater.inflate(R.layout.dialog_common, container);
//    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_common, null);
        builder.setView(view)
                .setPositiveButton("还不错", null)
                .setNegativeButton("一般般", null);
        return builder.create();
    }
}
