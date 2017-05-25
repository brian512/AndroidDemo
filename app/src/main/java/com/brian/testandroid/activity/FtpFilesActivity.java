package com.brian.testandroid.activity;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.brian.common.BaseActivity;
import com.brian.testandroid.R;

import java.io.File;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;

/**
 * 测试ftp访问
 * Created by huamm on 2017/1/10 0010.
 */

public class FtpFilesActivity extends BaseActivity {
    /**
     * 只需要ip地址，不需要前面的ftp://
     */
    private static String HOST = "192.168.1.199";
    private static final int PORT = 21;
    private String USERNAME = "CreativeDream";
    private String PASSWORD = "admin";

    private ListView listView;
    private ArrayAdapter<String> adapter;

    private FTPClient client;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x001) {
                adapter.add((String) msg.obj);
            } else if (msg.what == 0x002) {
                Toast.makeText(FtpFilesActivity.this, "connect fail", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftp);

        listView = (ListView) findViewById(R.id.listView1);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);
        findViewById(R.id.btnGet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HOST = ((EditText) findViewById(R.id.ipedt)).getText().toString();

                adapter.clear();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        client = new FTPClient();
                        try {
                            client.connect(HOST, PORT);
                            client.login(USERNAME, PASSWORD);
                            String[] file = client.listNames();
                            for (int i = 0; i < file.length; i++) {
                                Log.i("file", file[i]);
                                Message message = handler.obtainMessage(0x001, file[i]);
                                handler.sendMessage(message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            handler.sendEmptyMessage(0x002);
                        }
                    }
                }).start();
            }
        });
        /**
         * commons-net-3.0.1.jar
         * listNames返回NULL,list返回Int,listFiles返回NULL
         * 因为传进去的参数是(String)null
         * 自己可以去了解，我这里就不演示了
         */

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                String dir = Environment.getExternalStorageDirectory() + "/test/";
                File fileDir = new File(dir);
                if (!fileDir.exists()) {
                    fileDir.mkdirs();
                }
                String path = dir + adapter.getItem(position);
                final File file = new File(path);
                if (file.exists()) {
                    file.delete();
                    Log.i("delete", "original file deleted");
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // 参考/doc/manual.en.html，最后面的参数是监听器
                            client.download(adapter.getItem(position), file, new MyTransferListener());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    public class MyTransferListener implements FTPDataTransferListener {
        public void started() {
            Log.i("download", "download start");
        }

        public void transferred(int length) {
            Log.i("download", "have download " + length + " bytes");
        }

        public void completed() {
            Log.i("download", "download completed");
        }

        public void aborted() {
            Log.i("download", "download aborted");
        }

        public void failed() {
            Log.i("download", "download failed");
        }
    }
}
