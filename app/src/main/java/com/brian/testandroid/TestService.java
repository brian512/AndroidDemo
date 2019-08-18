package com.brian.testandroid;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.brian.common.util.LogUtil;
import com.brian.testandroid.activity.ConstrainLayoutDemoAcitivty;

/**
 * @author Brian on 2019/8/18.
 */
public class TestService extends Service {

    public static void start(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, TestService.class);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d("onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(1, createNotification());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
        LogUtil.d("onDestroy");
    }


    private Notification createNotification() {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_layout);//通知栏布局
        Intent intent_main = new Intent(this, ConstrainLayoutDemoAcitivty.class);
        PendingIntent pending_intent_go = PendingIntent.getActivity(this, 1, intent_main, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.layout_content, pending_intent_go);

        Intent intent_cancel = new Intent();
        intent_cancel.setAction("ACTION_CANCEL"); // 需要注册一个广播接收该点击事件
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
//        manager.notify(1, notification);
        return notification;
    }
}
