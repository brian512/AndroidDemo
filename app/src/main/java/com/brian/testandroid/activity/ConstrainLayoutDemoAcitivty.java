package com.brian.testandroid.activity;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;

import com.brian.common.BaseActivity;
import com.brian.testandroid.R;

/**
 * @author Brian on 2019/7/20.
 */
public class ConstrainLayoutDemoAcitivty extends BaseActivity {

    private ConstraintLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_constrainlayout_demo);
    }
}
