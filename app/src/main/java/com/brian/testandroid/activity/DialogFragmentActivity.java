package com.brian.testandroid.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.brian.common.BaseActivity;
import com.brian.common.util.ToastUtil;
import com.brian.common.view.CommonDialogFragment;

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
                CommonDialogFragment.create(getFragmentManager())
                        .setPositiveButton(getText(android.R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ToastUtil.showMsg("好哇");
                            }
                        })
                        .setMessage("第三方撒你看风景啊快乐")
                        .setTitle("标题哈哈")
                        .show();
            }
        });
        setContentView(show);
    }
}
