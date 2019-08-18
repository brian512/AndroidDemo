package com.brian.testandroid.activity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.brian.common.BaseActivity;
import com.brian.testandroid.R;
import com.brian.testandroid.TestService;

/**
 * @author Brian on 2019/8/18.
 */
public class NotificationActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        TextView showNotificaitonBtn = findViewById(R.id.btn_show_notification);
        TextView startServiceBtn = findViewById(R.id.btn_start_service);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "chat";
            String channelName = "聊天消息";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            NotificationManager notificationManager = (NotificationManager) getSystemService(
                    NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }


        showNotificaitonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNotification();
            }
        });

        startServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestService.start(getBaseContext());
            }
        });
    }


    private void createNotification() {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_layout);//通知栏布局
        Intent intent_main = new Intent(this, ConstrainLayoutDemoAcitivty.class);
        PendingIntent pending_intent_go = PendingIntent.getActivity(this, 1, intent_main, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.layout_content, pending_intent_go);

        Intent intent_cancel = new Intent();
        intent_cancel.setAction("ACTION_CANCEL");
        PendingIntent pending_intent_close = PendingIntent.getBroadcast(this, 2, intent_cancel, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.iv_close_notify, pending_intent_close);

        remoteViews.setTextViewText(R.id.title, "test");

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(getBaseContext(), "chat")
                .setContent(remoteViews)
//                .setContentTitle("收到一条聊天消息")
//                .setContentText("今天中午吃什么？")
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .build();
        manager.notify(1, notification);
    }
}
