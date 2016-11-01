package com.brian.testandroid.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.brian.testandroid.common.BaseActivity;
import com.brian.testandroid.util.ToastUtil;
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
                CommonDialogFragment.create(getSupportFragmentManager())
                        .setPositiveBtnListener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ToastUtil.showMsg("好哇");
                            }
                        })
                        .setContentText("第三方撒你看风景啊快乐")
                        .setTitleText("标题哈哈")
                        .show();
            }
        });
        setContentView(show);
    }
}
