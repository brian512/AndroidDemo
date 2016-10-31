package com.brian.testandroid.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.brian.testandroid.common.BaseActivity;
import com.brian.testandroid.view.CommonDialogFragment;

/**
 * 测试DialogFragment
 * Created by huamm on 2016/10/31 0031.
 */

public class DialogFragmentActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        Button show = new Button(this);
        show.setText("show");
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonDialogFragment dialogFragment = new CommonDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "tag");
            }
        });
        setContentView(show);
    }
}
