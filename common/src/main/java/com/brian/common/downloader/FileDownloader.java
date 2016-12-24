package com.brian.common.downloader;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.brian.common.WeakRefHandler;
import com.brian.common.util.LogUtil;

import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Queue;

import static com.brian.common.Env.TAG;

/**
 * 文件下载
 * Created by huamm on 2016/12/6 0006.
 */

public class FileDownloader {

    public static final int MAX_TASK_COUNT = 3;

    private HashMap<String, TaskManager> mTaskMap;

    private Queue<TaskInfo> mTaskQueue;


//    private static final HandlerThread mHThread = new HandlerThread("DownloadManager");
//    static {
//        mHThread.start();
//    }

    private static final int MSG_TASK_NEW = 0;
    private static final int MSG_TASK_START = 1;
    private static final int MSG_TASK_PAUSE = 2;
    private static final int MSG_TASK_RESUME = 3;
    private static final int MSG_TASK_CANCEL = 4;
    private static final int MSG_NEXT = 5;

    private Handler.Callback mCallback = new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_TASK_NEW:
                    TaskInfo info = (TaskInfo) msg.obj;
                    TaskManager manager = mTaskMap.get(info.url);
                    if (manager != null) { // 若任务已存在，则增加优先级
                        info = manager.getTaskInfo();
                        info.priority++;
                    } else {
                        mTaskQueue.add(info);
                        manager = new TaskManager(info);
                        mTaskMap.put(info.url, manager);
                    }
                    mH.obtainMessage(MSG_NEXT).sendToTarget();
                    break;
                case MSG_TASK_START:
                    String url = (String) msg.obj;
                    manager = mTaskMap.get(url);
                    if (manager != null) {
                        manager.start();
                    }
                    break;
                case MSG_TASK_PAUSE:
                    url = (String) msg.obj;
                    manager = mTaskMap.get(url);
                    if (manager != null) {
                        manager.pause();
                    }
                    break;
                case MSG_TASK_RESUME:
                    url = (String) msg.obj;
                    manager = mTaskMap.get(url);
                    if (manager != null) {
                        manager.resume();
                    }
                    break;
                case MSG_TASK_CANCEL:
                    url = (String) msg.obj;
                    manager = mTaskMap.get(url);
                    if (manager != null) {
                        manager.cancel();
                    }
                    break;
                case MSG_NEXT:
                    mH.removeMessages(MSG_NEXT);
                    startNextTask();
                    break;
            }
            return false;
        }
    };
    private WeakRefHandler mH = new WeakRefHandler(mCallback, Looper.getMainLooper());

    private static FileDownloader sInstance;
    private FileDownloader() {
        mTaskMap = new HashMap<>();
        mTaskQueue = new PriorityQueue<>();
    }

    public static FileDownloader getInstance() {
        if (sInstance == null) {
            synchronized (FileDownloader.class) {
                if (sInstance == null) {
                    sInstance = new FileDownloader();
                }
            }
        }
        return sInstance;
    }

    private void startNextTask() {
        int dlTaskCount = getTaskCountByState(DownloadState.STATE_DOWNLOADING);
        int waitCount = getTaskCountByState(DownloadState.STATE_WAITING);
        if (waitCount > 0 && dlTaskCount < MAX_TASK_COUNT) {
            TaskInfo info = mTaskQueue.poll();
            TaskManager manager = mTaskMap.get(info.url);
            manager.start();
            mH.removeMessages(MSG_NEXT);
        }

    }

    public void start(String url, IDownloadCallback callback) {
        TaskInfo info = new TaskInfo();
        info.url = url;
        info.callback = callback;
        info.priority = TaskInfo.PRIORITY_NORMAL;

        mH.obtainMessage(MSG_TASK_NEW, info).sendToTarget();
    }

    public void pause(String url) {
        mH.obtainMessage(MSG_TASK_PAUSE, url).sendToTarget();
    }

    public void resume(String url) {
        mH.obtainMessage(MSG_TASK_RESUME, url).sendToTarget();
    }

    public void cancel(String url) {
        mH.obtainMessage(MSG_TASK_CANCEL, url).sendToTarget();
    }

    protected boolean checkTaskExist(String url) {
        return true;
    }

    protected int getTaskCountByState(int state) {
        if (mTaskQueue.size() <= 0) {
            LogUtil.e(TAG, "mTaskQueue=" + mTaskQueue);
            return 0;
        }
        int count = 0;
        for (TaskInfo fileInfo : mTaskQueue) {
            if (fileInfo.state == state) {
                count++;
            }
        }
        return count;
    }
}
